package com.example.p5.ui.theme

import com.example.p5.domain.model.AppThemeId

object AppThemePalette {

    val defaultDark: AppColorScheme
        get() = DefaultThemePalettes.defaultDark

    val defaultLight: AppColorScheme
        get() = DefaultThemePalettes.defaultLight

    fun getPalette(id: AppThemeId): AppColorScheme {
        return when (id) {
            AppThemeId.DEFAULT_DARK -> DefaultThemePalettes.defaultDark
            AppThemeId.DEFAULT_LIGHT -> DefaultThemePalettes.defaultLight
            AppThemeId.ANIME_CYBERPUNK -> AnimeThemePalettes.animeCyberpunk
            AppThemeId.ANIME_INFINITY_BLUE -> AnimeThemePalettes.animeInfinityBlue
            AppThemeId.ANIME_CRIMSON_FLAME -> AnimeThemePalettes.animeCrimsonFlame
            AppThemeId.ANIME_SAKURA_TWILIGHT -> AnimeThemePalettes.animeSakuraTwilight
            AppThemeId.ANIME_EVA_UNIT01 -> AnimeThemePalettes.animeEvaUnit01
            AppThemeId.ANIME_SHADOW_MONARCH -> AnimeThemePalettes.animeShadowMonarch
            AppThemeId.ANIME_SUN_BREATH -> AnimeThemePalettes.animeSunBreath
            AppThemeId.ANIME_CURSE_KING -> AnimeThemePalettes.animeCurseKing
            AppThemeId.ANIME_FREEDOM_WINGS -> AnimeThemePalettes.animeFreedomWings
            AppThemeId.ANIME_MOONLESS_SKY -> AnimeThemePalettes.animeMoonlessSky
            AppThemeId.ANIME_STARLIGHT_SPY -> AnimeThemePalettes.animeStarlightSpy
            AppThemeId.ANIME_SUN_GOD -> AnimeThemePalettes.animeSunGod
            AppThemeId.ANIME_BLOOD_DEVIL -> AnimeThemePalettes.animeBloodDevil
            AppThemeId.ANIME_AUTO_MEMORY -> AnimeThemePalettes.animeAutoMemory
            AppThemeId.ANIME_PSY_SCIENTIST -> AnimeThemePalettes.animePsyScientist
            AppThemeId.MINIMAL_OLED -> MinimalistThemePalettes.minimalOled
            AppThemeId.MINIMAL_NORD -> MinimalistThemePalettes.minimalNord
            AppThemeId.MINIMAL_TOKYO -> MinimalistThemePalettes.minimalTokyo
            AppThemeId.MINIMAL_DRACULA -> MinimalistThemePalettes.minimalDracula
            AppThemeId.MINIMAL_EMERALD -> MinimalistThemePalettes.minimalEmerald
            AppThemeId.MINIMAL_MOCHA -> MinimalistThemePalettes.minimalMocha
            AppThemeId.MINIMAL_ROSE -> MinimalistThemePalettes.minimalRose
            AppThemeId.MINIMAL_PAPER -> MinimalistThemePalettes.minimalPaper
            AppThemeId.MINIMAL_TITANIUM -> MinimalistThemePalettes.minimalTitanium
            AppThemeId.MINIMAL_MATCHA -> MinimalistThemePalettes.minimalMatcha
            AppThemeId.MINIMAL_OBSIDIAN -> MinimalistThemePalettes.minimalObsidian
            AppThemeId.MINIMAL_GLACIER -> MinimalistThemePalettes.minimalGlacier
            AppThemeId.MINIMAL_LAVENDER -> MinimalistThemePalettes.minimalLavender
            AppThemeId.MINIMAL_CARBON -> MinimalistThemePalettes.minimalCarbon
            AppThemeId.MINIMAL_ESPRESSO -> MinimalistThemePalettes.minimalEspresso
            AppThemeId.MINIMAL_SAGE -> MinimalistThemePalettes.minimalSage
            AppThemeId.MINIMAL_MIDNIGHT_NOIR -> MinimalistThemePalettes.minimalMidnightNoir
            AppThemeId.MINIMAL_ALABASTER -> MinimalistThemePalettes.minimalAlabaster
        }
    }
}
