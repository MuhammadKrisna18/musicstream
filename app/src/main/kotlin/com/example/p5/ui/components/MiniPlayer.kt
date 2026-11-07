package com.example.p5.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.ArtworkThumbSize
import com.example.p5.ui.theme.AuroraCyan
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.DurationAtmospheric
import com.example.p5.ui.theme.DurationMedium
import com.example.p5.ui.theme.DurationNormal
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianFloating
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.PlayButtonSizeMini
import com.example.p5.ui.theme.ProgressBarHeightMini
import com.example.p5.ui.theme.ScalePressedRound
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthHairline
import com.example.p5.ui.theme.StrokeWidthThick
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun MiniPlayer(
    playerState: PlayerState,
    onTogglePlayPause: () -> Unit,
    onOpenFullPlayer: () -> Unit,
    modifier: Modifier = Modifier,
    onSkipNext: () -> Unit = {}
) {
    val currentSong = playerState.currentSong
    val islandShape = remember { RoundedCornerShape(CornerRadiusPill) }
    val playInteractionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = currentSong != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = DurationMedium)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = DurationMedium)
        ) + fadeOut(animationSpec = tween(durationMillis = DurationNormal)),
        modifier = modifier
    ) {
        if (currentSong == null) return@AnimatedVisibility

        val infiniteTransition = rememberInfiniteTransition(label = "island_art_rotate")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = DurationAtmospheric, easing = LinearEasing)
            ),
            label = "island_art_angle"
        )

        val progress = if (playerState.durationMs > 0) {
            (playerState.currentPositionMs.toFloat() / playerState.durationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .clip(islandShape)
                .background(ObsidianFloating)
                .then(
                    if (playerState.isPlaying) {
                        Modifier.glassBorder(
                            shape = islandShape,
                            strokeWidth = StrokeWidthBorder,
                            startColor = ToscaGlassBorder,
                            endColor = GlassBorderSubtle
                        )
                    } else {
                        Modifier.glassBorder(
                            shape = islandShape,
                            strokeWidth = StrokeWidthBorder,
                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                            endColor = GlassBorderSubtle
                        )
                    }
                )
                .clickable(onClick = onOpenFullPlayer)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 12.dp, top = SpacingSm, bottom = SpacingSm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(ArtworkThumbSize)
                            .clip(RoundedCornerShape(CornerRadiusCard))
                            .background(ObsidianSurfaceVariant)
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusCard),
                                strokeWidth = StrokeWidthHairline,
                                startColor = GlassBorderMedium,
                                endColor = Color.Transparent
                            )
                    ) {
                        AsyncImage(
                            model = currentSong.thumbnailUrl,
                            contentDescription = currentSong.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(if (playerState.isPlaying) rotationAngle else 0f)
                        )
                    }

                    Spacer(modifier = Modifier.width(SpacingMd))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentSong.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentSong.artist,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(SpacingSm))

                    if (playerState.isBuffering) {
                        CircularProgressIndicator(
                            color = ToscaPrimary,
                            strokeWidth = StrokeWidthThick,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(PlayButtonSizeMini)
                                .pressedScale(
                                    targetScale = ScalePressedRound,
                                    interactionSource = playInteractionSource
                                )
                                .clip(CircleShape)
                                .background(ToscaPrimary)
                                .clickable(
                                    interactionSource = playInteractionSource,
                                    indication = null,
                                    onClick = onTogglePlayPause
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = ObsidianBlack,
                                modifier = Modifier.size(IconSizeStandard)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpacingXs))

                    IconButton(
                        onClick = onSkipNext,
                        enabled = playerState.hasNext,
                        modifier = Modifier.size(ActionButtonSizeSm)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = stringResource(R.string.mini_player_next_desc),
                            tint = if (playerState.hasNext) TextPrimary else TextMuted,
                            modifier = Modifier.size(IconSizeStandard)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ProgressBarHeightMini)
                        .background(GlassFillSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .height(ProgressBarHeightMini)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(ToscaPrimary, AuroraCyan)
                                )
                            )
                    )
                }
            }
        }
    }
}
