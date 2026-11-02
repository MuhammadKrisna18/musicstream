package com.example.p5.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DownloadForOffline
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Nightlife
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.BlackBackground
import com.example.p5.ui.theme.DarkSurface
import com.example.p5.ui.theme.DarkSurfaceVariant
import com.example.p5.ui.theme.OrangeDark
import com.example.p5.ui.theme.OrangePrimary
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerState: PlayerState,
    downloadingIds: Set<String>,
    downloadedIds: Set<String>,
    onSongClick: (Song) -> Unit,
    onDownloadClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val homeSections by searchViewModel.homeSections.collectAsStateWithLifecycle()
    val categories by searchViewModel.categories.collectAsStateWithLifecycle()
    val moodTags by searchViewModel.moodTags.collectAsStateWithLifecycle()
    val isHomeLoading by searchViewModel.isHomeLoading.collectAsStateWithLifecycle()
    val isRefreshing by searchViewModel.isRefreshing.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    val refreshTransition = rememberInfiniteTransition(label = "refreshAnim")
    val refreshAngle by refreshTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshAngle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MusicStream",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Find and stream your favorite tracks",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisualizerIndicator(isPlaying = playerState.isPlaying)

                IconButton(
                    onClick = { searchViewModel.refreshHomeFeed() },
                    enabled = !isRefreshing
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh Feed",
                        tint = if (isRefreshing) OrangePrimary else TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (isRefreshing) refreshAngle else 0f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = searchViewModel::onQueryChanged,
                placeholder = { Text("Search songs, artists, albums...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = OrangePrimary
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { searchViewModel.onQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = OrangePrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    searchViewModel.searchNow()
                }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        AnimatedContent(
            targetState = uiState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "search_state_content",
            modifier = Modifier.weight(1f)
        ) { state ->
            when (state) {
                SearchUiState.Idle -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = { searchViewModel.refreshHomeFeed() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                        if (moodTags.isNotEmpty()) {
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(moodTags) { tag ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(DarkSurfaceVariant)
                                                .clickable { searchViewModel.searchCategory(tag.query) }
                                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = tag.label,
                                                color = OrangePrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isHomeLoading && homeSections.isEmpty()) {
                            items(3) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(140.dp)
                                            .height(20.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkSurface)
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(4) {
                                            Box(
                                                modifier = Modifier
                                                    .width(135.dp)
                                                    .height(175.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(DarkSurface)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            homeSections.forEach { section ->
                                if (section.tracks.isNotEmpty()) {
                                    item(key = section.id) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                                        fontWeight = FontWeight.Bold
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
                                                        text = "See all",
                                                        color = OrangePrimary,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.clickable { searchViewModel.searchCategory(section.query) }
                                                    )
                                                }
                                            }

                                            LazyRow(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                items(
                                                    items = section.tracks,
                                                    key = { it.id }
                                                ) { song ->
                                                    TrendingTrackCard(
                                                        song = song,
                                                        isPlaying = playerState.isPlaying,
                                                        isSelected = playerState.currentSong?.id == song.id,
                                                        isDownloaded = downloadedIds.contains(song.id),
                                                        isDownloading = downloadingIds.contains(song.id),
                                                        onSongClick = onSongClick,
                                                        onDownloadClick = onDownloadClick
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (categories.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Browse by Mood & Genre",
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            items(categories.chunked(2)) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    GenreCategoryCard(
                                        category = pair[0],
                                        onClick = { searchViewModel.searchCategory(pair[0].query) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (pair.size > 1) {
                                        GenreCategoryCard(
                                            category = pair[1],
                                            onClick = { searchViewModel.searchCategory(pair[1].query) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

                SearchUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = OrangePrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                is SearchUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = state.songs,
                            key = { it.id }
                        ) { song ->
                            val isSelected = playerState.currentSong?.id == song.id
                            val isDownloaded = downloadedIds.contains(song.id)
                            val isDownloading = downloadingIds.contains(song.id)

                            SongItem(
                                song = song,
                                isPlaying = playerState.isPlaying,
                                isSelected = isSelected,
                                isDownloaded = isDownloaded,
                                isDownloading = isDownloading,
                                onClick = { onSongClick(song) },
                                onDownloadClick = { onDownloadClick(song) }
                            )
                        }
                    }
                }

                SearchUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs found for \"$query\"",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                is SearchUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = OrangeDark,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = state.message,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { searchViewModel.searchNow() },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Retry", color = BlackBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualizerIndicator(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")

    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )
    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 16f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )
    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )
    val bar4Height by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar4"
    )

    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isPlaying) bar1Height.dp else 8.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isPlaying) bar2Height.dp else 15.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isPlaying) bar3Height.dp else 20.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(if (isPlaying) bar4Height.dp else 10.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
        )
    }
}

@Composable
private fun TrendingTrackCard(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onSongClick: (Song) -> Unit,
    onDownloadClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(135.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .clickable { onSongClick(song) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(119.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
        ) {
            AsyncImage(
                model = song.thumbnailUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected && isPlaying) Icons.Rounded.GraphicEq else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song.title,
            color = if (isSelected) OrangePrimary else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

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

            if (isDownloading) {
                CircularProgressIndicator(
                    color = OrangePrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isDownloaded) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                IconButton(
                    onClick = { onDownloadClick(song) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DownloadForOffline,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreCategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startColor = parseHexColor(category.startColor, OrangePrimary)
    val endColor = parseHexColor(category.endColor, OrangeDark)
    val icon = getCategoryIcon(category.icon)

    Box(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(startColor, endColor)
                )
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.22f),
            modifier = Modifier
                .size(46.dp)
                .align(Alignment.BottomEnd)
        )

        Text(
            text = category.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

private fun parseHexColor(hexString: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexString))
    } catch (_: Exception) {
        fallback
    }
}

private fun getCategoryIcon(iconKey: String): ImageVector {
    return when (iconKey.lowercase()) {
        "fire", "whatshot" -> Icons.Rounded.Whatshot
        "electric_bolt", "rock" -> Icons.Rounded.ElectricBolt
        "nightlife", "chill" -> Icons.Rounded.Nightlife
        "music_note", "pop" -> Icons.Rounded.MusicNote
        "headphones", "indie" -> Icons.Rounded.Headphones
        "bolt", "workout", "edm" -> Icons.Rounded.Bolt
        "library_music", "acoustic" -> Icons.Rounded.LibraryMusic
        "graphic_eq", "jazz" -> Icons.Rounded.GraphicEq
        else -> Icons.Rounded.MusicNote
    }
}
