package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerColorPaletteTest {

    @Test
    fun defaultPalette_hasConsistentFallbackValues() {
        val defaultPalette = PlayerColorPalette.Default

        assertNotNull(defaultPalette.dominant)
        assertNotNull(defaultPalette.vibrant)
        assertNotNull(defaultPalette.darkVibrant)
        assertNotNull(defaultPalette.lightVibrant)
        assertNotNull(defaultPalette.muted)
        assertNotNull(defaultPalette.accent)

        assertEquals(AuroraDeepIndigo, defaultPalette.dominant)
        assertEquals(ToscaPrimary, defaultPalette.vibrant)
        assertEquals(AuroraDeepIndigo, defaultPalette.darkVibrant)
        assertEquals(AuroraCyan, defaultPalette.lightVibrant)
        assertEquals(ToscaPrimary, defaultPalette.accent)
    }

    @Test
    fun customPalette_assignsColorsAccurately() {
        val custom = PlayerColorPalette(
            dominant = Color(0xFF112233),
            vibrant = Color(0xFF445566),
            darkVibrant = Color(0xFF001122),
            lightVibrant = Color(0xFF778899),
            muted = Color(0xFFAABBCC),
            accent = Color(0xFFFFCC00)
        )

        assertEquals(Color(0xFF112233), custom.dominant)
        assertEquals(Color(0xFF445566), custom.vibrant)
        assertEquals(Color(0xFF001122), custom.darkVibrant)
        assertEquals(Color(0xFF778899), custom.lightVibrant)
        assertEquals(Color(0xFFAABBCC), custom.muted)
        assertEquals(Color(0xFFFFCC00), custom.accent)
    }

    @Test
    fun ensureAccessibleAccent_lowSaturation_returnsFallback() {
        val grey = Color(0xFF404040)
        val fallback = Color(0xFF00E5FF)
        val result = PaletteExtractor.ensureAccessibleAccent(grey, fallback)
        assertEquals(fallback, result)
    }

    @Test
    fun ensureAccessibleAccent_lowLuminance_boostsLuminance() {
        val darkTeal = Color(0xFF004D40)
        val fallback = Color(0xFF00E5FF)
        val result = PaletteExtractor.ensureAccessibleAccent(darkTeal, fallback)
        val max = maxOf(result.red, result.green, result.blue)
        val min = minOf(result.red, result.green, result.blue)
        val luminance = (max + min) / 2f
        assertTrue(luminance >= 0.50f)
    }

    @Test
    fun ensureAccessibleAccent_sufficientLuminanceAndSaturation_returnsOriginal() {
        val brightTeal = Color(0xFF00E5FF)
        val fallback = Color(0xFFFF0055)
        val result = PaletteExtractor.ensureAccessibleAccent(brightTeal, fallback)
        assertEquals(brightTeal, result)
    }
}
