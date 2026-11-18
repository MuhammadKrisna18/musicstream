package com.example.p5.ui.search.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.IconSizeMd
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun SectionDetailContent(
    section: HomeSection,
    playerState: PlayerState,
    onBackClick: () -> Unit,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val playAllInteraction = remember { MutableInteractionSource() }
    val shuffleInteraction = remember { MutableInteractionSource() }
    val backInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .pressedScale(interactionSource = backInteraction)
                    .clip(CircleShape)
                    .background(appColors.card)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = appColors.glassBorderBright,
                        endColor = appColors.glassBorderSubtle
                    )
                    .clickable(
                        interactionSource = backInteraction,
                        indication = null
                    ) { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = appColors.textPrimary,
                    modifier = Modifier.size(IconSizeStandard)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.title,
                    color = appColors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val subtitleText = if (!section.subtitle.isNullOrBlank()) {
                    "${section.tracks.size} lagu • ${section.subtitle}"
                } else {
                    "${section.tracks.size} lagu"
                }
                Text(
                    text = subtitleText,
                    color = appColors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (section.tracks.isNotEmpty()) {
            val buttonShape = remember { RoundedCornerShape(CornerRadiusCard) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .pressedScale(interactionSource = playAllInteraction)
                        .clip(buttonShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(appColors.accent, appColors.accentDark)
                            )
                        )
                        .glassBorder(
                            shape = buttonShape,
                            strokeWidth = StrokeWidthBorder,
                            startColor = appColors.accent.copy(alpha = 0.5f),
                            endColor = appColors.glassBorderSubtle
                        )
                        .clickable(
                            interactionSource = playAllInteraction,
                            indication = null
                        ) {
                            onPlayQueue(section.tracks, 0)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = appColors.onAccent,
                        modifier = Modifier.size(IconSizeStandard)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_play_all),
                        color = appColors.onAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .pressedScale(interactionSource = shuffleInteraction)
                        .clip(buttonShape)
                        .background(appColors.card)
                        .glassBorder(
                            shape = buttonShape,
                            strokeWidth = StrokeWidthBorder,
                            startColor = appColors.glassBorderBright,
                            endColor = appColors.glassBorderSubtle
                        )
                        .clickable(
                            interactionSource = shuffleInteraction,
                            indication = null
                        ) {
                            onPlayQueue(section.tracks.shuffled(), 0)
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(IconSizeMd)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_shuffle),
                        color = appColors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 4.dp, bottom = BottomScrollPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(
                items = section.tracks,
                key = { _, song -> song.id }
            ) { index, song ->
                val isSelected = playerState.currentSong?.id == song.id
                SongItem(
                    song = song,
                    isPlaying = playerState.isPlaying,
                    isSelected = isSelected,
                    onClick = { onPlayQueue(section.tracks, index) },
                    onMoreOptionsClick = onMoreOptionsClick
                )
            }
        }
    }
}
