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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AuroraCyan
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianFloating
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlowSubtle
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
    val islandShape = remember { RoundedCornerShape(22.dp) }
    val playInteractionSource = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = currentSong != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(durationMillis = 250)),
        modifier = modifier
    ) {
        if (currentSong == null) return@AnimatedVisibility

        val infiniteTransition = rememberInfiniteTransition(label = "island_art_rotate")
        val rotationAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing)
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
                .ambientGlow(
                    color = ToscaGlowSubtle,
                    alpha = if (playerState.isPlaying) 0.6f else 0.2f,
                    shape = islandShape
                )
                .clip(islandShape)
                .background(ObsidianFloating)
                .then(
                    if (playerState.isPlaying) {
                        Modifier.glassBorder(
                            shape = islandShape,
                            strokeWidth = 1.dp,
                            startColor = ToscaGlassBorder,
                            endColor = GlassBorderSubtle
                        )
                    } else {
                        Modifier.glassBorder(
                            shape = islandShape,
                            strokeWidth = 1.dp,
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
                        .padding(start = 10.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianSurfaceVariant)
                            .glassBorder(
                                shape = RoundedCornerShape(14.dp),
                                strokeWidth = 0.5.dp,
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

                    Spacer(modifier = Modifier.width(12.dp))

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

                    Spacer(modifier = Modifier.width(8.dp))

                    if (playerState.isBuffering) {
                        CircularProgressIndicator(
                            color = ToscaPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .pressedScale(
                                    targetScale = 0.9f,
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
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onSkipNext,
                        enabled = playerState.hasNext,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SkipNext,
                            contentDescription = stringResource(R.string.mini_player_next_desc),
                            tint = if (playerState.hasNext) TextPrimary else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .background(GlassFillSubtle)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .height(2.5.dp)
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
