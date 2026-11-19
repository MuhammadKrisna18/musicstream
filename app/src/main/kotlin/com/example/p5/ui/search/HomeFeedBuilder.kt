package com.example.p5.ui.search

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Song

object HomeFeedBuilder {

    fun combineHomeSections(
        topPlayed: List<Song>,
        artistSection: HomeSection?,
        rawSections: List<HomeSection>,
        selectedGenre: Category?,
        genreSections: List<HomeSection>
    ): List<HomeSection> {
        if (selectedGenre != null) {
            return genreSections
        }

        val personalized = mutableListOf<HomeSection>()

        if (topPlayed.isNotEmpty()) {
            personalized.add(
                HomeSection(
                    id = "personalized_on_repeat",
                    title = "Sering Kamu Putar",
                    subtitle = "Lagu yang paling sering kamu dengarkan belakangan ini",
                    query = "",
                    tracks = topPlayed
                )
            )
        }

        if (artistSection != null && artistSection.tracks.isNotEmpty()) {
            personalized.add(artistSection)
        }

        return personalized + rawSections
    }

    fun buildArtistRecommendationSection(artist: String, songs: List<Song>): HomeSection? {
        if (songs.isEmpty()) return null
        return HomeSection(
            id = "recommended_${artist.hashCode()}",
            title = "Spesial Untukmu: $artist",
            subtitle = "Koleksi lagu pilihan berdasarkan artis favoritmu",
            query = "$artist hits",
            tracks = songs.take(12)
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
                tracks = songs
            )
        )
    }
}
