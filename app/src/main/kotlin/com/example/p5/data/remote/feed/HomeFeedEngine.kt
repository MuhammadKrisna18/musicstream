package com.example.p5.data.remote.feed

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.deezer.DeezerDataSource
import com.example.p5.data.remote.innertube.MusicFilterHelper
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFeedEngine(
    private val youTubeDataSource: YouTubeNativeDataSource,
    private val deezerDataSource: DeezerDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var cachedFeed: HomeFeed? = null

    suspend fun generateHomeFeed(refresh: Boolean = false): HomeFeed = withContext(ioDispatcher) {
        if (!refresh && cachedFeed != null) {
            return@withContext cachedFeed!!
        }

        val feed = try {
            buildDynamicFeed(refresh)
        } catch (_: Exception) {
            cachedFeed ?: youTubeDataSource.getHomeFeed(refresh = false)
        }

        cachedFeed = feed
        feed
    }

    private suspend fun buildDynamicFeed(forceRefresh: Boolean): HomeFeed = coroutineScope {
        val deezerDeferred = async {
            runCatching { deezerDataSource.getChart(forceRefresh) }.getOrNull()
        }
        val deezerGenresDeferred = async {
            runCatching { deezerDataSource.getGenres(forceRefresh) }.getOrNull()
        }

        val chosenChart = DynamicMusicCatalog.chartSeeds.shuffled().first()
        val chosenQuickPicks = DynamicMusicCatalog.quickPicksSeeds.shuffled().first()
        val chosenThematics = DynamicMusicCatalog.thematicSeeds.shuffled().take(3)

        val targetSeeds = mutableListOf<SeedSection>()
        targetSeeds.add(chosenChart)
        targetSeeds.add(chosenQuickPicks)
        targetSeeds.addAll(chosenThematics)

        val deezerData = deezerDeferred.await()

        val deezerArtist = deezerData?.artists?.firstOrNull { it.name.isNotBlank() }
        if (deezerArtist != null) {
            targetSeeds.add(
                SeedSection(
                    id = "deezer_artist_${deezerArtist.id}",
                    title = "Trending: ${deezerArtist.name}",
                    subtitle = "Lagu pilihan dari musisi yang merajai tangga musik dunia",
                    query = "${deezerArtist.name} official hits",
                    style = HomeSectionStyle.STANDARD_CARDS
                )
            )
        }

        val sectionTasks = targetSeeds.map { seed ->
            async {
                val songs = try {
                    val raw = youTubeDataSource.search(seed.query, limit = 16)
                    val clean = raw.filter { MusicFilterHelper.isValidMusicTrack(it) }
                    MusicFilterHelper.deduplicateSongs(clean).take(12)
                } catch (_: Exception) {
                    emptyList()
                }

                if (songs.isNotEmpty()) {
                    HomeSection(
                        id = seed.id,
                        title = seed.title,
                        subtitle = seed.subtitle,
                        query = seed.query,
                        tracks = songs,
                        style = seed.style
                    )
                } else {
                    null
                }
            }
        }

        val builtSections = sectionTasks.mapNotNull { it.await() }

        val genuinePlaylists = youTubeDataSource.getHomeFeed(refresh = false).playlists
            .filter { !MusicFilterHelper.isExcludedContent(it.title) }
            .take(10)

        val deezerGenres = deezerGenresDeferred.await()
        val dynamicGenres = mutableListOf<Category>()
        val localGems = listOf(
            Category(
                id = "local_genre_dangdut",
                title = "Dangdut Koplo",
                query = "Dangdut Koplo Terbaru",
                icon = "whatshot",
                startColor = "#7B3EDB",
                endColor = "#004D40"
            ),
            Category(
                id = "local_genre_pop_indo",
                title = "Pop Indonesia",
                query = "Lagu Pop Indonesia Terbaru",
                icon = "music_note",
                startColor = "#00B0FF",
                endColor = "#0D47A1"
            ),
            Category(
                id = "local_genre_indie",
                title = "Indie Indonesia",
                query = "Lagu Indie Indonesia Terbaik",
                icon = "headphones",
                startColor = "#7C4DFF",
                endColor = "#1A237E"
            )
        )
        dynamicGenres.addAll(localGems)

        if (!deezerGenres.isNullOrEmpty()) {
            deezerGenres.forEach { dg ->
                dynamicGenres.add(
                    Category(
                        id = "deezer_genre_${dg.id}",
                        title = dg.name,
                        query = "${dg.name} music hits",
                        icon = mapGenreToIcon(dg.name),
                        startColor = mapGenreToColor(dg.name),
                        imageUrl = dg.pictureUrl
                    )
                )
            }
        }

        val baseCategories = youTubeDataSource.getCategories()
        val finalCategories = if (dynamicGenres.size > localGems.size) {
            dynamicGenres
        } else if (baseCategories.categories.isNotEmpty()) {
            baseCategories.categories
        } else {
            dynamicGenres
        }

        HomeFeed(
            sections = builtSections,
            moodTags = emptyList(),
            categories = finalCategories,
            playlists = genuinePlaylists
        )
    }

    private fun mapGenreToIcon(genreName: String): String {
        val lower = genreName.lowercase(Locale.ROOT)
        return when {
            lower.contains("pop") -> "music_note"
            lower.contains("rock") || lower.contains("metal") -> "electric_bolt"
            lower.contains("jazz") || lower.contains("blues") -> "nightlife"
            lower.contains("rap") || lower.contains("hip") -> "bolt"
            lower.contains("dance") || lower.contains("electro") -> "bolt"
            lower.contains("r&b") || lower.contains("soul") -> "nightlife"
            lower.contains("folk") || lower.contains("acoustic") -> "library_music"
            lower.contains("classical") -> "graphic_eq"
            else -> "music_note"
        }
    }

    private fun mapGenreToColor(genreName: String): String {
        val lower = genreName.lowercase(Locale.ROOT)
        return when {
            lower.contains("pop") -> "#00B0FF"
            lower.contains("rock") -> "#00E5BF"
            lower.contains("metal") -> "#8C8C8C"
            lower.contains("jazz") -> "#A4C5FF"
            lower.contains("rap") || lower.contains("hip") -> "#E24B00"
            lower.contains("dance") || lower.contains("electro") -> "#FFD600"
            lower.contains("r&b") || lower.contains("soul") -> "#B47BFF"
            lower.contains("folk") || lower.contains("acoustic") -> "#00A513"
            lower.contains("classical") -> "#E0E0E0"
            lower.contains("blues") -> "#448AFF"
            lower.contains("reggae") -> "#76FF03"
            lower.contains("alternative") || lower.contains("punk") -> "#FF6E40"
            else -> "#00E5BF"
        }
    }
}
