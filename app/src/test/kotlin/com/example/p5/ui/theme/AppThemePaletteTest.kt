package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.p5.domain.model.AppThemeId
import com.example.p5.domain.model.ThemeCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppThemePaletteTest {

    @Test
    fun allThemeIds_haveValidPalettes() {
        val allThemes = AppThemeId.entries
        assertEquals(15, allThemes.size)

        for (theme in allThemes) {
            val palette = AppThemePalette.getPalette(theme)
            assertNotNull(palette)
            assertEquals(theme, palette.id)
            assertEquals(theme.isDark, palette.isDark)
            assertTrue(palette.background != Color.Unspecified)
            assertTrue(palette.surface != Color.Unspecified)
            assertTrue(palette.accent != Color.Unspecified)
            assertTrue(palette.textPrimary != Color.Unspecified)
        }
    }

    @Test
    fun defaultThemes_matchSpecification() {
        val defaultDark = AppThemePalette.getPalette(AppThemeId.DEFAULT_DARK)
        assertEquals(ThemeCategory.DEFAULT, defaultDark.id.category)
        assertTrue(defaultDark.isDark)
        assertEquals(Color(0xFF1DB954), defaultDark.accent)

        val defaultLight = AppThemePalette.getPalette(AppThemeId.DEFAULT_LIGHT)
        assertEquals(ThemeCategory.DEFAULT, defaultLight.id.category)
        assertFalse(defaultLight.isDark)
        assertEquals(Color(0xFF059669), defaultLight.accent)
    }

    @Test
    fun animeThemes_countAndCategoryMatch() {
        val animeThemes = AppThemeId.entries.filter { it.category == ThemeCategory.ANIME }
        assertEquals(5, animeThemes.size)

        val expectedIds = listOf(
            AppThemeId.ANIME_CYBERPUNK,
            AppThemeId.ANIME_INFINITY_BLUE,
            AppThemeId.ANIME_CRIMSON_FLAME,
            AppThemeId.ANIME_SAKURA_TWILIGHT,
            AppThemeId.ANIME_EVA_UNIT01
        )
        assertEquals(expectedIds, animeThemes)

        for (theme in animeThemes) {
            val palette = AppThemePalette.getPalette(theme)
            assertNotNull(palette.backgroundImageRes)
        }

        val backgroundResIds = animeThemes.map { AppThemePalette.getPalette(it).backgroundImageRes }
        assertEquals(5, backgroundResIds.toSet().size)
    }

    @Test
    fun minimalistThemes_countAndCategoryMatch() {
        val minimalThemes = AppThemeId.entries.filter { it.category == ThemeCategory.MINIMALIST }
        assertEquals(8, minimalThemes.size)

        val expectedIds = listOf(
            AppThemeId.MINIMAL_OLED,
            AppThemeId.MINIMAL_NORD,
            AppThemeId.MINIMAL_TOKYO,
            AppThemeId.MINIMAL_DRACULA,
            AppThemeId.MINIMAL_EMERALD,
            AppThemeId.MINIMAL_MOCHA,
            AppThemeId.MINIMAL_ROSE,
            AppThemeId.MINIMAL_PAPER
        )
        assertEquals(expectedIds, minimalThemes)

        for (theme in minimalThemes) {
            val palette = AppThemePalette.getPalette(theme)
            assertEquals(null, palette.backgroundImageRes)
        }
    }
}
