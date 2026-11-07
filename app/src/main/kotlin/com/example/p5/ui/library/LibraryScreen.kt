package com.example.p5.ui.library

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeMd
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.CornerRadiusDefault
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.CornerRadiusTab
import com.example.p5.ui.theme.CornerRadiusXs
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.IconSizeMd
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ObsidianBackground
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary
import java.util.Locale

@Composable
fun LibraryScreen(
    libraryViewModel: LibraryViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    currentVersion: String = "2.1.0",
    onCheckForUpdate: () -> Unit = {},
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {}
) {
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val favoriteSongs = uiState.favoriteSongs
    val savedSongs = uiState.savedSongs
    val historySongs = uiState.historySongs
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        libraryViewModel.refreshCacheSize(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = SpacingLg)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.library_title),
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = when (selectedTab) {
                        0 -> stringResource(R.string.library_subtitle_favorites, favoriteSongs.size)
                        1 -> stringResource(R.string.library_subtitle_saved, savedSongs.size)
                        else -> stringResource(R.string.library_subtitle_history, historySongs.size)
                    },
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            val updateSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(ActionButtonSizeMd)
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = updateSource)
                    .clip(CircleShape)
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                        endColor = GlassBorderSubtle
                    )
                    .clickable(
                        interactionSource = updateSource,
                        indication = null,
                        onClick = onCheckForUpdate
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = stringResource(R.string.library_check_update_desc),
                    tint = ToscaPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val tabContainerShape = remember { RoundedCornerShape(CornerRadiusPill) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(tabContainerShape)
                .background(GlassFillLight)
                .glassBorder(
                    shape = tabContainerShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                    endColor = GlassBorderSubtle
                )
                .padding(SpacingXs),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                stringResource(R.string.library_tab_favorites, favoriteSongs.size),
                stringResource(R.string.library_tab_saved, savedSongs.size),
                stringResource(R.string.library_tab_history, historySongs.size)
            )
            tabs.forEachIndexed { index, title ->
                val selected = selectedTab == index
                val itemShape = remember { RoundedCornerShape(CornerRadiusTab) }
                val itemSource = remember { MutableInteractionSource() }

                val itemBg by animateColorAsState(
                    targetValue = if (selected) ToscaPrimary.copy(alpha = 0.16f) else Color.Transparent,
                    animationSpec = tween(DurationFast),
                    label = "tab_bg"
                )

                val textColor by animateColorAsState(
                    targetValue = if (selected) ToscaPrimary else TextSecondary,
                    animationSpec = tween(DurationFast),
                    label = "tab_text"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .pressedScale(targetScale = ScalePressedCard, interactionSource = itemSource)
                        .clip(itemShape)
                        .background(itemBg)
                        .then(
                            if (selected) {
                                Modifier.glassBorder(
                                    shape = itemShape,
                                    strokeWidth = StrokeWidthBorder,
                                    startColor = ToscaGlassBorder,
                                    endColor = GlassBorderSubtle
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = itemSource,
                            indication = null
                        ) { selectedTab = index }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(SpacingMd))

        val cacheShape = remember { RoundedCornerShape(CornerRadiusDefault) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cacheShape)
                .background(GlassFillSubtle)
                .glassBorder(
                    shape = cacheShape,
                    strokeWidth = StrokeWidthStandard,
                    startColor = GlassBorderBright.copy(alpha = 0.25f),
                    endColor = Color.Transparent
                )
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Storage,
                    contentDescription = null,
                    tint = ToscaPrimary,
                    modifier = Modifier.size(IconSizeSm)
                )
                Spacer(modifier = Modifier.width(SpacingSm))
                Text(
                    text = stringResource(R.string.library_cache_label, formatCacheBytes(uiState.cacheSizeBytes)),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (uiState.cacheSizeBytes > 0) {
                val clearSource = remember { MutableInteractionSource() }
                Text(
                    text = stringResource(R.string.library_cache_clear),
                    color = ToscaPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .pressedScale(targetScale = ScalePressedButton, interactionSource = clearSource)
                        .clip(RoundedCornerShape(CornerRadiusSm))
                        .clickable(
                            interactionSource = clearSource,
                            indication = null
                        ) { libraryViewModel.clearStreamingCache(context) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> {
                if (favoriteSongs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(GlassFillLight)
                                .glassBorder(
                                    shape = CircleShape,
                                    strokeWidth = StrokeWidthBorder,
                                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                                    endColor = GlassBorderSubtle
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = ToscaPrimary,
                                modifier = Modifier.size(ActionButtonSizeSm)
                            )
                        }

                        Spacer(modifier = Modifier.height(SpacingLg))

                        Text(
                            text = stringResource(R.string.library_empty_favorites_title),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.library_empty_favorites_desc),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = BottomScrollPadding),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(
                            items = favoriteSongs,
                            key = { _, item -> item.id }
                        ) { index, song ->
                            val isSelected = playerState.currentSong?.id == song.id
                            SongItem(
                                song = song,
                                isPlaying = playerState.isPlaying,
                                isSelected = isSelected,
                                onClick = { onPlayQueue(favoriteSongs, index) },
                                onMoreOptionsClick = onMoreOptionsClick
                            )
                        }
                    }
                }
            }

            1 -> {
                if (savedSongs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(GlassFillLight)
                                .glassBorder(
                                    shape = CircleShape,
                                    strokeWidth = StrokeWidthBorder,
                                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                                    endColor = GlassBorderSubtle
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BookmarkBorder,
                                contentDescription = null,
                                tint = ToscaPrimary,
                                modifier = Modifier.size(ActionButtonSizeSm)
                            )
                        }

                        Spacer(modifier = Modifier.height(SpacingLg))

                        Text(
                            text = stringResource(R.string.library_empty_saved_title),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.library_empty_saved_desc),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = BottomScrollPadding),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(
                            items = savedSongs,
                            key = { _, item -> item.id }
                        ) { index, song ->
                            val isSelected = playerState.currentSong?.id == song.id
                            SongItem(
                                song = song,
                                isPlaying = playerState.isPlaying,
                                isSelected = isSelected,
                                onClick = { onPlayQueue(savedSongs, index) },
                                onMoreOptionsClick = onMoreOptionsClick
                            )
                        }
                    }
                }
            }

            else -> {
                if (historySongs.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(GlassFillLight)
                                .glassBorder(
                                    shape = CircleShape,
                                    strokeWidth = StrokeWidthBorder,
                                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                                    endColor = GlassBorderSubtle
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.History,
                                contentDescription = null,
                                tint = ToscaPrimary,
                                modifier = Modifier.size(ActionButtonSizeSm)
                            )
                        }

                        Spacer(modifier = Modifier.height(SpacingLg))

                        Text(
                            text = stringResource(R.string.library_empty_history_title),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.library_empty_history_desc),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = SpacingSm),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.library_history_last_listened),
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = stringResource(R.string.library_history_clear),
                                color = ToscaPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(CornerRadiusXs))
                                    .clickable { libraryViewModel.clearHistory() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = BottomScrollPadding),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            itemsIndexed(
                                items = historySongs,
                                key = { _, item -> item.id }
                            ) { index, song ->
                                val isSelected = playerState.currentSong?.id == song.id
                                SongItem(
                                    song = song,
                                    isSelected = isSelected,
                                    isPlaying = playerState.isPlaying,
                                    onClick = { onPlayQueue(historySongs, index) },
                                    onMoreOptionsClick = onMoreOptionsClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatCacheBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format(Locale.getDefault(), "%.1f MB", mb)
    } else {
        String.format(Locale.getDefault(), "%.1f KB", kb)
    }
}
