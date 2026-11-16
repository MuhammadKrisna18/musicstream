package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color

data class PlayerColorPalette(
    val dominant: Color,
    val vibrant: Color,
    val darkVibrant: Color,
    val lightVibrant: Color,
    val muted: Color,
    val accent: Color
) {
    companion object {
        val Default = PlayerColorPalette(
            dominant = AuroraDeepIndigo,
            vibrant = ToscaPrimary,
            darkVibrant = AuroraDeepIndigo,
            lightVibrant = AuroraCyan,
            muted = Color(0xFF1E1338),
            accent = ToscaPrimary
        )
    }
}
