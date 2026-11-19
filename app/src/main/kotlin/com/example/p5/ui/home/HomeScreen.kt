package com.example.p5.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.R
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.common.LocalNavAnimatedVisibilityScope
import com.example.p5.ui.common.LocalSharedTransitionScope
import com.example.p5.ui.search.SearchViewModel
import com.example.p5.ui.search.components.EditorialHeroCarousel
import com.example.p5.ui.search.components.GenreFilterRow
import com.example.p5.ui.search.components.HomeTrackSection
import com.example.p5.ui.search.components.PlaylistDetailContent
import com.example.p5.ui.search.components.PlaylistRowSection
import com.example.p5.ui.search.components.QuickPicksGridSection
import com.example.p5.ui.search.components.SearchEmptyState
import com.example.p5.ui.search.components.SearchTopHeader
import com.example.p5.ui.search.components.SectionDetailContent
import com.example.p5.ui.search.components.rememberShimmerBrush
import com.example.p5.ui.search.components.searchShimmerSkeleton
import com.example.p5.ui.theme.bottomScrollPaddingWithInsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    searchViewModel: SearchViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {}
) {
    val homeSections by searchViewModel.homeSections.collectAsStateWithLifecycle()
    val heroSpotlightTracks by searchViewModel.heroSpotlightTracks.collectAsStateWithLifecycle()
    val categories by searchViewModel.categories.collectAsStateWithLifecycle()
    val selectedGenre by searchViewModel.selectedGenre.collectAsStateWithLifecycle()
    val selectedSection by searchViewModel.selectedSection.collectAsStateWithLifecycle()
    val curatedPlaylists by searchViewModel.curatedPlaylists.collectAsStateWithLifecycle()
    val selectedPlaylist by searchViewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val selectedPlaylistTracks by searchViewModel.selectedPlaylistTracks.collectAsStateWithLifecycle()
    val isPlaylistLoading by searchViewModel.isPlaylistLoading.collectAsStateWithLifecycle()
    val isGenreLoading by searchViewModel.isGenreLoading.collectAsStateWithLifecycle()
    val isHomeLoading by searchViewModel.isHomeLoading.collectAsStateWithLifecycle()
    val isRefreshing by searchViewModel.isRefreshing.collectAsStateWithLifecycle()
    val shimmerBrush = rememberShimmerBrush()

    BackHandler(enabled = selectedSection != null || selectedPlaylist != null) {
        if (selectedPlaylist != null) {
            searchViewModel.closePlaylistDetail()
        } else {
            searchViewModel.closeSectionDetail()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        SharedTransitionLayout {
            CompositionLocalProvider(LocalSharedTransitionScope provides this) {
                AnimatedContent(
                    targetState = Pair(selectedSection, selectedPlaylist),
                    transitionSpec = {
                        fadeIn(tween(350)) togetherWith fadeOut(tween(350))
                    },
                    label = "home_section_or_playlist_detail",
                    modifier = Modifier.fillMaxSize()
                ) { (currentSection, currentPlaylist) ->
                    CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                        if (currentPlaylist != null) {
                PlaylistDetailContent(
                    playlist = currentPlaylist,
                    tracks = selectedPlaylistTracks,
                    isLoading = isPlaylistLoading,
                    playerState = playerState,
                    onBackClick = searchViewModel::closePlaylistDetail,
                    onPlayQueue = onPlayQueue,
                    onMoreOptionsClick = onMoreOptionsClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else if (currentSection != null) {
                SectionDetailContent(
                    section = currentSection,
                    playerState = playerState,
                    onBackClick = searchViewModel::closeSectionDetail,
                    onPlayQueue = onPlayQueue,
                    onMoreOptionsClick = onMoreOptionsClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    SearchTopHeader(
                        isRefreshing = isRefreshing,
                        isPlaying = playerState.isPlaying,
                        onRefresh = searchViewModel::refreshHomeFeed,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val hasPlaylists = selectedGenre == null && curatedPlaylists.isNotEmpty()
                    val playlistInsertIndex = remember(curatedPlaylists, homeSections) {
                        if (homeSections.size > 2) (1 until homeSections.size).random() else 0
                    }

                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = searchViewModel::refreshHomeFeed,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = bottomScrollPaddingWithInsets),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            if (categories.isNotEmpty()) {
                                item(key = "home_genre_filter_row") {
                                    GenreFilterRow(
                                        genres = categories,
                                        selectedGenre = selectedGenre,
                                        onSelectGenre = searchViewModel::selectGenre
                                    )
                                }
                            }

                            if (selectedGenre == null && heroSpotlightTracks.isNotEmpty()) {
                                item(key = "home_editorial_hero") {
                                    EditorialHeroCarousel(
                                        songs = heroSpotlightTracks,
                                        isPlaying = playerState.isPlaying,
                                        currentSongId = playerState.currentSong?.id,
                                        onPlayQueue = onPlayQueue
                                    )
                                }
                            }

                            if ((isHomeLoading && homeSections.isEmpty()) || isGenreLoading) {
                                searchShimmerSkeleton(shimmerBrush)
                            } else {
                                if (hasPlaylists && homeSections.isEmpty()) {
                                    item(key = "home_curated_playlists") {
                                        PlaylistRowSection(
                                            title = stringResource(R.string.section_playlists_title),
                                            subtitle = stringResource(R.string.section_playlists_subtitle),
                                            playlists = curatedPlaylists,
                                            onPlaylistClick = searchViewModel::openPlaylistDetail
                                        )
                                    }
                                }

                                homeSections.forEachIndexed { index, section ->
                                    if (hasPlaylists && index == playlistInsertIndex) {
                                        item(key = "home_curated_playlists") {
                                            PlaylistRowSection(
                                                title = stringResource(R.string.section_playlists_title),
                                                subtitle = stringResource(R.string.section_playlists_subtitle),
                                                playlists = curatedPlaylists,
                                                onPlaylistClick = searchViewModel::openPlaylistDetail
                                            )
                                        }
                                    }

                                    if (section.tracks.isNotEmpty()) {
                                        item(key = section.id) {
                                            when (section.style) {
                                                HomeSectionStyle.QUICK_PICKS_GRID -> {
                                                    QuickPicksGridSection(
                                                        section = section,
                                                        isPlaying = playerState.isPlaying,
                                                        currentSongId = playerState.currentSong?.id,
                                                        onPlayQueue = onPlayQueue,
                                                        onSeeAllClick = searchViewModel::openSectionDetail,
                                                        onMoreOptionsClick = onMoreOptionsClick
                                                    )
                                                }

                                                HomeSectionStyle.RANKED_CHARTS,
                                                HomeSectionStyle.STANDARD_CARDS -> {
                                                    HomeTrackSection(
                                                        section = section,
                                                        isPlaying = playerState.isPlaying,
                                                        currentSongId = playerState.currentSong?.id,
                                                        onPlayQueue = onPlayQueue,
                                                        onSeeAllClick = searchViewModel::openSectionDetail,
                                                        onMoreOptionsClick = onMoreOptionsClick
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                if (hasPlaylists && playlistInsertIndex >= homeSections.size) {
                                    item(key = "home_curated_playlists_end") {
                                        PlaylistRowSection(
                                            title = stringResource(R.string.section_playlists_title),
                                            subtitle = stringResource(R.string.section_playlists_subtitle),
                                            playlists = curatedPlaylists,
                                            onPlaylistClick = searchViewModel::openPlaylistDetail
                                        )
                                    }
                                }
                            }

                            if (selectedGenre != null && homeSections.isEmpty() && !isGenreLoading) {
                                item {
                                    SearchEmptyState(
                                        query = selectedGenre?.title.orEmpty(),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
        }
    }
}
