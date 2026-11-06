package com.example.p5.data.remote

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.MoodTag
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

class YouTubeNativeDataSource(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    @Volatile
    private var cachedVisitorData: String = "CgtRRENOdVRKd3FPcyjWjobXBjIKCgJJRBIEGgAgUGKrAgqoAkN0c0JDQVVTalFFQkRCNlBERGgwUlRKTWNXaUhzNkRRZl9XVkNGQ0RoQ0VyODZvdHlsNHdERUZ4aTNQWVRKVTBteXpETF9wQW84bVVhdFZqOFBocnNycGp2aWZCa1hVd043dGpMVzNLZGFuVHNxQmFrX2syS2JxcTVfT3N5eEVwQ0tZYWxseGF3bWFUZXBiM3hJeGtuZ3cyVmZsU3ZHeERDanRRd2ttMk52eUZEeDRRYWNJSXFFOE5HLWxtY3NOTGlIQVNPZkVvQlRKRkFSeFk5V3F6OWxHU3V4dmVJSmloenJjR3hwNHFGaUwwTk1KRGtYV0FDMmdFSjQ5dllSbG83ZVkwb0lUeVR6OFA5R3lEOFIwUUtvTlc1WnRNVldmdWpXQnBodklL"

    @Volatile
    private var cachedSts: Int = 20751

    @Volatile
    private var lastSessionFetchTime: Long = 0L

    private val isFetchingSession = AtomicBoolean(false)

    @Volatile
    private var cachedHomeFeed: HomeFeed? = null

    private val chartPlaylistIds = listOf(
        "VLPL4fGSI1pDJn4lW6MJA2q37CfnraqAizqE", // Tangga Lagu Top 50 Indonesia
        "VLPL4fGSI1pDJn40q9u1gXg7m3A0PZp0o28S", // Top 100 Global Hits
        "VLPLFgquLnL59alW3xmYiWRaoz0o56HTr",    // Pop Terkini Indonesia
        "VLPL4fGSI1pDJn5x6p9_2N14k9A44eB26Zp8", // Akustik & Santai
        "VLPL4fGSI1pDJn4fGzC1bO8vO8S0B5D9l_x2"  // Fresh New Releases
    )

    private val staticMoodTags = listOf(
        MoodTag("top_hits", "🔥 Top Hits", "Lagu Trending"),
        MoodTag("santai", "☕ Santai", "Lagu Santai"),
        MoodTag("olah_raga", "⚡ Olah Raga", "Lagu Workout"),
        MoodTag("rock", "🎸 Rock", "Lagu Rock"),
        MoodTag("akustik", "🌙 Akustik", "Lagu Akustik"),
        MoodTag("pesta", "🎉 Pesta", "Lagu Pesta"),
        MoodTag("fokus", "🎧 Fokus", "Lagu Fokus")
    )

    private val staticCategories = listOf(
        Category("top_hits", "Top Hits", "Lagu Trending", "whatshot", "#00E5BF", "#004D40"),
        Category("santai", "Santai & Rileks", "Lagu Santai", "nightlife", "#00BFA5", "#121519"),
        Category("olah_raga", "Olah Raga & Energi", "Lagu Workout", "bolt", "#1DE9B6", "#004D40"),
        Category("pop_vibes", "Populer Indonesia", "Lagu Pop Indonesia", "music_note", "#00B0FF", "#0D47A1"),
        Category("indie_wave", "Indie & Senja", "Lagu Indie Indonesia", "headphones", "#7C4DFF", "#1A237E"),
        Category("rock_metal", "Rock & Metal", "Lagu Rock", "electric_bolt", "#00E5BF", "#263238"),
        Category("fokus_kerja", "Fokus & Belajar", "Lagu Fokus Lofi", "headphones", "#64FFDA", "#263238"),
        Category("romantis", "Romantis & Cinta", "Lagu Romantis", "favorite", "#26A69A", "#004D40")
    )

    suspend fun search(query: String, limit: Int = 20): List<Song> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "WEB")
                    put("clientVersion", "2.20240801.01.00")
                    put("hl", "id")
                    put("gl", "ID")
                })
            })
            put("query", query)
        }

        val request = Request.Builder()
            .url("https://www.youtube.com/youtubei/v1/search")
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Search HTTP error: ${response.code}")
        }

        val responseBody = response.body?.string() ?: throw IOException("Empty search response body")
        parseSearchResults(responseBody, limit)
    }

    suspend fun getAudioStream(videoId: String): AudioStream = withContext(ioDispatcher) {
        refreshSessionDataIfNeeded()

        val payload = JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "VISIONOS")
                    put("clientVersion", "1.02")
                    put("deviceMake", "Apple")
                    put("deviceModel", "RealityDevice17,1")
                    put("userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 15_7_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15")
                    put("osName", "visionOS")
                    put("osVersion", "26.5.23O471")
                    put("hl", "en")
                    put("timeZone", "UTC")
                    put("utcOffsetMinutes", 0)
                })
            })
            put("videoId", videoId)
            put("playbackContext", JSONObject().apply {
                put("contentPlaybackContext", JSONObject().apply {
                    put("html5Preference", "HTML5_PREF_WANTS")
                    put("signatureTimestamp", cachedSts)
                })
            })
            put("contentCheckOk", true)
            put("racyCheckOk", true)
        }

        val request = Request.Builder()
            .url("https://www.youtube.com/youtubei/v1/player")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-YouTube-Client-Name", "101")
            .addHeader("X-YouTube-Client-Version", "1.02")
            .addHeader("Origin", "https://www.youtube.com")
            .addHeader("X-Goog-Visitor-Id", cachedVisitorData)
            .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 15_7_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Player API error: ${response.code}")
        }

        val responseBody = response.body?.string() ?: throw IOException("Empty player response body")
        parsePlayerResponse(videoId, responseBody)
    }

    suspend fun getHomeFeed(refresh: Boolean = false): HomeFeed = withContext(ioDispatcher) {
        if (!refresh && cachedHomeFeed != null) {
            return@withContext cachedHomeFeed!!
        }

        try {
            val nativeFeed = fetchNativeMusicFeed(randomize = refresh)
            if (nativeFeed.sections.isNotEmpty()) {
                cachedHomeFeed = nativeFeed
                return@withContext nativeFeed
            }
        } catch (_: Exception) {
        }

        val fallbackFeed = loadFallbackFeed(randomize = refresh)
        cachedHomeFeed = fallbackFeed
        fallbackFeed
    }

    private suspend fun fetchNativeMusicFeed(randomize: Boolean = false): HomeFeed = coroutineScope {
        val selectedChartId = if (randomize) {
            chartPlaylistIds.shuffled().first()
        } else {
            chartPlaylistIds.first()
        }

        val homeDeferred = async { runCatching { fetchBrowse("FEmusic_home") }.getOrNull() }
        val exploreDeferred = async { runCatching { fetchBrowse("FEmusic_explore") }.getOrNull() }
        val chartDeferred = async { runCatching { fetchBrowse(selectedChartId) }.getOrNull() }

        val homeJson = homeDeferred.await()
        val exploreJson = exploreDeferred.await()
        val chartJson = chartDeferred.await()

        val sections = mutableListOf<HomeSection>()
        var dynamicMoodTags: List<MoodTag> = emptyList()
        var dynamicCategories: List<Category> = emptyList()

        if (!chartJson.isNullOrEmpty()) {
            val parsedChart = parseBrowseResponse(chartJson, prefixId = "chart")
            sections.addAll(parsedChart.sections)
        }

        if (!homeJson.isNullOrEmpty()) {
            val parsedHome = parseBrowseResponse(homeJson, prefixId = "home")
            sections.addAll(parsedHome.sections)
        }

        if (!exploreJson.isNullOrEmpty()) {
            val parsedExplore = parseBrowseResponse(exploreJson, prefixId = "explore")
            sections.addAll(parsedExplore.sections)
            if (parsedExplore.moodTags.isNotEmpty()) {
                dynamicMoodTags = parsedExplore.moodTags
            }
            if (parsedExplore.categories.isNotEmpty()) {
                dynamicCategories = parsedExplore.categories
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

        val baseMoodTags = if (dynamicMoodTags.isNotEmpty()) dynamicMoodTags else staticMoodTags
        val baseCategories = if (dynamicCategories.isNotEmpty()) dynamicCategories else staticCategories

        HomeFeed(
            sections = finalSections,
            moodTags = if (randomize) baseMoodTags.shuffled() else baseMoodTags,
            categories = if (randomize) baseCategories.shuffled() else baseCategories
        )
    }

    private fun fetchBrowse(browseId: String): String {
        val payload = JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "WEB_REMIX")
                    put("clientVersion", "1.20240801.01.00")
                    put("hl", "id")
                    put("gl", "ID")
                })
            })
            put("browseId", browseId)
        }

        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/browse")
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .addHeader("Referer", "https://music.youtube.com/")
            .addHeader("Origin", "https://music.youtube.com")
            .post(payload.toString().toRequestBody(jsonMediaType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Browse API error: ${response.code}")
        }
        return response.body?.string() ?: throw IOException("Empty browse response")
    }

    private data class ParsedBrowse(
        val sections: List<HomeSection>,
        val moodTags: List<MoodTag>,
        val categories: List<Category>
    )

    private fun parseBrowseResponse(jsonString: String, prefixId: String): ParsedBrowse {
        val root = JSONObject(jsonString)
        val sections = mutableListOf<HomeSection>()
        val parsedMoodTags = mutableListOf<MoodTag>()
        val parsedCategories = mutableListOf<Category>()

        val contentsRoot = root.optJSONObject("contents")

        // Handle playlist/chart responses (twoColumnBrowseResultsRenderer)
        val twoCol = contentsRoot?.optJSONObject("twoColumnBrowseResultsRenderer")
        if (twoCol != null) {
            val secContents = twoCol.optJSONObject("secondaryContents")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: JSONArray()

            for (i in 0 until secContents.length()) {
                val playlistShelf = secContents.optJSONObject(i)?.optJSONObject("musicPlaylistShelfRenderer") ?: continue
                val items = playlistShelf.optJSONArray("contents") ?: continue
                val tracks = mutableListOf<Song>()

                for (j in 0 until items.length()) {
                    val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    parseSongFromResponsive(item)?.let { tracks.add(it) }
                }

                if (tracks.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "${prefixId}_$i",
                            title = "Top Charts Indonesia",
                            subtitle = "Tangga lagu resmi paling populer di Indonesia",
                            query = "Top Hits Indonesia",
                            tracks = tracks.take(20)
                        )
                    )
                }
            }
            return ParsedBrowse(sections, parsedMoodTags, parsedCategories)
        }

        // Handle standard browse pages (singleColumnBrowseResultsRenderer)
        val tabs = contentsRoot
            ?.optJSONObject("singleColumnBrowseResultsRenderer")
            ?.optJSONArray("tabs") ?: JSONArray()

        val contents = if (tabs.length() > 0) {
            tabs.optJSONObject(0)
                ?.optJSONObject("tabRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: JSONArray()
        } else {
            JSONArray()
        }

        for (i in 0 until contents.length()) {
            val sectionObj = contents.optJSONObject(i) ?: continue
            val carousel = sectionObj.optJSONObject("musicCarouselShelfRenderer") ?: continue
            val header = carousel.optJSONObject("header")?.optJSONObject("musicCarouselShelfBasicHeaderRenderer")

            val title = header?.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
            var subtitle = header?.optJSONObject("strapline")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""

            if (subtitle.isBlank()) {
                subtitle = getDefaultSubtitle(title)
            }

            val items = carousel.optJSONArray("contents") ?: continue
            val tracks = mutableListOf<Song>()

            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue

                // 1. Mood & Genre Navigation Button
                val navButton = item.optJSONObject("musicNavigationButtonRenderer")
                if (navButton != null) {
                    val btnText = navButton.optJSONObject("buttonText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
                    val stripeColor = navButton.optJSONObject("solid")?.optLong("leftStripeColor", 0L) ?: 0L
                    val colorHex = if (stripeColor != 0L) {
                        String.format(Locale.ROOT, "#%06X", 0xFFFFFF and stripeColor.toInt())
                    } else {
                        "#00E5BF"
                    }

                    if (btnText.isNotBlank()) {
                        val moodId = "mood_${btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")}"
                        if (parsedMoodTags.size < 12) {
                            parsedMoodTags.add(MoodTag(id = moodId, label = btnText, query = "Lagu $btnText"))
                        }
                        if (parsedCategories.size < 12) {
                            val icon = mapMoodToIcon(btnText)
                            parsedCategories.add(
                                Category(
                                    id = moodId,
                                    title = btnText,
                                    query = "Lagu $btnText",
                                    icon = icon,
                                    startColor = colorHex,
                                    endColor = "#1A1A1E"
                                )
                            )
                        }
                    }
                    continue
                }

                // 2. Responsive list item (Standard YouTube Music Single Song)
                val responsive = item.optJSONObject("musicResponsiveListItemRenderer")
                if (responsive != null) {
                    parseSongFromResponsive(responsive)?.let { tracks.add(it) }
                    continue
                }

                // 3. Two-row item (Music video / single track card)
                val twoRow = item.optJSONObject("musicTwoRowItemRenderer")
                if (twoRow != null) {
                    parseSongFromTwoRow(twoRow)?.let { tracks.add(it) }
                    continue
                }
            }

            if (tracks.isNotEmpty() && title.isNotBlank()) {
                sections.add(
                    HomeSection(
                        id = "${prefixId}_$i",
                        title = title,
                        subtitle = subtitle,
                        query = title,
                        tracks = tracks
                    )
                )
            }
        }

        return ParsedBrowse(sections, parsedMoodTags, parsedCategories)
    }

    private fun parseSongFromResponsive(responsive: JSONObject): Song? {
        val flexColumns = responsive.optJSONArray("flexColumns") ?: return null
        if (flexColumns.length() == 0) return null

        val col0 = flexColumns.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
        val title = col0?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
        if (title.isBlank()) return null

        var artist = "YouTube Music"
        var durationFormatted = "3:30"

        if (flexColumns.length() > 1) {
            val col1 = flexColumns.optJSONObject(1)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            val runs = col1?.optJSONObject("text")?.optJSONArray("runs")
            if (runs != null) {
                val texts = mutableListOf<String>()
                for (k in 0 until runs.length()) {
                    val t = runs.optJSONObject(k)?.optString("text", "") ?: ""
                    if (t.isNotBlank() && t != " • ") {
                        texts.add(t)
                    }
                }
                if (texts.isNotEmpty()) {
                    artist = texts[0]
                }
                for (t in texts) {
                    if (t.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
                        durationFormatted = t
                    }
                }
            }
        }

        var videoId = responsive.optJSONObject("playlistItemData")?.optString("videoId") ?: ""
        if (videoId.isBlank()) {
            videoId = responsive.optJSONObject("overlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId") ?: ""
        }
        if (videoId.isBlank()) return null

        val thumbs = responsive.optJSONObject("thumbnail")
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")

        val thumbnailUrl = if (thumbs != null && thumbs.length() > 0) {
            thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        } else {
            "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        }

        val durationSec = parseDurationToSeconds(durationFormatted)

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = durationSec,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = "https://www.youtube.com/watch?v=$videoId"
        )
    }

    private fun parseSongFromTwoRow(twoRow: JSONObject): Song? {
        val title = twoRow.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
        if (title.isBlank()) return null

        val subRuns = twoRow.optJSONObject("subtitle")?.optJSONArray("runs")
        val artist = subRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Music"

        var videoId = twoRow.optJSONObject("navigationEndpoint")
            ?.optJSONObject("watchEndpoint")
            ?.optString("videoId") ?: ""

        if (videoId.isBlank()) {
            videoId = twoRow.optJSONObject("thumbnailOverlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId") ?: ""
        }

        if (videoId.isBlank()) return null

        val thumbs = twoRow.optJSONObject("thumbnailRenderer")
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")

        val thumbnailUrl = if (thumbs != null && thumbs.length() > 0) {
            thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        } else {
            "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
        }

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = 210,
            durationFormatted = "3:30",
            thumbnailUrl = thumbnailUrl,
            webUrl = "https://www.youtube.com/watch?v=$videoId"
        )
    }

    private fun getDefaultSubtitle(title: String): String {
        val lower = title.lowercase(Locale.ROOT)
        return when {
            lower.contains("pilihan") || lower.contains("quick") -> "Lagu rekomendasi terbaik hari ini"
            lower.contains("trending") -> "Lagu paling populer dan viral saat ini"
            lower.contains("video") -> "Video musik yang baru saja diluncurkan"
            lower.contains("album") || lower.contains("single") -> "Rilisan musik terbaru musisi favorit"
            else -> "Koleksi hits musik pilihan"
        }
    }

    private fun mapMoodToIcon(title: String): String {
        val lower = title.lowercase(Locale.ROOT)
        return when {
            lower.contains("olah raga") || lower.contains("workout") -> "bolt"
            lower.contains("santai") || lower.contains("chill") || lower.contains("lofi") -> "nightlife"
            lower.contains("pesta") || lower.contains("party") || lower.contains("dance") -> "nightlife"
            lower.contains("fokus") || lower.contains("belajar") -> "headphones"
            lower.contains("romantis") || lower.contains("cinta") -> "music_note"
            lower.contains("rock") || lower.contains("metal") -> "electric_bolt"
            lower.contains("jazz") || lower.contains("blues") -> "graphic_eq"
            lower.contains("senang") || lower.contains("happy") -> "whatshot"
            else -> "music_note"
        }
    }

    private suspend fun loadFallbackFeed(randomize: Boolean = false): HomeFeed = coroutineScope {
        val allQueries = listOf(
            Triple("Trending Now", "Lagu paling populer dan viral saat ini", "top hits indonesia official video singles"),
            Triple("Rilisan Terbaru", "Karya musik terbaru dari para musisi", "latest official music video singles"),
            Triple("Chill & Akustik", "Teman santai dan rileks hari ini", "acoustic chill pop singles"),
            Triple("Indie & Senja", "Melodi tenang pengantar hari", "lagu indie indonesia hits"),
            Triple("Populer Tanah Air", "Deretan hits terbaik anak bangsa", "populer tanah air musik indonesia"),
            Triple("Workout & EDM", "Dentuman nada pemacu semangat", "workout edm upbeat tracks"),
            Triple("Nostalgia Hits", "Koleksi tembang kenangan terfavorit", "lagu pop indonesia 2000an hits"),
            Triple("Fokus & Belajar", "Irama santai pengiring konsentrasi", "lofi chill study beats instrumental")
        )

        val selectedQueries = if (randomize) {
            allQueries.shuffled().take(4)
        } else {
            allQueries.take(3)
        }

        val sections = selectedQueries.map { (title, subtitle, q) ->
            async {
                val songs = try {
                    val raw = search(q, limit = 10)
                    if (randomize) raw.shuffled() else raw
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
            moodTags = if (randomize) staticMoodTags.shuffled() else staticMoodTags,
            categories = if (randomize) staticCategories.shuffled() else staticCategories
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
                moodTags = staticMoodTags,
                categories = staticCategories
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

    private fun parseSearchResults(jsonString: String, limit: Int): List<Song> {
        val root = JSONObject(jsonString)
        val contents = root.optJSONObject("contents")
            ?.optJSONObject("twoColumnSearchResultsRenderer")
            ?.optJSONObject("primaryContents")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents") ?: JSONArray()

        val songs = mutableListOf<Song>()

        for (i in 0 until contents.length()) {
            val section = contents.optJSONObject(i) ?: continue
            val itemSection = section.optJSONObject("itemSectionRenderer") ?: continue
            val items = itemSection.optJSONArray("contents") ?: continue

            for (j in 0 until items.length()) {
                if (songs.size >= limit) break
                val item = items.optJSONObject(j) ?: continue
                val vr = item.optJSONObject("videoRenderer") ?: continue

                val videoId = vr.optString("videoId")
                if (videoId.isEmpty()) continue

                val titleRuns = vr.optJSONObject("title")?.optJSONArray("runs")
                val title = titleRuns?.optJSONObject(0)?.optString("text") ?: ""
                if (title.isEmpty()) continue

                val ownerRuns = vr.optJSONObject("ownerText")?.optJSONArray("runs")
                    ?: vr.optJSONObject("longBylineText")?.optJSONArray("runs")
                val artist = ownerRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Music"

                val durationSimple = vr.optJSONObject("lengthText")?.optString("simpleText")
                    ?: vr.optJSONObject("lengthText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                    ?: ""
                val durationFormatted = if (durationSimple.isNotEmpty()) durationSimple.replace('.', ':') else "3:30"
                val durationSec = parseDurationToSeconds(durationFormatted)

                // Filter out compilation/playlist videos (> 10 minutes or > 6 minutes with compilation keywords)
                val isLikelyCompilation = durationSec > 600 || (durationSec > 360 && (
                    title.contains("playlist", ignoreCase = true) ||
                    title.contains("full album", ignoreCase = true) ||
                    title.contains("kompilasi", ignoreCase = true) ||
                    title.contains("kumpulan lagu", ignoreCase = true) ||
                    title.contains("top hits spotify", ignoreCase = true) ||
                    title.contains("nonstop", ignoreCase = true)
                ))

                if (isLikelyCompilation) continue

                val thumbnails = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                val thumbnailUrl = if (thumbnails != null && thumbnails.length() > 0) {
                    thumbnails.optJSONObject(thumbnails.length() - 1)?.optString("url")
                        ?: "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                } else {
                    "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"
                }

                songs.add(
                    Song(
                        id = videoId,
                        title = title,
                        artist = artist,
                        durationSeconds = durationSec,
                        durationFormatted = durationFormatted,
                        thumbnailUrl = thumbnailUrl,
                        webUrl = "https://www.youtube.com/watch?v=$videoId"
                    )
                )
            }
        }

        return songs
    }

    private fun parsePlayerResponse(videoId: String, jsonString: String): AudioStream {
        val root = JSONObject(jsonString)
        val playabilityStatus = root.optJSONObject("playabilityStatus")
        val status = playabilityStatus?.optString("status") ?: ""
        if (status != "OK") {
            val reason = playabilityStatus?.optString("reason") ?: "Stream unavailable ($status)"
            throw IllegalStateException(reason)
        }

        val streamingData = root.optJSONObject("streamingData") ?: throw IllegalStateException("No streamingData found")
        val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats") ?: JSONArray()

        var bestAudioUrl: String? = null
        var bestBitrate: Double? = null
        var bestFormat: String = "m4a"

        for (i in 0 until adaptiveFormats.length()) {
            val formatObj = adaptiveFormats.getJSONObject(i)
            val mimeType = formatObj.optString("mimeType", "")
            if (!mimeType.contains("audio")) continue

            val url = formatObj.optString("url")
            if (url.isNullOrEmpty()) continue

            val itag = formatObj.optInt("itag", 0)
            val bitrate = formatObj.optDouble("bitrate", 0.0)

            if (itag == 140) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = "m4a"
                break
            } else if (bestAudioUrl == null || bitrate > (bestBitrate ?: 0.0)) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = if (mimeType.contains("mp4")) "m4a" else "webm"
            }
        }

        val finalUrl = bestAudioUrl ?: throw IllegalStateException("No playable direct audio stream found")

        val videoDetails = root.optJSONObject("videoDetails")
        val title = videoDetails?.optString("title") ?: "Unknown Title"
        val author = videoDetails?.optString("author") ?: "YouTube Music"
        val lengthSeconds = videoDetails?.optString("lengthSeconds")?.toIntOrNull() ?: 0
        val thumbUrl = "https://i.ytimg.com/vi/$videoId/hqdefault.jpg"

        return AudioStream(
            id = videoId,
            title = title,
            artist = author,
            streamUrl = finalUrl,
            durationSeconds = lengthSeconds,
            durationFormatted = formatSeconds(lengthSeconds),
            thumbnailUrl = thumbUrl,
            format = bestFormat,
            bitrate = bestBitrate
        )
    }

    private fun refreshSessionDataIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastSessionFetchTime < 3600000L) return
        if (!isFetchingSession.compareAndSet(false, true)) return

        try {
            val request = Request.Builder()
                .url("https://www.youtube.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string() ?: ""
                val visitorMatcher = Pattern.compile("\"VISITOR_DATA\":\"([^\"]+)\"").matcher(html)
                if (visitorMatcher.find()) {
                    cachedVisitorData = visitorMatcher.group(1) ?: cachedVisitorData
                }

                val stsMatcher = Pattern.compile("\"STS\":([0-9]+)").matcher(html)
                if (stsMatcher.find()) {
                    val foundSts = stsMatcher.group(1)?.toIntOrNull()
                    if (foundSts != null && foundSts > 0) {
                        cachedSts = foundSts
                    }
                }
                lastSessionFetchTime = now
            }
        } catch (_: Exception) {
        } finally {
            isFetchingSession.set(false)
        }
    }

    private fun parseDurationToSeconds(formatted: String): Int {
        val parts = formatted.split(":").mapNotNull { it.trim().toIntOrNull() }
        return when (parts.size) {
            1 -> parts[0]
            2 -> parts[0] * 60 + parts[1]
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
            else -> 210
        }
    }

    private fun formatSeconds(totalSec: Int): String {
        if (totalSec <= 0) return "0:00"
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}
