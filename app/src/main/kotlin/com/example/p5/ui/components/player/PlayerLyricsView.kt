package com.example.p5.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.LyricsData
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeDisplay
import com.example.p5.ui.theme.IconSizeHero
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ScaleActiveLyric
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
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
            val activeIndex = lines.indexOfLast { currentPositionMs >= it.timestampMs }
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
