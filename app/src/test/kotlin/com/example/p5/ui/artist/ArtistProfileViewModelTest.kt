package com.example.p5.ui.artist

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.LyricsData
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.RepeatMode
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.player.AudioPlayer
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.LyricsRepository
import com.example.p5.domain.repository.MusicRepository
import com.example.p5.domain.usecase.GetArtistAllSongsUseCase
import com.example.p5.domain.usecase.GetArtistProfileUseCase
import com.example.p5.domain.usecase.GetStreamAudioUseCase
import com.example.p5.playback.MusicPlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeAudioPlayer : AudioPlayer {
        var lastQueue: List<Song> = emptyList()
        var lastCurrentIndex: Int = -1

        private val _state = MutableStateFlow(PlayerState())
        override val playerState: StateFlow<PlayerState> = _state

        override fun setOnPlaybackEndedListener(listener: () -> Unit) {}
        override fun prepareSong(song: Song) {}
        override fun play(song: Song, stream: AudioStream) {}
        override fun updateQueueState(
            queue: List<Song>,
            currentIndex: Int,
            isShuffleEnabled: Boolean,
            repeatMode: RepeatMode
        ) {
            lastQueue = queue
            lastCurrentIndex = currentIndex
            _state.value = _state.value.copy(
                queue = queue,
                currentQueueIndex = currentIndex,
                currentSong = queue.getOrNull(currentIndex)
            )
        }

        override fun updateSleepTimerState(secondsRemaining: Long?, isEndOfTrack: Boolean) {}
        override fun updateLyricsState(lyrics: LyricsData?, isLoading: Boolean) {}
        override fun updateFavoriteState(isFavorite: Boolean) {}
        override fun setError(message: String) {}
        override fun togglePlayPause() {}
        override fun pause() {}
        override fun resume() {}
        override fun seekTo(positionMs: Long) {}
        override fun stop() {}
        override fun release() {}
    }

    private class FakeMusicRepository : MusicRepository {
        var shouldFail: Boolean = false

        override suspend fun searchSongs(query: String, limit: Int) = Result.success(emptyList<Song>())
        override suspend fun searchSongsAndPlaylists(query: String, limit: Int) = Result.success(SearchResult(emptyList(), emptyList()))
        override suspend fun getAudioStream(videoId: String) = Result.failure<AudioStream>(Exception())
        override suspend fun getTrending(limit: Int) = Result.success(emptyList<Song>())
        override suspend fun getHomeCategories() = Result.success(HomeCategories(emptyList(), emptyList()))
        override suspend fun getHomeFeed(refresh: Boolean) = Result.success(HomeFeed(emptyList(), emptyList(), emptyList()))
        override suspend fun getGenreFeed(category: Category) = Result.success(emptyList<HomeSection>())
        override suspend fun getCuratedPlaylists() = Result.success(emptyList<Playlist>())
        override suspend fun getPlaylistTracks(playlist: Playlist) = Result.success(emptyList<Song>())
        override suspend fun searchPlaylists(query: String) = Result.success(emptyList<Playlist>())

        override suspend fun getArtistProfile(channelId: String): Result<ArtistProfile> {
            return if (shouldFail) {
                Result.failure(IllegalStateException("Network error"))
            } else {
                Result.success(
                    ArtistProfile(
                        id = channelId,
                        name = "Denny Caknan",
                        avatarUrl = "https://example.com/avatar.jpg",
                        topSongs = listOf(
                            Song(id = "s1", title = "Lagu 1", artist = "Denny Caknan", durationSeconds = 180, durationFormatted = "3:00", thumbnailUrl = "", webUrl = "")
                        ),
                        allSongsPlaylistId = "VL_all"
                    )
                )
            }
        }

        override suspend fun getArtistAllSongs(playlistId: String): Result<List<Song>> {
            return Result.success(
                listOf(
                    Song(id = "s2", title = "Lagu 2", artist = "Denny Caknan", durationSeconds = 200, durationFormatted = "3:20", thumbnailUrl = "", webUrl = "")
                )
            )
        }
    }

    private lateinit var repository: FakeMusicRepository
    private lateinit var getArtistProfileUseCase: GetArtistProfileUseCase
    private lateinit var getArtistAllSongsUseCase: GetArtistAllSongsUseCase
    private lateinit var playerManager: MusicPlayerManager
    private lateinit var viewModel: ArtistProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMusicRepository()
        getArtistProfileUseCase = GetArtistProfileUseCase(repository)
        getArtistAllSongsUseCase = GetArtistAllSongsUseCase(repository)

        val fakeAudioPlayer = FakeAudioPlayer()
        val dummyGetStream = GetStreamAudioUseCase(repository)
        val dummyLyrics = object : LyricsRepository {
            override suspend fun getLyrics(song: Song) = Result.failure<LyricsData>(Exception())
        }
        val dummyFav = object : FavoriteRepository {
            override fun getAllFavoriteSongs(): Flow<List<Song>> = emptyFlow()
            override suspend fun isFavorite(songId: String) = false
            override suspend fun toggleFavorite(song: Song) = false
            override suspend fun removeFavorite(songId: String) = Result.success(Unit)
        }
        val dummyHistory = object : HistoryRepository {
            override fun getRecentlyPlayed(limit: Int) = emptyFlow<List<Song>>()
            override fun getTopPlayedSongs(limit: Int) = emptyFlow<List<Song>>()
            override fun getTopArtists(limit: Int) = emptyFlow<List<String>>()
            override suspend fun recordPlay(song: Song) {}
            override suspend fun clearHistory() = Result.success(Unit)
        }

        playerManager = MusicPlayerManager(
            audioPlayer = fakeAudioPlayer,
            getStreamAudioUseCase = dummyGetStream,
            lyricsRepository = dummyLyrics,
            favoriteRepository = dummyFav,
            historyRepository = dummyHistory,
            scope = kotlinx.coroutines.CoroutineScope(testDispatcher)
        )

        viewModel = ArtistProfileViewModel(
            getArtistProfileUseCase = getArtistProfileUseCase,
            getArtistAllSongsUseCase = getArtistAllSongsUseCase,
            musicPlayerManager = playerManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadArtist_success_updatesUiStateToSuccess() = runTest(testDispatcher) {
        viewModel.loadArtist("UCrXRY_7SVVmc6TykwhRGUNQ")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ArtistProfileUiState.Success)
        val success = state as ArtistProfileUiState.Success
        assertEquals("Denny Caknan", success.profile.name)
        assertEquals(1, success.allSongs.size)
    }

    @Test
    fun loadArtist_failure_updatesUiStateToError() = runTest(testDispatcher) {
        repository.shouldFail = true
        viewModel.loadArtist("UCrXRY_7SVVmc6TykwhRGUNQ")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ArtistProfileUiState.Error)
        val error = state as ArtistProfileUiState.Error
        assertEquals("Network error", error.message)
    }

    @Test
    fun loadAllSongs_success_appendsSongs() = runTest(testDispatcher) {
        viewModel.loadArtist("UCrXRY_7SVVmc6TykwhRGUNQ")
        advanceUntilIdle()

        viewModel.loadAllSongs()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ArtistProfileUiState.Success)
        val success = state as ArtistProfileUiState.Success
        assertEquals(2, success.allSongs.size)
        assertTrue(success.hasLoadedAllSongs)
    }

    @Test
    fun playAll_setsQueueInPlayerManager() = runTest(testDispatcher) {
        val songs = listOf(
            Song(id = "s1", title = "Lagu 1", artist = "Denny Caknan", durationSeconds = 180, durationFormatted = "3:00", thumbnailUrl = "", webUrl = "")
        )

        viewModel.playAll(songs)

        assertEquals("s1", playerManager.playerState.value.currentSong?.id)
    }

    @Test
    fun loadArtist_emptyChannelIdWithFallback_updatesUiStateToErrorWithFallback() = runTest(testDispatcher) {
        viewModel.loadArtist("", "Denny Caknan")

        val state = viewModel.uiState.value
        assertTrue(state is ArtistProfileUiState.Error)
        val error = state as ArtistProfileUiState.Error
        assertEquals("Profil artis tidak ditemukan untuk Denny Caknan", error.message)
    }

    @Test
    fun loadArtist_emptyChannelIdWithoutFallback_updatesUiStateToError() = runTest(testDispatcher) {
        viewModel.loadArtist("   ")

        val state = viewModel.uiState.value
        assertTrue(state is ArtistProfileUiState.Error)
        val error = state as ArtistProfileUiState.Error
        assertEquals("Channel ID tidak valid", error.message)
    }
}
