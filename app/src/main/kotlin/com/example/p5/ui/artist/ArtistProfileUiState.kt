package com.example.p5.ui.artist

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.Song

sealed interface ArtistProfileUiState {
    object Loading : ArtistProfileUiState
    data class Success(
        val profile: ArtistProfile,
        val allSongs: List<Song> = emptyList(),
        val isLoadingAllSongs: Boolean = false,
        val hasLoadedAllSongs: Boolean = false
    ) : ArtistProfileUiState
    data class Error(val message: String) : ArtistProfileUiState
}
