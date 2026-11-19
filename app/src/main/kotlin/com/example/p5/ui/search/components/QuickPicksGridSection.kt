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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeXs
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun QuickPicksGridSection(
    section: HomeSection,
    isPlaying: Boolean,
    currentSongId: String?,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onSeeAllClick: (HomeSection) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    if (section.tracks.isEmpty()) return

    val appColors = AppTheme.colors
    val columns = remember(section.tracks) {
        section.tracks.take(15).chunked(3)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.title,
                    color = appColors.textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                if (!section.subtitle.isNullOrBlank()) {
                    Text(
                        text = section.subtitle,
                        color = appColors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = stringResource(R.string.section_see_all),
                color = appColors.accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSeeAllClick(section) }
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            itemsIndexed(
                items = columns,
                key = { colIndex, colSongs -> "quick_col_${colIndex}_${colSongs.firstOrNull()?.id}" }
            ) { colIndex, columnSongs ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.width(278.dp)
                ) {
                    columnSongs.forEachIndexed { rowIndex, song ->
                        val globalIndex = colIndex * 3 + rowIndex
                        QuickPickCompactItem(
                            song = song,
                            isPlaying = isPlaying,
                            isSelected = currentSongId == song.id,
                            onClick = { onPlayQueue(section.tracks, globalIndex) },
                            onMoreOptionsClick = { onMoreOptionsClick(song) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPickCompactItem(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onMoreOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val moreSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(CornerRadiusCard) }
    val thumbShape = remember { RoundedCornerShape(10.dp) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .pressedScale(targetScale = ScalePressedCard, interactionSource = interactionSource)
            .clip(cardShape)
            .background(if (isSelected) appColors.cardHigh else appColors.card)
            .then(
                if (isSelected) {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = appColors.accent.copy(alpha = 0.55f),
                        endColor = appColors.glassBorderSubtle,
                        accentGlow = appColors.accentGlow
                    )
                } else {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = appColors.glassBorderBright.copy(alpha = 0.25f),
                        endColor = appColors.glassBorderSubtle
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(thumbShape)
                .background(appColors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = song.thumbnailUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected && isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appColors.background.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    MiniEqualizer(
                        barColor = appColors.accent,
                        isPlaying = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = song.title,
                color = if (isSelected) appColors.accent else appColors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            val metaText = if (song.durationFormatted.isNotBlank()) {
                "${song.artist} • ${song.durationFormatted}"
            } else {
                song.artist
            }
            Text(
                text = metaText,
                color = appColors.textSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Box(
            modifier = Modifier
                .size(ActionButtonSizeXs)
                .pressedScale(targetScale = ScalePressedButton, interactionSource = moreSource)
                .clip(CircleShape)
                .background(appColors.glassFillSubtle)
                .clickable(
                    interactionSource = moreSource,
                    indication = null,
                    onClick = onMoreOptionsClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = null,
                tint = appColors.textSecondary,
                modifier = Modifier.size(IconSizeSm)
            )
        }
    }
}
