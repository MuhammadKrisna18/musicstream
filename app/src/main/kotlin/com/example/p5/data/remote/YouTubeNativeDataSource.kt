package com.example.p5.data.remote

import com.example.p5.data.config.MusicConfig
import com.example.p5.data.remote.innertube.InnerTubeFormatter
import com.example.p5.data.remote.innertube.InnerTubeParser
import com.example.p5.data.remote.innertube.InnerTubeSessionManager
import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeGenreConfig
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class YouTubeNativeDataSource(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val sessionManager: InnerTubeSessionManager = InnerTubeSessionManager(okHttpClient),
    private val parser: InnerTubeParser = InnerTubeParser()
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private var cachedHomeFeed: HomeFeed? = null
    private val genreFeedCache = ConcurrentHashMap<String, List<HomeSection>>()
    private val audioStreamCache = ConcurrentHashMap<String, AudioStream>()

    suspend fun search(query: String, limit: Int = 20): List<Song> = withContext(ioDispatcher) {
        searchWithPlaylists(query, limit).first
    }

    suspend fun searchWithPlaylists(query: String, limit: Int = 20): Pair<List<Song>, List<Playlist>> = withContext(ioDispatcher) {
        val payload = sessionManager.buildSearchPayload(query)

        val request = Request.Builder()
            .url(MusicConfig.YOUTUBE_SEARCH_URL)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .addHeader("Origin", MusicConfig.YOUTUBE_MUSIC_ORIGIN)
            .addHeader("Referer", MusicConfig.YOUTUBE_MUSIC_REFERER)
            .addHeader("X-YouTube-Client-Name", "67")
            .addHeader("X-YouTube-Client-Version", "1.20240801.01.00")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Search request failed: ${response.code}")
        }

        val jsonString = response.body?.string() ?: throw IOException("Empty search response")
        val searchPayload = parser.parseSearchPayload(jsonString, limit)
        Pair(searchPayload.songs, searchPayload.playlists)
    }

    suspend fun searchPlaylists(query: String, limit: Int = 5): List<Playlist> {
        return searchWithPlaylists(query, limit).second
    }

    suspend fun getPlaylistTracks(playlistId: String): List<Song> = withContext(ioDispatcher) {
        val targetBrowseId = if (playlistId.startsWith("VL")) playlistId else "VL$playlistId"
        try {
            val jsonString = fetchBrowse(targetBrowseId)
            val songs = parser.parsePlaylistTracks(jsonString)
            if (songs.isNotEmpty()) return@withContext songs
        } catch (_: Exception) {
        }
        emptyList()
    }

    suspend fun getArtistProfile(channelId: String): ArtistProfile = withContext(ioDispatcher) {
        val jsonString = fetchBrowse(channelId)
        parser.parseArtistProfile(channelId, jsonString)
    }

    suspend fun getArtistAllSongs(playlistId: String): List<Song> = withContext(ioDispatcher) {
        getPlaylistTracks(playlistId)
    }

    suspend fun getAudioStream(videoId: String): AudioStream = withContext(ioDispatcher) {
        val payload = sessionManager.buildPlayerPayload(videoId)

        val request = Request.Builder()
            .url(MusicConfig.YOUTUBE_PLAYER_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-YouTube-Client-Name", "101")
            .addHeader("X-YouTube-Client-Version", "1.02")
            .addHeader("Origin", MusicConfig.YOUTUBE_BASE_ORIGIN)
            .addHeader("X-Goog-Visitor-Id", sessionManager.getVisitorData())
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 15_7_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonString = response.body?.string()
                if (!jsonString.isNullOrEmpty()) {
                    val stream = parser.parsePlayerResponse(videoId, jsonString)
                    audioStreamCache[videoId] = stream
                    return@withContext stream
                }
            }
        } catch (_: Exception) {
        }

        try {
            val secondaryPayload = sessionManager.buildSecondaryPlayerPayload(videoId)
            val secondaryRequest = Request.Builder()
                .url(MusicConfig.YOUTUBE_PLAYER_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-YouTube-Client-Name", "5")
                .addHeader("X-YouTube-Client-Version", "20.10.1")
                .addHeader("Origin", MusicConfig.YOUTUBE_BASE_ORIGIN)
                .addHeader("User-Agent", "com.google.ios.youtube/20.10.1 (iPhone16,2; U; CPU iOS 18_3_2 like Mac OS X;)")
                .post(secondaryPayload.toString().toRequestBody(jsonMediaType))
                .build()

            val secondaryResponse = okHttpClient.newCall(secondaryRequest).execute()
            if (secondaryResponse.isSuccessful) {
                val jsonString = secondaryResponse.body?.string()
                if (!jsonString.isNullOrEmpty()) {
                    val stream = parser.parsePlayerResponse(videoId, jsonString)
                    audioStreamCache[videoId] = stream
                    return@withContext stream
                }
            }
        } catch (_: Exception) {
        }

        audioStreamCache[videoId]?.let {
            return@withContext it
        }

        throw IOException("Failed to load audio stream and no cached stream available")
    }

    suspend fun getHomeFeed(refresh: Boolean = false): HomeFeed = withContext(ioDispatcher) {
        if (!refresh && cachedHomeFeed != null) {
            return@withContext cachedHomeFeed!!
        }

        val feed = try {
            fetchNativeMusicFeed(randomize = refresh)
        } catch (_: Exception) {
            loadFallbackFeed(randomize = refresh)
        }

        cachedHomeFeed = feed
        feed
    }

    private suspend fun fetchNativeMusicFeed(randomize: Boolean = false): HomeFeed = coroutineScope {
        val homeDeferred = async { runCatching { fetchBrowse("FEmusic_home") }.getOrNull() }
        val exploreDeferred = async { runCatching { fetchBrowse("FEmusic_explore") }.getOrNull() }
        val moodsGenresDeferred = async { runCatching { fetchBrowse("FEmusic_moods_and_genres") }.getOrNull() }
        val chartDeferred = async { runCatching { fetchBrowse("FEmusic_charts") }.getOrNull() }

        val homeJson = homeDeferred.await()
        val exploreJson = exploreDeferred.await()
        val moodsGenresJson = moodsGenresDeferred.await()
        val chartJson = chartDeferred.await()

        val sections = mutableListOf<HomeSection>()
        val dynamicPlaylists = mutableListOf<Playlist>()
        val dynamicCategories = mutableListOf<Category>()

        if (!chartJson.isNullOrEmpty()) {
            val parsedChart = parser.parseBrowseResponse(chartJson, prefixId = "chart")
            sections.addAll(parsedChart.sections)
            dynamicPlaylists.addAll(parsedChart.playlists)
        }

        if (!homeJson.isNullOrEmpty()) {
            val parsedHome = parser.parseBrowseResponse(homeJson, prefixId = "home")
            sections.addAll(parsedHome.sections)
            dynamicPlaylists.addAll(parsedHome.playlists)
        }

        if (!exploreJson.isNullOrEmpty()) {
            val parsedExplore = parser.parseBrowseResponse(exploreJson, prefixId = "explore")
            sections.addAll(parsedExplore.sections)
            dynamicPlaylists.addAll(parsedExplore.playlists)
            val cleanCategories = parsedExplore.categories.filter { !InnerTubeFormatter.isExcludedMoodOrActivity(it.title) }
            dynamicCategories.addAll(cleanCategories)
        }

        if (!moodsGenresJson.isNullOrEmpty()) {
            val parsedMoods = parser.parseBrowseResponse(moodsGenresJson, prefixId = "moods_genres")
            dynamicPlaylists.addAll(parsedMoods.playlists)
            val cleanCategories = parsedMoods.categories.filter { !InnerTubeFormatter.isExcludedMoodOrActivity(it.title) }
            dynamicCategories.addAll(cleanCategories)
        }

        val filteredSections = sections.filter { it.tracks.isNotEmpty() }
        val finalSections = if (randomize && filteredSections.size > 1) {
            val first = filteredSections.first().let { sec ->
                sec.copy(tracks = sec.tracks.shuffled())
            }
            val rest = filteredSections.drop(1).shuffled().map { sec ->
                sec.copy(tracks = sec.tracks.shuffled())
            }
            listOf(first) + rest
        } else {
            filteredSections
        }

        val curatedCategories = HomeGenreConfig.ALL.map { genre ->
            val matched = dynamicCategories.find {
                it.title.equals(genre.title, ignoreCase = true) ||
                    it.id.equals(genre.id, ignoreCase = true)
            }
            if (matched != null && matched.browseId.isNotBlank()) {
                genre.toCategory().copy(browseId = matched.browseId, params = matched.params)
            } else {
                genre.toCategory()
            }
        }

        HomeFeed(
            sections = finalSections,
            moodTags = emptyList(),
            categories = curatedCategories,
            playlists = dynamicPlaylists.distinctBy { it.id }.take(10)
        )
    }

    private fun fetchBrowse(browseId: String, params: String? = null): String {
        val payload = sessionManager.buildBrowsePayload(browseId, params)

        val request = Request.Builder()
            .url(MusicConfig.YOUTUBE_BROWSE_URL)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .addHeader("Origin", MusicConfig.YOUTUBE_MUSIC_ORIGIN)
            .addHeader("Referer", MusicConfig.YOUTUBE_MUSIC_REFERER)
            .addHeader("X-YouTube-Client-Name", "67")
            .addHeader("X-YouTube-Client-Version", "1.20240801.01.00")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Browse request failed: ${response.code}")
        }

        return response.body?.string() ?: throw IOException("Empty browse response")
    }

    suspend fun getGenreFeed(category: Category): List<HomeSection> = withContext(ioDispatcher) {
        val cached = genreFeedCache[category.id]
        if (!cached.isNullOrEmpty()) {
            return@withContext cached
        }

        if (category.browseId.isNotBlank() && category.params.isNotBlank()) {
            try {
                val json = fetchBrowse(category.browseId, category.params)
                val parsed = parser.parseBrowseResponse(json, prefixId = "genre_${category.id}")
                if (parsed.sections.isNotEmpty()) {
                    genreFeedCache[category.id] = parsed.sections
                    return@withContext parsed.sections
                }
            } catch (_: Exception) {
            }
        }

        val homeGenre = HomeGenreConfig.findById(category.id) ?: HomeGenreConfig.findByTitle(category.title)
        val fallbackQuery = homeGenre?.query ?: category.query.ifBlank { "Lagu ${category.title}" }
        val songs = search(fallbackQuery, limit = 20)
        val fallbackSections = if (songs.isNotEmpty()) {
            val subtitle = homeGenre?.subtitle ?: "Pilihan lagu ${category.title}"
            listOf(
                HomeSection(
                    id = "genre_section_${category.id}",
                    title = category.title,
                    subtitle = subtitle,
                    query = fallbackQuery,
                    tracks = songs,
                    style = HomeSectionStyle.STANDARD_CARDS
                )
            )
        } else {
            emptyList()
        }

        if (fallbackSections.isNotEmpty()) {
            genreFeedCache[category.id] = fallbackSections
        }
        fallbackSections
    }

    private suspend fun loadFallbackFeed(randomize: Boolean = false): HomeFeed = coroutineScope {
        val allQueries = listOf(
            Triple("Trending Populer", "Lagu paling populer dan viral saat ini", HomeGenreConfig.POP.query),
            Triple("Indie Pop & Akustik", "Alunan melodi indie dan petikan akustik syahdu", HomeGenreConfig.INDIE_POP.query),
            Triple("Dangdut & Koplo Terkini", "Irama kendang koplo dan dangdut pilihan", HomeGenreConfig.KOPLO.query),
            Triple("R&B & Soul Lounge", "Groove berkelas dan alunan vokal memikat", HomeGenreConfig.RNB.query),
            Triple("Jazz & Keroncong Syahdu", "Harmoni indah jazz dan langgam keroncong modern", HomeGenreConfig.JAZZ.query)
        )

        val selectedQueries = if (randomize) {
            listOf(allQueries.first()) + allQueries.drop(1).shuffled()
        } else {
            allQueries
        }

        val sections = selectedQueries.map { (title, subtitle, q) ->
            async {
                val songs = try {
                    search(q, limit = 10)
                } catch (_: Exception) {
                    emptyList()
                }
                val sectionStyle = when {
                    title.contains("Chart", ignoreCase = true) || title.contains("Trending", ignoreCase = true) -> HomeSectionStyle.RANKED_CHARTS
                    title.contains("Rilisan", ignoreCase = true) -> HomeSectionStyle.QUICK_PICKS_GRID
                    else -> HomeSectionStyle.STANDARD_CARDS
                }
                HomeSection(
                    id = title.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_"),
                    title = title,
                    subtitle = subtitle,
                    query = q,
                    tracks = songs,
                    style = sectionStyle
                )
            }
        }.map { it.await() }.filter { it.tracks.isNotEmpty() }

        HomeFeed(
            sections = sections,
            moodTags = emptyList(),
            categories = HomeGenreConfig.CATEGORIES
        )
    }

    fun getCategories(): HomeCategories {
        val feed = cachedHomeFeed
        val categories = if (feed != null && feed.categories.isNotEmpty()) {
            feed.categories
        } else {
            HomeGenreConfig.CATEGORIES
        }
        return HomeCategories(
            moodTags = emptyList(),
            categories = categories
        )
    }

    suspend fun getTrending(limit: Int = 15): List<Song> {
        val feed = cachedHomeFeed ?: try {
            getHomeFeed(refresh = false)
        } catch (_: Exception) {
            null
        }

        val trendingSection = feed?.sections?.firstOrNull {
            it.title.contains("trending", ignoreCase = true) || it.title.contains("pilihan", ignoreCase = true)
        } ?: feed?.sections?.firstOrNull()

        if (trendingSection != null && trendingSection.tracks.isNotEmpty()) {
            return trendingSection.tracks.take(limit)
        }

        return search("top hits indonesia official video singles", limit)
    }
}
