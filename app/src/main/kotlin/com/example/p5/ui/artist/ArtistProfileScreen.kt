package com.example.p5.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import com.example.p5.ui.artist.components.ArtistAlbumsSection
import com.example.p5.ui.artist.components.ArtistAllReleasesHeader
import com.example.p5.ui.artist.components.ArtistErrorView
import com.example.p5.ui.artist.components.ArtistHeaderSection
import com.example.p5.ui.artist.components.ArtistLoadMoreSection
import com.example.p5.ui.artist.components.ArtistLoadingView
import com.example.p5.ui.artist.components.ArtistThemeBackground
import com.example.p5.ui.artist.components.ArtistTopSongsSection
import com.example.p5.ui.common.AppViewModelProvider
import com.example.p5.ui.components.FullPlayerSheet
import com.example.p5.ui.components.MiniPlayer
import com.example.p5.ui.components.SongActionBottomSheet
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.library.LibraryViewModel
import com.example.p5.ui.player.PlayerViewModel
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.rememberDynamicPlayerPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistProfileScreen(
    channelId: String,
    artistNameFallback: String = "",
    onBackClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    playerViewModel: PlayerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    libraryViewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToArtist: ((channelId: String, artistName: String) -> Unit)? = null
) {
    val appColors = AppTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by playerViewModel.playerState.collectAsStateWithLifecycle()
    val libraryUiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val currentSongId = playerState.currentSong?.id

    var isFullPlayerVisible by remember { mutableStateOf(false) }
    var selectedSongForMenu by remember { mutableStateOf<Song?>(null) }

    LaunchedEffect(channelId) {
        viewModel.loadArtist(channelId, artistNameFallback)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background),
        containerColor = appColors.background,
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val state = uiState) {
                        is ArtistProfileUiState.Success -> state.profile.name
                        else -> artistNameFallback
                    }
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = appColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = appColors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.background.copy(alpha = 0.92f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ArtistThemeBackground()

            when (val state = uiState) {
                is ArtistProfileUiState.Loading -> {
                    ArtistLoadingView()
                }

                is ArtistProfileUiState.Error -> {
                    ArtistErrorView(
                        message = state.message,
                        onRetry = { viewModel.loadArtist(channelId, artistNameFallback) }
                    )
                }

                is ArtistProfileUiState.Success -> {
                    val bottomPadding = if (playerState.currentSong != null) 92.dp else 24.dp

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = bottomPadding)
                    ) {
                        item(key = "header") {
                            ArtistHeaderSection(
                                profile = state.profile,
                                onPlayAll = { viewModel.playAll(state.allSongs) },
                                onShuffle = { viewModel.shuffleAll(state.allSongs) }
                            )
                        }

                        if (state.profile.topSongs.isNotEmpty()) {
                            item(key = "top_songs") {
                                ArtistTopSongsSection(
                                    songs = state.profile.topSongs,
                                    currentPlayingSongId = currentSongId,
                                    onSongClick = { song ->
                                        viewModel.playSong(song, state.profile.topSongs)
                                    }
                                )
                            }
                        }

                        if (state.profile.albums.isNotEmpty()) {
                            item(key = "albums") {
                                ArtistAlbumsSection(
                                    title = "Album",
                                    playlists = state.profile.albums,
                                    onPlaylistClick = onPlaylistClick
                                )
                            }
                        }

                        if (state.profile.singles.isNotEmpty()) {
                            item(key = "singles") {
                                ArtistAlbumsSection(
                                    title = "Single & EP",
                                    playlists = state.profile.singles,
                                    onPlaylistClick = onPlaylistClick
                                )
                            }
                        }

                        if (state.allSongs.isNotEmpty()) {
                            item(key = "all_songs_header") {
                                ArtistAllReleasesHeader()
                            }

                            items(state.allSongs, key = { it.id }) { song ->
                                val isPlaying = song.id == currentSongId
                                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                    SongItem(
                                        song = song,
                                        isPlaying = isPlaying,
                                        isSelected = isPlaying,
                                        onClick = { viewModel.playSong(song, state.allSongs) },
                                        onMoreOptionsClick = { songMenu ->
                                            selectedSongForMenu = songMenu
                                        }
                                    )
                                }
                            }

                            if (!state.hasLoadedAllSongs && !state.profile.allSongsPlaylistId.isNullOrBlank()) {
                                item(key = "load_more") {
                                    ArtistLoadMoreSection(
                                        isLoading = state.isLoadingAllSongs,
                                        onLoadMore = { viewModel.loadAllSongs() }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (playerState.currentSong != null) {
                val miniPlayerPalette = rememberDynamicPlayerPalette(artworkUrl = playerState.currentSong?.thumbnailUrl)

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
                        onSkipNext = playerViewModel::playNext,
                        accentColor = miniPlayerPalette.accent
                    )
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
                    onMoveQueueItem = playerViewModel::moveQueueItem,
                    onRemoveQueueItem = playerViewModel::removeQueueItem,
                    onSetSleepTimerMinutes = playerViewModel::setSleepTimerMinutes,
                    onSetSleepTimerEndOfTrack = playerViewModel::setSleepTimerEndOfTrack,
                    onCancelSleepTimer = playerViewModel::cancelSleepTimer,
                    onRetryFetchLyrics = playerViewModel::retryFetchLyrics,
                    onToggleFavorite = playerViewModel::toggleFavoriteCurrentSong,
                    onDismiss = { isFullPlayerVisible = false },
                    onArtistClick = { nextChannelId, nextArtistName ->
                        isFullPlayerVisible = false
                        if (!nextChannelId.isNullOrBlank() && nextChannelId != channelId) {
                            onNavigateToArtist?.invoke(nextChannelId, nextArtistName)
                        }
                    }
                )
            }

            if (selectedSongForMenu != null) {
                val isSongFavorite = libraryUiState.favoriteSongs.any { it.id == selectedSongForMenu?.id }
                val isSongSaved = libraryUiState.savedSongs.any { it.id == selectedSongForMenu?.id }
                val isSongDownloaded = libraryUiState.downloadedSongs.any { it.id == selectedSongForMenu?.id }
                val isSongDownloading = selectedSongForMenu?.id in libraryUiState.activeDownloadIds

                SongActionBottomSheet(
                    song = selectedSongForMenu,
                    isSaved = isSongSaved,
                    isFavorite = isSongFavorite,
                    isDownloaded = isSongDownloaded,
                    isDownloading = isSongDownloading,
                    downloadProgress = libraryUiState.downloadProgress[selectedSongForMenu?.id],
                    onDismiss = { selectedSongForMenu = null },
                    onToggleSave = { song -> libraryViewModel.toggleSaveSong(song) },
                    onToggleFavorite = { song ->
                        libraryViewModel.toggleFavorite(song)
                        if (playerState.currentSong?.id == song.id) {
                            playerViewModel.toggleFavoriteCurrentSong()
                        }
                    },
                    onToggleDownload = { song -> libraryViewModel.toggleDownload(song) },
                    onAddToQueueNext = { song ->
                        playerViewModel.addToQueueNext(song)
                    },
                    onViewArtist = { nextChannelId, nextArtistName ->
                        if (!nextChannelId.isNullOrBlank() && nextChannelId != channelId) {
                            onNavigateToArtist?.invoke(nextChannelId, nextArtistName)
                        }
                    }
                )
            }
        }
    }
}
