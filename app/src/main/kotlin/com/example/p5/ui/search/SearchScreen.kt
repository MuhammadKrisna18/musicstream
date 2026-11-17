package com.example.p5.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.SearchFilter
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.search.components.ArtistSearchItem
import com.example.p5.ui.search.components.CategoryGridSection
import com.example.p5.ui.search.components.EditorialHeroCard
import com.example.p5.ui.search.components.GenreFilterRow
import com.example.p5.ui.search.components.HomeTrackSection
import com.example.p5.ui.search.components.PlaylistDetailContent
import com.example.p5.ui.search.components.PlaylistRowSection
import com.example.p5.ui.search.components.SearchBarField
import com.example.p5.ui.search.components.SearchArtistItem
import com.example.p5.ui.search.components.SearchEmptyState
import com.example.p5.ui.search.components.SearchErrorState
import com.example.p5.ui.search.components.SearchFilterBar
import com.example.p5.ui.search.components.SearchHistorySection
import com.example.p5.ui.search.components.SearchPlaylistItem
import com.example.p5.ui.search.components.SearchTopHeader
import com.example.p5.ui.search.components.SectionDetailContent
import com.example.p5.ui.search.components.rememberShimmerBrush
import com.example.p5.ui.search.components.searchShimmerSkeleton
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.ToscaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {},
    onArtistClick: (channelId: String, artistName: String) -> Unit = { _, _ -> }
) {
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val homeSections by searchViewModel.homeSections.collectAsStateWithLifecycle()
    val categories by searchViewModel.categories.collectAsStateWithLifecycle()
    val selectedGenre by searchViewModel.selectedGenre.collectAsStateWithLifecycle()
    val selectedSection by searchViewModel.selectedSection.collectAsStateWithLifecycle()
    val curatedPlaylists by searchViewModel.curatedPlaylists.collectAsStateWithLifecycle()
    val selectedPlaylist by searchViewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val selectedPlaylistTracks by searchViewModel.selectedPlaylistTracks.collectAsStateWithLifecycle()
    val isPlaylistLoading by searchViewModel.isPlaylistLoading.collectAsStateWithLifecycle()
    val isGenreLoading by searchViewModel.isGenreLoading.collectAsStateWithLifecycle()
    val searchFilter by searchViewModel.searchFilter.collectAsStateWithLifecycle()
    val isHomeLoading by searchViewModel.isHomeLoading.collectAsStateWithLifecycle()
    val isRefreshing by searchViewModel.isRefreshing.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val shimmerBrush = rememberShimmerBrush()

    val editorialFeaturedTrack = remember(homeSections) {
        homeSections.firstOrNull { it.tracks.isNotEmpty() }?.tracks?.firstOrNull()
    }

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
            .padding(horizontal = 16.dp)
    ) {
        AnimatedContent(
            targetState = Pair(selectedSection, selectedPlaylist),
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "section_or_playlist_detail_or_home",
            modifier = Modifier.fillMaxSize()
        ) { (currentSection, currentPlaylist) ->
            if (currentPlaylist != null) {
                PlaylistDetailContent(
                    playlist = currentPlaylist,
                    tracks = selectedPlaylistTracks,
                    isLoading = isPlaylistLoading,
                    playerState = playerState,
                    onBackClick = searchViewModel::closePlaylistDetail,
                    onPlayQueue = onPlayQueue,
                    onMoreOptionsClick = onMoreOptionsClick
                )
            } else if (currentSection != null) {
                SectionDetailContent(
                    section = currentSection,
                    playerState = playerState,
                    onBackClick = searchViewModel::closeSectionDetail,
                    onPlayQueue = onPlayQueue,
                    onMoreOptionsClick = onMoreOptionsClick
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    SearchTopHeader(
                        isRefreshing = isRefreshing,
                        isPlaying = playerState.isPlaying,
                        onRefresh = searchViewModel::refreshHomeFeed
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SearchBarField(
                        query = query,
                        onQueryChanged = searchViewModel::onQueryChanged,
                        onSearch = searchViewModel::searchNow
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "search_state_content",
                        modifier = Modifier.weight(1f)
                    ) { state ->
                        when (state) {
                            SearchUiState.Idle -> {
                                PullToRefreshBox(
                                    isRefreshing = isRefreshing,
                                    onRefresh = searchViewModel::refreshHomeFeed,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(bottom = BottomScrollPadding),
                                        verticalArrangement = Arrangement.spacedBy(18.dp)
                                    ) {
                                        if (searchHistory.isNotEmpty()) {
                                            item {
                                                SearchHistorySection(
                                                    searchHistory = searchHistory,
                                                    onSelectQuery = { selectedQuery ->
                                                        focusManager.clearFocus()
                                                        searchViewModel.selectHistoryQuery(selectedQuery)
                                                    },
                                                    onDeleteItem = searchViewModel::deleteSearchHistoryItem,
                                                    onClearAll = searchViewModel::clearSearchHistory
                                                )
                                            }
                                        }

                                        if (categories.isNotEmpty()) {
                                            item(key = "genre_filter_row") {
                                                GenreFilterRow(
                                                    genres = categories,
                                                    selectedGenre = selectedGenre,
                                                    onSelectGenre = searchViewModel::selectGenre
                                                )
                                            }
                                        }

                                        if (selectedGenre == null) {
                                            if (editorialFeaturedTrack != null && query.isEmpty()) {
                                                item(key = "editorial_hero") {
                                                    EditorialHeroCard(
                                                        song = editorialFeaturedTrack,
                                                        isPlaying = playerState.isPlaying,
                                                        isSelected = playerState.currentSong?.id == editorialFeaturedTrack.id,
                                                        onClick = { onSongClick(editorialFeaturedTrack) }
                                                    )
                                                }
                                            }
                                        }

                                        if ((isHomeLoading && homeSections.isEmpty()) || isGenreLoading) {
                                            searchShimmerSkeleton(shimmerBrush)
                                        } else {
                                            if (selectedGenre == null && curatedPlaylists.isNotEmpty()) {
                                                item(key = "section_curated_playlists") {
                                                    PlaylistRowSection(
                                                        title = stringResource(R.string.section_playlists_title),
                                                        subtitle = stringResource(R.string.section_playlists_subtitle),
                                                        playlists = curatedPlaylists,
                                                        onPlaylistClick = searchViewModel::openPlaylistDetail
                                                    )
                                                }
                                            }

                                            homeSections.forEach { section ->
                                                if (section.tracks.isNotEmpty()) {
                                                    item(key = section.id) {
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

                                        if (selectedGenre != null && homeSections.isEmpty() && !isGenreLoading) {
                                            item {
                                                SearchEmptyState(
                                                    query = selectedGenre?.title.orEmpty(),
                                                    modifier = Modifier.padding(vertical = 32.dp)
                                                )
                                            }
                                        }

                                        if (selectedGenre == null && categories.isNotEmpty()) {
                                            item {
                                                CategoryGridSection(
                                                    categories = categories,
                                                    onCategoryClick = searchViewModel::selectGenre
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            SearchUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = ToscaPrimary,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            is SearchUiState.Success -> {
                                val albums = remember(state.playlists) {
                                    state.playlists.filter {
                                        it.id.startsWith("MPRE") ||
                                            it.subtitle.contains("album", ignoreCase = true) ||
                                            it.title.contains("album", ignoreCase = true)
                                    }
                                }

                                val playlistsOnly = remember(state.playlists) {
                                    state.playlists.filter {
                                        !it.id.startsWith("MPRE") &&
                                            !it.subtitle.contains("album", ignoreCase = true) &&
                                            !it.title.contains("album", ignoreCase = true)
                                    }
                                }

                                val matchingArtists = remember(state.songs, state.playlists) {
                                    val artistList = mutableListOf<ArtistSearchItem>()
                                    val seenNames = mutableSetOf<String>()

                                    for (song in state.songs) {
                                        val name = song.artist.trim()
                                        if (name.isNotBlank() && name != "YouTube Music" && seenNames.add(name.lowercase(Locale.ROOT))) {
                                            artistList.add(
                                                ArtistSearchItem(
                                                    name = name,
                                                    channelId = song.channelId ?: "",
                                                    thumbnailUrl = song.thumbnailUrl
                                                )
                                            )
                                        }
                                    }

                                    for (pl in state.playlists) {
                                        val name = pl.subtitle.trim()
                                        if (name.isNotBlank() && name != "YouTube Music" && seenNames.add(name.lowercase(Locale.ROOT))) {
                                            artistList.add(
                                                ArtistSearchItem(
                                                    name = name,
                                                    channelId = if (pl.id.startsWith("UC")) pl.id else "",
                                                    thumbnailUrl = pl.thumbnailUrl
                                                )
                                            )
                                        }
                                    }

                                    artistList
                                }

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = BottomScrollPadding),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    item(key = "search_filter_bar") {
                                        SearchFilterBar(
                                            selectedFilter = searchFilter,
                                            onSelectFilter = searchViewModel::setSearchFilter,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                    }

                                    when (searchFilter) {
                                        SearchFilter.ALL -> {
                                            if (state.playlists.isNotEmpty()) {
                                                item(key = "search_playlists_row") {
                                                    PlaylistRowSection(
                                                        title = stringResource(R.string.search_playlists_title),
                                                        subtitle = stringResource(R.string.search_playlists_subtitle),
                                                        playlists = state.playlists,
                                                        onPlaylistClick = searchViewModel::onPlaylistSelectedFromSearch,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                    )
                                                }
                                            }

                                            itemsIndexed(
                                                items = state.songs,
                                                key = { _, item -> item.id }
                                            ) { index, song ->
                                                val isSelected = playerState.currentSong?.id == song.id

                                                SongItem(
                                                    song = song,
                                                    isPlaying = playerState.isPlaying,
                                                    isSelected = isSelected,
                                                    onClick = {
                                                        searchViewModel.onSongSelectedFromSearch(song)
                                                        onPlayQueue(state.songs, index)
                                                    },
                                                    onMoreOptionsClick = onMoreOptionsClick
                                                )
                                            }
                                        }

                                        SearchFilter.SONGS -> {
                                            if (state.songs.isNotEmpty()) {
                                                itemsIndexed(
                                                    items = state.songs,
                                                    key = { _, item -> item.id }
                                                ) { index, song ->
                                                    val isSelected = playerState.currentSong?.id == song.id

                                                    SongItem(
                                                        song = song,
                                                        isPlaying = playerState.isPlaying,
                                                        isSelected = isSelected,
                                                        onClick = {
                                                            searchViewModel.onSongSelectedFromSearch(song)
                                                            onPlayQueue(state.songs, index)
                                                        },
                                                        onMoreOptionsClick = onMoreOptionsClick
                                                    )
                                                }
                                            } else {
                                                item {
                                                    SearchEmptyState(
                                                        query = "Lagu untuk \"$query\"",
                                                        modifier = Modifier.padding(vertical = 32.dp)
                                                    )
                                                }
                                            }
                                        }

                                        SearchFilter.ALBUMS -> {
                                            if (albums.isNotEmpty()) {
                                                items(
                                                    items = albums,
                                                    key = { it.id }
                                                ) { album ->
                                                    SearchPlaylistItem(
                                                        playlist = album,
                                                        categoryBadge = "Album",
                                                        onClick = { searchViewModel.onPlaylistSelectedFromSearch(album) }
                                                    )
                                                }
                                            } else {
                                                item {
                                                    SearchEmptyState(
                                                        query = "Album untuk \"$query\"",
                                                        modifier = Modifier.padding(vertical = 32.dp)
                                                    )
                                                }
                                            }
                                        }

                                        SearchFilter.PLAYLISTS -> {
                                            if (playlistsOnly.isNotEmpty()) {
                                                items(
                                                    items = playlistsOnly,
                                                    key = { it.id }
                                                ) { playlist ->
                                                    SearchPlaylistItem(
                                                        playlist = playlist,
                                                        categoryBadge = "Playlist",
                                                        onClick = { searchViewModel.onPlaylistSelectedFromSearch(playlist) }
                                                    )
                                                }
                                            } else {
                                                item {
                                                    SearchEmptyState(
                                                        query = "Playlist untuk \"$query\"",
                                                        modifier = Modifier.padding(vertical = 32.dp)
                                                    )
                                                }
                                            }
                                        }

                                        SearchFilter.ARTISTS -> {
                                            if (matchingArtists.isNotEmpty()) {
                                                items(
                                                    items = matchingArtists,
                                                    key = { it.name }
                                                ) { artistItem ->
                                                    SearchArtistItem(
                                                        artist = artistItem,
                                                        onClick = {
                                                            onArtistClick(artistItem.channelId, artistItem.name)
                                                        }
                                                    )
                                                }
                                            } else {
                                                item {
                                                    SearchEmptyState(
                                                        query = "Artis untuk \"$query\"",
                                                        modifier = Modifier.padding(vertical = 32.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            SearchUiState.Empty -> {
                                SearchEmptyState(query = query)
                            }

                            is SearchUiState.Error -> {
                                SearchErrorState(
                                    message = state.message,
                                    onRetry = searchViewModel::searchNow
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
