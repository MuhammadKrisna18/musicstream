package com.example.p5.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode as AnimRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MusicNote
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.RepeatMode
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeLg
import com.example.p5.ui.theme.ActionButtonSizeMd
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.ActionButtonSizeXl
import com.example.p5.ui.theme.ArtworkQueueThumbSize
import com.example.p5.ui.theme.AuroraCyan
import com.example.p5.ui.theme.AuroraDeepIndigo
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.CornerRadiusModal
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.DurationAuroraFlow
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.DurationNormal
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.IconSizeAction
import com.example.p5.ui.theme.IconSizeDisplay
import com.example.p5.ui.theme.IconSizeHero
import com.example.p5.ui.theme.IconSizeLg
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.IconSizeXl
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.PlayButtonSizeFull
import com.example.p5.ui.theme.ScaleActiveLyric
import com.example.p5.ui.theme.ScaleAlbumPaused
import com.example.p5.ui.theme.ScaleAlbumPlaying
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedGlass
import com.example.p5.ui.theme.ScalePressedIcon
import com.example.p5.ui.theme.ScalePressedRound
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXl
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.SpacingXxl
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthProgress
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlow
import com.example.p5.ui.theme.ToscaPrimary

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AuroraDeepIndigo.copy(alpha = 0.55f),
                                ObsidianBlack,
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
                                ToscaPrimary.copy(alpha = 0.14f),
                                AuroraCyan.copy(alpha = 0.08f),
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
                Box(
                    modifier = Modifier
                        .size(width = ActionButtonSizeMd, height = SpacingXs)
                        .clip(CircleShape)
                        .background(GlassBorderBright.copy(alpha = 0.45f))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val dismissSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(ActionButtonSizeMd)
                            .pressedScale(targetScale = ScalePressedButton, interactionSource = dismissSource)
                            .clip(CircleShape)
                            .background(GlassFillLight)
                            .glassBorder(
                                shape = CircleShape,
                                strokeWidth = StrokeWidthBorder,
                                startColor = GlassBorderBright.copy(alpha = 0.35f),
                                endColor = GlassBorderSubtle
                            )
                            .clickable(
                                interactionSource = dismissSource,
                                indication = null,
                                onClick = onDismiss
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.player_dismiss_desc),
                            tint = TextPrimary,
                            modifier = Modifier.size(IconSizeAction)
                        )
                    }

                    Text(
                        text = when (centerView) {
                            PlayerCenterView.ALBUM -> stringResource(R.string.player_header_now_playing)
                            PlayerCenterView.QUEUE -> stringResource(R.string.player_header_queue)
                            PlayerCenterView.LYRICS -> stringResource(R.string.player_header_lyrics)
                        },
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlayerTopActionIcon(
                            icon = Icons.Rounded.Bedtime,
                            isActive = playerState.isSleepTimerActive,
                            onClick = { showSleepTimerDialog = true }
                        )

                        PlayerTopActionIcon(
                            icon = Icons.Rounded.Lyrics,
                            isActive = centerView == PlayerCenterView.LYRICS,
                            onClick = {
                                centerView = if (centerView == PlayerCenterView.LYRICS) PlayerCenterView.ALBUM else PlayerCenterView.LYRICS
                            }
                        )

                        PlayerTopActionIcon(
                            icon = Icons.AutoMirrored.Rounded.QueueMusic,
                            isActive = centerView == PlayerCenterView.QUEUE,
                            onClick = {
                                centerView = if (centerView == PlayerCenterView.QUEUE) PlayerCenterView.ALBUM else PlayerCenterView.QUEUE
                            }
                        )
                    }
                }

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
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val albumScale by animateFloatAsState(
                                        targetValue = if (playerState.isPlaying) ScaleAlbumPlaying else ScaleAlbumPaused,
                                        animationSpec = spring(dampingRatio = 0.72f, stiffness = 300f),
                                        label = "album_scale"
                                    )

                                    val albumShape = remember { RoundedCornerShape(CornerRadiusModal) }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .aspectRatio(1f)
                                            .scale(albumScale)
                                            .ambientGlow(
                                                color = ToscaGlow,
                                                alpha = if (playerState.isPlaying) 0.55f else 0.2f,
                                                shape = albumShape
                                            )
                                            .clip(albumShape)
                                            .background(ObsidianCardHigh)
                                            .glassBorder(
                                                shape = albumShape,
                                                strokeWidth = StrokeWidthBorder,
                                                startColor = GlassBorderBright.copy(alpha = 0.55f),
                                                endColor = GlassBorderSubtle
                                            )
                                    ) {
                                        AsyncImage(
                                            model = currentSong.thumbnailUrl,
                                            contentDescription = currentSong.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(CornerRadiusModal))

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
                                                color = ToscaPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(top = SpacingXs)
                                            )
                                        }

                                        val favSource = remember { MutableInteractionSource() }
                                        Box(
                                            modifier = Modifier
                                                .size(ActionButtonSizeLg)
                                                .pressedScale(targetScale = ScalePressedIcon, interactionSource = favSource)
                                                .clip(CircleShape)
                                                .background(GlassFillLight)
                                                .glassBorder(
                                                    shape = CircleShape,
                                                    strokeWidth = StrokeWidthBorder,
                                                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                                                    endColor = GlassBorderSubtle
                                                )
                                                .clickable(
                                                    interactionSource = favSource,
                                                    indication = null,
                                                    onClick = onToggleFavorite
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (playerState.isCurrentSongFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                                contentDescription = if (playerState.isCurrentSongFavorite) stringResource(R.string.library_remove_favorite_desc) else stringResource(R.string.library_add_favorite_desc),
                                                tint = if (playerState.isCurrentSongFavorite) Color(0xFFFF3B30) else TextMuted,
                                                modifier = Modifier.size(IconSizeLg)
                                            )
                                        }
                                    }
                                }
                            }

                            PlayerCenterView.QUEUE -> {
                                val queueShape = remember { RoundedCornerShape(CornerRadiusPill) }
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(queueShape)
                                        .background(GlassFillLight)
                                        .glassBorder(
                                            shape = queueShape,
                                            strokeWidth = StrokeWidthBorder,
                                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                                            endColor = GlassBorderSubtle
                                        )
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.player_queue_title, playerState.queue.size),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = SpacingSm, vertical = 6.dp)
                                    )

                                    if (playerState.queue.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(stringResource(R.string.player_queue_empty), color = TextMuted, fontSize = 13.sp)
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            itemsIndexed(playerState.queue) { index, song ->
                                                val isCurrent = index == playerState.currentQueueIndex
                                                val rowShape = remember { RoundedCornerShape(CornerRadiusMd) }
                                                val rowSource = remember { MutableInteractionSource() }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .pressedScale(targetScale = ScalePressedGlass, interactionSource = rowSource)
                                                        .clip(rowShape)
                                                        .background(if (isCurrent) ToscaPrimary.copy(alpha = 0.14f) else GlassFillSubtle)
                                                        .then(
                                                            if (isCurrent) {
                                                                Modifier.glassBorder(
                                                                    shape = rowShape,
                                                                    strokeWidth = StrokeWidthBorder,
                                                                    startColor = ToscaGlassBorder,
                                                                    endColor = GlassBorderSubtle
                                                                )
                                                            } else {
                                                                Modifier.glassBorder(
                                                                    shape = rowShape,
                                                                    strokeWidth = StrokeWidthStandard,
                                                                    startColor = GlassBorderBright.copy(alpha = 0.2f),
                                                                    endColor = Color.Transparent
                                                                )
                                                            }
                                                        )
                                                        .clickable(
                                                            interactionSource = rowSource,
                                                            indication = null
                                                        ) { onPlayQueueIndex(index) }
                                                        .padding(horizontal = 10.dp, vertical = SpacingSm),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier.size(IconSizeLg),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (isCurrent && playerState.isPlaying) {
                                                            MiniEqualizer(isPlaying = true)
                                                        } else {
                                                            Text(
                                                                text = "${index + 1}",
                                                                color = if (isCurrent) ToscaPrimary else TextMuted,
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(SpacingSm))

                                                    AsyncImage(
                                                        model = song.thumbnailUrl,
                                                        contentDescription = song.title,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(ArtworkQueueThumbSize)
                                                            .clip(RoundedCornerShape(CornerRadiusSm))
                                                    )

                                                    Spacer(modifier = Modifier.width(10.dp))

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = song.title,
                                                            color = if (isCurrent) ToscaPrimary else TextPrimary,
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

                                                    Spacer(modifier = Modifier.width(SpacingSm))

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
                                val lyricsShape = remember { RoundedCornerShape(CornerRadiusPill) }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(lyricsShape)
                                        .background(GlassFillLight)
                                        .glassBorder(
                                            shape = lyricsShape,
                                            strokeWidth = StrokeWidthBorder,
                                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                                            endColor = GlassBorderSubtle
                                        )
                                        .padding(SpacingLg)
                                ) {
                                    if (isLyricsLoading) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator(
                                                color = ToscaPrimary,
                                                strokeWidth = StrokeWidthProgress,
                                                modifier = Modifier.size(ActionButtonSizeSm)
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Text(
                                                text = stringResource(R.string.player_lyrics_loading),
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
                                                modifier = Modifier.size(IconSizeDisplay)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = stringResource(R.string.player_lyrics_not_found_title),
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = stringResource(R.string.player_lyrics_not_found_desc),
                                                color = TextSecondary,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = SpacingXxl, vertical = SpacingXs)
                                            )
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Button(
                                                onClick = onRetryFetchLyrics,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = ToscaPrimary.copy(alpha = 0.16f),
                                                    contentColor = ToscaPrimary
                                                ),
                                                shape = RoundedCornerShape(CornerRadiusMd)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Refresh,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(IconSizeSm)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(stringResource(R.string.player_lyrics_retry), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                    } else if (lyrics.isInstrumental) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Rounded.MusicNote,
                                                    contentDescription = null,
                                                    tint = ToscaPrimary,
                                                    modifier = Modifier.size(IconSizeHero)
                                                )
                                                Spacer(modifier = Modifier.height(SpacingSm))
                                                Text(
                                                    text = stringResource(R.string.player_instrumental_title),
                                                    color = TextPrimary,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = stringResource(R.string.player_instrumental_desc),
                                                    color = TextSecondary,
                                                    fontSize = 13.sp
                                                )
                                            }
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
                                            contentPadding = PaddingValues(vertical = 48.dp),
                                            verticalArrangement = Arrangement.spacedBy(18.dp)
                                        ) {
                                            itemsIndexed(lines) { index, line ->
                                                val isActive = index == activeIndex
                                                val isPast = index < activeIndex
                                                val lineSource = remember { MutableInteractionSource() }

                                                Text(
                                                    text = line.text,
                                                    fontSize = if (isActive) 21.sp else 16.sp,
                                                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                                    color = when {
                                                        isActive -> TextPrimary
                                                        isPast -> TextSecondary.copy(alpha = 0.5f)
                                                        else -> TextMuted.copy(alpha = 0.45f)
                                                    },
                                                    lineHeight = if (isActive) 29.sp else 23.sp,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .scale(if (isActive) ScaleActiveLyric else 1.0f)
                                                        .clickable(
                                                            interactionSource = lineSource,
                                                            indication = null
                                                        ) { onSeekTo(line.timestampMs) }
                                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .verticalScroll(rememberScrollState())
                                                .padding(10.dp)
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
                        thumbColor = ToscaPrimary,
                        activeTrackColor = ToscaPrimary,
                        inactiveTrackColor = GlassBorderSubtle
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
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = formatDurationMs(playerState.durationMs),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(SpacingLg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerIconButton(
                        icon = Icons.Rounded.Shuffle,
                        contentDescription = stringResource(R.string.player_shuffle_desc),
                        isActive = playerState.isShuffleEnabled,
                        onClick = onToggleShuffle
                    )

                    PlayerIconButton(
                        icon = Icons.Rounded.SkipPrevious,
                        contentDescription = stringResource(R.string.player_previous_desc),
                        enabled = playerState.hasPrevious,
                        size = IconSizeXl,
                        onClick = onSkipPrevious
                    )

                    val playSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .size(PlayButtonSizeFull)
                            .ambientGlow(
                                color = ToscaGlow,
                                alpha = if (playerState.isPlaying) 0.8f else 0.3f,
                                shape = CircleShape
                            )
                            .pressedScale(targetScale = ScalePressedButton, interactionSource = playSource)
                            .clip(CircleShape)
                            .background(ToscaPrimary)
                            .clickable(
                                interactionSource = playSource,
                                indication = null,
                                onClick = onTogglePlayPause
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (playerState.isBuffering) {
                            CircularProgressIndicator(
                                color = ObsidianBlack,
                                strokeWidth = StrokeWidthProgress,
                                modifier = Modifier.size(IconSizeXl)
                            )
                        } else {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = if (playerState.isPlaying) stringResource(R.string.action_pause) else stringResource(R.string.action_play),
                                tint = ObsidianBlack,
                                modifier = Modifier.size(ActionButtonSizeSm)
                            )
                        }
                    }

                    PlayerIconButton(
                        icon = Icons.Rounded.SkipNext,
                        contentDescription = stringResource(R.string.mini_player_next_desc),
                        enabled = playerState.hasNext,
                        size = IconSizeXl,
                        onClick = onSkipNext
                    )

                    PlayerIconButton(
                        icon = if (playerState.repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                        contentDescription = stringResource(R.string.player_repeat_desc),
                        isActive = playerState.repeatMode != RepeatMode.OFF,
                        onClick = onToggleRepeatMode
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerTopActionIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(ActionButtonSizeSm)
            .pressedScale(targetScale = ScalePressedRound, interactionSource = interactionSource)
            .clip(CircleShape)
            .background(if (isActive) ToscaPrimary.copy(alpha = 0.16f) else GlassFillLight)
            .then(
                if (isActive) {
                    Modifier.glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle
                    )
                } else {
                    Modifier.glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthStandard,
                        startColor = GlassBorderBright.copy(alpha = 0.25f),
                        endColor = Color.Transparent
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) ToscaPrimary else TextSecondary,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun PlayerIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isActive: Boolean = false,
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(ActionButtonSizeXl)
            .pressedScale(targetScale = ScalePressedRound, interactionSource = interactionSource)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> TextMuted.copy(alpha = 0.5f)
                isActive -> ToscaPrimary
                else -> TextPrimary
            },
            modifier = Modifier.size(size)
        )
    }
}

private fun formatDurationMs(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
