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

    private val sectionVariations = mapOf(
        "trending" to listOf(
            SectionQuery("Trending Now", "Lagu paling populer dan viral saat ini", "trending music hits 2026"),
            SectionQuery("Global Viral Hits", "Hits yang sedang meledak di seluruh dunia", "viral songs billboard global hits 2026"),
            SectionQuery("Top Streaming Hits", "Lagu paling banyak didengarkan minggu ini", "top music official video trending 2026"),
            SectionQuery("TikTok Viral Sounds", "Sound dan lagu terpopuler di media sosial", "trending tiktok songs viral music 2026")
        ),
        "indonesia_top" to listOf(
            SectionQuery("Top Hits Indonesia", "Musik terfavorit dan viral di Indonesia", "top hits indonesia lagu viral 2026"),
            SectionQuery("Populer Tanah Air", "Koleksi hits pop terbaik musisi Indonesia", "lagu pop indonesia terbaru hits viral"),
            SectionQuery("Indie Senja Indonesia", "Lagu indie lokal yang syahdu dan bermakna", "indie pop indonesia hits playlist"),
            SectionQuery("Akustik Hits Indonesia", "Cover akustik lagu Indonesia terfavorit", "akustik lagu indonesia hits santai")
        ),
        "new_releases" to listOf(
            SectionQuery("Rilisan Terbaru", "Karya musik terbaru dari para musisi", "official music video 2026 new release"),
            SectionQuery("Fresh Friday Music", "Lagu-lagu yang baru saja diluncurkan", "latest official music video singles 2026"),
            SectionQuery("Lagu Baru Indonesia", "Single musik terbaru karya anak bangsa", "lagu baru indonesia official music video 2026"),
            SectionQuery("International New Beats", "Rilisan terbaru musisi mancanegara", "new international official music video 2026")
        ),
        "chill_acoustic" to listOf(
            SectionQuery("Chill & Akustik", "Teman santai dan rileks hari ini", "acoustic chill pop acoustic cover"),
            SectionQuery("Coffee & Lofi", "Irama santai untuk menemani harimu", "lofi chill beats relaxing music study"),
            SectionQuery("Late Night Vibes", "Lagu lembut pengantar istirahat malam", "late night acoustic chill songs peaceful"),
            SectionQuery("Senja & Santai", "Musik syahdu pengiring waktu luang", "lagu santai sore cafe acoustic vibe")
        ),
        "workout_party" to listOf(
            SectionQuery("Energy & Workout", "Semangat dengan beat cepat dan bertenaga", "edm workout motivation party hits"),
            SectionQuery("EDM Party Hits", "Dentuman bas menghentak untuk pesta", "edm dance festival party hits 2026"),
            SectionQuery("Gym Pump & Focus", "Bakar semangat latihanmu sekarang", "gym workout motivation music pump 2026"),
            SectionQuery("Upbeat Dance Pop", "Lagu pop energik yang bikin semangat", "high energy upbeat pop dance music hits")
        )
    )

    private val staticMoodTags = listOf(
        MoodTag("top_hits", "🔥 Top Hits", "Trending Music 2026"),
        MoodTag("chill_lofi", "☕ Chill Lofi", "Lofi Chill Beats"),
        MoodTag("workout", "⚡ Workout", "Workout Motivation Music"),
        MoodTag("rock", "🎸 Rock Hits", "Rock Classics Hits"),
        MoodTag("acoustic", "🌙 Acoustic", "Acoustic Pop Songs"),
        MoodTag("jazz", "🎷 Smooth Jazz", "Smooth Jazz Instrumental"),
        MoodTag("edm", "🎧 EDM Beats", "EDM Dance Hits")
    )

    private val staticCategories = listOf(
        Category("top_hits", "Top Hits", "Top Hits 2026", "fire", "#FF6D00", "#D84315"),
        Category("rock_metal", "Rock & Metal", "Rock Classics Hits", "electric_bolt", "#E65100", "#263238"),
        Category("lofi_chill", "Lofi & Chill", "Lofi Chill Beats", "nightlife", "#FF8F00", "#3E2723"),
        Category("pop_vibes", "Pop Vibes", "Pop Music Hits", "music_note", "#FFA000", "#E65100"),
        Category("indie_wave", "Indie Wave", "Indie Pop Hits", "headphones", "#BF360C", "#1A237E"),
        Category("workout_edm", "Workout & EDM", "EDM Workout Music", "bolt", "#FF5722", "#4A148C"),
        Category("acoustic_cafe", "Acoustic Cafe", "Acoustic Pop Songs", "library_music", "#D84315", "#3E2723"),
        Category("jazz_blues", "Jazz & Blues", "Smooth Jazz Instrumental", "graphic_eq", "#FF6D00", "#0D47A1")
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

        val sections = coroutineScope {
            sectionVariations.map { (key, variations) ->
                async {
                    val selected = variations.random()
                    val songs = try {
                        search(selected.query, limit = 8)
                    } catch (e: Exception) {
                        emptyList()
                    }
                    HomeSection(
                        id = key,
                        title = selected.title,
                        subtitle = selected.subtitle,
                        query = selected.query,
                        tracks = songs
                    )
                }
            }.map { it.await() }.filter { it.tracks.isNotEmpty() }
        }

        val feed = HomeFeed(
            sections = sections,
            moodTags = staticMoodTags,
            categories = staticCategories
        )
        cachedHomeFeed = feed
        feed
    }

    fun getCategories(): HomeCategories {
        return HomeCategories(
            moodTags = staticMoodTags,
            categories = staticCategories
        )
    }

    suspend fun getTrending(limit: Int = 15): List<Song> {
        return search("top hits indonesia trending 2026", limit)
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

    private data class SectionQuery(
        val title: String,
        val subtitle: String,
        val query: String
    )
}
