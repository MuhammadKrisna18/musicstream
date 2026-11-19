package com.example.p5.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.R
import com.example.p5.domain.model.LibrarySortOrder
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.library.components.LibraryEmptyState
import com.example.p5.ui.library.components.LibraryFilterBar
import com.example.p5.ui.library.components.LibraryHistoryHeader
import com.example.p5.ui.library.components.LibrarySongList
import com.example.p5.ui.library.components.LibraryTabBar
import com.example.p5.ui.library.components.LibraryTopHeader
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import java.util.Locale

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {}
) {
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val favoriteSongs = uiState.favoriteSongs
    val downloadedSongs = uiState.downloadedSongs
    val savedSongs = uiState.savedSongs
    val historySongs = uiState.historySongs
    val sortOrder by libraryViewModel.sortOrder.collectAsStateWithLifecycle()
    val selectedArtist by libraryViewModel.selectedArtist.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    val favoriteArtists = remember(favoriteSongs) {
        favoriteSongs.map { it.artist }.distinct().filter { it.isNotBlank() }.sorted()
    }

    val displayFavoriteSongs = remember(favoriteSongs, sortOrder, selectedArtist) {
        val filtered = if (selectedArtist.isNullOrBlank()) {
            favoriteSongs
        } else {
            favoriteSongs.filter { it.artist.equals(selectedArtist, ignoreCase = true) }
        }
        when (sortOrder) {
            LibrarySortOrder.LATEST_ADDED -> filtered
            LibrarySortOrder.TITLE_AZ -> filtered.sortedBy { it.title.lowercase(Locale.ROOT) }
            LibrarySortOrder.ARTIST_AZ -> filtered.sortedBy { it.artist.lowercase(Locale.ROOT) }
            LibrarySortOrder.DURATION -> filtered.sortedByDescending { it.durationSeconds }
        }
    }

    val downloadedArtists = remember(downloadedSongs) {
        downloadedSongs.map { it.artist }.distinct().filter { it.isNotBlank() }.sorted()
    }

    val displayDownloadedSongs = remember(downloadedSongs, sortOrder, selectedArtist) {
        val filtered = if (selectedArtist.isNullOrBlank()) {
            downloadedSongs
        } else {
            downloadedSongs.filter { it.artist.equals(selectedArtist, ignoreCase = true) }
        }
        when (sortOrder) {
            LibrarySortOrder.LATEST_ADDED -> filtered
            LibrarySortOrder.TITLE_AZ -> filtered.sortedBy { it.title.lowercase(Locale.ROOT) }
            LibrarySortOrder.ARTIST_AZ -> filtered.sortedBy { it.artist.lowercase(Locale.ROOT) }
            LibrarySortOrder.DURATION -> filtered.sortedByDescending { it.durationSeconds }
        }
    }

    val savedArtists = remember(savedSongs) {
        savedSongs.map { it.artist }.distinct().filter { it.isNotBlank() }.sorted()
    }

    val displaySavedSongs = remember(savedSongs, sortOrder, selectedArtist) {
        val filtered = if (selectedArtist.isNullOrBlank()) {
            savedSongs
        } else {
            savedSongs.filter { it.artist.equals(selectedArtist, ignoreCase = true) }
        }
        when (sortOrder) {
            LibrarySortOrder.LATEST_ADDED -> filtered
            LibrarySortOrder.TITLE_AZ -> filtered.sortedBy { it.title.lowercase(Locale.ROOT) }
            LibrarySortOrder.ARTIST_AZ -> filtered.sortedBy { it.artist.lowercase(Locale.ROOT) }
            LibrarySortOrder.DURATION -> filtered.sortedByDescending { it.durationSeconds }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = SpacingLg)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        LibraryTopHeader(
            selectedTab = selectedTab,
            favoriteCount = favoriteSongs.size,
            downloadedCount = downloadedSongs.size,
            savedCount = savedSongs.size,
            historyCount = historySongs.size
        )

        Spacer(modifier = Modifier.height(14.dp))

        LibraryTabBar(
            selectedTab = selectedTab,
            favoriteCount = favoriteSongs.size,
            downloadedCount = downloadedSongs.size,
            savedCount = savedSongs.size,
            historyCount = historySongs.size,
            onTabSelected = {
                selectedTab = it
                libraryViewModel.selectArtist(null)
            }
        )

        Spacer(modifier = Modifier.height(SpacingMd))

        when (selectedTab) {
            0 -> {
                if (favoriteSongs.isEmpty()) {
                    LibraryEmptyState(
                        icon = Icons.Rounded.FavoriteBorder,
                        title = stringResource(R.string.library_empty_favorites_title),
                        description = stringResource(R.string.library_empty_favorites_desc)
                    )
                } else {
                    LibrarySongList(
                        songs = displayFavoriteSongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick,
                        headerContent = {
                            LibraryFilterBar(
                                sortOrder = sortOrder,
                                onSortOrderSelected = libraryViewModel::setSortOrder,
                                artists = favoriteArtists,
                                selectedArtist = selectedArtist,
                                onArtistSelected = libraryViewModel::selectArtist,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    )
                }
            }

            1 -> {
                if (downloadedSongs.isEmpty()) {
                    LibraryEmptyState(
                        icon = Icons.Rounded.FileDownload,
                        title = stringResource(R.string.library_empty_downloaded_title),
                        description = stringResource(R.string.library_empty_downloaded_desc)
                    )
                } else {
                    LibrarySongList(
                        songs = displayDownloadedSongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick,
                        headerContent = {
                            LibraryFilterBar(
                                sortOrder = sortOrder,
                                onSortOrderSelected = libraryViewModel::setSortOrder,
                                artists = downloadedArtists,
                                selectedArtist = selectedArtist,
                                onArtistSelected = libraryViewModel::selectArtist,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    )
                }
            }

            2 -> {
                if (savedSongs.isEmpty()) {
                    LibraryEmptyState(
                        icon = Icons.Rounded.BookmarkBorder,
                        title = stringResource(R.string.library_empty_saved_title),
                        description = stringResource(R.string.library_empty_saved_desc)
                    )
                } else {
                    LibrarySongList(
                        songs = displaySavedSongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick,
                        headerContent = {
                            LibraryFilterBar(
                                sortOrder = sortOrder,
                                onSortOrderSelected = libraryViewModel::setSortOrder,
                                artists = savedArtists,
                                selectedArtist = selectedArtist,
                                onArtistSelected = libraryViewModel::selectArtist,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    )
                }
            }

            else -> {
                if (historySongs.isEmpty()) {
                    LibraryEmptyState(
                        icon = Icons.Rounded.History,
                        title = stringResource(R.string.library_empty_history_title),
                        description = stringResource(R.string.library_empty_history_desc)
                    )
                } else {
                    LibrarySongList(
                        songs = historySongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick,
                        headerContent = {
                            LibraryHistoryHeader(
                                onClearHistory = libraryViewModel::clearHistory
                            )
                        }
                    )
                }
            }
        }
    }
}
