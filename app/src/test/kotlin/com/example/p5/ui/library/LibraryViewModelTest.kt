package com.example.p5.ui.library

import com.example.p5.domain.model.LibrarySortOrder
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.SavedSongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeFavoriteRepository : FavoriteRepository {
        val favoritesFlow = MutableStateFlow<List<Song>>(emptyList())
        override fun getAllFavoriteSongs(): Flow<List<Song>> = favoritesFlow
        override suspend fun isFavorite(songId: String): Boolean = favoritesFlow.value.any { it.id == songId }
        override suspend fun toggleFavorite(song: Song): Boolean {
            val current = favoritesFlow.value.toMutableList()
            val exists = current.any { it.id == song.id }
            if (exists) {
                current.removeAll { it.id == song.id }
                favoritesFlow.value = current
                return false
            } else {
                current.add(song)
                favoritesFlow.value = current
                return true
            }
        }
        override suspend fun removeFavorite(songId: String): Result<Unit> {
            val current = favoritesFlow.value.toMutableList()
            current.removeAll { it.id == songId }
            favoritesFlow.value = current
            return Result.success(Unit)
        }
    }

    private class FakeHistoryRepository : HistoryRepository {
        val historyFlow = MutableStateFlow<List<Song>>(emptyList())
        override fun getRecentlyPlayed(limit: Int): Flow<List<Song>> = historyFlow
        override fun getTopPlayedSongs(limit: Int): Flow<List<Song>> = historyFlow
        override fun getTopArtists(limit: Int): Flow<List<String>> = MutableStateFlow(emptyList())
        override suspend fun recordPlay(song: Song) {}
        override suspend fun clearHistory(): Result<Unit> {
            historyFlow.value = emptyList()
            return Result.success(Unit)
        }
    }

    private class FakeSavedSongRepository : SavedSongRepository {
        val savedFlow = MutableStateFlow<List<Song>>(emptyList())
        override fun getAllSavedSongs(): Flow<List<Song>> = savedFlow
        override suspend fun isSaved(songId: String): Boolean = savedFlow.value.any { it.id == songId }
        override suspend fun toggleSaveSong(song: Song): Boolean {
            val current = savedFlow.value.toMutableList()
            val exists = current.any { it.id == song.id }
            if (exists) {
                current.removeAll { it.id == song.id }
                savedFlow.value = current
                return false
            } else {
                current.add(song)
                savedFlow.value = current
                return true
            }
        }
        override suspend fun removeSavedSong(songId: String): Result<Unit> {
            val current = savedFlow.value.toMutableList()
            current.removeAll { it.id == songId }
            savedFlow.value = current
            return Result.success(Unit)
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun setSortOrder_updatesSortOrderFlow() {
        val viewModel = LibraryViewModel(
            favoriteRepository = FakeFavoriteRepository(),
            historyRepository = FakeHistoryRepository(),
            savedSongRepository = FakeSavedSongRepository()
        )

        assertEquals(LibrarySortOrder.LATEST_ADDED, viewModel.sortOrder.value)
        viewModel.setSortOrder(LibrarySortOrder.TITLE_AZ)
        assertEquals(LibrarySortOrder.TITLE_AZ, viewModel.sortOrder.value)
    }

    @Test
    fun selectArtist_togglesArtistSelection() {
        val viewModel = LibraryViewModel(
            favoriteRepository = FakeFavoriteRepository(),
            historyRepository = FakeHistoryRepository(),
            savedSongRepository = FakeSavedSongRepository()
        )

        assertNull(viewModel.selectedArtist.value)
        viewModel.selectArtist("YOASOBI")
        assertEquals("YOASOBI", viewModel.selectedArtist.value)

        viewModel.selectArtist("YOASOBI")
        assertNull(viewModel.selectedArtist.value)
    }

    @Test
    fun toggleFavorite_updatesFavoritesAndEmitsUserMessage() = runTest {
        val favRepo = FakeFavoriteRepository()
        val viewModel = LibraryViewModel(
            favoriteRepository = favRepo,
            historyRepository = FakeHistoryRepository(),
            savedSongRepository = FakeSavedSongRepository()
        )

        val testSong = Song(
            id = "s1",
            title = "Idol",
            artist = "YOASOBI",
            thumbnailUrl = "",
            webUrl = "",
            durationSeconds = 210,
            durationFormatted = "3:30"
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.toggleFavorite(testSong)
        assertEquals(1, favRepo.favoritesFlow.value.size)
        assertEquals("Ditambahkan ke lagu favorit", viewModel.uiState.value.message)

        viewModel.clearUserMessage()
        assertNull(viewModel.uiState.value.message)

        collectJob.cancel()
    }

    @Test
    fun clearHistory_clearsHistoryAndEmitsUserMessage() = runTest {
        val histRepo = FakeHistoryRepository()
        val viewModel = LibraryViewModel(
            favoriteRepository = FakeFavoriteRepository(),
            historyRepository = histRepo,
            savedSongRepository = FakeSavedSongRepository()
        )

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        viewModel.clearHistory()
        assertEquals(0, histRepo.historyFlow.value.size)
        assertEquals("Riwayat pemutaran dibersihkan", viewModel.uiState.value.message)

        collectJob.cancel()
    }
}
