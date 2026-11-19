package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId

object DefaultThemePalettes {

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
}
