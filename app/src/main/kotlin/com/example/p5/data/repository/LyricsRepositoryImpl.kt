package com.example.p5.data.repository

import com.example.p5.domain.model.LyricLine
import com.example.p5.domain.model.LyricsData
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.LyricsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class LyricsRepositoryImpl(
    private val okHttpClient: OkHttpClient
) : LyricsRepository {

    private val cache = ConcurrentHashMap<String, LyricsData>()
    private val lrcLinePattern = Pattern.compile("^\\[(\\d{1,2}):(\\d{1,2}(?:\\.\\d{1,3})?)\\](.*)$")

    override suspend fun getLyrics(song: Song): Result<LyricsData> = withContext(Dispatchers.IO) {
        runCatching {
            cache[song.id]?.let { return@runCatching it }

            val cleaned = parseCleanArtistAndTitle(song.artist, song.title)
            val trackName = cleaned.first
            val artistName = cleaned.second

            // Try exact match first
            var lyricsData = fetchExactLyrics(song.id, trackName, artistName, song.durationSeconds)

            // If not found, try search fallback
            if (lyricsData == null) {
                val searchQuery = "$artistName $trackName".trim()
                lyricsData = fetchSearchLyrics(song.id, searchQuery)
            }

            lyricsData?.also {
                cache[song.id] = it
            } ?: throw IllegalStateException("Lirik tidak ditemukan untuk lagu ini")
        }
    }

    private fun fetchExactLyrics(
        songId: String,
        trackName: String,
        artistName: String,
        durationSeconds: Int
    ): LyricsData? {
        val urlBuilder = "https://lrclib.net/api/get".toHttpUrlOrNull()?.newBuilder() ?: return null
        urlBuilder.addQueryParameter("track_name", trackName)
        if (artistName.isNotBlank() && !artistName.equals("YouTube Music", ignoreCase = true)) {
            urlBuilder.addQueryParameter("artist_name", artistName)
        }
        if (durationSeconds > 0) {
            urlBuilder.addQueryParameter("duration", durationSeconds.toString())
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .header("User-Agent", "MusicStream-Android/1.0")
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            parseLyricsJson(songId, JSONObject(body))
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchSearchLyrics(songId: String, query: String): LyricsData? {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "https://lrclib.net/api/search?q=$encodedQuery"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "MusicStream-Android/1.0")
            .build()

        return try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val array = JSONArray(body)
            if (array.length() == 0) return null

            // Pick first item that has synced or plain lyrics
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val parsed = parseLyricsJson(songId, item)
                if (parsed != null) return parsed
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseLyricsJson(songId: String, json: JSONObject): LyricsData? {
        val instrumental = json.optBoolean("instrumental", false)
        val synced = json.optString("syncedLyrics", "").trim()
        val plain = json.optString("plainLyrics", "").trim()

        if (instrumental) {
            return LyricsData(
                trackId = songId,
                isSynced = false,
                plainLyrics = "Musik Instrumental",
                isInstrumental = true
            )
        }

        if (synced.isNotEmpty()) {
            val lines = parseLrc(synced)
            if (lines.isNotEmpty()) {
                return LyricsData(
                    trackId = songId,
                    isSynced = true,
                    lines = lines,
                    plainLyrics = plain,
                    isInstrumental = false
                )
            }
        }

        if (plain.isNotEmpty()) {
            return LyricsData(
                trackId = songId,
                isSynced = false,
                lines = emptyList(),
                plainLyrics = plain,
                isInstrumental = false
            )
        }

        return null
    }

    private fun parseLrc(lrcText: String): List<LyricLine> {
        val lines = mutableListOf<LyricLine>()
        val rawLines = lrcText.split("\n")

        for (raw in rawLines) {
            val trimmed = raw.trim()
            if (trimmed.isEmpty()) continue

            val matcher = lrcLinePattern.matcher(trimmed)
            if (matcher.matches()) {
                val minStr = matcher.group(1) ?: "0"
                val secStr = matcher.group(2) ?: "0"
                val text = matcher.group(3)?.trim().orEmpty()

                val min = minStr.toLongOrNull() ?: 0L
                val sec = secStr.toDoubleOrNull() ?: 0.0
                val timestampMs = min * 60 * 1000L + (sec * 1000L).toLong()

                if (text.isNotBlank()) {
                    lines.add(LyricLine(timestampMs, text))
                }
            }
        }
        return lines.sortedBy { it.timestampMs }
    }

    private fun parseCleanArtistAndTitle(rawArtist: String, rawTitle: String): Pair<String, String> {
        // If title contains " - ", often it's "Artist - Title"
        var artist = rawArtist
        var title = rawTitle

        if (rawTitle.contains(" - ")) {
            val parts = rawTitle.split(" - ", limit = 2)
            if (parts.size == 2) {
                artist = parts[0].trim()
                title = parts[1].trim()
            }
        }

        // Clean common noise in titles like (Official Music Video), [MV], (Lyrics), etc.
        title = title
            .replace(Regex("\\[.*?\\]|\\(.*?\\)"), "")
            .replace(Regex("(?i)official\\s*(video|audio|music video|lyric video)?"), "")
            .replace(Regex("(?i)ft\\.?|feat\\.?", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)lyrics?"), "")
            .replace(Regex("(?i)audio"), "")
            .replace(Regex("(?i)visualizer"), "")
            .trim()

        artist = artist
            .replace(Regex("(?i) - Topic"), "")
            .replace(Regex("(?i)VEVO"), "")
            .trim()

        return Pair(title, artist)
    }
}

