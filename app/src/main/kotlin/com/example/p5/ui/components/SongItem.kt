package com.example.p5.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ArtworkTrackSize
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.DurationNormal
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedSubtle
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthHairline
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaContainer
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    onMoreOptionsClick: ((Song) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(CornerRadiusCard) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            ToscaPrimary.copy(alpha = 0.12f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = DurationNormal),
        label = "song_item_bg"
    )

    val titleColor by animateColorAsState(
        targetValue = if (isSelected) ToscaPrimary else TextPrimary,
        animationSpec = tween(durationMillis = DurationFast),
        label = "song_item_title_color"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressedScale(targetScale = ScalePressedSubtle, interactionSource = interactionSource)
            .clip(cardShape)
            .background(backgroundColor)
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
                        strokeWidth = StrokeWidthStandard,
                        startColor = GlassBorderSubtle.copy(alpha = 0.35f),
                        endColor = Color.Transparent
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = SpacingSm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(ArtworkTrackSize)
                .clip(RoundedCornerShape(CornerRadiusMd))
                .background(ObsidianSurfaceVariant)
                .glassBorder(
                    shape = RoundedCornerShape(CornerRadiusMd),
                    strokeWidth = StrokeWidthHairline,
                    startColor = GlassBorderMedium,
                    endColor = Color.Transparent
                )
        ) {
            AsyncImage(
                model = song.thumbnailUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isPlaying) {
                                ToscaContainer.copy(alpha = 0.72f)
                            } else {
                                Color.Black.copy(alpha = 0.55f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    MiniEqualizer(
                        barColor = if (isPlaying) ToscaPrimary else TextSecondary,
                        isPlaying = isPlaying
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = song.artist,
                color = TextSecondary,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.width(SpacingSm))

        if (trailingContent != null) {
            trailingContent()
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (song.durationFormatted.isNotBlank()) {
                    Text(
                        text = song.durationFormatted,
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (onMoreOptionsClick != null) {
                    val moreSource = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(32.dp)
                            .pressedScale(targetScale = ScalePressedButton, interactionSource = moreSource)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = moreSource,
                                indication = null
                            ) { onMoreOptionsClick(song) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(IconSizeSm)
                        )
                    }
                }
            }
        }
    }
}
