package com.example.p5.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchSortOrder
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.SearchHistoryRepository
import com.example.p5.domain.usecase.GetCuratedPlaylistsUseCase
import com.example.p5.domain.usecase.GetGenreFeedUseCase
import com.example.p5.domain.usecase.GetHomeCategoriesUseCase
import com.example.p5.domain.usecase.GetHomeFeedUseCase
import com.example.p5.domain.usecase.GetPlaylistTracksUseCase
import com.example.p5.domain.usecase.GetTrendingMusicUseCase
import com.example.p5.domain.usecase.SearchMusicUseCase
import com.example.p5.domain.usecase.SearchPlaylistsUseCase
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
    private val getGenreFeedUseCase: GetGenreFeedUseCase,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val historyRepository: HistoryRepository,
    private val getCuratedPlaylistsUseCase: GetCuratedPlaylistsUseCase,
    private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
    private val searchPlaylistsUseCase: SearchPlaylistsUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchSortOrder = MutableStateFlow(SearchSortOrder.RELEVANCE)
    val searchSortOrder: StateFlow<SearchSortOrder> = _searchSortOrder.asStateFlow()

    fun setSearchSortOrder(order: SearchSortOrder) {
        _searchSortOrder.value = order
    }

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
    private val _selectedGenre = MutableStateFlow<Category?>(null)
    val selectedGenre: StateFlow<Category?> = _selectedGenre.asStateFlow()

    private val _genreSections = MutableStateFlow<List<HomeSection>>(emptyList())
    private val _isGenreLoading = MutableStateFlow(false)
    val isGenreLoading: StateFlow<Boolean> = _isGenreLoading.asStateFlow()

    val homeSections: StateFlow<List<HomeSection>> = combine(
        historyRepository.getTopPlayedSongs(10),
        _artistRecommendationSection,
        _rawHomeSections,
        _selectedGenre,
        _genreSections
    ) { topPlayed, artistSec, rawSecs, selectedGenre, genreSecs ->
        if (selectedGenre != null) {
            genreSecs
        } else {
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
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _curatedPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val curatedPlaylists: StateFlow<List<Playlist>> = _curatedPlaylists.asStateFlow()

    private val _selectedSection = MutableStateFlow<HomeSection?>(null)
    val selectedSection: StateFlow<HomeSection?> = _selectedSection.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    private val _selectedPlaylistTracks = MutableStateFlow<List<Song>>(emptyList())
    val selectedPlaylistTracks: StateFlow<List<Song>> = _selectedPlaylistTracks.asStateFlow()

    private val _isPlaylistLoading = MutableStateFlow(false)
    val isPlaylistLoading: StateFlow<Boolean> = _isPlaylistLoading.asStateFlow()

    private val _moodTags = MutableStateFlow<List<MoodTag>>(emptyList())
    val moodTags: StateFlow<List<MoodTag>> = _moodTags.asStateFlow()

    private val _isHomeLoading = MutableStateFlow(true)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null
    private var artistJob: Job? = null
    private var genreJob: Job? = null
    private var playlistJob: Job? = null

    init {
        loadDynamicContent()
        observeTopArtists()
        loadCuratedPlaylists()
    }

    private fun loadCuratedPlaylists() {
        viewModelScope.launch {
            getCuratedPlaylistsUseCase()
                .onSuccess { playlists ->
                    _curatedPlaylists.value = playlists
                }
        }
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

    fun selectGenre(category: Category?) {
        _selectedSection.value = null
        _selectedPlaylist.value = null
        if (_selectedGenre.value?.id == category?.id && category != null) {
            _selectedGenre.value = null
            _genreSections.value = emptyList()
            return
        }
        _selectedGenre.value = category
        genreJob?.cancel()
        if (category == null) {
            _genreSections.value = emptyList()
            _isGenreLoading.value = false
            return
        }

        genreJob = viewModelScope.launch {
            _isGenreLoading.value = true
            getGenreFeedUseCase(category)
                .onSuccess { sections ->
                    _genreSections.value = sections
                    _isGenreLoading.value = false
                }
                .onFailure {
                    _genreSections.value = emptyList()
                    _isGenreLoading.value = false
                }
        }
    }

    fun refreshHomeFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val currentGenre = _selectedGenre.value
            if (currentGenre != null) {
                getGenreFeedUseCase(currentGenre)
                    .onSuccess { sections ->
                        _genreSections.value = sections
                    }
            }

            val topArtist = historyRepository.getTopArtists(1).firstOrNull()?.firstOrNull()
            if (!topArtist.isNullOrBlank()) {
                loadArtistRecommendations(topArtist)
            }

            loadCuratedPlaylists()

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
        _selectedSection.value = null
        _selectedPlaylist.value = null
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

    fun openSectionDetail(section: HomeSection) {
        _selectedPlaylist.value = null
        _selectedSection.value = section
    }

    fun closeSectionDetail() {
        _selectedSection.value = null
    }

    fun openPlaylistDetail(playlist: Playlist) {
        _selectedSection.value = null
        _selectedPlaylist.value = playlist
        playlistJob?.cancel()

        if (playlist.tracks.isNotEmpty()) {
            _selectedPlaylistTracks.value = playlist.tracks
            _isPlaylistLoading.value = false
        } else {
            _selectedPlaylistTracks.value = emptyList()
            _isPlaylistLoading.value = true
            playlistJob = viewModelScope.launch {
                getPlaylistTracksUseCase(playlist)
                    .onSuccess { songs ->
                        _selectedPlaylistTracks.value = songs
                        _isPlaylistLoading.value = false
                    }
                    .onFailure {
                        _selectedPlaylistTracks.value = emptyList()
                        _isPlaylistLoading.value = false
                    }
            }
        }
    }

    fun closePlaylistDetail() {
        _selectedPlaylist.value = null
        _selectedPlaylistTracks.value = emptyList()
        _isPlaylistLoading.value = false
        playlistJob?.cancel()
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
