package com.example.p5.ui.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.search.components.CategoryGridSection
import com.example.p5.ui.search.components.EditorialHeroCard
import com.example.p5.ui.search.components.HomeTrackSection
import com.example.p5.ui.search.components.MoodTagsRow
import com.example.p5.ui.search.components.SearchBarField
import com.example.p5.ui.search.components.SearchEmptyState
import com.example.p5.ui.search.components.SearchErrorState
import com.example.p5.ui.search.components.SearchHistorySection
import com.example.p5.ui.search.components.SearchTopHeader
import com.example.p5.ui.search.components.rememberShimmerBrush
import com.example.p5.ui.search.components.searchShimmerSkeleton
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.ToscaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    playerState: PlayerState,
    onSongClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    onPlayQueue: (List<Song>, Int) -> Unit = { list, idx -> onSongClick(list[idx]) },
    onMoreOptionsClick: (Song) -> Unit = {}
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

    val editorialFeaturedTrack = remember(homeSections) {
        homeSections.firstOrNull { it.tracks.isNotEmpty() }?.tracks?.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        SearchTopHeader(
            isRefreshing = isRefreshing,
            isPlaying = playerState.isPlaying,
            onRefresh = searchViewModel::refreshHomeFeed
        )

        Spacer(modifier = Modifier.height(14.dp))

        SearchBarField(
            query = query,
            onQueryChanged = searchViewModel::onQueryChanged,
            onSearch = searchViewModel::searchNow
        )

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
                        onRefresh = searchViewModel::refreshHomeFeed,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = BottomScrollPadding),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            if (searchHistory.isNotEmpty()) {
                                item {
                                    SearchHistorySection(
                                        searchHistory = searchHistory,
                                        onSelectQuery = { selectedQuery ->
                                            focusManager.clearFocus()
                                            searchViewModel.selectHistoryQuery(selectedQuery)
                                        },
                                        onDeleteItem = searchViewModel::deleteSearchHistoryItem,
                                        onClearAll = searchViewModel::clearSearchHistory
                                    )
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
                                    MoodTagsRow(
                                        moodTags = moodTags,
                                        onSelectMoodTag = searchViewModel::searchCategory
                                    )
                                }
                            }

                            if (isHomeLoading && homeSections.isEmpty()) {
                                searchShimmerSkeleton(shimmerBrush)
                            } else {
                                homeSections.forEach { section ->
                                    if (section.tracks.isNotEmpty()) {
                                        item(key = section.id) {
                                            HomeTrackSection(
                                                section = section,
                                                isPlaying = playerState.isPlaying,
                                                currentSongId = playerState.currentSong?.id,
                                                onPlayQueue = onPlayQueue,
                                                onSeeAllClick = searchViewModel::searchCategory,
                                                onMoreOptionsClick = onMoreOptionsClick
                                            )
                                        }
                                    }
                                }
                            }

                            if (categories.isNotEmpty()) {
                                item {
                                    CategoryGridSection(
                                        categories = categories,
                                        onCategoryClick = searchViewModel::searchCategory
                                    )
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
                        contentPadding = PaddingValues(bottom = BottomScrollPadding),
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
                                },
                                onMoreOptionsClick = onMoreOptionsClick
                            )
                        }
                    }
                }

                SearchUiState.Empty -> {
                    SearchEmptyState(query = query)
                }

                is SearchUiState.Error -> {
                    SearchErrorState(
                        message = state.message,
                        onRetry = searchViewModel::searchNow
                    )
                }
            }
        }
    }
}
