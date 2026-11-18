package com.example.p5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId

val LocalAppColors = staticCompositionLocalOf { AppThemePalette.defaultDark }

object AppTheme {
    val colors: AppColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

@Composable
fun P5AppTheme(
    themeId: AppThemeId = AppThemeId.DEFAULT_DARK,
    content: @Composable () -> Unit
) {
    val colors = AppThemePalette.getPalette(themeId)

    val colorScheme = if (colors.isDark) {
        darkColorScheme(
            primary = colors.accent,
            onPrimary = colors.background,
            primaryContainer = colors.surfaceVariant,
            onPrimaryContainer = colors.accentLight,
            secondary = colors.accentSecondary,
            onSecondary = colors.background,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.glassBorderSubtle
        )
    } else {
        lightColorScheme(
            primary = colors.accent,
            onPrimary = Color.White,
            primaryContainer = colors.surfaceVariant,
            onPrimaryContainer = colors.accentDark,
            secondary = colors.accentSecondary,
            onSecondary = Color.White,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.surface,
            onSurface = colors.textPrimary,
            surfaceVariant = colors.surfaceVariant,
            onSurfaceVariant = colors.textSecondary,
            outline = colors.glassBorderSubtle
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
