package com.example.p5.ui.theme

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.LruCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PaletteExtractor {
    private val cache = LruCache<String, PlayerColorPalette>(60)

    fun ensureAccessibleAccent(color: Color, fallback: Color = PlayerColorPalette.Default.vibrant): Color {
        val r = color.red
        val g = color.green
        val b = color.blue
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min
        val l = (max + min) / 2f
        val s = if (delta == 0f) 0f else delta / (1f - kotlin.math.abs(2f * l - 1f))

        return when {
            s < 0.12f -> fallback
            l < 0.45f -> {
                val boost = 0.58f / maxOf(l, 0.01f)
                Color(
                    red = (r * boost).coerceIn(0f, 1f),
                    green = (g * boost).coerceIn(0f, 1f),
                    blue = (b * boost).coerceIn(0f, 1f),
                    alpha = color.alpha
                )
            }
            else -> color
        }
    }

    suspend fun extractFromUrl(context: Context, url: String?): PlayerColorPalette = withContext(Dispatchers.IO) {
        if (url.isNullOrBlank()) {
            return@withContext PlayerColorPalette.Default
        }

        cache.get(url)?.let { return@withContext it }

        val request = ImageRequest.Builder(context)
            .data(url)
            .allowHardware(false)
            .size(128, 128)
            .build()

        val drawable = (context.imageLoader.execute(request) as? SuccessResult)?.drawable
        val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return@withContext PlayerColorPalette.Default

        val palette = Palette.from(bitmap)
            .maximumColorCount(16)
            .generate()

        val defaultDominant = PlayerColorPalette.Default.dominant
        val defaultVibrant = PlayerColorPalette.Default.vibrant
        val defaultDarkVibrant = PlayerColorPalette.Default.darkVibrant
        val defaultLightVibrant = PlayerColorPalette.Default.lightVibrant
        val defaultMuted = PlayerColorPalette.Default.muted

        val dominantInt = palette.getDominantColor(defaultDominant.toArgb())
        val vibrantInt = palette.getVibrantColor(defaultVibrant.toArgb())
        val darkVibrantInt = palette.getDarkVibrantColor(defaultDarkVibrant.toArgb())
        val lightVibrantInt = palette.getLightVibrantColor(defaultLightVibrant.toArgb())
        val mutedInt = palette.getMutedColor(defaultMuted.toArgb())

        val dominant = Color(dominantInt)
        val vibrant = Color(vibrantInt)
        val darkVibrant = Color(darkVibrantInt)
        val lightVibrant = Color(lightVibrantInt)
        val muted = Color(mutedInt)

        val rawAccent = when {
            palette.vibrantSwatch != null -> vibrant
            palette.lightVibrantSwatch != null -> lightVibrant
            palette.dominantSwatch != null -> dominant
            else -> defaultVibrant
        }

        val accent = ensureAccessibleAccent(rawAccent, defaultVibrant)

        val result = PlayerColorPalette(
            dominant = dominant,
            vibrant = vibrant,
            darkVibrant = darkVibrant,
            lightVibrant = lightVibrant,
            muted = muted,
            accent = accent
        )

        cache.put(url, result)
        result
    }
}
