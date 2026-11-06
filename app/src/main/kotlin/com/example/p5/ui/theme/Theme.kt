package com.example.p5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ToscaPrimary,
    onPrimary = ObsidianBlack,
    primaryContainer = ToscaContainer,
    onPrimaryContainer = ToscaLight,
    secondary = ToscaLight,
    onSecondary = ObsidianBlack,
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderSubtle
)

@Composable
fun P5AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
