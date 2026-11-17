package com.example.p5.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.RepeatMode
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.ActionButtonSizeXl
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.IconSizeXl
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.PlayButtonSizeFull
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedRound
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthProgress
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlayerControlsView(
    currentPositionMs: Long,
    durationMs: Long,
    isPlaying: Boolean,
    isBuffering: Boolean,
    isShuffleEnabled: Boolean,
    repeatMode: RepeatMode,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onSeekTo: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeatMode: () -> Unit,
    accentColor: Color = ToscaPrimary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PlayerProgressBar(
            currentPositionMs = currentPositionMs,
            durationMs = durationMs,
            onSeekTo = onSeekTo,
            accentColor = accentColor
        )

        Spacer(modifier = Modifier.height(SpacingLg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerIconButton(
                icon = Icons.Rounded.Shuffle,
                contentDescription = stringResource(R.string.player_shuffle_desc),
                isActive = isShuffleEnabled,
                onClick = onToggleShuffle,
                accentColor = accentColor
            )

            PlayerIconButton(
                icon = Icons.Rounded.SkipPrevious,
                contentDescription = stringResource(R.string.player_previous_desc),
                enabled = hasPrevious,
                size = IconSizeXl,
                onClick = onSkipPrevious,
                accentColor = accentColor
            )

            val playSource = remember { MutableInteractionSource() }
            val iconTint = if (accentColor.luminance() > 0.45f) ObsidianBlack else Color.White
            Box(
                modifier = Modifier
                    .size(PlayButtonSizeFull)
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = playSource)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = Color.White.copy(alpha = 0.45f),
                        endColor = accentColor.copy(alpha = 0.20f),
                        accentGlow = accentColor.copy(alpha = 0.50f)
                    )
                    .clip(CircleShape)
                    .background(accentColor)
                    .clickable(
                        interactionSource = playSource,
                        indication = null,
                        onClick = onTogglePlayPause
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        color = iconTint,
                        strokeWidth = StrokeWidthProgress,
                        modifier = Modifier.size(IconSizeXl)
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) stringResource(R.string.action_pause) else stringResource(R.string.action_play),
                        tint = iconTint,
                        modifier = Modifier.size(ActionButtonSizeSm)
                    )
                }
            }

            PlayerIconButton(
                icon = Icons.Rounded.SkipNext,
                contentDescription = stringResource(R.string.mini_player_next_desc),
                enabled = hasNext,
                size = IconSizeXl,
                onClick = onSkipNext,
                accentColor = accentColor
            )

            PlayerIconButton(
                icon = if (repeatMode == RepeatMode.ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                contentDescription = stringResource(R.string.player_repeat_desc),
                isActive = repeatMode != RepeatMode.OFF,
                onClick = onToggleRepeatMode,
                accentColor = accentColor
            )
        }
    }
}

@Composable
fun PlayerProgressBar(
    currentPositionMs: Long,
    durationMs: Long,
    onSeekTo: (Long) -> Unit,
    accentColor: Color = ToscaPrimary,
    modifier: Modifier = Modifier
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragPositionMs by remember { mutableFloatStateOf(0f) }

    val currentMs = if (isDraggingSlider) dragPositionMs.toLong() else currentPositionMs
    val totalMs = durationMs.coerceAtLeast(1L)
    val sliderValue = (currentMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
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
                thumbColor = accentColor,
                activeTrackColor = accentColor,
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
                text = formatDurationMs(durationMs),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PlayerIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isActive: Boolean = false,
    accentColor: Color = ToscaPrimary,
    size: Dp = 24.dp
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
                isActive -> accentColor
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
