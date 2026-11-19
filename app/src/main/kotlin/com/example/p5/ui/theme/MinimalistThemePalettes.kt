package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId

object MinimalistThemePalettes {

    val minimalOled = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_OLED,
        background = Color(0xFF000000),
        surface = Color(0xFF0D0D0D),
        surfaceVariant = Color(0xFF161616),
        card = Color(0xFF111111),
        cardHigh = Color(0xFF1A1A1A),
        floatingDock = Color(0xF00A0A0A),
        accentRgb = 0xFFFFFF,
        accentSecondary = Color(0xFFCCCCCC),
        accentLight = Color(0xFFFFFFFF),
        accentDark = Color(0xFFAAAAAA),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA0A0A0),
        textTertiary = Color(0xFF707070),
        textMuted = Color(0xFF444444),
        accentGlowAlpha = 0x33
    )

    val minimalNord = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_NORD,
        background = Color(0xFF242933),
        surface = Color(0xFF2E3440),
        surfaceVariant = Color(0xFF3B4252),
        card = Color(0xFF2E3440),
        cardHigh = Color(0xFF434C5E),
        floatingDock = Color(0xEB262C36),
        accentRgb = 0x88C0D0,
        accentSecondary = Color(0xFF81A1C1),
        accentLight = Color(0xFF8FBCBB),
        accentDark = Color(0xFF5E81AC),
        textPrimary = Color(0xFFECEFF4),
        textSecondary = Color(0xFFD8DEE9),
        textTertiary = Color(0xFF81A1C1),
        textMuted = Color(0xFF4C566A)
    )

    val minimalTokyo = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_TOKYO,
        background = Color(0xFF16161E),
        surface = Color(0xFF1A1B26),
        surfaceVariant = Color(0xFF24283B),
        card = Color(0xFF1F2335),
        cardHigh = Color(0xFF292E42),
        floatingDock = Color(0xEB181923),
        accentRgb = 0x7AA2F7,
        accentSecondary = Color(0xFFBB9AF7),
        accentLight = Color(0xFF89DDFF),
        accentDark = Color(0xFF3D59A1),
        textPrimary = Color(0xFFC0CAF5),
        textSecondary = Color(0xFF9AA5CE),
        textTertiary = Color(0xFF7AA2F7),
        textMuted = Color(0xFF565F89)
    )

    val minimalDracula = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_DRACULA,
        background = Color(0xFF1E1F29),
        surface = Color(0xFF282A36),
        surfaceVariant = Color(0xFF383A59),
        card = Color(0xFF2B2D3C),
        cardHigh = Color(0xFF44475A),
        floatingDock = Color(0xEB232531),
        accentRgb = 0xBD93F9,
        accentSecondary = Color(0xFFFF79C6),
        accentLight = Color(0xFFFF92DF),
        accentDark = Color(0xFF8A5CF5),
        textPrimary = Color(0xFFF8F8F2),
        textSecondary = Color(0xFFBFBFBA),
        textTertiary = Color(0xFFBD93F9),
        textMuted = Color(0xFF6272A4)
    )

    val minimalEmerald = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_EMERALD,
        background = Color(0xFF091410),
        surface = Color(0xFF10201A),
        surfaceVariant = Color(0xFF183027),
        card = Color(0xFF13251E),
        cardHigh = Color(0xFF1C382D),
        floatingDock = Color(0xEB0E1D17),
        accentRgb = 0x10B981,
        accentSecondary = Color(0xFF34D399),
        accentLight = Color(0xFF6EE7B7),
        accentDark = Color(0xFF047857),
        textPrimary = Color(0xFFECFDF5),
        textSecondary = Color(0xFFA7F3D0),
        textTertiary = Color(0xFF34D399),
        textMuted = Color(0xFF406558)
    )

    val minimalMocha = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_MOCHA,
        background = Color(0xFF140F0D),
        surface = Color(0xFF1F1815),
        surfaceVariant = Color(0xFF2C221E),
        card = Color(0xFF231B17),
        cardHigh = Color(0xFF362A24),
        floatingDock = Color(0xEB1A1411),
        accentRgb = 0xF59E0B,
        accentSecondary = Color(0xFFFBBF24),
        accentLight = Color(0xFFFCD34D),
        accentDark = Color(0xFFB45309),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFFDE68A),
        textTertiary = Color(0xFFF59E0B),
        textMuted = Color(0xFF786255)
    )

    val minimalRose = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_ROSE,
        background = Color(0xFF16141F),
        surface = Color(0xFF191724),
        surfaceVariant = Color(0xFF26233A),
        card = Color(0xFF1F1D2E),
        cardHigh = Color(0xFF2A283E),
        floatingDock = Color(0xEB181622),
        accentRgb = 0xEB6F92,
        accentSecondary = Color(0xFFF6C177),
        accentLight = Color(0xFFEA9A97),
        accentDark = Color(0xFFB4436C),
        textPrimary = Color(0xFFE0DEF4),
        textSecondary = Color(0xFF908CAA),
        textTertiary = Color(0xFFEB6F92),
        textMuted = Color(0xFF56526E)
    )

    val minimalPaper = createLightMinimalScheme(
        id = AppThemeId.MINIMAL_PAPER,
        background = Color(0xFFFDFBF7),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF5F2EB),
        card = Color(0xFFFFFFFF),
        cardHigh = Color(0xFFF5F2EB),
        floatingDock = Color(0xF0FFFFFF),
        accentRgb = 0x0F766E,
        accentSecondary = Color(0xFF14B8A6),
        accentLight = Color(0xFF2DD4BF),
        accentDark = Color(0xFF115E59),
        textPrimary = Color(0xFF1E293B),
        textSecondary = Color(0xFF64748B),
        textTertiary = Color(0xFF94A3B8),
        textMuted = Color(0xFFCBD5E1)
    )

    val minimalTitanium = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_TITANIUM,
        background = Color(0xFF111318),
        surface = Color(0xFF191C24),
        surfaceVariant = Color(0xFF232733),
        card = Color(0xFF1D212B),
        cardHigh = Color(0xFF2B303E),
        floatingDock = Color(0xEB161921),
        accentRgb = 0x94A3B8,
        accentSecondary = Color(0xFFCBD5E1),
        accentLight = Color(0xFFE2E8F0),
        accentDark = Color(0xFF64748B),
        textPrimary = Color(0xFFF1F5F9),
        textSecondary = Color(0xFF94A3B8),
        textTertiary = Color(0xFF64748B),
        textMuted = Color(0xFF475569)
    )

    val minimalMatcha = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_MATCHA,
        background = Color(0xFF121513),
        surface = Color(0xFF1B211D),
        surfaceVariant = Color(0xFF262E28),
        card = Color(0xFF202722),
        cardHigh = Color(0xFF2F3A32),
        floatingDock = Color(0xEB181E19),
        accentRgb = 0x86EFAC,
        accentSecondary = Color(0xFFBBF7D0),
        accentLight = Color(0xFFDCFCE7),
        accentDark = Color(0xFF4ADE80),
        textPrimary = Color(0xFFF0FDF4),
        textSecondary = Color(0xFFBBEFCE),
        textTertiary = Color(0xFF86EFAC),
        textMuted = Color(0xFF4B6353)
    )

    val minimalObsidian = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_OBSIDIAN,
        background = Color(0xFF0C0C0E),
        surface = Color(0xFF151518),
        surfaceVariant = Color(0xFF202024),
        card = Color(0xFF1A1A1E),
        cardHigh = Color(0xFF28282E),
        floatingDock = Color(0xEB131316),
        accentRgb = 0xE2B755,
        accentSecondary = Color(0xFFF3D284),
        accentLight = Color(0xFFFDE68A),
        accentDark = Color(0xFFC4972E),
        textPrimary = Color(0xFFFAF7EE),
        textSecondary = Color(0xFFC7C2B2),
        textTertiary = Color(0xFFE2B755),
        textMuted = Color(0xFF6B6758)
    )

    val minimalGlacier = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_GLACIER,
        background = Color(0xFF0B121A),
        surface = Color(0xFF121D28),
        surfaceVariant = Color(0xFF1B2B3C),
        card = Color(0xFF162331),
        cardHigh = Color(0xFF22374C),
        floatingDock = Color(0xEB0F1923),
        accentRgb = 0x38BDF8,
        accentSecondary = Color(0xFF7DD3FC),
        accentLight = Color(0xFFBAE6FD),
        accentDark = Color(0xFF0284C7),
        textPrimary = Color(0xFFF0F9FF),
        textSecondary = Color(0xFFBAE6FD),
        textTertiary = Color(0xFF38BDF8),
        textMuted = Color(0xFF436380)
    )

    val minimalLavender = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_LAVENDER,
        background = Color(0xFF131118),
        surface = Color(0xFF1C1924),
        surfaceVariant = Color(0xFF282333),
        card = Color(0xFF201D2A),
        cardHigh = Color(0xFF322C40),
        floatingDock = Color(0xEB1A1721),
        accentRgb = 0xC084FC,
        accentSecondary = Color(0xFFD8B4FE),
        accentLight = Color(0xFFE9D5FF),
        accentDark = Color(0xFFA855F7),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFD8B4FE),
        textTertiary = Color(0xFFC084FC),
        textMuted = Color(0xFF605175)
    )

    val minimalCarbon = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_CARBON,
        background = Color(0xFF121214),
        surface = Color(0xFF1A1A1E),
        surfaceVariant = Color(0xFF26262B),
        card = Color(0xFF1F1F24),
        cardHigh = Color(0xFF2E2E35),
        floatingDock = Color(0xEB17171B),
        accentRgb = 0xA3E635,
        accentSecondary = Color(0xFFBEF264),
        accentLight = Color(0xFFD9F99D),
        accentDark = Color(0xFF65A30D),
        textPrimary = Color(0xFFF7FEE7),
        textSecondary = Color(0xFFBEF264),
        textTertiary = Color(0xFFA3E635),
        textMuted = Color(0xFF536928)
    )

    val minimalEspresso = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_ESPRESSO,
        background = Color(0xFF100C0A),
        surface = Color(0xFF1B1512),
        surfaceVariant = Color(0xFF271F1B),
        card = Color(0xFF211A16),
        cardHigh = Color(0xFF332822),
        floatingDock = Color(0xEB181310),
        accentRgb = 0xD97706,
        accentSecondary = Color(0xFFF59E0B),
        accentLight = Color(0xFFFCD34D),
        accentDark = Color(0xFF92400E),
        textPrimary = Color(0xFFFFFBEB),
        textSecondary = Color(0xFFFDE68A),
        textTertiary = Color(0xFFD97706),
        textMuted = Color(0xFF6E5343)
    )

    val minimalSage = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_SAGE,
        background = Color(0xFF0F1412),
        surface = Color(0xFF17201D),
        surfaceVariant = Color(0xFF22302B),
        card = Color(0xFF1C2723),
        cardHigh = Color(0xFF2B3D37),
        floatingDock = Color(0xEB141C19),
        accentRgb = 0x5EEAD4,
        accentSecondary = Color(0xFF99F6E4),
        accentLight = Color(0xFFCCFBF1),
        accentDark = Color(0xFF0D9488),
        textPrimary = Color(0xFFF0FDFA),
        textSecondary = Color(0xFF99F6E4),
        textTertiary = Color(0xFF5EEAD4),
        textMuted = Color(0xFF40655E)
    )

    val minimalMidnightNoir = createDarkMinimalScheme(
        id = AppThemeId.MINIMAL_MIDNIGHT_NOIR,
        background = Color(0xFF050505),
        surface = Color(0xFF0E0E0E),
        surfaceVariant = Color(0xFF191919),
        card = Color(0xFF131313),
        cardHigh = Color(0xFF212121),
        floatingDock = Color(0xEB0A0A0A),
        accentRgb = 0xFFFFFF,
        accentSecondary = Color(0xFFE5E5E5),
        accentLight = Color(0xFFF5F5F5),
        accentDark = Color(0xFFA3A3A3),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA3A3A3),
        textTertiary = Color(0xFF737373),
        textMuted = Color(0xFF525252)
    )

    val minimalAlabaster = createLightMinimalScheme(
        id = AppThemeId.MINIMAL_ALABASTER,
        background = Color(0xFFF7F5F0),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFEDE8DE),
        card = Color(0xFFFFFFFF),
        cardHigh = Color(0xFFF0ECE3),
        floatingDock = Color(0xF0FFFFFF),
        accentRgb = 0xC2410C,
        accentSecondary = Color(0xFFEA580C),
        accentLight = Color(0xFFF97316),
        accentDark = Color(0xFF9A3412),
        textPrimary = Color(0xFF1C1917),
        textSecondary = Color(0xFF57534E),
        textTertiary = Color(0xFF78716C),
        textMuted = Color(0xFFA8A29E)
    )

    private fun createDarkMinimalScheme(
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
        accentGlowAlpha: Long = 0x4D
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
            accentGlow = Color((accentGlowAlpha shl 24) or cleanRgb),
            glassFillSubtle = Color((0x0AL shl 24) or cleanRgb),
            glassFillLight = Color((0x14L shl 24) or cleanRgb),
            glassFillMedium = Color((0x1FL shl 24) or cleanRgb),
            glassBorderSubtle = Color((0x26L shl 24) or cleanRgb),
            glassBorderBright = Color((0x59L shl 24) or cleanRgb),
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            textMuted = textMuted,
            backgroundImageRes = null
        )
    }

    private fun createLightMinimalScheme(
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
        textMuted: Color
    ): AppColorScheme {
        val cleanRgb = accentRgb and 0xFFFFFFL
        return AppColorScheme(
            id = id,
            isDark = false,
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
            accentGlow = Color((0x33L shl 24) or cleanRgb),
            glassFillSubtle = Color((0x08L shl 24) or cleanRgb),
            glassFillLight = Color((0x0FL shl 24) or cleanRgb),
            glassFillMedium = Color((0x17L shl 24) or cleanRgb),
            glassBorderSubtle = Color((0x14L shl 24) or cleanRgb),
            glassBorderBright = Color((0x2EL shl 24) or cleanRgb),
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            textTertiary = textTertiary,
            textMuted = textMuted,
            backgroundImageRes = null
        )
    }
}
