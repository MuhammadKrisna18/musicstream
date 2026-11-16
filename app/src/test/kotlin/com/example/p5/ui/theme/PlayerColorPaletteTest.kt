package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
}
