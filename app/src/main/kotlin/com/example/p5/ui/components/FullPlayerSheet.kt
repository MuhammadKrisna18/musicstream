package com.example.p5.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.ui.components.player.PlayerAlbumView
import com.example.p5.ui.components.player.PlayerCenterView
import com.example.p5.ui.components.player.PlayerControlsView
import com.example.p5.ui.components.player.PlayerLyricsView
import com.example.p5.ui.components.player.PlayerQueueView
import com.example.p5.ui.components.player.PlayerTopBar
import com.example.p5.ui.theme.AuroraCyan
import com.example.p5.ui.theme.AuroraDeepIndigo
import com.example.p5.ui.theme.DurationAuroraFlow
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.DurationNormal
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingXxl
import com.example.p5.ui.theme.ToscaPrimary
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
    onSetSleepTimerMinutes: (Int) -> Unit,
    onSetSleepTimerEndOfTrack: () -> Unit,
    onCancelSleepTimer: () -> Unit,
    onRetryFetchLyrics: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
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

    val auroraTransition = rememberInfiniteTransition(label = "aurora_flow")
    val auroraOffset1 by auroraTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DurationAuroraFlow, easing = FastOutSlowInEasing),
            repeatMode = AnimRepeatMode.Reverse
        ),
        label = "aurora1"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianBlack,
        dragHandle = null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.bg_anime_midnight),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.35f,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                palette.darkVibrant.copy(alpha = 0.70f),
                                ObsidianBlack.copy(alpha = 0.88f),
                                ObsidianBlack
                            ),
                            startY = auroraOffset1,
                            endY = 1600f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                palette.vibrant.copy(alpha = 0.20f),
                                palette.lightVibrant.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            radius = 650f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                    glowColor = palette.vibrant
                                )
                            }
                            PlayerCenterView.QUEUE -> {
                                PlayerQueueView(
                                    queue = playerState.queue,
                                    currentQueueIndex = playerState.currentQueueIndex,
                                    isPlaying = playerState.isPlaying,
                                    onPlayQueueIndex = onPlayQueueIndex,
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
