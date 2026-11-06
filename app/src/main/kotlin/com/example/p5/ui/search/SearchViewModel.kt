package com.example.p5.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.SearchHistoryRepository
import com.example.p5.domain.usecase.GetHomeCategoriesUseCase
import com.example.p5.domain.usecase.GetHomeFeedUseCase
import com.example.p5.domain.usecase.GetTrendingMusicUseCase
import com.example.p5.domain.usecase.SearchMusicUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchMusicUseCase: SearchMusicUseCase,
    private val getTrendingMusicUseCase: GetTrendingMusicUseCase,
    private val getHomeCategoriesUseCase: GetHomeCategoriesUseCase,
    private val getHomeFeedUseCase: GetHomeFeedUseCase,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val searchHistory: StateFlow<List<String>> = searchHistoryRepository.getSearchHistory(15)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _rawHomeSections = MutableStateFlow<List<HomeSection>>(emptyList())
    private val _artistRecommendationSection = MutableStateFlow<HomeSection?>(null)

    val homeSections: StateFlow<List<HomeSection>> = combine(
        historyRepository.getTopPlayedSongs(10),
        _artistRecommendationSection,
        _rawHomeSections
    ) { topPlayed, artistSec, rawSecs ->
        val personalized = mutableListOf<HomeSection>()

        if (topPlayed.isNotEmpty()) {
            personalized.add(
                HomeSection(
                    id = "personalized_on_repeat",
                    title = "Sering Kamu Putar",
                    subtitle = "Lagu yang paling sering kamu dengarkan belakangan ini",
                    query = "",
                    tracks = topPlayed
                )
            )
        }

        if (artistSec != null && artistSec.tracks.isNotEmpty()) {
            personalized.add(artistSec)
        }

        personalized + rawSecs
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _moodTags = MutableStateFlow<List<MoodTag>>(emptyList())
    val moodTags: StateFlow<List<MoodTag>> = _moodTags.asStateFlow()

    private val _isHomeLoading = MutableStateFlow(true)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null
    private var artistJob: Job? = null

    init {
        loadDynamicContent()
        observeTopArtists()
    }

    private fun observeTopArtists() {
        viewModelScope.launch {
            historyRepository.getTopArtists(1).collect { artists ->
                val topArtist = artists.firstOrNull()
                if (topArtist.isNullOrBlank()) {
                    _artistRecommendationSection.value = null
                } else {
                    loadArtistRecommendations(topArtist)
                }
            }
        }
    }

    private fun loadArtistRecommendations(artist: String) {
        artistJob?.cancel()
        artistJob = viewModelScope.launch {
            searchMusicUseCase("$artist official hits")
                .onSuccess { songs ->
                    if (songs.isNotEmpty()) {
                        _artistRecommendationSection.value = HomeSection(
                            id = "recommended_${artist.hashCode()}",
                            title = "Spesial Untukmu: $artist",
                            subtitle = "Koleksi lagu pilihan berdasarkan artis favoritmu",
                            query = "$artist hits",
                            tracks = songs.take(12)
                        )
                    }
                }
        }
    }

    fun loadDynamicContent() {
        viewModelScope.launch {
            _isHomeLoading.value = true
            getHomeFeedUseCase(refresh = false)
                .onSuccess { feed ->
                    _rawHomeSections.value = feed.sections
                    _categories.value = feed.categories
                    _moodTags.value = feed.moodTags
                    _isHomeLoading.value = false
                }
                .onFailure {
                    loadFallback()
                }
        }
    }

    fun refreshHomeFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val topArtist = historyRepository.getTopArtists(1).firstOrNull()?.firstOrNull()
            if (!topArtist.isNullOrBlank()) {
                loadArtistRecommendations(topArtist)
            }

            getHomeFeedUseCase(refresh = true)
                .onSuccess { feed ->
                    _rawHomeSections.value = feed.sections
                    _categories.value = feed.categories
                    _moodTags.value = feed.moodTags
                    _isRefreshing.value = false
                }
                .onFailure {
                    _isRefreshing.value = false
                }
        }
    }

    private suspend fun loadFallback() {
        getHomeCategoriesUseCase()
            .onSuccess { data ->
                _categories.value = data.categories
                _moodTags.value = data.moodTags
            }
        getTrendingMusicUseCase(15)
            .onSuccess { songs ->
                if (songs.isNotEmpty()) {
                    _rawHomeSections.value = listOf(
                        HomeSection(
                            id = "trending",
                            title = "Trending Now",
                            subtitle = "Lagu paling populer dan viral saat ini",
                            query = "trending music hits 2026",
                            tracks = songs
                        )
                    )
                }
                _isHomeLoading.value = false
            }
            .onFailure {
                _isHomeLoading.value = false
            }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()

        val trimmed = newQuery.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            executeSearch(trimmed)
        }
    }

    fun searchCategory(categoryQuery: String) {
        _query.value = categoryQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            executeSearch(categoryQuery)
        }
    }

    fun searchNow() {
        val trimmed = _query.value.trim()
        if (trimmed.isEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchHistoryRepository.addSearchQuery(trimmed)
            executeSearch(trimmed)
        }
    }

    fun selectHistoryQuery(historyQuery: String) {
        _query.value = historyQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            searchHistoryRepository.addSearchQuery(historyQuery)
            executeSearch(historyQuery)
        }
    }

    fun onSongSelectedFromSearch(song: Song) {
        val trimmed = _query.value.trim()
        if (trimmed.length >= 2) {
            viewModelScope.launch {
                searchHistoryRepository.addSearchQuery(trimmed)
            }
        }
    }

    fun deleteSearchHistoryItem(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
        }
    }

    private suspend fun executeSearch(query: String) {
        _uiState.value = SearchUiState.Loading
        searchMusicUseCase(query)
            .onSuccess { songs ->
                _uiState.value = if (songs.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(songs)
                }
            }
            .onFailure { throwable ->
                _uiState.value = SearchUiState.Error(
                    throwable.localizedMessage ?: "Failed to perform search"
                )
            }
    }
}
