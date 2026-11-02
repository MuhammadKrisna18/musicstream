package com.example.p5.ui.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.usecase.DeleteDownloadedSongUseCase
import com.example.p5.domain.usecase.DownloadSongUseCase
import com.example.p5.domain.usecase.GetDownloadedSongsUseCase
import com.example.p5.playback.MediaCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    getDownloadedSongsUseCase: GetDownloadedSongsUseCase,
    private val downloadSongUseCase: DownloadSongUseCase,
    private val deleteDownloadedSongUseCase: DeleteDownloadedSongUseCase,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val downloadingIdsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val messageFlow = MutableStateFlow<String?>(null)
    private val cacheSizeBytesFlow = MutableStateFlow(0L)

    val uiState: StateFlow<LibraryUiState> = combine(
        getDownloadedSongsUseCase(),
        favoriteRepository.getAllFavoriteSongs(),
        downloadingIdsFlow,
        messageFlow,
        cacheSizeBytesFlow
    ) { downloaded, favorites, downloadingIds, message, cacheSize ->
        LibraryUiState(
            downloadedSongs = downloaded,
            favoriteSongs = favorites,
            downloadingIds = downloadingIds,
            message = message,
            cacheSizeBytes = cacheSize
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun refreshCacheSize(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val size = MediaCacheManager.getCachedSizeBytes(context)
            cacheSizeBytesFlow.value = size
        }
    }

    fun clearStreamingCache(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            MediaCacheManager.clearCache(context)
            cacheSizeBytesFlow.value = 0L
            messageFlow.value = "Cache streaming dibersihkan"
        }
    }

    fun removeFavorite(songId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(songId)
                .onSuccess {
                    messageFlow.value = "Dihapus dari lagu favorit"
                }
        }
    }

    fun downloadSong(song: Song) {
        if (downloadingIdsFlow.value.contains(song.id)) return

        downloadingIdsFlow.update { it + song.id }
        viewModelScope.launch {
            downloadSongUseCase(song)
                .onSuccess {
                    messageFlow.value = "Downloaded ${song.title}"
                }
                .onFailure { throwable ->
                    messageFlow.value = "Failed to download ${song.title}: ${throwable.localizedMessage}"
                }
            downloadingIdsFlow.update { it - song.id }
        }
    }

    fun deleteSong(songId: String) {
        viewModelScope.launch {
            deleteDownloadedSongUseCase(songId)
                .onSuccess {
                    messageFlow.value = "Removed from downloads"
                }
                .onFailure { throwable ->
                    messageFlow.value = "Failed to delete: ${throwable.localizedMessage}"
                }
        }
    }

    fun clearMessage() {
        messageFlow.value = null
    }
}
