package com.example.p5.ui.search

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.Song

object HomeFeedBuilder {

    fun combineHomeSections(
        artistSections: List<HomeSection>,
        rawSections: List<HomeSection>,
        selectedGenre: Category?,
        genreSections: List<HomeSection>
    ): List<HomeSection> {
        if (selectedGenre != null) {
            return genreSections
        }

        val validArtists = artistSections.filter { it.tracks.isNotEmpty() }
        val validRaw = rawSections.filter { it.tracks.isNotEmpty() }
        return validRaw + validArtists
    }

    fun curateHeroSpotlightTracks(
        sections: List<HomeSection>,
        maxCount: Int = 5
    ): List<Song> {
        if (sections.isEmpty() || maxCount <= 0) return emptyList()

        val spotlight = mutableListOf<Song>()
        val seenIds = mutableSetOf<String>()
        val seenArtists = mutableSetOf<String>()

        val candidateSections = sections.filter {
            !it.id.startsWith("personalized_") && it.tracks.isNotEmpty()
        }.ifEmpty { sections.filter { it.tracks.isNotEmpty() } }

        for (section in candidateSections) {
            if (spotlight.size >= maxCount) break
            val topSong = section.tracks.firstOrNull { song ->
                seenIds.add(song.id) && seenArtists.add(song.artist.lowercase())
            } ?: section.tracks.firstOrNull { seenIds.add(it.id) }

            if (topSong != null) {
                spotlight.add(topSong)
            }
        }

        if (spotlight.size < maxCount) {
            for (section in candidateSections) {
                for (song in section.tracks) {
                    if (spotlight.size >= maxCount) break
                    if (seenIds.add(song.id)) {
                        spotlight.add(song)
                    }
                }
            }
        }

        return spotlight
    }

    fun buildArtistRecommendationSection(artist: String, songs: List<Song>): HomeSection? {
        if (songs.isEmpty()) return null
        return HomeSection(
            id = "recommended_${artist.hashCode()}",
            title = "Spesial Untukmu: $artist",
            subtitle = "Koleksi lagu pilihan berdasarkan artis favoritmu",
            query = "$artist hits",
            tracks = songs.take(12),
            style = HomeSectionStyle.STANDARD_CARDS
        )
    }

    fun buildTrendingFallbackSections(songs: List<Song>): List<HomeSection> {
        if (songs.isEmpty()) return emptyList()
        return listOf(
            HomeSection(
                id = "trending",
                title = "Trending Now",
                subtitle = "Lagu paling populer dan viral saat ini",
                query = "trending music hits 2026",
                tracks = songs,
                style = HomeSectionStyle.RANKED_CHARTS
            )
        )
    }
}
