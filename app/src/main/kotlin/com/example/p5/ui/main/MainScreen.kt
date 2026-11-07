package com.example.p5.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.p5.domain.model.Song
import com.example.p5.ui.common.AppViewModelProvider
import com.example.p5.ui.components.FloatingDock
import com.example.p5.ui.components.FullPlayerSheet
import com.example.p5.ui.components.MiniPlayer
import com.example.p5.ui.components.SongActionBottomSheet
import com.example.p5.ui.components.UpdateDialog
import com.example.p5.ui.library.LibraryScreen
import com.example.p5.ui.library.LibraryViewModel
import com.example.p5.ui.player.PlayerViewModel
import com.example.p5.ui.search.SearchScreen
import com.example.p5.ui.search.SearchViewModel
import com.example.p5.ui.theme.ObsidianBackground
import com.example.p5.ui.update.UpdateViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playerViewModel: PlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    updateViewModel: UpdateViewModel = viewModel(factory = AppViewModelProvider.Factory),
    modifier: Modifier = Modifier
) {
    val playerState by playerViewModel.playerState.collectAsStateWithLifecycle()
    val libraryUiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val updateUiState by updateViewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var isFullPlayerVisible by remember { mutableStateOf(false) }
    var selectedSongForMenu by remember { mutableStateOf<Song?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(playerState.errorMessage) {
        playerState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    LaunchedEffect(libraryUiState.message) {
        libraryUiState.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            libraryViewModel.clearUserMessage()
        }
    }

    LaunchedEffect(updateUiState.userMessage) {
        updateUiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            updateViewModel.clearUserMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ObsidianBackground,
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
        ) {
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(durationMillis = 200),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    0 -> {
                        SearchScreen(
                            searchViewModel = searchViewModel,
                            playerState = playerState,
                            onSongClick = { song -> playerViewModel.playSong(song) },
                            onPlayQueue = { songs, index -> playerViewModel.playQueue(songs, index) },
                            onMoreOptionsClick = { song -> selectedSongForMenu = song },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    1 -> {
                        LibraryScreen(
                            libraryViewModel = libraryViewModel,
                            playerState = playerState,
                            onSongClick = { song -> playerViewModel.playSong(song) },
                            onPlayQueue = { songs, index -> playerViewModel.playQueue(songs, index) },
                            onMoreOptionsClick = { song -> selectedSongForMenu = song },
                            currentVersion = updateViewModel.currentVersionName,
                            onCheckForUpdate = { updateViewModel.checkForUpdates(isManualCheck = true) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            ) {
                MiniPlayer(
                    playerState = playerState,
                    onTogglePlayPause = playerViewModel::togglePlayPause,
                    onOpenFullPlayer = { isFullPlayerVisible = true },
                    onSkipNext = playerViewModel::playNext
                )

                FloatingDock(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        if (tab == 0 && selectedTab == 0) {
                            searchViewModel.onQueryChanged("")
                        } else {
                            selectedTab = tab
                        }
                    },
                    favoriteCount = libraryUiState.favoriteSongs.size
                )
            }

            if (isFullPlayerVisible && playerState.currentSong != null) {
                FullPlayerSheet(
                    playerState = playerState,
                    onTogglePlayPause = playerViewModel::togglePlayPause,
                    onSeekTo = playerViewModel::seekTo,
                    onSkipNext = playerViewModel::playNext,
                    onSkipPrevious = playerViewModel::playPrevious,
                    onToggleShuffle = playerViewModel::toggleShuffle,
                    onToggleRepeatMode = playerViewModel::toggleRepeatMode,
                    onPlayQueueIndex = playerViewModel::playQueueIndex,
                    onSetSleepTimerMinutes = playerViewModel::setSleepTimerMinutes,
                    onSetSleepTimerEndOfTrack = playerViewModel::setSleepTimerEndOfTrack,
                    onCancelSleepTimer = playerViewModel::cancelSleepTimer,
                    onRetryFetchLyrics = playerViewModel::retryFetchLyrics,
                    onToggleFavorite = playerViewModel::toggleFavoriteCurrentSong,
                    onDismiss = { isFullPlayerVisible = false }
                )
            }

            if (updateUiState.showDialog && updateUiState.updateInfo != null) {
                UpdateDialog(
                    updateInfo = updateUiState.updateInfo!!,
                    isDownloading = updateUiState.isDownloading,
                    downloadProgress = updateUiState.downloadProgress,
                    downloadedFile = updateUiState.downloadedApkFile,
                    onDismiss = updateViewModel::dismissDialog,
                    onStartDownload = updateViewModel::startDownload,
                    onInstall = updateViewModel::installDownloadedApk
                )
            }

            if (selectedSongForMenu != null) {
                val isSongFavorite = libraryUiState.favoriteSongs.any { it.id == selectedSongForMenu?.id }
                val isSongSaved = libraryUiState.savedSongs.any { it.id == selectedSongForMenu?.id }

                SongActionBottomSheet(
                    song = selectedSongForMenu,
                    isSaved = isSongSaved,
                    isFavorite = isSongFavorite,
                    onDismiss = { selectedSongForMenu = null },
                    onToggleSave = { song -> libraryViewModel.toggleSaveSong(song) },
                    onToggleFavorite = { song ->
                        libraryViewModel.toggleFavorite(song)
                        if (playerState.currentSong?.id == song.id) {
                            playerViewModel.toggleFavoriteCurrentSong()
                        }
                    },
                    onAddToQueueNext = { song ->
                        val added = playerViewModel.addToQueueNext(song)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                if (added) "Ditambahkan ke antrean berikutnya" else "Lagu sedang diputar"
                            )
                        }
                    }
                )
            }
        }
    }
}
