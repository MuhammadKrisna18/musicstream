package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.Song
import java.util.Locale

object MusicFilterHelper {

    private val podcastKeywords = listOf(
        "podcast", "podkes", "talkshow", "talk show", "interview", "wawancara",
        "ngobrol", "curhat", "monolog", "stand up comedy", "open mic",
        "cerita misteri", "misteri", "horor", "kisah misteri",
        "kajian", "ceramah", "khotbah", "dakwah", "pengajian", "ustadz", "habib",
        "bedah buku", "tutorial", "unboxing", "reaction", "review", "gameplay",
        "eps.", "episode", "part 1", "part 2", "part 3", "season 1", "season 2",
        "audiobook", "audio book", "dongeng", "alur cerita"
    )

    private val compilationKeywords = listOf(
        "full album", "fullalbum", "album full", "kumpulan lagu", "daftar lagu",
        "kompilasi", "compilation", "nonstop", "non stop", "non-stop",
        "top 100", "top 50", "top 40", "top 30", "top 20", "top 10",
        "hot 100", "billboard hot", "billboard",
        "1 jam", "2 jam", "3 jam", "1 hour", "2 hour", "3 hour", "10 hour", "60 menit",
        "dj remix full", "remix full", "jedag jedug full", "full bass", "megamix", "mixtape",
        "best songs of", "greatest hits of", "best of ", "all songs",
        "tracklist", "audio album", "ost full"
    )

    private val compilationArtistKeywords = listOf(
        "playlist", "kompilasi", "kumpulan", "full album", "charts", "records compilation"
    )

    private val noiseRegex = Regex(
        "(?i)\\[.*?(official|video|lirik|lyric|audio|clip|hd|hq|mv|remix).*?\\]|\\(.*?((official|video|lirik|lyric|audio|clip|hd|hq|mv).*?)\\)|\\|.*$|•.*$"
    )

    fun isValidMusicTrack(song: Song): Boolean {
        if (song.durationSeconds > 540 || (song.durationSeconds in 1..44)) {
            return false
        }

        if (song.durationFormatted.isNotBlank()) {
            val parts = song.durationFormatted.split(":").mapNotNull { it.trim().toIntOrNull() }
            if (parts.size >= 3) {
                return false
            } else if (parts.size == 2) {
                val totalSec = parts[0] * 60 + parts[1]
                if (totalSec > 540 || (totalSec in 1..44)) {
                    return false
                }
            }
        }

        val lowerTitle = song.title.lowercase(Locale.ROOT)
        val lowerArtist = song.artist.lowercase(Locale.ROOT)

        if (podcastKeywords.any { lowerTitle.contains(it) || lowerArtist.contains(it) }) {
            return false
        }

        if (compilationKeywords.any { lowerTitle.contains(it) }) {
            return false
        }

        if (lowerTitle.contains("playlist")) {
            return false
        }

        if (compilationArtistKeywords.any { lowerArtist.contains(it) }) {
            return false
        }

        return true
    }

    fun isExcludedPlaylist(title: String, author: String = ""): Boolean {
        val lowerTitle = title.lowercase(Locale.ROOT)
        val lowerAuthor = author.lowercase(Locale.ROOT)
        return podcastKeywords.any { lowerTitle.contains(it) || lowerAuthor.contains(it) }
    }

    fun isExcludedContent(title: String, artist: String = ""): Boolean {
        val lowerTitle = title.lowercase(Locale.ROOT)
        val lowerArtist = artist.lowercase(Locale.ROOT)
        return podcastKeywords.any { lowerTitle.contains(it) || lowerArtist.contains(it) } ||
            compilationKeywords.any { lowerTitle.contains(it) } ||
            compilationArtistKeywords.any { lowerArtist.contains(it) }
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
