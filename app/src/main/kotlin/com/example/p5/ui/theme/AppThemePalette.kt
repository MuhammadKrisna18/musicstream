package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.R
import com.example.p5.domain.model.AppThemeId

object AppThemePalette {

    val defaultDark = AppColorScheme(
        id = AppThemeId.DEFAULT_DARK,
        isDark = true,
        background = Color(0xFF0D0E12),
        surface = Color(0xFF16181F),
        surfaceVariant = Color(0xFF1E212B),
        card = Color(0xFF181B24),
        cardHigh = Color(0xFF222634),
        floatingDock = Color(0xEB151720),
        accent = Color(0xFF1DB954),
        accentSecondary = Color(0xFF10B981),
        accentLight = Color(0xFF6EE7B7),
        accentDark = Color(0xFF047857),
        accentGlow = Color(0x4D1DB954),
        glassFillSubtle = Color(0x0AFFFFFF),
        glassFillLight = Color(0x14FFFFFF),
        glassFillMedium = Color(0x1FFFFFFF),
        glassBorderSubtle = Color(0x1FFFFFFF),
        glassBorderBright = Color(0x59FFFFFF),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8),
        textTertiary = Color(0xFF64748B),
        textMuted = Color(0xFF475569),
        backgroundImageRes = null
    )

    val defaultLight = AppColorScheme(
        id = AppThemeId.DEFAULT_LIGHT,
        isDark = false,
        background = Color(0xFFF8FAFC),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F5F9),
        card = Color(0xFFFFFFFF),
        cardHigh = Color(0xFFF8FAFC),
        floatingDock = Color(0xF0FFFFFF),
        accent = Color(0xFF059669),
        accentSecondary = Color(0xFF10B981),
        accentLight = Color(0xFF34D399),
        accentDark = Color(0xFF065F46),
        accentGlow = Color(0x33059669),
        glassFillSubtle = Color(0x08000000),
        glassFillLight = Color(0x0F000000),
        glassFillMedium = Color(0x17000000),
        glassBorderSubtle = Color(0x14000000),
        glassBorderBright = Color(0x2E000000),
        textPrimary = Color(0xFF0F172A),
        textSecondary = Color(0xFF475569),
        textTertiary = Color(0xFF64748B),
        textMuted = Color(0xFF94A3B8),
        backgroundImageRes = null
    )

    val animeCyberpunk = AppColorScheme(
        id = AppThemeId.ANIME_CYBERPUNK,
        isDark = true,
        background = Color(0xFF080A14),
        surface = Color(0xFF111424),
        surfaceVariant = Color(0xFF1A1F36),
        card = Color(0xFF13172B),
        cardHigh = Color(0xFF1D2340),
        floatingDock = Color(0xEB101322),
        accent = Color(0xFF00F2FE),
        accentSecondary = Color(0xFFFF007F),
        accentLight = Color(0xFF70F8FF),
        accentDark = Color(0xFF00B0BC),
        accentGlow = Color(0x4D00F2FE),
        glassFillSubtle = Color(0x0A00F2FE),
        glassFillLight = Color(0x1400F2FE),
        glassFillMedium = Color(0x1F00F2FE),
        glassBorderSubtle = Color(0x2600F2FE),
        glassBorderBright = Color(0x6600F2FE),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFFA5B4FC),
        textTertiary = Color(0xFF6366F1),
        textMuted = Color(0xFF475569),
        backgroundImageRes = R.drawable.bg_anime_midnight
    )

    val animeInfinityBlue = AppColorScheme(
        id = AppThemeId.ANIME_INFINITY_BLUE,
        isDark = true,
        background = Color(0xFF070B14),
        surface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFF1E293B),
        card = Color(0xFF111D35),
        cardHigh = Color(0xFF192A4D),
        floatingDock = Color(0xEB0E182E),
        accent = Color(0xFF38BDF8),
        accentSecondary = Color(0xFF818CF8),
        accentLight = Color(0xFF7DD3FC),
        accentDark = Color(0xFF0284C7),
        accentGlow = Color(0x4D38BDF8),
        glassFillSubtle = Color(0x0A38BDF8),
        glassFillLight = Color(0x1438BDF8),
        glassFillMedium = Color(0x1F38BDF8),
        glassBorderSubtle = Color(0x2638BDF8),
        glassBorderBright = Color(0x6638BDF8),
        textPrimary = Color(0xFFF0F9FF),
        textSecondary = Color(0xFFBAE6FD),
        textTertiary = Color(0xFF7DD3FC),
        textMuted = Color(0xFF475569),
        backgroundImageRes = R.drawable.bg_anime_midnight
    )

    val animeCrimsonFlame = AppColorScheme(
        id = AppThemeId.ANIME_CRIMSON_FLAME,
        isDark = true,
        background = Color(0xFF11070A),
        surface = Color(0xFF1D0E13),
        surfaceVariant = Color(0xFF2C151C),
        card = Color(0xFF221016),
        cardHigh = Color(0xFF331922),
        floatingDock = Color(0xEB1C0C12),
        accent = Color(0xFFFF3366),
        accentSecondary = Color(0xFFFB923C),
        accentLight = Color(0xFFFF6688),
        accentDark = Color(0xFFCC1144),
        accentGlow = Color(0x4DFF3366),
        glassFillSubtle = Color(0x0AFF3366),
        glassFillLight = Color(0x14FF3366),
        glassFillMedium = Color(0x1FFF3366),
        glassBorderSubtle = Color(0x26FF3366),
        glassBorderBright = Color(0x66FF3366),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFECDD3),
        textTertiary = Color(0xFFFDA4AF),
        textMuted = Color(0xFF6B4750),
        backgroundImageRes = R.drawable.bg_anime_midnight
    )

    val animeSakuraTwilight = AppColorScheme(
        id = AppThemeId.ANIME_SAKURA_TWILIGHT,
        isDark = true,
        background = Color(0xFF120C19),
        surface = Color(0xFF1D1427),
        surfaceVariant = Color(0xFF2B1F3A),
        card = Color(0xFF21162E),
        cardHigh = Color(0xFF312244),
        floatingDock = Color(0xEB1C1227),
        accent = Color(0xFFF472B6),
        accentSecondary = Color(0xFFFCD34D),
        accentLight = Color(0xFFF9A8D4),
        accentDark = Color(0xFFDB2777),
        accentGlow = Color(0x4DF472B6),
        glassFillSubtle = Color(0x0AF472B6),
        glassFillLight = Color(0x14F472B6),
        glassFillMedium = Color(0x1FF472B6),
        glassBorderSubtle = Color(0x26F472B6),
        glassBorderBright = Color(0x66F472B6),
        textPrimary = Color(0xFFFDF2F8),
        textSecondary = Color(0xFFFBCFE8),
        textTertiary = Color(0xFFF472B6),
        textMuted = Color(0xFF6E5677),
        backgroundImageRes = R.drawable.bg_anime_midnight
    )

    val animeEvaUnit01 = AppColorScheme(
        id = AppThemeId.ANIME_EVA_UNIT01,
        isDark = true,
        background = Color(0xFF0E0A1A),
        surface = Color(0xFF19122C),
        surfaceVariant = Color(0xFF271C44),
        card = Color(0xFF1D1533),
        cardHigh = Color(0xFF2B204C),
        floatingDock = Color(0xEB171028),
        accent = Color(0xFF10B981),
        accentSecondary = Color(0xFFA855F7),
        accentLight = Color(0xFF34D399),
        accentDark = Color(0xFF059669),
        accentGlow = Color(0x4D10B981),
        glassFillSubtle = Color(0x0A10B981),
        glassFillLight = Color(0x1410B981),
        glassFillMedium = Color(0x1F10B981),
        glassBorderSubtle = Color(0x2610B981),
        glassBorderBright = Color(0x6610B981),
        textPrimary = Color(0xFFF5F3FF),
        textSecondary = Color(0xFFDDD6FE),
        textTertiary = Color(0xFFA78BFA),
        textMuted = Color(0xFF5E4F76),
        backgroundImageRes = R.drawable.bg_anime_midnight
    )

    val minimalOled = AppColorScheme(
        id = AppThemeId.MINIMAL_OLED,
        isDark = true,
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        surfaceVariant = Color(0xFF161616),
        card = Color(0xFF111111),
        cardHigh = Color(0xFF1A1A1A),
        floatingDock = Color(0xF00A0A0A),
        accent = Color(0xFFFFFFFF),
        accentSecondary = Color(0xFFCCCCCC),
        accentLight = Color(0xFFFFFFFF),
        accentDark = Color(0xFFAAAAAA),
        accentGlow = Color(0x33FFFFFF),
        glassFillSubtle = Color(0x0AFFFFFF),
        glassFillLight = Color(0x14FFFFFF),
        glassFillMedium = Color(0x1FFFFFFF),
        glassBorderSubtle = Color(0x26FFFFFF),
        glassBorderBright = Color(0x59FFFFFF),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA0A0A0),
        textTertiary = Color(0xFF707070),
        textMuted = Color(0xFF444444),
        backgroundImageRes = null
    )

    val minimalNord = AppColorScheme(
        id = AppThemeId.MINIMAL_NORD,
        isDark = true,
        background = Color(0xFF242933),
        surface = Color(0xFF2E3440),
        surfaceVariant = Color(0xFF3B4252),
        card = Color(0xFF2E3440),
        cardHigh = Color(0xFF434C5E),
        floatingDock = Color(0xEB262C36),
        accent = Color(0xFF88C0D0),
        accentSecondary = Color(0xFF81A1C1),
        accentLight = Color(0xFF8FBCBB),
        accentDark = Color(0xFF5E81AC),
        accentGlow = Color(0x4D88C0D0),
        glassFillSubtle = Color(0x0A88C0D0),
        glassFillLight = Color(0x1488C0D0),
        glassFillMedium = Color(0x1F88C0D0),
        glassBorderSubtle = Color(0x2688C0D0),
        glassBorderBright = Color(0x5988C0D0),
        textPrimary = Color(0xFFECEFF4),
        textSecondary = Color(0xFFD8DEE9),
        textTertiary = Color(0xFF81A1C1),
        textMuted = Color(0xFF4C566A),
        backgroundImageRes = null
    )

    val minimalTokyo = AppColorScheme(
        id = AppThemeId.MINIMAL_TOKYO,
        isDark = true,
        background = Color(0xFF16161E),
        surface = Color(0xFF1A1B26),
        surfaceVariant = Color(0xFF24283B),
        card = Color(0xFF1F2335),
        cardHigh = Color(0xFF292E42),
        floatingDock = Color(0xEB181923),
        accent = Color(0xFF7AA2F7),
        accentSecondary = Color(0xFFBB9AF7),
        accentLight = Color(0xFF89DDFF),
        accentDark = Color(0xFF3D59A1),
        accentGlow = Color(0x4D7AA2F7),
        glassFillSubtle = Color(0x0A7AA2F7),
        glassFillLight = Color(0x147AA2F7),
        glassFillMedium = Color(0x1F7AA2F7),
        glassBorderSubtle = Color(0x267AA2F7),
        glassBorderBright = Color(0x597AA2F7),
        textPrimary = Color(0xFFC0CAF5),
        textSecondary = Color(0xFF9AA5CE),
        textTertiary = Color(0xFF7AA2F7),
        textMuted = Color(0xFF565F89),
        backgroundImageRes = null
    )

    val minimalDracula = AppColorScheme(
        id = AppThemeId.MINIMAL_DRACULA,
        isDark = true,
        background = Color(0xFF1E1F29),
        surface = Color(0xFF282A36),
        surfaceVariant = Color(0xFF383A59),
        card = Color(0xFF2B2D3C),
        cardHigh = Color(0xFF44475A),
        floatingDock = Color(0xEB232531),
        accent = Color(0xFFBD93F9),
        accentSecondary = Color(0xFFFF79C6),
        accentLight = Color(0xFFFF92DF),
        accentDark = Color(0xFF8A5CF5),
        accentGlow = Color(0x4DBD93F9),
        glassFillSubtle = Color(0x0ABD93F9),
        glassFillLight = Color(0x14BD93F9),
        glassFillMedium = Color(0x1FBD93F9),
        glassBorderSubtle = Color(0x26BD93F9),
        glassBorderBright = Color(0x59BD93F9),
        textPrimary = Color(0xFFF8F8F2),
        textSecondary = Color(0xFFBFBFBA),
        textTertiary = Color(0xFFBD93F9),
        textMuted = Color(0xFF6272A4),
        backgroundImageRes = null
    )

    val minimalEmerald = AppColorScheme(
        id = AppThemeId.MINIMAL_EMERALD,
        isDark = true,
        background = Color(0xFF091410),
        surface = Color(0xFF10201A),
        surfaceVariant = Color(0xFF183027),
        card = Color(0xFF13251E),
        cardHigh = Color(0xFF1C382D),
        floatingDock = Color(0xEB0E1D17),
        accent = Color(0xFF10B981),
        accentSecondary = Color(0xFF34D399),
        accentLight = Color(0xFF6EE7B7),
        accentDark = Color(0xFF047857),
        accentGlow = Color(0x4D10B981),
        glassFillSubtle = Color(0x0A10B981),
        glassFillLight = Color(0x1410B981),
        glassFillMedium = Color(0x1F10B981),
        glassBorderSubtle = Color(0x2610B981),
        glassBorderBright = Color(0x5910B981),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFFA7F3D0),
        textTertiary = Color(0xFF34D399),
        textMuted = Color(0xFF406558),
        backgroundImageRes = null
    )

    val minimalMocha = AppColorScheme(
        id = AppThemeId.MINIMAL_MOCHA,
        isDark = true,
        background = Color(0xFF140F0D),
        surface = Color(0xFF1F1815),
        surfaceVariant = Color(0xFF2C221E),
        card = Color(0xFF231B17),
        cardHigh = Color(0xFF362A24),
        floatingDock = Color(0xEB1A1411),
        accent = Color(0xFFF59E0B),
        accentSecondary = Color(0xFFFBBF24),
        accentLight = Color(0xFFFCD34D),
        accentDark = Color(0xFFB45309),
        accentGlow = Color(0x4DF59E0B),
        glassFillSubtle = Color(0x0AF59E0B),
        glassFillLight = Color(0x14F59E0B),
        glassFillMedium = Color(0x1FF59E0B),
        glassBorderSubtle = Color(0x26F59E0B),
        glassBorderBright = Color(0x59F59E0B),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFFDE68A),
        textTertiary = Color(0xFFF59E0B),
        textMuted = Color(0xFF786255),
        backgroundImageRes = null
    )

    val minimalRose = AppColorScheme(
        id = AppThemeId.MINIMAL_ROSE,
        isDark = true,
        background = Color(0xFF16141F),
        surface = Color(0xFF191724),
        surfaceVariant = Color(0xFF26233A),
        card = Color(0xFF1F1D2E),
        cardHigh = Color(0xFF2A283E),
        floatingDock = Color(0xEB181622),
        accent = Color(0xFFEB6F92),
        accentSecondary = Color(0xFFF6C177),
        accentLight = Color(0xFFEA9A97),
        accentDark = Color(0xFFB4436C),
        accentGlow = Color(0x4DEB6F92),
        glassFillSubtle = Color(0x0AEB6F92),
        glassFillLight = Color(0x14EB6F92),
        glassFillMedium = Color(0x1FEB6F92),
        glassBorderSubtle = Color(0x26EB6F92),
        glassBorderBright = Color(0x59EB6F92),
        textPrimary = Color(0xFFE0DEF4),
        textSecondary = Color(0xFF908CAA),
        textTertiary = Color(0xFFEB6F92),
        textMuted = Color(0xFF56526E),
        backgroundImageRes = null
    )

    val minimalPaper = AppColorScheme(
        id = AppThemeId.MINIMAL_PAPER,
        isDark = false,
        background = Color(0xFFFDFBF7),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF5F2EB),
        card = Color(0xFFFFFFFF),
        cardHigh = Color(0xFFF5F2EB),
        floatingDock = Color(0xF0FFFFFF),
        accent = Color(0xFF0F766E),
        accentSecondary = Color(0xFF14B8A6),
        accentLight = Color(0xFF2DD4BF),
        accentDark = Color(0xFF115E59),
        accentGlow = Color(0x330F766E),
        glassFillSubtle = Color(0x080F766E),
        glassFillLight = Color(0x0F0F766E),
        glassFillMedium = Color(0x170F766E),
        glassBorderSubtle = Color(0x140F766E),
        glassBorderBright = Color(0x2E0F766E),
        textPrimary = Color(0xFF1E293B),
        textSecondary = Color(0xFF64748B),
        textTertiary = Color(0xFF94A3B8),
        textMuted = Color(0xFFCBD5E1),
        backgroundImageRes = null
    )

    fun getPalette(id: AppThemeId): AppColorScheme {
        return when (id) {
            AppThemeId.DEFAULT_DARK -> defaultDark
            AppThemeId.DEFAULT_LIGHT -> defaultLight
            AppThemeId.ANIME_CYBERPUNK -> animeCyberpunk
            AppThemeId.ANIME_INFINITY_BLUE -> animeInfinityBlue
            AppThemeId.ANIME_CRIMSON_FLAME -> animeCrimsonFlame
            AppThemeId.ANIME_SAKURA_TWILIGHT -> animeSakuraTwilight
            AppThemeId.ANIME_EVA_UNIT01 -> animeEvaUnit01
            AppThemeId.MINIMAL_OLED -> minimalOled
            AppThemeId.MINIMAL_NORD -> minimalNord
            AppThemeId.MINIMAL_TOKYO -> minimalTokyo
            AppThemeId.MINIMAL_DRACULA -> minimalDracula
            AppThemeId.MINIMAL_EMERALD -> minimalEmerald
            AppThemeId.MINIMAL_MOCHA -> minimalMocha
            AppThemeId.MINIMAL_ROSE -> minimalRose
            AppThemeId.MINIMAL_PAPER -> minimalPaper
        }
    }
}
