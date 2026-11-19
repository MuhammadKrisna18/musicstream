package com.example.p5.domain.model

enum class AppThemeId(
    val title: String,
    val category: ThemeCategory,
    val isDark: Boolean
) {
    DEFAULT_DARK(
        title = "Default Dark",
        category = ThemeCategory.DEFAULT,
        isDark = true
    ),
    DEFAULT_LIGHT(
        title = "Default Light",
        category = ThemeCategory.DEFAULT,
        isDark = false
    ),
    ANIME_CYBERPUNK(
        title = "Cyberpunk Lucy",
        category = ThemeCategory.ANIME,
        isDark = true
    ),
    ANIME_INFINITY_BLUE(
        title = "Infinity Gojo",
        category = ThemeCategory.ANIME,
        isDark = true
    ),
    ANIME_CRIMSON_FLAME(
        title = "Crimson Flame",
        category = ThemeCategory.ANIME,
        isDark = true
    ),
    ANIME_SAKURA_TWILIGHT(
        title = "Sakura Frieren",
        category = ThemeCategory.ANIME,
        isDark = true
    ),
    ANIME_EVA_UNIT01(
        title = "EVA Unit-01",
        category = ThemeCategory.ANIME,
        isDark = true
    ),
    MINIMAL_OLED(
        title = "OLED Pitch Black",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_NORD(
        title = "Nordic Frost",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_TOKYO(
        title = "Tokyo Night",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_DRACULA(
        title = "Dracula",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_EMERALD(
        title = "Emerald Pine",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_MOCHA(
        title = "Warm Mocha",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_ROSE(
        title = "Rose Pine",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_PAPER(
        title = "Paper Light",
        category = ThemeCategory.MINIMALIST,
        isDark = false
    ),
    MINIMAL_TITANIUM(
        title = "Titanium Slate",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_MATCHA(
        title = "Matcha Cream",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_OBSIDIAN(
        title = "Obsidian Gold",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_GLACIER(
        title = "Glacier Blue",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_LAVENDER(
        title = "Muted Lavender",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_CARBON(
        title = "Carbon Matrix",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_ESPRESSO(
        title = "Dark Espresso",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_SAGE(
        title = "Botanical Sage",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_MIDNIGHT_NOIR(
        title = "Midnight Noir",
        category = ThemeCategory.MINIMALIST,
        isDark = true
    ),
    MINIMAL_ALABASTER(
        title = "Alabaster Light",
        category = ThemeCategory.MINIMALIST,
        isDark = false
    )
}
