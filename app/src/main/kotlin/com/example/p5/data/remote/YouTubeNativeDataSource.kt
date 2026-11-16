package com.example.p5.data.remote

import com.example.p5.data.config.MusicConfig
import com.example.p5.data.remote.innertube.InnerTubeFormatter
import com.example.p5.data.remote.innertube.InnerTubeParser
import com.example.p5.data.remote.innertube.InnerTubeSessionManager
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
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

    suspend fun search(query: String, limit: Int = 20): List<Song> = withContext(ioDispatcher) {
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
        parser.parseSearchResults(jsonString, limit)
    }

    suspend fun searchWithPlaylists(query: String, limit: Int = 20): Pair<List<Song>, List<Playlist>> = withContext(ioDispatcher) {
        val payload = sessionManager.buildSearchPayload(query)

        val request = Request.Builder()
            .url(MusicConfig.YOUTUBE_SEARCH_URL)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .addHeader("Origin", MusicConfig.YOUTUBE_BASE_ORIGIN)
            .addHeader("X-YouTube-Client-Name", "1")
            .addHeader("X-YouTube-Client-Version", "2.20240726.00.00")
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

    suspend fun getAudioStream(videoId: String): AudioStream = withContext(ioDispatcher) {
        val payload = sessionManager.buildPlayerPayload(videoId)

        val request = Request.Builder()
            .url(MusicConfig.YOUTUBE_PLAYER_URL)
            .post(payload.toString().toRequestBody(jsonMediaType))
            .addHeader("User-Agent", "com.google.android.apps.youtube.music/6.40.52 (Linux; U; Android 14)")
            .addHeader("X-YouTube-Client-Name", "ANDROID")
            .addHeader("X-YouTube-Client-Version", "20.05.34")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonString = response.body?.string()
                if (!jsonString.isNullOrEmpty()) {
                    return@withContext parser.parsePlayerResponse(videoId, jsonString)
                }
            }
        } catch (_: Exception) {
        }

        val secondaryPayload = sessionManager.buildSecondaryPlayerPayload(videoId)
        val secondaryRequest = Request.Builder()
            .url(MusicConfig.YOUTUBE_PLAYER_URL)
            .post(secondaryPayload.toString().toRequestBody(jsonMediaType))
            .addHeader("User-Agent", "com.google.android.apps.youtube.music/6.40.52 (Linux; U; Android 14)")
            .addHeader("X-YouTube-Client-Name", "ANDROID")
            .addHeader("X-YouTube-Client-Version", "20.10.38")
            .build()

        val secondaryResponse = okHttpClient.newCall(secondaryRequest).execute()
        if (!secondaryResponse.isSuccessful) {
            throw IOException("Player request failed: ${secondaryResponse.code}")
        }

        val jsonString = secondaryResponse.body?.string() ?: throw IOException("Empty player response")
        parser.parsePlayerResponse(videoId, jsonString)
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
        val selectedChartId = if (randomize) {
            MusicConfig.chartPlaylistIds.shuffled().first()
        } else {
            MusicConfig.chartPlaylistIds.first()
        }

        val homeDeferred = async { runCatching { fetchBrowse("FEmusic_home") }.getOrNull() }
        val exploreDeferred = async { runCatching { fetchBrowse("FEmusic_explore") }.getOrNull() }
        val moodsGenresDeferred = async { runCatching { fetchBrowse("FEmusic_moods_and_genres") }.getOrNull() }
        val chartDeferred = async { runCatching { fetchBrowse(selectedChartId) }.getOrNull() }

        val homeJson = homeDeferred.await()
        val exploreJson = exploreDeferred.await()
        val moodsGenresJson = moodsGenresDeferred.await()
        val chartJson = chartDeferred.await()

        val sections = mutableListOf<HomeSection>()
        val dynamicPlaylists = mutableListOf<Playlist>()
        var dynamicMoodTags: List<MoodTag> = emptyList()
        var dynamicCategories: List<Category> = emptyList()

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
            if (parsedExplore.moodTags.isNotEmpty()) {
                dynamicMoodTags = parsedExplore.moodTags
            }
            val cleanCategories = parsedExplore.categories.filter { !InnerTubeFormatter.isExcludedMoodOrActivity(it.title) }
            if (cleanCategories.isNotEmpty()) {
                dynamicCategories = cleanCategories
            }
        }

        if (!moodsGenresJson.isNullOrEmpty()) {
            val parsedMoods = parser.parseBrowseResponse(moodsGenresJson, prefixId = "moods_genres")
            dynamicPlaylists.addAll(parsedMoods.playlists)
            if (parsedMoods.moodTags.isNotEmpty()) {
                dynamicMoodTags = parsedMoods.moodTags
            }
            val cleanCategories = parsedMoods.categories.filter { !InnerTubeFormatter.isExcludedMoodOrActivity(it.title) }
            if (cleanCategories.isNotEmpty()) {
                dynamicCategories = cleanCategories
            }
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

        val baseMoodTags = if (dynamicMoodTags.isNotEmpty()) dynamicMoodTags else MusicConfig.staticMoodTags
        val pureCategories = dynamicCategories.filter { !InnerTubeFormatter.isExcludedMoodOrActivity(it.title) }
        val baseCategories = if (pureCategories.isNotEmpty()) pureCategories else MusicConfig.staticCategories

        HomeFeed(
            sections = finalSections,
            moodTags = if (randomize) baseMoodTags.shuffled() else baseMoodTags,
            categories = if (randomize) baseCategories.shuffled() else baseCategories,
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

        val fallbackQuery = category.query.ifBlank { "Lagu ${category.title}" }
        val songs = search(fallbackQuery, limit = 20)
        val fallbackSections = if (songs.isNotEmpty()) {
            listOf(
                HomeSection(
                    id = "genre_section_${category.id}",
                    title = category.title,
                    subtitle = "Pilihan lagu ${category.title}",
                    query = fallbackQuery,
                    tracks = songs
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
            Triple("Trending Now", "Lagu paling populer dan viral saat ini", "top hits indonesia official video singles"),
            Triple("Rilisan Terbaru", "Karya musik terbaru dari para musisi", "latest official music video singles"),
            Triple("Chill & Akustik", "Teman santai dan rileks hari ini", "acoustic chill pop singles"),
            Triple("Top Charts Populer", "Daftar tangga lagu yang sering didengarkan", "indonesia top chart music video"),
            Triple("Indie Senja", "Alunan melodi indie lokal favorit", "indie pop indonesia music video")
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
                HomeSection(
                    id = title.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_"),
                    title = title,
                    subtitle = subtitle,
                    query = q,
                    tracks = songs
                )
            }
        }.map { it.await() }.filter { it.tracks.isNotEmpty() }

        HomeFeed(
            sections = sections,
            moodTags = if (randomize) MusicConfig.staticMoodTags.shuffled() else MusicConfig.staticMoodTags,
            categories = if (randomize) MusicConfig.staticCategories.shuffled() else MusicConfig.staticCategories
        )
    }

    fun getCategories(): HomeCategories {
        val feed = cachedHomeFeed
        return if (feed != null && feed.categories.isNotEmpty()) {
            HomeCategories(
                moodTags = feed.moodTags,
                categories = feed.categories
            )
        } else {
            HomeCategories(
                moodTags = MusicConfig.staticMoodTags,
                categories = MusicConfig.staticCategories
            )
        }
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
