package com.example.p5.data.remote.innertube

import java.util.Locale

object InnerTubeFormatter {

    fun parseDurationToSeconds(formatted: String): Int {
        if (formatted.isBlank()) return 0
        val parts = formatted.split(":").mapNotNull { it.trim().toIntOrNull() }
        return when (parts.size) {
            1 -> parts[0]
            2 -> parts[0] * 60 + parts[1]
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
            else -> 0
        }
    }

    fun parseViewCount(rawText: String): Long {
        if (rawText.isBlank()) return 0L
        val clean = rawText.lowercase(Locale.ROOT).trim()

        val multiplier = when {
            clean.contains("miliar") || clean.contains("milyar") || clean.contains("billion") || Regex("""\d\s*b\b""").containsMatchIn(clean) -> 1_000_000_000.0
            clean.contains("jt") || clean.contains("juta") || clean.contains("million") || Regex("""\d\s*m\b""").containsMatchIn(clean) -> 1_000_000.0
            clean.contains("rb") || clean.contains("ribu") || clean.contains("thousand") || Regex("""\d\s*k\b""").containsMatchIn(clean) -> 1_000.0
            else -> 1.0
        }

        return if (multiplier > 1.0) {
            val numberPattern = Regex("""[\d]+(?:[.,]\d+)?""")
            val match = numberPattern.find(clean)?.value ?: return 0L
            val normalizedNumber = match.replace(',', '.')
            val doubleVal = normalizedNumber.toDoubleOrNull() ?: 0.0
            (doubleVal * multiplier).toLong()
        } else {
            val digitsOnly = clean.filter { it.isDigit() }
            digitsOnly.toLongOrNull() ?: 0L
        }
    }

    fun formatSeconds(totalSec: Int): String {
        if (totalSec <= 0) return "0:00"
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }

    fun mapMoodToIcon(title: String): String {
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

    fun getDefaultSubtitle(title: String): String {
        val lower = title.lowercase(Locale.ROOT)
        return when {
            lower.contains("lagu cepat") || lower.contains("quick") -> "Pilihan lagu singkat untuk memulai harimu"
            lower.contains("charts") || lower.contains("tangga") -> "Tangga musik teratas dan paling digemari"
            lower.contains("trending") -> "Lagu paling populer dan viral saat ini"
            lower.contains("video") -> "Video musik yang baru saja diluncurkan"
            lower.contains("album") || lower.contains("single") -> "Rilisan musik terbaru musisi favorit"
            else -> "Koleksi hits musik pilihan"
        }
    }

    fun isExcludedMoodOrActivity(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        val excludedKeywords = listOf(
            "semangat", "santai", "fokus", "olah raga", "olahraga", "workout", "pesta", "party",
            "romantis", "tidur", "sleep", "gaming", "game", "bepergian", "commute", "travel",
            "merasa senang", "feel good", "sedih", "sad", "chill", "lofi", "energi", "energy",
            "belajar", "study", "makan malam", "dinner", "keluarga", "family", "liburan", "holiday",
            "relaks", "relax", "meditasi", "meditation", "motivasi", "motivation", "pagi", "malam"
        )
        return excludedKeywords.any { lower.contains(it) }
    }

    fun isExcludedCompilation(title: String): Boolean {
        val lower = title.lowercase(Locale.ROOT)
        return lower.contains("full album") ||
            lower.contains("kompilasi") ||
            lower.contains("1 jam") ||
            lower.contains("kumpulan lagu") ||
            lower.contains("nonstop") ||
            lower.contains("top hits spotify")
    }
}
