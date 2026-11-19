package com.example.p5.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.p5.R
import com.example.p5.domain.model.AppThemeId

object AnimeThemePalettes {

    val animeCyberpunk = createAnimeScheme(
        id = AppThemeId.ANIME_CYBERPUNK,
        background = Color(0xFF080A14),
        surface = Color(0xFF111424),
        surfaceVariant = Color(0xFF1A1F36),
        card = Color(0xFF13172B),
        cardHigh = Color(0xFF1D2340),
        floatingDock = Color(0xEB101322),
        accentRgb = 0x00F2FE,
        accentSecondary = Color(0xFFFF007F),
        accentLight = Color(0xFF70F8FF),
        accentDark = Color(0xFF00B0BC),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFA5B4FC),
        textTertiary = Color(0xFF6366F1),
        textMuted = Color(0xFF475569),
        backgroundImageRes = R.drawable.bg_anime_cyberpunk
    )

    val animeInfinityBlue = createAnimeScheme(
        id = AppThemeId.ANIME_INFINITY_BLUE,
        background = Color(0xFF070B14),
        surface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFF1E293B),
        card = Color(0xFF111D35),
        cardHigh = Color(0xFF192A4D),
        floatingDock = Color(0xEB0E182E),
        accentRgb = 0x38BDF8,
        accentSecondary = Color(0xFF818CF8),
        accentLight = Color(0xFF7DD3FC),
        accentDark = Color(0xFF0284C7),
        textPrimary = Color(0xFFF0F9FF),
        textSecondary = Color(0xFFBAE6FD),
        textTertiary = Color(0xFF7DD3FC),
        textMuted = Color(0xFF475569),
        backgroundImageRes = R.drawable.bg_anime_infinity
    )

    val animeCrimsonFlame = createAnimeScheme(
        id = AppThemeId.ANIME_CRIMSON_FLAME,
        background = Color(0xFF11070A),
        surface = Color(0xFF1D0E13),
        surfaceVariant = Color(0xFF2C151C),
        card = Color(0xFF221016),
        cardHigh = Color(0xFF331922),
        floatingDock = Color(0xEB1C0C12),
        accentRgb = 0xFF3366,
        accentSecondary = Color(0xFFFB923C),
        accentLight = Color(0xFFFF6688),
        accentDark = Color(0xFFCC1144),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFECDD3),
        textTertiary = Color(0xFFFDA4AF),
        textMuted = Color(0xFF6B4750),
        backgroundImageRes = R.drawable.bg_anime_crimson
    )

    val animeSakuraTwilight = createAnimeScheme(
        id = AppThemeId.ANIME_SAKURA_TWILIGHT,
        background = Color(0xFF120C19),
        surface = Color(0xFF1D1427),
        surfaceVariant = Color(0xFF2B1F3A),
        card = Color(0xFF21162E),
        cardHigh = Color(0xFF312244),
        floatingDock = Color(0xEB1C1227),
        accentRgb = 0xF472B6,
        accentSecondary = Color(0xFFFCD34D),
        accentLight = Color(0xFFF9A8D4),
        accentDark = Color(0xFFDB2777),
        textPrimary = Color(0xFFFDF2F8),
        textSecondary = Color(0xFFFBCFE8),
        textTertiary = Color(0xFFF472B6),
        textMuted = Color(0xFF6E5677),
        backgroundImageRes = R.drawable.bg_anime_sakura
    )

    val animeEvaUnit01 = createAnimeScheme(
        id = AppThemeId.ANIME_EVA_UNIT01,
        background = Color(0xFF0E0A1A),
        surface = Color(0xFF19122C),
        surfaceVariant = Color(0xFF271C44),
        card = Color(0xFF1D1533),
        cardHigh = Color(0xFF2B204C),
        floatingDock = Color(0xEB171028),
        accentRgb = 0x10B981,
        accentSecondary = Color(0xFFA855F7),
        accentLight = Color(0xFF34D399),
        accentDark = Color(0xFF059669),
        textPrimary = Color(0xFFF5F3FF),
        textSecondary = Color(0xFFDDD6FE),
        textTertiary = Color(0xFFA78BFA),
        textMuted = Color(0xFF5E4F76),
        backgroundImageRes = R.drawable.bg_anime_mecha
    )

    val animeShadowMonarch = createAnimeScheme(
        id = AppThemeId.ANIME_SHADOW_MONARCH,
        background = Color(0xFF090812),
        surface = Color(0xFF131024),
        surfaceVariant = Color(0xFF1C1736),
        card = Color(0xFF161229),
        cardHigh = Color(0xFF241E45),
        floatingDock = Color(0xEB100D1E),
        accentRgb = 0x8B5CF6,
        accentSecondary = Color(0xFF06B6D4),
        accentLight = Color(0xFFA78BFA),
        accentDark = Color(0xFF6D28D9),
        textPrimary = Color(0xFFF5F3FF),
        textSecondary = Color(0xFFC4B5FD),
        textTertiary = Color(0xFF8B5CF6),
        textMuted = Color(0xFF5B527E),
        backgroundImageRes = R.drawable.bg_anime_shadow
    )

    val animeSunBreath = createAnimeScheme(
        id = AppThemeId.ANIME_SUN_BREATH,
        background = Color(0xFF140808),
        surface = Color(0xFF200F0F),
        surfaceVariant = Color(0xFF311717),
        card = Color(0xFF261212),
        cardHigh = Color(0xFF3D1D1D),
        floatingDock = Color(0xEB1A0B0B),
        accentRgb = 0xF97316,
        accentSecondary = Color(0xFFEAB308),
        accentLight = Color(0xFFFDBA74),
        accentDark = Color(0xFFC2410C),
        textPrimary = Color(0xFFFFF7ED),
        textSecondary = Color(0xFFFED7AA),
        textTertiary = Color(0xFFF97316),
        textMuted = Color(0xFF7C4B4B),
        backgroundImageRes = R.drawable.bg_anime_sun
    )

    val animeCurseKing = createAnimeScheme(
        id = AppThemeId.ANIME_CURSE_KING,
        background = Color(0xFF100507),
        surface = Color(0xFF1C0A0D),
        surfaceVariant = Color(0xFF2B0F14),
        card = Color(0xFF210C10),
        cardHigh = Color(0xFF37141A),
        floatingDock = Color(0xEB170709),
        accentRgb = 0xE11D48,
        accentSecondary = Color(0xFFF43F5E),
        accentLight = Color(0xFFFDA4AF),
        accentDark = Color(0xFF9F1239),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFECDD3),
        textTertiary = Color(0xFFE11D48),
        textMuted = Color(0xFF70323B),
        backgroundImageRes = R.drawable.bg_anime_curse
    )

    val animeFreedomWings = createAnimeScheme(
        id = AppThemeId.ANIME_FREEDOM_WINGS,
        background = Color(0xFF0A100C),
        surface = Color(0xFF121B15),
        surfaceVariant = Color(0xFF1B2820),
        card = Color(0xFF152019),
        cardHigh = Color(0xFF23352A),
        floatingDock = Color(0xEB0E1612),
        accentRgb = 0x10B981,
        accentSecondary = Color(0xFF38BDF8),
        accentLight = Color(0xFF6EE7B7),
        accentDark = Color(0xFF047857),
        textPrimary = Color(0xFFF0FDF4),
        textSecondary = Color(0xFFA7F3D0),
        textTertiary = Color(0xFF10B981),
        textMuted = Color(0xFF4B6654),
        backgroundImageRes = R.drawable.bg_anime_titan
    )

    val animeMoonlessSky = createAnimeScheme(
        id = AppThemeId.ANIME_MOONLESS_SKY,
        background = Color(0xFF060608),
        surface = Color(0xFF101015),
        surfaceVariant = Color(0xFF1B1B22),
        card = Color(0xFF14141A),
        cardHigh = Color(0xFF23232C),
        floatingDock = Color(0xEB0D0D11),
        accentRgb = 0xEF4444,
        accentSecondary = Color(0xFFF97316),
        accentLight = Color(0xFFFCA5A5),
        accentDark = Color(0xFFB91C1C),
        textPrimary = Color(0xFFFEF2F2),
        textSecondary = Color(0xFFFECACA),
        textTertiary = Color(0xFFEF4444),
        textMuted = Color(0xFF5A4848),
        backgroundImageRes = R.drawable.bg_anime_bankai
    )

    val animeStarlightSpy = createAnimeScheme(
        id = AppThemeId.ANIME_STARLIGHT_SPY,
        background = Color(0xFF120A10),
        surface = Color(0xFF1C111A),
        surfaceVariant = Color(0xFF2C1B29),
        card = Color(0xFF221520),
        cardHigh = Color(0xFF382335),
        floatingDock = Color(0xEB180E16),
        accentRgb = 0xF472B6,
        accentSecondary = Color(0xFFFBBF24),
        accentLight = Color(0xFFFBCFE8),
        accentDark = Color(0xFFDB2777),
        textPrimary = Color(0xFFFDF2F8),
        textSecondary = Color(0xFFFBCFE8),
        textTertiary = Color(0xFFF472B6),
        textMuted = Color(0xFF6E4D65),
        backgroundImageRes = R.drawable.bg_anime_starlight
    )

    val animeSunGod = createAnimeScheme(
        id = AppThemeId.ANIME_SUN_GOD,
        background = Color(0xFF121008),
        surface = Color(0xFF1C190F),
        surfaceVariant = Color(0xFF2A2617),
        card = Color(0xFF211E13),
        cardHigh = Color(0xFF36311F),
        floatingDock = Color(0xEB18150C),
        accentRgb = 0xFACC15,
        accentSecondary = Color(0xFFFB923C),
        accentLight = Color(0xFFFEF08A),
        accentDark = Color(0xFFCA8A04),
        textPrimary = Color(0xFFFEFCE8),
        textSecondary = Color(0xFFFEF08A),
        textTertiary = Color(0xFFFACC15),
        textMuted = Color(0xFF6B6543),
        backgroundImageRes = R.drawable.bg_anime_nika
    )

    val animeBloodDevil = createAnimeScheme(
        id = AppThemeId.ANIME_BLOOD_DEVIL,
        background = Color(0xFF14070A),
        surface = Color(0xFF1E0E12),
        surfaceVariant = Color(0xFF2D161C),
        card = Color(0xFF231116),
        cardHigh = Color(0xFF381C23),
        floatingDock = Color(0xEB190B0E),
        accentRgb = 0xFF2E63,
        accentSecondary = Color(0xFFFFA41B),
        accentLight = Color(0xFFFF7597),
        accentDark = Color(0xFFCC1444),
        textPrimary = Color(0xFFFFF0F3),
        textSecondary = Color(0xFFFFCCD5),
        textTertiary = Color(0xFFFF2E63),
        textMuted = Color(0xFF75404B),
        backgroundImageRes = R.drawable.bg_anime_chainsaw
    )

    val animeAutoMemory = createAnimeScheme(
        id = AppThemeId.ANIME_AUTO_MEMORY,
        background = Color(0xFF080D16),
        surface = Color(0xFF101825),
        surfaceVariant = Color(0xFF1A263B),
        card = Color(0xFF131E2E),
        cardHigh = Color(0xFF21314C),
        floatingDock = Color(0xEB0D1420),
        accentRgb = 0x38BDF8,
        accentSecondary = Color(0xFF34D399),
        accentLight = Color(0xFF7DD3FC),
        accentDark = Color(0xFF0284C7),
        textPrimary = Color(0xFFF0F9FF),
        textSecondary = Color(0xFFBAE6FD),
        textTertiary = Color(0xFF38BDF8),
        textMuted = Color(0xFF485E7B),
        backgroundImageRes = R.drawable.bg_anime_violet
    )

    val animePsyScientist = createAnimeScheme(
        id = AppThemeId.ANIME_PSY_SCIENTIST,
        background = Color(0xFF0E0A07),
        surface = Color(0xFF18120E),
        surfaceVariant = Color(0xFF261C16),
        card = Color(0xFF1E1611),
        cardHigh = Color(0xFF31241C),
        floatingDock = Color(0xEB140E0A),
        accentRgb = 0xFB923C,
        accentSecondary = Color(0xFF22C55E),
        accentLight = Color(0xFFFDBA74),
        accentDark = Color(0xFFC2410C),
        textPrimary = Color(0xFFFFF7ED),
        textSecondary = Color(0xFFFED7AA),
        textTertiary = Color(0xFFFB923C),
        textMuted = Color(0xFF6E5647),
        backgroundImageRes = R.drawable.bg_anime_steins
    )

    private fun createAnimeScheme(
        id: AppThemeId,
        background: Color,
        surface: Color,
        surfaceVariant: Color,
        card: Color,
        cardHigh: Color,
        floatingDock: Color,
        accentRgb: Long,
        accentSecondary: Color,
        accentLight: Color,
        accentDark: Color,
        textPrimary: Color,
        textSecondary: Color,
        textTertiary: Color,
        textMuted: Color,
        @DrawableRes backgroundImageRes: Int
    ): AppColorScheme {
        val cleanRgb = accentRgb and 0xFFFFFFL
        return AppColorScheme(
            id = id,
            isDark = true,
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            card = card,
            cardHigh = cardHigh,
            floatingDock = floatingDock,
            accent = Color((0xFFL shl 24) or cleanRgb),
            accentSecondary = accentSecondary,
            accentLight = accentLight,
            accentDark = accentDark,
            accentGlow = Color((0x4DL shl 24) or cleanRgb),
            glassFillSubtle = Color((0x0AL shl 24) or cleanRgb),
            glassFillLight = Color((0x14L shl 24) or cleanRgb),
            glassFillMedium = Color((0x1FL shl 24) or cleanRgb),
            glassBorderSubtle = Color((0x26L shl 24) or cleanRgb),
            glassBorderBright = Color((0x66L shl 24) or cleanRgb),
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            textMuted = textMuted,
            backgroundImageRes = backgroundImageRes
        )
    }
}
