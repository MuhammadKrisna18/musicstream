package com.example.p5.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeXs
import com.example.p5.ui.theme.ArtworkCardHeight
import com.example.p5.ui.theme.ArtworkCardWidth
import com.example.p5.ui.theme.CornerRadiusCardLg
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCard
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun HomeTrackSection(
    section: HomeSection,
    isPlaying: Boolean,
    currentSongId: String?,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onSeeAllClick: (String) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    if (section.tracks.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = section.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                if (!section.subtitle.isNullOrBlank()) {
                    Text(
                        text = section.subtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (!section.query.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.section_see_all),
                    color = ToscaPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onSeeAllClick(section.query) }
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = section.tracks,
                key = { _, item -> item.id }
            ) { index, song ->
                TrendingTrackCard(
                    song = song,
                    isPlaying = isPlaying,
                    isSelected = currentSongId == song.id,
                    onSongClick = { onPlayQueue(section.tracks, index) },
                    onMoreOptionsClick = onMoreOptionsClick
                )
            }
        }
    }
}

@Composable
fun TrendingTrackCard(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onMoreOptionsClick: ((Song) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(CornerRadiusCardLg) }

    Box(
        modifier = modifier
            .width(ArtworkCardWidth)
            .height(ArtworkCardHeight)
            .pressedScale(targetScale = ScalePressedCard, interactionSource = interactionSource)
            .clip(cardShape)
            .background(ObsidianCard)
            .then(
                if (isSelected) {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle,
                        accentGlow = ToscaPrimary.copy(alpha = 0.5f)
                    )
                } else {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.3f),
                        endColor = GlassBorderSubtle
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onSongClick(song) }
    ) {
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            ObsidianBlack.copy(alpha = 0.92f)
                        ),
                        startY = 60f
                    )
                )
        )

        if (isSelected && isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ToscaPrimary.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                MiniEqualizer(
                    barColor = ObsidianBlack,
                    isPlaying = true
                )
            }
        }

        if (onMoreOptionsClick != null) {
            val moreSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(ActionButtonSizeXs)
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = moreSource)
                    .clip(CircleShape)
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                        endColor = GlassBorderSubtle
                    )
                    .clickable(
                        interactionSource = moreSource,
                        indication = null
                    ) { onMoreOptionsClick(song) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(IconSizeSm)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = song.title,
                color = if (isSelected) ToscaPrimary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 17.sp,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = song.artist,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (song.durationFormatted.isNotBlank()) {
                    Text(
                        text = song.durationFormatted,
                        color = TextMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
