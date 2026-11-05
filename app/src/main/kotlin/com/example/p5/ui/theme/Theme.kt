package com.example.p5.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ToscaPrimary,
    onPrimary = BlackBackground,
    primaryContainer = ToscaContainer,
    onPrimaryContainer = ToscaLight,
    secondary = ToscaLight,
    onSecondary = BlackBackground,
    background = BlackBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor
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
