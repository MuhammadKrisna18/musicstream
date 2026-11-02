package com.example.p5.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.RepeatMode
import com.example.p5.ui.theme.BlackBackground
import com.example.p5.ui.theme.DarkSurface
import com.example.p5.ui.theme.DarkSurfaceVariant
import com.example.p5.ui.theme.OrangePrimary
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary

enum class PlayerCenterView {
    ALBUM,
    QUEUE,
    LYRICS
}

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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BlackBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Tutup",
                        tint = TextPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = when (centerView) {
                        PlayerCenterView.ALBUM -> "NOW PLAYING"
                        PlayerCenterView.QUEUE -> "ANTREAN PUTAR"
                        PlayerCenterView.LYRICS -> "LIRIK LAGU"
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sleep Timer Button
                    IconButton(
                        onClick = { showSleepTimerDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bedtime,
                            contentDescription = "Sleep Timer",
                            tint = if (playerState.isSleepTimerActive) OrangePrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Lyrics Button
                    IconButton(
                        onClick = {
                            centerView = if (centerView == PlayerCenterView.LYRICS) PlayerCenterView.ALBUM else PlayerCenterView.LYRICS
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lyrics,
                            contentDescription = "Lirik",
                            tint = if (centerView == PlayerCenterView.LYRICS) OrangePrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Queue Button
                    IconButton(
                        onClick = {
                            centerView = if (centerView == PlayerCenterView.QUEUE) PlayerCenterView.ALBUM else PlayerCenterView.QUEUE
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = "Antrean",
                            tint = if (centerView == PlayerCenterView.QUEUE) OrangePrimary else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Body: Album Art / Queue / Lyrics
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = centerView,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "center_view_switch"
                ) { currentView ->
                    when (currentView) {
                        PlayerCenterView.ALBUM -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val albumScale by animateFloatAsState(
                                    targetValue = if (playerState.isPlaying) 1.0f else 0.92f,
                                    animationSpec = spring(dampingRatio = 0.7f),
                                    label = "album_scale"
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .aspectRatio(1f)
                                        .scale(albumScale)
                                        .shadow(
                                            elevation = 20.dp,
                                            shape = RoundedCornerShape(20.dp),
                                            spotColor = OrangePrimary.copy(alpha = 0.35f)
                                        )
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkSurfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = currentSong.thumbnailUrl,
                                        contentDescription = currentSong.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(28.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = currentSong.title,
                                            color = TextPrimary,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = currentSong.artist,
                                            color = OrangePrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = onToggleFavorite,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (playerState.isCurrentSongFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                            contentDescription = if (playerState.isCurrentSongFavorite) "Hapus dari Favorit" else "Tambah ke Favorit",
                                            tint = if (playerState.isCurrentSongFavorite) Color(0xFFFF3B30) else TextMuted,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }

                        PlayerCenterView.QUEUE -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurface)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Lagu Berikutnya (${playerState.queue.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )

                                if (playerState.queue.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Tidak ada antrean", color = TextMuted, fontSize = 13.sp)
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        itemsIndexed(playerState.queue) { index, song ->
                                            val isCurrent = index == playerState.currentQueueIndex
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isCurrent) DarkSurfaceVariant else DarkSurface)
                                                    .then(
                                                        if (isCurrent) Modifier.border(
                                                            1.dp,
                                                            OrangePrimary.copy(alpha = 0.5f),
                                                            RoundedCornerShape(10.dp)
                                                        ) else Modifier
                                                    )
                                                    .clickable { onPlayQueueIndex(index) }
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier.size(24.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isCurrent && playerState.isPlaying) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.GraphicEq,
                                                            contentDescription = null,
                                                            tint = OrangePrimary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    } else {
                                                        Text(
                                                            text = "${index + 1}",
                                                            color = if (isCurrent) OrangePrimary else TextMuted,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                AsyncImage(
                                                    model = song.thumbnailUrl,
                                                    contentDescription = song.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                )

                                                Spacer(modifier = Modifier.width(10.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = song.title,
                                                        color = if (isCurrent) OrangePrimary else TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = song.artist,
                                                        color = TextSecondary,
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(8.dp))

                                                Text(
                                                    text = song.durationFormatted,
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PlayerCenterView.LYRICS -> {
                            val lyrics = playerState.currentLyrics
                            val isLyricsLoading = playerState.isLyricsLoading

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DarkSurface)
                                    .padding(14.dp)
                            ) {
                                if (isLyricsLoading) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = OrangePrimary,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Mencari lirik...",
                                            color = TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                } else if (lyrics == null) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Lyrics,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Lirik belum ditemukan",
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Lirik untuk lagu ini belum tersedia di database",
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = onRetryFetchLyrics,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DarkSurfaceVariant,
                                                contentColor = OrangePrimary
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Refresh,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Coba Lagi", fontSize = 12.sp)
                                        }
                                    }
                                } else if (lyrics.isInstrumental) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "♪ Musik Instrumental ♪\n(Lagu ini tidak memiliki vokal)",
                                            color = TextSecondary,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else if (lyrics.isSynced && lyrics.lines.isNotEmpty()) {
                                    val lines = lyrics.lines
                                    val activeIndex = lines.indexOfLast { playerState.currentPositionMs >= it.timestampMs }
                                    val listState = rememberLazyListState()

                                    LaunchedEffect(activeIndex) {
                                        if (activeIndex >= 0) {
                                            listState.animateScrollToItem((activeIndex - 2).coerceAtLeast(0))
                                        }
                                    }

                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(vertical = 40.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        itemsIndexed(lines) { index, line ->
                                            val isActive = index == activeIndex
                                            val isPast = index < activeIndex

                                            Text(
                                                text = line.text,
                                                fontSize = if (isActive) 19.sp else 15.sp,
                                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                                color = when {
                                                    isActive -> OrangePrimary
                                                    isPast -> TextSecondary
                                                    else -> TextMuted
                                                },
                                                lineHeight = 26.sp,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onSeekTo(line.timestampMs) }
                                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Plain lyrics
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = lyrics.plainLyrics,
                                            fontSize = 15.sp,
                                            color = TextSecondary,
                                            lineHeight = 24.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Slider & Timers
            var isDraggingSlider by remember { mutableStateOf(false) }
            var dragPositionMs by remember { mutableFloatStateOf(0f) }

            val currentMs = if (isDraggingSlider) dragPositionMs.toLong() else playerState.currentPositionMs
            val totalMs = playerState.durationMs.coerceAtLeast(1L)
            val sliderValue = (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

            Slider(
                value = sliderValue,
                onValueChange = { fraction ->
                    isDraggingSlider = true
                    dragPositionMs = fraction * totalMs
                },
                onValueChangeFinished = {
                    isDraggingSlider = false
                    onSeekTo(dragPositionMs.toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = OrangePrimary,
                    activeTrackColor = OrangePrimary,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDurationMs(currentMs),
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = formatDurationMs(playerState.durationMs),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback Controls (Shuffle, Prev, Play/Pause, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle Button
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Acak",
                        tint = if (playerState.isShuffleEnabled) OrangePrimary else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous Button
                IconButton(
                    onClick = onSkipPrevious,
                    enabled = playerState.hasPrevious
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipPrevious,
                        contentDescription = "Sebelumnya",
                        tint = if (playerState.hasPrevious) TextPrimary else TextMuted,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(elevation = 12.dp, shape = CircleShape, spotColor = OrangePrimary)
                        .clip(CircleShape)
                        .background(OrangePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    if (playerState.isBuffering) {
                        CircularProgressIndicator(
                            color = BlackBackground,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Jeda" else "Putar",
                                tint = BlackBackground,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Next Button
                IconButton(
                    onClick = onSkipNext,
                    enabled = playerState.hasNext
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SkipNext,
                        contentDescription = "Berikutnya",
                        tint = if (playerState.hasNext) TextPrimary else TextMuted,
                        modifier = Modifier.size(34.dp)
                    )
                }

                // Repeat Button
                IconButton(onClick = onToggleRepeatMode) {
                    Icon(
                        imageVector = if (playerState.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                        contentDescription = "Ulangi",
                        tint = if (playerState.repeatMode != RepeatMode.OFF) OrangePrimary else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

private fun formatDurationMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
