package com.example.p5.ui.search.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusHero
import com.example.p5.ui.theme.HeroBannerHeight
import com.example.p5.ui.theme.IconSizeLg
import com.example.p5.ui.theme.PlayButtonSizeHero
import com.example.p5.ui.theme.ScalePressedSubtle
import com.example.p5.ui.theme.StrokeWidthBorder
import kotlinx.coroutines.delay

@Composable
fun EditorialHeroCarousel(
    songs: List<Song>,
    isPlaying: Boolean,
    currentSongId: String?,
    onPlayQueue: (List<Song>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (songs.isEmpty()) return

    val appColors = AppTheme.colors
    val pagerState = rememberPagerState(pageCount = { songs.size })

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress, songs.size) {
        if (songs.size > 1 && !pagerState.isScrollInProgress) {
            delay(5000L)
            val nextPage = (pagerState.currentPage + 1) % songs.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            val song = songs[pageIndex]
            EditorialHeroCard(
                song = song,
                isPlaying = isPlaying,
                isSelected = currentSongId == song.id,
                onClick = { onPlayQueue(songs, pageIndex) },
                slideBadgeText = if (songs.size > 1) "${pageIndex + 1} / ${songs.size}" else null
            )
        }

        if (songs.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in songs.indices) {
                    val isCurrent = pagerState.currentPage == index
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isCurrent) 20.dp else 6.dp,
                        animationSpec = tween(durationMillis = 250),
                        label = "hero_indicator_width"
                    )
                    val indicatorColor by animateColorAsState(
                        targetValue = if (isCurrent) appColors.accent else appColors.textMuted.copy(alpha = 0.45f),
                        animationSpec = tween(durationMillis = 250),
                        label = "hero_indicator_color"
                    )

                    Box(
                        modifier = Modifier
                            .width(indicatorWidth)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }
        }
    }
}

@Composable
fun EditorialHeroCard(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    slideBadgeText: String? = null
) {
    val appColors = AppTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(CornerRadiusHero) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(HeroBannerHeight)
            .pressedScale(targetScale = ScalePressedSubtle, interactionSource = interactionSource)
            .clip(cardShape)
            .background(appColors.cardHigh)
            .glassBorder(
                shape = cardShape,
                strokeWidth = StrokeWidthBorder,
                startColor = appColors.glassBorderBright.copy(alpha = 0.5f),
                endColor = appColors.glassBorderSubtle
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
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
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.65f),
                            appColors.cardHigh.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColors.accent.copy(alpha = 0.2f))
                        .glassBorder(
                            shape = RoundedCornerShape(12.dp),
                            strokeWidth = 1.dp,
                            startColor = appColors.accent.copy(alpha = 0.35f),
                            endColor = appColors.glassBorderSubtle
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = stringResource(R.string.editorial_spotlight_badge),
                        color = appColors.accent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }

                if (!slideBadgeText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .glassBorder(
                                shape = RoundedCornerShape(10.dp),
                                strokeWidth = 1.dp,
                                startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                                endColor = appColors.glassBorderSubtle
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = slideBadgeText,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = song.title,
                        color = appColors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        color = appColors.textSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(PlayButtonSizeHero)
                        .clip(CircleShape)
                        .background(appColors.accent),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected && isPlaying) {
                        MiniEqualizer(
                            barColor = appColors.onAccent,
                            isPlaying = true
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.action_play),
                            tint = appColors.onAccent,
                            modifier = Modifier.size(IconSizeLg)
                        )
                    }
                }
            }
        }
    }
}
