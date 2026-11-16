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

        val accent = when {
            palette.vibrantSwatch != null -> vibrant
            palette.lightVibrantSwatch != null -> lightVibrant
            palette.dominantSwatch != null -> dominant
            else -> defaultVibrant
        }

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
