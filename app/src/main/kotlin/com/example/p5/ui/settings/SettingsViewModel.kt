package com.example.p5.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.repository.UpdateRepository
import com.example.p5.playback.MediaCacheManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val updateRepository: UpdateRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(currentVersion = updateRepository.currentVersionName)
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateCacheSize(bytes: Long) {
        _uiState.update { it.copy(cacheSizeBytes = bytes) }
    }

    fun refreshCacheSize(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            val bytes = MediaCacheManager.getCachedSizeBytes(context)
            _uiState.update { it.copy(cacheSizeBytes = bytes) }
        }
    }

    fun clearCache(context: Context) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isClearingCache = true) }
            MediaCacheManager.clearCache(context)
            _uiState.update {
                it.copy(
                    cacheSizeBytes = 0L,
                    isClearingCache = false,
                    userMessage = "Cache streaming berhasil dibersihkan"
                )
            }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}
