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
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.CornerRadiusCardLg
import com.example.p5.ui.theme.IconSizeMd
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun PlaylistDetailContent(
    playlist: Playlist,
    tracks: List<Song>,
    isLoading: Boolean,
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
    val cardShape = remember { RoundedCornerShape(CornerRadiusCardLg) }
    val badgeShape = remember { RoundedCornerShape(6.dp) }
    val buttonShape = remember { RoundedCornerShape(CornerRadiusCard) }

    val badgeText = when (playlist.type) {
        PlaylistType.GENRE -> stringResource(R.string.playlist_badge_genre)
        PlaylistType.ARTIST -> stringResource(R.string.playlist_badge_artist)
        PlaylistType.RANDOM -> stringResource(R.string.playlist_badge_random)
    }

    val badgeAccent = when (playlist.type) {
        PlaylistType.GENRE -> appColors.accent
        PlaylistType.ARTIST -> appColors.accentSecondary
        PlaylistType.RANDOM -> Color(0xFFFBBF24)
    }

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
                    text = playlist.title,
                    color = appColors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val countText = if (isLoading) {
                    "Memuat daftar lagu..."
                } else {
                    "${tracks.size} lagu single"
                }
                Text(
                    text = countText,
                    color = appColors.textSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(appColors.card)
                .glassBorder(
                    shape = cardShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                    endColor = appColors.glassBorderSubtle
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(appColors.background)
                        .glassBorder(
                            shape = RoundedCornerShape(14.dp),
                            strokeWidth = StrokeWidthBorder,
                            startColor = badgeAccent.copy(alpha = 0.5f),
                            endColor = appColors.glassBorderSubtle
                        )
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
                                tint = Color.White.copy(alpha = 0.35f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(badgeShape)
                            .background(appColors.background.copy(alpha = 0.75f))
                            .glassBorder(
                                shape = badgeShape,
                                strokeWidth = StrokeWidthBorder,
                                startColor = badgeAccent.copy(alpha = 0.6f),
                                endColor = appColors.glassBorderSubtle
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeAccent,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = playlist.title,
                        color = appColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = playlist.subtitle,
                        color = appColors.textSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = appColors.accent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else if (tracks.isNotEmpty()) {
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
                            onPlayQueue(tracks, 0)
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
                            onPlayQueue(tracks.shuffled(), 0)
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = BottomScrollPadding),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(
                    items = tracks,
                    key = { _, song -> song.id }
                ) { index, song ->
                    val isSelected = playerState.currentSong?.id == song.id
                    SongItem(
                        song = song,
                        isPlaying = playerState.isPlaying,
                        isSelected = isSelected,
                        onClick = { onPlayQueue(tracks, index) },
                        onMoreOptionsClick = onMoreOptionsClick
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Daftar lagu kosong atau gagal dimuat.",
                    color = appColors.textMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}
