package com.example.p5.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDynamicPlayerPalette(artworkUrl: String?): PlayerColorPalette {
    val context = LocalContext.current
    var targetPalette by remember(artworkUrl) { mutableStateOf(PlayerColorPalette.Default) }

    LaunchedEffect(artworkUrl) {
        if (artworkUrl.isNullOrBlank()) {
            targetPalette = PlayerColorPalette.Default
        } else {
            targetPalette = PaletteExtractor.extractFromUrl(context, artworkUrl)
        }
    }

    val animationSpec = tween<Color>(durationMillis = 650, easing = FastOutSlowInEasing)

    val dominant by animateColorAsState(targetPalette.dominant, animationSpec, label = "palette_dominant")
    val vibrant by animateColorAsState(targetPalette.vibrant, animationSpec, label = "palette_vibrant")
    val darkVibrant by animateColorAsState(targetPalette.darkVibrant, animationSpec, label = "palette_dark_vibrant")
    val lightVibrant by animateColorAsState(targetPalette.lightVibrant, animationSpec, label = "palette_light_vibrant")
    val muted by animateColorAsState(targetPalette.muted, animationSpec, label = "palette_muted")
    val accent by animateColorAsState(targetPalette.accent, animationSpec, label = "palette_accent")

    return PlayerColorPalette(
        dominant = dominant,
        vibrant = vibrant,
        darkVibrant = darkVibrant,
        lightVibrant = lightVibrant,
        muted = muted,
        accent = accent
    )
}
