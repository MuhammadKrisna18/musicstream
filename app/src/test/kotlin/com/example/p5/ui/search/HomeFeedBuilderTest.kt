package com.example.p5.ui.search

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeFeedBuilderTest {

    private fun createSong(id: String, artist: String): Song {
        return Song(
            id = id,
            title = "Title $id",
            artist = artist,
            durationSeconds = 180,
            durationFormatted = "3:00",
            thumbnailUrl = "thumb",
            webUrl = "web"
        )
    }

    private fun createSection(id: String, songs: List<Song>): HomeSection {
        return HomeSection(
            id = id,
            title = "Section $id",
            subtitle = "Subtitle",
            query = "query",
            tracks = songs,
            style = HomeSectionStyle.STANDARD_CARDS
        )
    }

    @Test
    fun combineHomeSections_combinesAllSectionsWithoutStaticHistory() {
        val artistSec = createSection("artist_1", listOf(createSong("1", "Artist A")))
        val rawSec1 = createSection("raw_1", listOf(createSong("2", "Artist B")))
        val rawSec2 = createSection("raw_2", listOf(createSong("3", "Artist C")))

        val result = HomeFeedBuilder.combineHomeSections(
            artistSections = listOf(artistSec),
            rawSections = listOf(rawSec1, rawSec2),
            selectedGenre = null,
            genreSections = emptyList()
        )

        assertEquals(3, result.size)
        assertTrue(result.any { it.id == "artist_1" })
        assertTrue(result.any { it.id == "raw_1" })
        assertTrue(result.any { it.id == "raw_2" })
    }

    @Test
    fun combineHomeSections_whenGenreSelected_returnsGenreSectionsOnly() {
        val genre = Category("jazz", "Jazz", "Jazz hits")
        val genreSec = createSection("genre_jazz", listOf(createSong("g1", "Jazz Artist")))
        val rawSec = createSection("raw", listOf(createSong("r1", "Pop Artist")))

        val result = HomeFeedBuilder.combineHomeSections(
            artistSections = emptyList(),
            rawSections = listOf(rawSec),
            selectedGenre = genre,
            genreSections = listOf(genreSec)
        )

        assertEquals(1, result.size)
        assertEquals("genre_jazz", result[0].id)
    }

    @Test
    fun curateHeroSpotlightTracks_extractsDiverseTracks() {
        val sec1 = createSection("sec_1", listOf(createSong("1", "Artist A"), createSong("2", "Artist A")))
        val sec2 = createSection("sec_2", listOf(createSong("3", "Artist B"), createSong("4", "Artist C")))

        val spotlight = HomeFeedBuilder.curateHeroSpotlightTracks(listOf(sec1, sec2), maxCount = 3)

        assertEquals(3, spotlight.size)
        val artists = spotlight.map { it.artist }.distinct()
        assertTrue(artists.size >= 2)
    }
}
