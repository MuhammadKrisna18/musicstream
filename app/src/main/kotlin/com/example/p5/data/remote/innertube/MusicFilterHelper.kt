package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.Song
import java.util.Locale

object MusicFilterHelper {

    private val podcastKeywords = listOf(
        "podcast", "podkes", "talkshow", "talk show", "interview", "wawancara",
        "ngobrol", "curhat", "eps.", "episode", "part 1", "part 2", "part 3",
        "stand up comedy", "cerita misteri", "kajian", "monolog", "bedah buku"
    )

    private val compilationKeywords = listOf(
        "full album", "kompilasi", "top 100", "top 50", "kumpulan lagu",
        "nonstop", "non stop", "1 jam", "1 hour", "2 jam", "2 hours",
        "billboard hot 100 top music video", "dj remix full", "lagu nonstop"
    )

    private val noiseRegex = Regex(
        "(?i)\\[.*?(official|video|lirik|lyric|audio|clip|hd|hq|mv|remix).*?\\]|\\(.*?((official|video|lirik|lyric|audio|clip|hd|hq|mv).*?)\\)|\\|.*$|•.*$"
    )

    fun isValidMusicTrack(song: Song): Boolean {
        if (song.durationSeconds > 720 || (song.durationSeconds in 1..44)) {
            return false
        }
        val lowerTitle = song.title.lowercase(Locale.ROOT)
        val lowerArtist = song.artist.lowercase(Locale.ROOT)
        if (podcastKeywords.any { lowerTitle.contains(it) || lowerArtist.contains(it) }) {
            return false
        }
        if (compilationKeywords.any { lowerTitle.contains(it) }) {
            return false
        }
        return true
    }

    fun isExcludedContent(title: String, artist: String = ""): Boolean {
        val lowerTitle = title.lowercase(Locale.ROOT)
        val lowerArtist = artist.lowercase(Locale.ROOT)
        return podcastKeywords.any { lowerTitle.contains(it) || lowerArtist.contains(it) } ||
            compilationKeywords.any { lowerTitle.contains(it) }
    }

    fun normalizeTitle(title: String, artist: String = ""): String {
        var clean = title.replace(noiseRegex, "")
        if (artist.isNotBlank() && artist.length >= 2) {
            val escapedArtist = Regex.escape(artist.trim())
            clean = clean.replace(Regex("(?i)^\\s*$escapedArtist\\s*[-–:]\\s*"), "")
                .replace(Regex("(?i)\\s*[-–:]\\s*$escapedArtist.*$"), "")
        }
        clean = clean.replace(Regex("(?i)\\s*(\\((feat|ft)\\.?.*?\\)|\\[(feat|ft)\\.?.*?\\]|\\b(feat|ft)\\.?\\s+.*$)"), "")
            .replace(Regex("(?i)\\b(official\\s*(music\\s*)?video|official\\s*(lyric\\s*)?video|lirik\\s*(lagu|video)?|video\\s*clip)\\b"), "")
            .replace(Regex("[^a-zA-Z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .lowercase(Locale.ROOT)
        return clean
    }

    fun deduplicateSongs(songs: List<Song>): List<Song> {
        val seenKeys = mutableSetOf<String>()
        val result = mutableListOf<Song>()
        for (song in songs) {
            val cleanTitle = normalizeTitle(song.title, song.artist)
            val cleanKey = if (cleanTitle.length >= 3) {
                "${cleanTitle}_${song.artist.lowercase(Locale.ROOT).trim()}"
            } else {
                song.id
            }
            if (seenKeys.add(cleanKey)) {
                result.add(song)
            }
        }
        return result
    }
}
