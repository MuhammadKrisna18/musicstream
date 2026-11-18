package com.example.p5.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.AppThemeId
import com.example.p5.domain.repository.ThemeRepository
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
    private val themeRepository: ThemeRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            currentVersion = updateRepository.currentVersionName,
            currentTheme = themeRepository.currentTheme.value
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            themeRepository.currentTheme.collect { themeId ->
                _uiState.update { it.copy(currentTheme = themeId) }
            }
        }
    }

    fun selectTheme(themeId: AppThemeId) {
        viewModelScope.launch {
            themeRepository.setTheme(themeId)
            _uiState.update { it.copy(currentTheme = themeId) }
        }
    }

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
