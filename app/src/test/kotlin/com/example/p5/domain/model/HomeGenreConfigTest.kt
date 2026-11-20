package com.example.p5.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeGenreConfigTest {

    @Test
    fun allGenres_containsExactlyConfiguredEightGenres() {
        val expectedTitles = listOf(
            "Pop",
            "Indie Pop",
            "Koplo",
            "Dangdut",
            "R&B",
            "Jazz",
            "Acoustic",
            "Keroncong"
        )
        val actualTitles = HomeGenreConfig.ALL.map { it.title }
        assertEquals(expectedTitles, actualTitles)
        assertEquals(8, HomeGenreConfig.ALL.size)
        assertEquals(8, HomeGenreConfig.CATEGORIES.size)
    }

    @Test
    fun toCategory_correctlyMapsFields() {
        val genre = HomeGenreConfig.KOPLO
        val category = genre.toCategory()

        assertEquals("koplo", category.id)
        assertEquals("Koplo", category.title)
        assertEquals(genre.query, category.query)
        assertEquals(genre.startColor, category.startColor)
        assertEquals(genre.endColor, category.endColor)
    }

    @Test
    fun isAllowedGenre_returnsTrueForConfiguredGenres() {
        assertTrue(HomeGenreConfig.isAllowedGenre("pop"))
        assertTrue(HomeGenreConfig.isAllowedGenre("Pop"))
        assertTrue(HomeGenreConfig.isAllowedGenre("Indie Pop"))
        assertTrue(HomeGenreConfig.isAllowedGenre("indie_pop"))
        assertTrue(HomeGenreConfig.isAllowedGenre("koplo"))
        assertTrue(HomeGenreConfig.isAllowedGenre("Dangdut"))
        assertTrue(HomeGenreConfig.isAllowedGenre("rnb"))
        assertTrue(HomeGenreConfig.isAllowedGenre("R&B"))
        assertTrue(HomeGenreConfig.isAllowedGenre("Jazz"))
        assertTrue(HomeGenreConfig.isAllowedGenre("acoustic"))
        assertTrue(HomeGenreConfig.isAllowedGenre("Keroncong"))
    }

    @Test
    fun isAllowedGenre_returnsFalseForUnconfiguredGenres() {
        assertFalse(HomeGenreConfig.isAllowedGenre("Metal"))
        assertFalse(HomeGenreConfig.isAllowedGenre("Rock"))
        assertFalse(HomeGenreConfig.isAllowedGenre("Anime"))
        assertFalse(HomeGenreConfig.isAllowedGenre("Podcast"))
        assertFalse(HomeGenreConfig.isAllowedGenre(""))
    }

    @Test
    fun lookupHelpers_findCorrectGenre() {
        assertNotNull(HomeGenreConfig.findById("koplo"))
        assertEquals("Koplo", HomeGenreConfig.findById("koplo")?.title)

        assertNotNull(HomeGenreConfig.findByTitle("Keroncong"))
        assertEquals("keroncong", HomeGenreConfig.findByTitle("Keroncong")?.id)

        assertNull(HomeGenreConfig.findById("non_existent"))
        assertNull(HomeGenreConfig.findByTitle("non_existent"))
    }
}
