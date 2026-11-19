package com.example.p5.ui.search

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.Song

object HomeFeedBuilder {

    fun combineHomeSections(
        recentlyPlayed: List<Song>,
        topPlayed: List<Song>,
        artistSections: List<HomeSection>,
        rawSections: List<HomeSection>,
        selectedGenre: Category?,
        genreSections: List<HomeSection>
    ): List<HomeSection> {
        if (selectedGenre != null) {
            return genreSections
        }

        val result = mutableListOf<HomeSection>()

        if (recentlyPlayed.isNotEmpty()) {
            result.add(
                HomeSection(
                    id = "personalized_quick_picks",
                    title = "Pilihan Cepat Untukmu",
                    subtitle = "Lanjutkan mendengarkan lagu yang baru kamu putar",
                    query = "",
                    tracks = recentlyPlayed,
                    style = HomeSectionStyle.QUICK_PICKS_GRID
                )
            )
        }

        val firstChartSection = rawSections.firstOrNull { it.style == HomeSectionStyle.RANKED_CHARTS }
        if (firstChartSection != null) {
            result.add(firstChartSection)
        }

        if (topPlayed.isNotEmpty()) {
            val topStyle = if (recentlyPlayed.isEmpty()) {
                HomeSectionStyle.QUICK_PICKS_GRID
            } else {
                HomeSectionStyle.STANDARD_CARDS
            }
            result.add(
                HomeSection(
                    id = "personalized_on_repeat",
                    title = "Sering Kamu Putar",
                    subtitle = "Lagu yang paling sering kamu dengarkan belakangan ini",
                    query = "",
                    tracks = topPlayed,
                    style = topStyle
                )
            )
        }

        val remainingRaw = rawSections.filter { it.id != firstChartSection?.id }
        val validArtistSections = artistSections.filter { it.tracks.isNotEmpty() }

        val maxLoops = maxOf(remainingRaw.size, validArtistSections.size)
        for (i in 0 until maxLoops) {
            if (i < validArtistSections.size) {
                result.add(validArtistSections[i])
            }
            if (i < remainingRaw.size) {
                result.add(remainingRaw[i])
            }
        }

        return result
    }

    fun curateHeroSpotlightTracks(
        sections: List<HomeSection>,
        maxCount: Int = 5
    ): List<Song> {
        if (sections.isEmpty() || maxCount <= 0) return emptyList()

        val spotlight = mutableListOf<Song>()
        val seenIds = mutableSetOf<String>()

        val nonHistorySections = sections.filter {
            it.id != "personalized_quick_picks" &&
                it.id != "personalized_on_repeat" &&
                it.tracks.isNotEmpty()
        }
        val sourceSections = if (nonHistorySections.isNotEmpty()) nonHistorySections else sections.filter { it.tracks.isNotEmpty() }

        var trackDepth = 0
        while (spotlight.size < maxCount && trackDepth < 5) {
            var addedInPass = false
            for (section in sourceSections) {
                if (spotlight.size >= maxCount) break
                val candidate = section.tracks.getOrNull(trackDepth) ?: continue
                if (seenIds.add(candidate.id)) {
                    spotlight.add(candidate)
                    addedInPass = true
                }
            }
            if (!addedInPass) break
            trackDepth++
        }

        if (spotlight.size < maxCount) {
            for (section in sections) {
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
