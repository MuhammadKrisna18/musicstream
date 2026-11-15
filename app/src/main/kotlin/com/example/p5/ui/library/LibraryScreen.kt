package com.example.p5.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.library.components.LibraryCacheBar
import com.example.p5.ui.library.components.LibraryEmptyState
import com.example.p5.ui.library.components.LibraryHistoryHeader
import com.example.p5.ui.library.components.LibrarySongList
import com.example.p5.ui.library.components.LibraryTabBar
import com.example.p5.ui.library.components.LibraryTopHeader
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    currentVersion: String = "2.3.0",
    onCheckForUpdate: () -> Unit = {},
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {}
) {
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val favoriteSongs = uiState.favoriteSongs
    val savedSongs = uiState.savedSongs
    val historySongs = uiState.historySongs
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        libraryViewModel.refreshCacheSize(context)
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
            savedCount = savedSongs.size,
            historyCount = historySongs.size,
            onCheckForUpdate = onCheckForUpdate
        )

        Spacer(modifier = Modifier.height(14.dp))

        LibraryTabBar(
            selectedTab = selectedTab,
            favoriteCount = favoriteSongs.size,
            savedCount = savedSongs.size,
            historyCount = historySongs.size,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(SpacingMd))

        LibraryCacheBar(
            cacheSizeBytes = uiState.cacheSizeBytes,
            onClearCache = { libraryViewModel.clearStreamingCache(context) }
        )

        Spacer(modifier = Modifier.height(14.dp))

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
                        songs = favoriteSongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick
                    )
                }
            }

            1 -> {
                if (savedSongs.isEmpty()) {
                    LibraryEmptyState(
                        icon = Icons.Rounded.BookmarkBorder,
                        title = stringResource(R.string.library_empty_saved_title),
                        description = stringResource(R.string.library_empty_saved_desc)
                    )
                } else {
                    LibrarySongList(
                        songs = savedSongs,
                        currentSongId = playerState.currentSong?.id,
                        isPlaying = playerState.isPlaying,
                        onPlayQueue = onPlayQueue,
                        onMoreOptionsClick = onMoreOptionsClick
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
