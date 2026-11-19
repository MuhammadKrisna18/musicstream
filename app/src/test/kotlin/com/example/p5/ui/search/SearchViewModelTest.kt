package com.example.p5.ui.search

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchFilter
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.MusicRepository
import com.example.p5.domain.repository.SearchHistoryRepository
import com.example.p5.domain.usecase.GetCuratedPlaylistsUseCase
import com.example.p5.domain.usecase.GetGenreFeedUseCase
import com.example.p5.domain.usecase.GetHomeCategoriesUseCase
import com.example.p5.domain.usecase.GetHomeFeedUseCase
import com.example.p5.domain.usecase.GetPlaylistTracksUseCase
import com.example.p5.domain.usecase.GetTrendingMusicUseCase
import com.example.p5.domain.usecase.SearchMusicUseCase
import com.example.p5.domain.usecase.SearchPlaylistsUseCase
import com.example.p5.domain.usecase.SearchSongsAndPlaylistsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeSearchHistoryRepository : SearchHistoryRepository {
        val history = mutableListOf<String>()
        override fun getSearchHistory(limit: Int): Flow<List<String>> = flowOf(history)
        override suspend fun addSearchQuery(query: String) { history.add(query) }
        override suspend fun deleteSearchQuery(query: String) { history.remove(query) }
        override suspend fun clearSearchHistory() { history.clear() }
    }

    private class FakeHistoryRepository : HistoryRepository {
        override fun getRecentlyPlayed(limit: Int): Flow<List<Song>> = emptyFlow()
        override fun getTopPlayedSongs(limit: Int): Flow<List<Song>> = emptyFlow()
        override fun getTopArtists(limit: Int): Flow<List<String>> = emptyFlow()
        override suspend fun recordPlay(song: Song) {}
        override suspend fun clearHistory(): Result<Unit> = Result.success(Unit)
    }

    private class FakeMusicRepository : MusicRepository {
        override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> = Result.success(emptyList())
        override suspend fun searchSongsAndPlaylists(query: String, limit: Int): Result<SearchResult> = Result.success(SearchResult(emptyList()))
        override suspend fun getAudioStream(videoId: String): Result<AudioStream> = Result.failure(Exception())
        override suspend fun getTrending(limit: Int): Result<List<Song>> = Result.success(emptyList())
        override suspend fun getHomeCategories(): Result<HomeCategories> = Result.success(HomeCategories(emptyList(), emptyList()))
        override suspend fun getHomeFeed(refresh: Boolean): Result<HomeFeed> = Result.success(HomeFeed(emptyList(), emptyList(), emptyList(), emptyList()))
        override suspend fun getGenreFeed(category: Category): Result<List<HomeSection>> = Result.success(emptyList())
        override suspend fun getCuratedPlaylists(): Result<List<Playlist>> = Result.success(emptyList())
        override suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>> = Result.success(emptyList())
        override suspend fun searchPlaylists(query: String): Result<List<Playlist>> = Result.success(emptyList())
        override suspend fun getArtistProfile(channelId: String): Result<ArtistProfile> = Result.failure(Exception())
        override suspend fun getArtistAllSongs(playlistId: String): Result<List<Song>> = Result.success(emptyList())
    }

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val fakeMusicRepo = FakeMusicRepository()
        val searchMusicUseCase = SearchMusicUseCase(fakeMusicRepo)
        val getTrendingMusicUseCase = GetTrendingMusicUseCase(fakeMusicRepo)
        val getHomeCategoriesUseCase = GetHomeCategoriesUseCase(fakeMusicRepo)
        val getHomeFeedUseCase = GetHomeFeedUseCase(fakeMusicRepo)
        val getGenreFeedUseCase = GetGenreFeedUseCase(fakeMusicRepo)
        val getCuratedPlaylistsUseCase = GetCuratedPlaylistsUseCase(fakeMusicRepo)
        val getPlaylistTracksUseCase = GetPlaylistTracksUseCase(fakeMusicRepo)
        val searchPlaylistsUseCase = SearchPlaylistsUseCase(fakeMusicRepo)
        val searchSongsAndPlaylistsUseCase = SearchSongsAndPlaylistsUseCase(fakeMusicRepo)

        viewModel = SearchViewModel(
            searchMusicUseCase = searchMusicUseCase,
            getTrendingMusicUseCase = getTrendingMusicUseCase,
            getHomeCategoriesUseCase = getHomeCategoriesUseCase,
            getHomeFeedUseCase = getHomeFeedUseCase,
            getGenreFeedUseCase = getGenreFeedUseCase,
            searchHistoryRepository = FakeSearchHistoryRepository(),
            historyRepository = FakeHistoryRepository(),
            getCuratedPlaylistsUseCase = getCuratedPlaylistsUseCase,
            getPlaylistTracksUseCase = getPlaylistTracksUseCase,
            searchPlaylistsUseCase = searchPlaylistsUseCase,
            searchSongsAndPlaylistsUseCase = searchSongsAndPlaylistsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchFilter_initialStateIsAll() {
        assertEquals(SearchFilter.ALL, viewModel.searchFilter.value)
    }

    @Test
    fun setSearchFilter_updatesSearchFilterState() {
        viewModel.setSearchFilter(SearchFilter.SONGS)
        assertEquals(SearchFilter.SONGS, viewModel.searchFilter.value)

        viewModel.setSearchFilter(SearchFilter.ARTISTS)
        assertEquals(SearchFilter.ARTISTS, viewModel.searchFilter.value)

        viewModel.setSearchFilter(SearchFilter.ALBUMS)
        assertEquals(SearchFilter.ALBUMS, viewModel.searchFilter.value)

        viewModel.setSearchFilter(SearchFilter.PLAYLISTS)
        assertEquals(SearchFilter.PLAYLISTS, viewModel.searchFilter.value)
    }

    @Test
    fun onQueryChanged_resetsSearchFilterToAll() {
        viewModel.setSearchFilter(SearchFilter.ARTISTS)
        assertEquals(SearchFilter.ARTISTS, viewModel.searchFilter.value)

        viewModel.onQueryChanged("New Query")
        assertEquals(SearchFilter.ALL, viewModel.searchFilter.value)
    }

    @Test
    fun homeFeedBuilder_curateHeroSpotlightTracks_prioritizesNonHistorySections() {
        val songHistory = Song("h1", "History 1", "Artist H", 200, "3:20", "", "")
        val songChart = Song("c1", "Chart 1", "Artist C", 210, "3:30", "", "")
        val songMood = Song("m1", "Mood 1", "Artist M", 190, "3:10", "", "")

        val sections = listOf(
            HomeSection("personalized_quick_picks", "Pilihan Cepat", null, null, listOf(songHistory), HomeSectionStyle.QUICK_PICKS_GRID),
            HomeSection("chart_0", "Top Charts", null, null, listOf(songChart), HomeSectionStyle.RANKED_CHARTS),
            HomeSection("thematic_indie", "Indie Hangat", null, null, listOf(songMood), HomeSectionStyle.STANDARD_CARDS)
        )

        val spotlight = HomeFeedBuilder.curateHeroSpotlightTracks(sections, maxCount = 2)
        assertEquals(2, spotlight.size)
        assertEquals("c1", spotlight[0].id)
        assertEquals("m1", spotlight[1].id)
    }
}
