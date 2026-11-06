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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.History
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AuroraCyan
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillMedium
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.ObsidianBackground
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCard
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ObsidianSurface
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaDark
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlow
import com.example.p5.ui.theme.ToscaGlowSubtle
import com.example.p5.ui.theme.ToscaLight
import com.example.p5.ui.theme.ToscaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) }
) {
    val query by searchViewModel.query.collectAsStateWithLifecycle()
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val homeSections by searchViewModel.homeSections.collectAsStateWithLifecycle()
    val categories by searchViewModel.categories.collectAsStateWithLifecycle()
    val moodTags by searchViewModel.moodTags.collectAsStateWithLifecycle()
    val isHomeLoading by searchViewModel.isHomeLoading.collectAsStateWithLifecycle()
    val isRefreshing by searchViewModel.isRefreshing.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val shimmerBrush = rememberShimmerBrush()

    val refreshTransition = rememberInfiniteTransition(label = "refresh_angle")
    val refreshAngle by refreshTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_spin"
    )

    val editorialFeaturedTrack = remember(homeSections) {
        homeSections.firstOrNull { it.tracks.isNotEmpty() }?.tracks?.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.app_title_music),
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = stringResource(R.string.app_title_stream),
                        color = ToscaPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                }
                Text(
                    text = stringResource(R.string.home_subtitle),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (playerState.isPlaying) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ToscaPrimary.copy(alpha = 0.12f))
                            .glassBorder(
                                shape = RoundedCornerShape(12.dp),
                                strokeWidth = 1.dp,
                                startColor = ToscaGlassBorder,
                                endColor = GlassBorderSubtle
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        MiniEqualizer(isPlaying = true)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GlassFillLight)
                        .glassBorder(
                            shape = CircleShape,
                            strokeWidth = 1.dp,
                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                            endColor = GlassBorderSubtle
                        )
                        .clickable(
                            enabled = !isRefreshing,
                            onClick = { searchViewModel.refreshHomeFeed() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.home_refresh_content_desc),
                        tint = if (isRefreshing) ToscaPrimary else TextSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isRefreshing) refreshAngle else 0f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(GlassFillLight)
                .glassBorder(
                    shape = RoundedCornerShape(22.dp),
                    strokeWidth = 1.dp,
                    startColor = GlassBorderBright.copy(alpha = 0.4f),
                    endColor = GlassBorderSubtle
                )
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = searchViewModel::onQueryChanged,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = ToscaPrimary,
                        modifier = Modifier.size(22.dp)
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
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = ToscaPrimary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = ToscaPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    searchViewModel.searchNow()
                }),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                            contentPadding = PaddingValues(bottom = 140.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            if (searchHistory.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.History,
                                                    contentDescription = null,
                                                    tint = ToscaPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = stringResource(R.string.search_recent_title),
                                                    color = TextPrimary,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            Text(
                                                text = stringResource(R.string.search_clear_all),
                                                color = TextSecondary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable { searchViewModel.clearSearchHistory() }
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            contentPadding = PaddingValues(vertical = 2.dp)
                                        ) {
                                            items(searchHistory) { historyQuery ->
                                                val interactionSource = remember { MutableInteractionSource() }
                                                Box(
                                                    modifier = Modifier
                                                        .pressedScale(
                                                            targetScale = 0.94f,
                                                            interactionSource = interactionSource
                                                        )
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .background(GlassFillLight)
                                                        .glassBorder(
                                                            shape = RoundedCornerShape(20.dp),
                                                            strokeWidth = 1.dp,
                                                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                                                            endColor = GlassBorderSubtle
                                                        )
                                                        .clickable(
                                                            interactionSource = interactionSource,
                                                            indication = null
                                                        ) {
                                                            focusManager.clearFocus()
                                                            searchViewModel.selectHistoryQuery(historyQuery)
                                                        }
                                                        .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Text(
                                                            text = historyQuery,
                                                            color = TextPrimary,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .clip(CircleShape)
                                                                .clickable {
                                                                    searchViewModel.deleteSearchHistoryItem(historyQuery)
                                                                },
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Rounded.Close,
                                                                contentDescription = stringResource(R.string.search_remove_item_desc),
                                                                tint = TextMuted,
                                                                modifier = Modifier.size(13.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (editorialFeaturedTrack != null && query.isEmpty()) {
                                item(key = "editorial_hero") {
                                    EditorialHeroCard(
                                        song = editorialFeaturedTrack,
                                        isPlaying = playerState.isPlaying,
                                        isSelected = playerState.currentSong?.id == editorialFeaturedTrack.id,
                                        onClick = { onSongClick(editorialFeaturedTrack) }
                                    )
                                }
                            }

                            if (moodTags.isNotEmpty()) {
                                item {
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 2.dp)
                                    ) {
                                        items(moodTags) { tag ->
                                            val interactionSource = remember { MutableInteractionSource() }
                                            Box(
                                                modifier = Modifier
                                                    .pressedScale(
                                                        targetScale = 0.94f,
                                                        interactionSource = interactionSource
                                                    )
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(GlassFillLight)
                                                    .glassBorder(
                                                        shape = RoundedCornerShape(20.dp),
                                                        strokeWidth = 1.dp,
                                                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                                                        endColor = GlassBorderSubtle
                                                    )
                                                    .clickable(
                                                        interactionSource = interactionSource,
                                                        indication = null
                                                    ) { searchViewModel.searchCategory(tag.query) }
                                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                            ) {
                                                Text(
                                                    text = tag.label,
                                                    color = ToscaPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
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
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(160.dp)
                                                .height(20.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(shimmerBrush)
                                        )
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            items(4) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(150.dp)
                                                        .height(210.dp)
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .background(shimmerBrush)
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
                                                            modifier = Modifier.clickable { searchViewModel.searchCategory(section.query) }
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
                                                            isPlaying = playerState.isPlaying,
                                                            isSelected = playerState.currentSong?.id == song.id,
                                                            onSongClick = { onPlayQueue(section.tracks, index) }
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
                                        text = stringResource(R.string.categories_title),
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.3).sp
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
                            color = ToscaPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                is SearchUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(
                            items = state.songs,
                            key = { _, item -> item.id }
                        ) { index, song ->
                            val isSelected = playerState.currentSong?.id == song.id

                            SongItem(
                                song = song,
                                isPlaying = playerState.isPlaying,
                                isSelected = isSelected,
                                onClick = {
                                    searchViewModel.onSongSelectedFromSearch(song)
                                    onPlayQueue(state.songs, index)
                                }
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
                            text = stringResource(R.string.search_empty_message, query),
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
                            tint = ToscaDark,
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
                            colors = ButtonDefaults.buttonColors(containerColor = ToscaPrimary)
                        ) {
                            Text(stringResource(R.string.action_retry), color = ObsidianBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorialHeroCard(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(24.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(205.dp)
            .pressedScale(targetScale = 0.98f, interactionSource = interactionSource)
            .ambientGlow(
                color = ToscaGlowSubtle,
                alpha = 0.4f,
                shape = cardShape
            )
            .clip(cardShape)
            .background(ObsidianCardHigh)
            .glassBorder(
                shape = cardShape,
                strokeWidth = 1.dp,
                startColor = GlassBorderBright.copy(alpha = 0.5f),
                endColor = GlassBorderSubtle
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
                            ObsidianBlack.copy(alpha = 0.94f)
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
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ToscaPrimary.copy(alpha = 0.2f))
                    .glassBorder(
                        shape = RoundedCornerShape(12.dp),
                        strokeWidth = 1.dp,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ToscaPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(R.string.editorial_spotlight_badge),
                    color = ToscaPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = song.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
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

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .ambientGlow(
                            color = ToscaGlow,
                            alpha = 0.7f,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(ToscaPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected && isPlaying) {
                        MiniEqualizer(
                            barColor = ObsidianBlack,
                            isPlaying = true
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(R.string.action_play),
                            tint = ObsidianBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendingTrackCard(
    song: Song,
    isPlaying: Boolean,
    isSelected: Boolean,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val cardShape = remember { RoundedCornerShape(20.dp) }

    Box(
        modifier = modifier
            .width(150.dp)
            .height(212.dp)
            .pressedScale(targetScale = 0.95f, interactionSource = interactionSource)
            .clip(cardShape)
            .background(ObsidianCard)
            .then(
                if (isSelected) {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = 1.dp,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle,
                        accentGlow = ToscaPrimary.copy(alpha = 0.5f)
                    )
                } else {
                    Modifier.glassBorder(
                        shape = cardShape,
                        strokeWidth = 1.dp,
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
                    .align(Alignment.TopEnd)
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

@Composable
private fun GenreCategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = parseHexColor(category.startColor, ToscaPrimary)
    val icon = getCategoryIcon(category.icon)
    val cardShape = remember { RoundedCornerShape(20.dp) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(92.dp)
            .pressedScale(targetScale = 0.95f, interactionSource = interactionSource)
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.3f),
                        ObsidianCardHigh
                    )
                )
            )
            .glassBorder(
                shape = cardShape,
                strokeWidth = 1.dp,
                startColor = baseColor.copy(alpha = 0.45f),
                endColor = GlassBorderSubtle
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = baseColor.copy(alpha = 0.55f),
            modifier = Modifier
                .size(42.dp)
                .align(Alignment.BottomEnd)
        )

        Text(
            text = category.title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 19.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.72f)
        )
    }
}

@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            ObsidianSurfaceVariant.copy(alpha = 0.5f),
            ObsidianSurfaceVariant.copy(alpha = 0.85f),
            ToscaPrimary.copy(alpha = 0.16f),
            ObsidianSurfaceVariant.copy(alpha = 0.85f),
            ObsidianSurfaceVariant.copy(alpha = 0.5f)
        ),
        start = androidx.compose.ui.geometry.Offset(translateAnim, translateAnim),
        end = androidx.compose.ui.geometry.Offset(translateAnim + 260f, translateAnim + 260f)
    )
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
