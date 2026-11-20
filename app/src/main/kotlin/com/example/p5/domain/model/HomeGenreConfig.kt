package com.example.p5.domain.model

data class HomeGenre(
    val id: String,
    val title: String,
    val subtitle: String,
    val query: String,
    val startColor: String,
    val endColor: String,
    val icon: String = "music_note"
) {
    fun toCategory(): Category {
        return Category(
            id = id,
            title = title,
            query = query,
            icon = icon,
            startColor = startColor,
            endColor = endColor
        )
    }
}

object HomeGenreConfig {
    val POP = HomeGenre(
        id = "pop",
        title = "Pop",
        subtitle = "Hits lagu pop terbaik dan terpopuler",
        query = "top pop indonesia official music video singles",
        startColor = "#8B5CF6",
        endColor = "#6D28D9"
    )

    val INDIE_POP = HomeGenre(
        id = "indie_pop",
        title = "Indie Pop",
        subtitle = "Melodi indie senja dan rilisan syahdu",
        query = "indie pop indonesia official music video",
        startColor = "#A855F7",
        endColor = "#7C3AED"
    )

    val KOPLO = HomeGenre(
        id = "koplo",
        title = "Koplo",
        subtitle = "Irama kendang koplo kekinian terpopuler",
        query = "koplo terbaru official music video",
        startColor = "#EC4899",
        endColor = "#BE185D"
    )

    val DANGDUT = HomeGenre(
        id = "dangdut",
        title = "Dangdut",
        subtitle = "Dangdut hits pilihan dan lagu viral",
        query = "dangdut terbaru official music video",
        startColor = "#F59E0B",
        endColor = "#B45309"
    )

    val RNB = HomeGenre(
        id = "rnb",
        title = "R&B",
        subtitle = "Groove R&B dan alunan soul berkelas",
        query = "r&b soul indonesia official singles",
        startColor = "#3B82F6",
        endColor = "#1D4ED8"
    )

    val JAZZ = HomeGenre(
        id = "jazz",
        title = "Jazz",
        subtitle = "Alunan jazz santai dan harmoni indah",
        query = "jazz indonesia official music video singles",
        startColor = "#10B981",
        endColor = "#047857"
    )

    val ACOUSTIC = HomeGenre(
        id = "acoustic",
        title = "Acoustic",
        subtitle = "Petikan gitar akustik hangat dan menenangkan",
        query = "akustik pop indonesia official music video",
        startColor = "#F97316",
        endColor = "#C2410C"
    )

    val KERONCONG = HomeGenre(
        id = "keroncong",
        title = "Keroncong",
        subtitle = "Harmoni langgam keroncong modern dan klasik",
        query = "keroncong modern official music video",
        startColor = "#14B8A6",
        endColor = "#0F766E"
    )

    val ALL: List<HomeGenre> = listOf(
        POP,
        INDIE_POP,
        KOPLO,
        DANGDUT,
        RNB,
        JAZZ,
        ACOUSTIC,
        KERONCONG
    )

    val CATEGORIES: List<Category> = ALL.map { it.toCategory() }

    fun isAllowedGenre(genreNameOrId: String): Boolean {
        val trimmed = genreNameOrId.trim()
        return ALL.any {
            it.id.equals(trimmed, ignoreCase = true) ||
                it.title.equals(trimmed, ignoreCase = true)
        }
    }

    fun findById(id: String): HomeGenre? {
        return ALL.firstOrNull { it.id.equals(id.trim(), ignoreCase = true) }
    }

    fun findByTitle(title: String): HomeGenre? {
        return ALL.firstOrNull { it.title.equals(title.trim(), ignoreCase = true) }
    }
}
