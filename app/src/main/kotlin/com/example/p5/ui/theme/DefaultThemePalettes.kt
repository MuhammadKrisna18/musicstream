package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId

object DefaultThemePalettes {

    val defaultDark = AppColorScheme(
        id = AppThemeId.DEFAULT_DARK,
        isDark = true,
        background = Color(0xFF08060E),
        surface = Color(0xFF120D1F),
        surfaceVariant = Color(0xFF1A132C),
        card = Color(0xFF150F26),
        cardHigh = Color(0xFF20173B),
        floatingDock = Color(0xEB0E091B),
        accent = Color(0xFF8B5CF6),
        accentSecondary = Color(0xFFA855F7),
        accentLight = Color(0xFFDDD6FE),
        accentDark = Color(0xFF6D28D9),
        accentGlow = Color(0x4D8B5CF6),
        onAccent = Color(0xFFFFFFFF),
        glassFillSubtle = Color(0x0AFFFFFF),
        glassFillLight = Color(0x14FFFFFF),
        glassFillMedium = Color(0x1FFFFFFF),
        glassBorderSubtle = Color(0x1FDDD6FE),
        glassBorderBright = Color(0x59A855F7),
        textPrimary = Color(0xFFFAF5FF),
        textSecondary = Color(0xFFC4B5FD),
        textTertiary = Color(0xFF8B7AA8),
        textMuted = Color(0xFF5B4E75),
        backgroundImageRes = null
    )

    val defaultLight = AppColorScheme(
        id = AppThemeId.DEFAULT_LIGHT,
        isDark = false,
        background = Color(0xFFF9F7FD),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1EDFB),
        card = Color(0xFFFFFFFF),
        cardHigh = Color(0xFFF6F3FD),
        floatingDock = Color(0xF0FFFFFF),
        accent = Color(0xFF7C3AED),
        accentSecondary = Color(0xFF8B5CF6),
        accentLight = Color(0xFFC4B5FD),
        accentDark = Color(0xFF5B21B6),
        accentGlow = Color(0x337C3AED),
        onAccent = Color(0xFFFFFFFF),
        glassFillSubtle = Color(0x08000000),
        glassFillLight = Color(0x0F000000),
        glassFillMedium = Color(0x17000000),
        glassBorderSubtle = Color(0x147C3AED),
        glassBorderBright = Color(0x2E7C3AED),
        textPrimary = Color(0xFF1E1035),
        textSecondary = Color(0xFF5B4E75),
        textTertiary = Color(0xFF7C6F9B),
        textMuted = Color(0xFFA59AB8),
        backgroundImageRes = null
    )
}
