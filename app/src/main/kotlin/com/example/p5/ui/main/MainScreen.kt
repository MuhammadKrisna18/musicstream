package com.example.p5.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.p5.ui.common.AppViewModelProvider
import com.example.p5.ui.components.FullPlayerSheet
import com.example.p5.ui.components.MiniPlayer
import com.example.p5.ui.components.UpdateDialog
import com.example.p5.ui.library.LibraryScreen
import com.example.p5.ui.library.LibraryViewModel
import com.example.p5.ui.player.PlayerViewModel
import com.example.p5.ui.search.SearchScreen
import com.example.p5.ui.search.SearchViewModel
import com.example.p5.ui.update.UpdateViewModel
import com.example.p5.ui.theme.BlackBackground
import com.example.p5.ui.theme.DarkSurface
import com.example.p5.ui.theme.DividerColor
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

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
    val snackbarHostState = remember { SnackbarHostState() }

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
        containerColor = BlackBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
            ) {
                MiniPlayer(
                    playerState = playerState,
                    onTogglePlayPause = playerViewModel::togglePlayPause,
                    onOpenFullPlayer = { isFullPlayerVisible = true },
                    onSkipNext = playerViewModel::playNext
                )

                // Elegant subtle border separator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(DividerColor.copy(alpha = 0.5f))
                )

                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = {
                            if (selectedTab == 0) {
                                searchViewModel.onQueryChanged("")
                            } else {
                                selectedTab = 0
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                "Home",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ToscaPrimary,
                            selectedTextColor = ToscaPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = ToscaPrimary.copy(alpha = 0.16f)
                        )
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (libraryUiState.favoriteSongs.isNotEmpty()) {
                                        Badge(
                                            containerColor = ToscaPrimary,
                                            contentColor = Color.Black
                                        ) {
                                            Text(
                                                "${libraryUiState.favoriteSongs.size}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LibraryMusic,
                                    contentDescription = "Library",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                "Library",
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ToscaPrimary,
                            selectedTextColor = ToscaPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = ToscaPrimary.copy(alpha = 0.16f)
                        )
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BlackBackground)
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    1 -> {
                        LibraryScreen(
                            libraryViewModel = libraryViewModel,
                            playerState = playerState,
                            onSongClick = { song -> playerViewModel.playSong(song) },
                            onPlayQueue = { songs, index -> playerViewModel.playQueue(songs, index) },
                            currentVersion = updateViewModel.currentVersionName,
                            onCheckForUpdate = { updateViewModel.checkForUpdates(isManualCheck = true) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
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
        }
    }
}
