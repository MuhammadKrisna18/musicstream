package com.example.p5.ui.components.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.LyricsData
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXxl
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthProgress
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlayerLyricsView(
    lyrics: LyricsData?,
    isLyricsLoading: Boolean,
    currentPositionMs: Long,
    onSeekTo: (Long) -> Unit,
    onRetryFetchLyrics: () -> Unit,
    accentColor: Color = ToscaPrimary,
    modifier: Modifier = Modifier
) {
    val lyricsShape = remember { RoundedCornerShape(CornerRadiusPill) }

    Column(
        modifier = modifier
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
                    color = accentColor,
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
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lyrics,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.player_lyrics_not_found_title),
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.player_lyrics_not_found_desc),
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = SpacingXxl)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetryFetchLyrics,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor.copy(alpha = 0.14f),
                        contentColor = accentColor
                    ),
                    shape = RoundedCornerShape(CornerRadiusPill)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(IconSizeSm)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.player_lyrics_retry),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else if (lyrics.isInstrumental) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.player_instrumental_title),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.player_instrumental_desc),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = SpacingXxl)
                    )
                }
            }
        } else if (lyrics.isSynced && lyrics.lines.isNotEmpty()) {
            val lines = lyrics.lines
            val activeIndex = lines.indexOfLast { currentPositionMs >= it.timestampMs }
            val listState = rememberLazyListState()

            LaunchedEffect(activeIndex) {
                if (activeIndex in lines.indices) {
                    listState.animateScrollToItem(
                        index = (activeIndex - 1).coerceAtLeast(0),
                        scrollOffset = -120
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadiusPill))
                        .background(accentColor.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = stringResource(R.string.player_lyrics_karaoke_badge),
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Text(
                    text = stringResource(R.string.player_lyrics_line_count, lines.size),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(lines) { index, line ->
                    val isActive = index == activeIndex
                    val isPast = index < activeIndex
                    val lineSource = remember { MutableInteractionSource() }

                    val lineScale by animateFloatAsState(
                        targetValue = if (isActive) 1.05f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = 380f),
                        label = "karaoke_scale"
                    )

                    val lineAlpha by animateFloatAsState(
                        targetValue = when {
                            isActive -> 1.0f
                            isPast -> 0.48f
                            else -> 0.36f
                        },
                        animationSpec = tween(durationMillis = 250),
                        label = "karaoke_alpha"
                    )

                    val textColor by animateColorAsState(
                        targetValue = if (isActive) TextPrimary else TextSecondary,
                        animationSpec = tween(durationMillis = 250),
                        label = "karaoke_text_color"
                    )

                    val hasLongBreakBefore = index > 0 && (line.timestampMs - lines[index - 1].timestampMs) > 9000L

                    if (hasLongBreakBefore) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "·  ·  ·",
                                color = accentColor.copy(alpha = 0.45f),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = line.text,
                        fontSize = if (isActive) 21.sp else 16.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                        color = textColor,
                        lineHeight = if (isActive) 29.sp else 23.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(lineScale)
                            .alpha(lineAlpha)
                            .clickable(
                                interactionSource = lineSource,
                                indication = null
                            ) { onSeekTo(line.timestampMs) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.player_lyrics_plain_badge),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadiusPill))
                            .background(GlassBorderSubtle)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = lyrics.plainLyrics,
                        fontSize = 15.sp,
                        color = TextSecondary,
                        lineHeight = 26.sp
                    )
                }
            }
        }
    }
}
