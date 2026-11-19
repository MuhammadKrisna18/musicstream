package com.example.p5.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.LibrarySortOrder
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.DownloadRepository
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.SavedSongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository,
    private val savedSongRepository: SavedSongRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val messageFlow = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow(LibrarySortOrder.LATEST_ADDED)
    val sortOrder: StateFlow<LibrarySortOrder> = _sortOrder.asStateFlow()
    private val _selectedArtist = MutableStateFlow<String?>(null)
    val selectedArtist: StateFlow<String?> = _selectedArtist.asStateFlow()

    fun setSortOrder(order: LibrarySortOrder) {
        _sortOrder.value = order
    }

    fun selectArtist(artist: String?) {
        if (_selectedArtist.value == artist) {
            _selectedArtist.value = null
        } else {
            _selectedArtist.value = artist
        }
    }

    private data class StoredLists(
        val favorites: List<Song>,
        val saved: List<Song>,
        val downloaded: List<Song>
    )

    val uiState: StateFlow<LibraryUiState> = combine(
        combine(
            favoriteRepository.getAllFavoriteSongs(),
            savedSongRepository.getAllSavedSongs(),
            downloadRepository.getAllDownloadedSongs()
        ) { favorites, saved, downloaded ->
            StoredLists(favorites, saved, downloaded)
        },
        historyRepository.getRecentlyPlayed(50),
        downloadRepository.getActiveDownloads(),
        messageFlow
    ) { stored, history, activeDownloads, message ->
        val activeIds = activeDownloads.map { it.song.id }.toSet()
        val progressMap = activeDownloads.associate { it.song.id to it.progress }
        LibraryUiState(
            favoriteSongs = stored.favorites,
            savedSongs = stored.saved,
            downloadedSongs = stored.downloaded,
            historySongs = history,
            activeDownloads = activeDownloads,
            activeDownloadIds = activeIds,
            downloadProgress = progressMap,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun removeFavorite(songId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(songId)
                .onSuccess {
                    messageFlow.value = "Dihapus dari lagu favorit"
                }
        }
    }

    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val isFav = favoriteRepository.toggleFavorite(song)
            messageFlow.value = if (isFav) "Ditambahkan ke lagu favorit" else "Dihapus dari lagu favorit"
        }
    }

    fun removeSavedSong(songId: String) {
        viewModelScope.launch {
            savedSongRepository.removeSavedSong(songId)
                .onSuccess {
                    messageFlow.value = "Dihapus dari lagu tersimpan"
                }
        }
    }

    fun toggleSaveSong(song: Song) {
        viewModelScope.launch {
            val isSaved = savedSongRepository.toggleSaveSong(song)
            messageFlow.value = if (isSaved) "Lagu disimpan" else "Dihapus dari lagu tersimpan"
        }
    }

    fun toggleDownload(song: Song) {
        viewModelScope.launch {
            if (downloadRepository.isDownloaded(song.id)) {
                downloadRepository.deleteDownload(song.id)
                    .onSuccess {
                        messageFlow.value = "Unduhan lagu dihapus"
                    }
            } else {
                downloadRepository.enqueueDownload(song)
                messageFlow.value = "Mengunduh lagu untuk pemutaran offline..."
            }
        }
    }

    fun deleteDownload(songId: String) {
        viewModelScope.launch {
            downloadRepository.deleteDownload(songId)
                .onSuccess {
                    messageFlow.value = "Unduhan lagu dihapus"
                }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
                .onSuccess {
                    messageFlow.value = "Riwayat pemutaran dibersihkan"
                }
        }
    }

    fun clearUserMessage() {
        messageFlow.value = null
    }
}
