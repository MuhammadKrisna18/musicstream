package com.example.p5.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.ui.components.player.PlayerAlbumView
import com.example.p5.ui.components.player.PlayerAuroraGlow
import com.example.p5.ui.components.player.PlayerCenterView
import com.example.p5.ui.components.player.PlayerControlsView
import com.example.p5.ui.components.player.PlayerLyricsView
import com.example.p5.ui.components.player.PlayerQueueView
import com.example.p5.ui.components.player.PlayerTopBar
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.DurationNormal
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingXxl
import com.example.p5.ui.theme.rememberDynamicPlayerPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerSheet(
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeatMode: () -> Unit,
    onPlayQueueIndex: (Int) -> Unit,
    onMoveQueueItem: (Int, Int) -> Unit = { _, _ -> },
    onRemoveQueueItem: (Int) -> Unit = {},
    onSetSleepTimerMinutes: (Int) -> Unit,
    onSetSleepTimerEndOfTrack: () -> Unit,
    onCancelSleepTimer: () -> Unit,
    onRetryFetchLyrics: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit,
    onArtistClick: ((channelId: String?, artistName: String) -> Unit)? = null
) {
    val currentSong = playerState.currentSong ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val palette = rememberDynamicPlayerPalette(artworkUrl = currentSong.thumbnailUrl)
    var centerView by remember { mutableStateOf(PlayerCenterView.ALBUM) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }

    if (showSleepTimerDialog) {
        SleepTimerDialog(
            secondsRemaining = playerState.sleepTimerSecondsRemaining,
            isEndOfTrack = playerState.isSleepTimerEndOfTrack,
            onSetMinutes = onSetSleepTimerMinutes,
            onSetEndOfTrack = onSetSleepTimerEndOfTrack,
            onCancelTimer = onCancelSleepTimer,
            onDismiss = { showSleepTimerDialog = false }
        )
    }

    val appColors = AppTheme.colors

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = appColors.background,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (appColors.backgroundImageRes != null) {
                Image(
                    painter = painterResource(id = appColors.backgroundImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f,
                    modifier = Modifier.fillMaxSize()
                )
            }

            PlayerAuroraGlow(
                palette = palette,
                backgroundColor = appColors.background,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = SpacingXxl)
                    .padding(top = SpacingMd, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PlayerTopBar(
                    centerView = centerView,
                    isSleepTimerActive = playerState.isSleepTimerActive,
                    onDismiss = onDismiss,
                    onOpenSleepTimer = { showSleepTimerDialog = true },
                    onToggleLyrics = {
                        centerView = if (centerView == PlayerCenterView.LYRICS) PlayerCenterView.ALBUM else PlayerCenterView.LYRICS
                    },
                    onToggleQueue = {
                        centerView = if (centerView == PlayerCenterView.QUEUE) PlayerCenterView.ALBUM else PlayerCenterView.QUEUE
                    },
                    activeColor = palette.accent
                )

                Spacer(modifier = Modifier.height(SpacingLg))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = centerView,
                        transitionSpec = { fadeIn(tween(DurationNormal)) togetherWith fadeOut(tween(DurationFast)) },
                        label = "center_view_switch"
                    ) { currentView ->
                        when (currentView) {
                            PlayerCenterView.ALBUM -> {
                                PlayerAlbumView(
                                    song = currentSong,
                                    isPlaying = playerState.isPlaying,
                                    isFavorite = playerState.isCurrentSongFavorite,
                                    onToggleFavorite = onToggleFavorite,
                                    accentColor = palette.accent,
                                    glowColor = palette.vibrant,
                                    onArtistClick = onArtistClick
                                )
                            }
                            PlayerCenterView.QUEUE -> {
                                PlayerQueueView(
                                    queue = playerState.queue,
                                    currentQueueIndex = playerState.currentQueueIndex,
                                    isPlaying = playerState.isPlaying,
                                    onPlayQueueIndex = onPlayQueueIndex,
                                    onMoveQueueItem = onMoveQueueItem,
                                    onRemoveQueueItem = onRemoveQueueItem,
                                    accentColor = palette.accent
                                )
                            }
                            PlayerCenterView.LYRICS -> {
                                PlayerLyricsView(
                                    lyrics = playerState.currentLyrics,
                                    isLyricsLoading = playerState.isLyricsLoading,
                                    currentPositionMs = playerState.currentPositionMs,
                                    onSeekTo = onSeekTo,
                                    onRetryFetchLyrics = onRetryFetchLyrics,
                                    accentColor = palette.accent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                PlayerControlsView(
                    currentPositionMs = playerState.currentPositionMs,
                    durationMs = playerState.durationMs,
                    isPlaying = playerState.isPlaying,
                    isBuffering = playerState.isBuffering,
                    isShuffleEnabled = playerState.isShuffleEnabled,
                    repeatMode = playerState.repeatMode,
                    hasPrevious = playerState.hasPrevious,
                    hasNext = playerState.hasNext,
                    onSeekTo = onSeekTo,
                    onTogglePlayPause = onTogglePlayPause,
                    onSkipNext = onSkipNext,
                    onSkipPrevious = onSkipPrevious,
                    onToggleShuffle = onToggleShuffle,
                    onToggleRepeatMode = onToggleRepeatMode,
                    accentColor = palette.accent
                )
            }
        }
    }
}
