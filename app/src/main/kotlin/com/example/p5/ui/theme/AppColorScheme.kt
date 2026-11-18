package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId

data class AppColorScheme(
    val id: AppThemeId,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val card: Color,
    val cardHigh: Color,
    val floatingDock: Color,
    val accent: Color,
    val accentSecondary: Color,
    val accentLight: Color,
    val accentDark: Color,
    val accentGlow: Color,
    val glassFillSubtle: Color,
    val glassFillLight: Color,
    val glassFillMedium: Color,
    val glassBorderSubtle: Color,
    val glassBorderBright: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val backgroundImageRes: Int? = null
)
