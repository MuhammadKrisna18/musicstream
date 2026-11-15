package com.example.p5.ui.search

import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(
        val songs: List<Song>,
        val playlists: List<Playlist> = emptyList()
    ) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}
