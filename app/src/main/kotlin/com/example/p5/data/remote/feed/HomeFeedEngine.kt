package com.example.p5.data.remote.feed

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.deezer.DeezerDataSource
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
                    youTubeDataSource.search(seed.query, limit = 12)
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

        val dynamicPlaylists = mutableListOf<Playlist>()
        if (deezerData != null && deezerData.playlists.isNotEmpty()) {
            deezerData.playlists.take(8).forEach { dp ->
                dynamicPlaylists.add(
                    Playlist(
                        id = "deezer_pl_${dp.id}",
                        title = dp.title,
                        subtitle = "${dp.trackCount} Lagu - Deezer Curated",
                        thumbnailUrl = dp.pictureUrl,
                        type = PlaylistType.GENRE,
                        query = "${dp.title} playlist official"
                    )
                )
            }
        }

        val baseCategories = youTubeDataSource.getCategories()
        val finalPlaylists = if (dynamicPlaylists.isNotEmpty()) {
            dynamicPlaylists
        } else {
            youTubeDataSource.getHomeFeed(refresh = false).playlists
        }

        HomeFeed(
            sections = builtSections,
            moodTags = baseCategories.moodTags,
            categories = baseCategories.categories,
            playlists = finalPlaylists
        )
    }
}
