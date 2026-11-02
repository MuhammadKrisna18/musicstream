package com.example.p5.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.Song
import com.example.p5.domain.usecase.DeleteDownloadedSongUseCase
import com.example.p5.domain.usecase.DownloadSongUseCase
import com.example.p5.domain.usecase.GetDownloadedSongsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    getDownloadedSongsUseCase: GetDownloadedSongsUseCase,
    private val downloadSongUseCase: DownloadSongUseCase,
    private val deleteDownloadedSongUseCase: DeleteDownloadedSongUseCase
) : ViewModel() {

    private val downloadingIdsFlow = MutableStateFlow<Set<String>>(emptySet())
    private val messageFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        getDownloadedSongsUseCase(),
        downloadingIdsFlow,
        messageFlow
    ) { songs, downloadingIds, message ->
        LibraryUiState(
            downloadedSongs = songs,
            downloadingIds = downloadingIds,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

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
