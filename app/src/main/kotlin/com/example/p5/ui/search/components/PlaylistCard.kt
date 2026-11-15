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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ArtworkCardHeight
import com.example.p5.ui.theme.ArtworkCardWidth
import com.example.p5.ui.theme.CornerRadiusCardLg
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCard
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlaylistCard(
    playlist: Playlist,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(CornerRadiusCardLg) }
    val badgeShape = remember { RoundedCornerShape(6.dp) }

    val badgeText = when (playlist.type) {
        PlaylistType.GENRE -> stringResource(R.string.playlist_badge_genre)
        PlaylistType.ARTIST -> stringResource(R.string.playlist_badge_artist)
        PlaylistType.RANDOM -> stringResource(R.string.playlist_badge_random)
    }

    val badgeAccent = when (playlist.type) {
        PlaylistType.GENRE -> ToscaPrimary
        PlaylistType.ARTIST -> Color(0xFFA78BFA)
        PlaylistType.RANDOM -> Color(0xFFFBBF24)
    }

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
            ) { onPlaylistClick(playlist) }
    ) {
        if (playlist.thumbnailUrl.isNotBlank()) {
            AsyncImage(
                model = playlist.thumbnailUrl,
                contentDescription = playlist.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val placeholderBrush = when (playlist.type) {
                PlaylistType.GENRE -> Brush.linearGradient(
                    listOf(Color(0xFF0F766E), Color(0xFF042F2E))
                )
                PlaylistType.ARTIST -> Brush.linearGradient(
                    listOf(Color(0xFF6D28D9), Color(0xFF1E1B4B))
                )
                PlaylistType.RANDOM -> Brush.linearGradient(
                    listOf(Color(0xFFD97706), Color(0xFF451A03))
                )
            }
            val placeholderIcon = when (playlist.type) {
                PlaylistType.GENRE -> Icons.AutoMirrored.Rounded.QueueMusic
                PlaylistType.ARTIST -> Icons.Rounded.Person
                PlaylistType.RANDOM -> Icons.Rounded.Shuffle
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(placeholderBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = placeholderIcon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.28f),
                    modifier = Modifier.size(52.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f),
                            ObsidianBlack.copy(alpha = 0.94f)
                        ),
                        startY = 50f
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(badgeShape)
                    .background(ObsidianBlack.copy(alpha = 0.75f))
                    .glassBorder(
                        shape = badgeShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = badgeAccent.copy(alpha = 0.6f),
                        endColor = GlassBorderSubtle
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeAccent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.4f),
                        endColor = GlassBorderSubtle
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = ToscaPrimary,
                    modifier = Modifier.size(15.dp)
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
                text = playlist.title,
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(11.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = playlist.subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
