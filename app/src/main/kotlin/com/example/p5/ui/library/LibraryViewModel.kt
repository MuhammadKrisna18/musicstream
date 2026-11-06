package com.example.p5.ui.library

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.playback.MediaCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val messageFlow = MutableStateFlow<String?>(null)
    private val cacheSizeBytesFlow = MutableStateFlow(0L)

    val uiState: StateFlow<LibraryUiState> = combine(
        favoriteRepository.getAllFavoriteSongs(),
        historyRepository.getRecentlyPlayed(50),
        messageFlow,
        cacheSizeBytesFlow
    ) { favorites, history, message, cacheSize ->
        LibraryUiState(
            favoriteSongs = favorites,
            historySongs = history,
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
