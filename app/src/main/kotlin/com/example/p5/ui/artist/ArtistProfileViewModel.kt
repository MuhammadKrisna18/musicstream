package com.example.p5.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import com.example.p5.domain.usecase.GetArtistAllSongsUseCase
import com.example.p5.domain.usecase.GetArtistProfileUseCase
import com.example.p5.playback.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtistProfileViewModel(
    private val getArtistProfileUseCase: GetArtistProfileUseCase,
    private val getArtistAllSongsUseCase: GetArtistAllSongsUseCase,
    private val musicPlayerManager: MusicPlayerManager
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = musicPlayerManager.playerState

    private val _uiState = MutableStateFlow<ArtistProfileUiState>(ArtistProfileUiState.Loading)
    val uiState: StateFlow<ArtistProfileUiState> = _uiState.asStateFlow()

    private var currentChannelId: String? = null

    fun loadArtist(channelId: String) {
        val trimmed = channelId.trim()
        if (trimmed.isEmpty()) return
        if (currentChannelId == trimmed && _uiState.value is ArtistProfileUiState.Success) return

        currentChannelId = trimmed
        _uiState.value = ArtistProfileUiState.Loading

        viewModelScope.launch {
            val result = getArtistProfileUseCase(trimmed)
            result.onSuccess { profile ->
                _uiState.value = ArtistProfileUiState.Success(
                    profile = profile,
                    allSongs = profile.topSongs
                )
            }.onFailure { error ->
                _uiState.value = ArtistProfileUiState.Error(
                    error.message ?: "Failed to load artist profile"
                )
            }
        }
    }

    fun loadAllSongs() {
        val currentState = _uiState.value as? ArtistProfileUiState.Success ?: return
        if (currentState.isLoadingAllSongs || currentState.hasLoadedAllSongs) return

        val playlistId = currentState.profile.allSongsPlaylistId
        if (playlistId.isNullOrBlank()) {
            _uiState.value = currentState.copy(hasLoadedAllSongs = true)
            return
        }

        _uiState.value = currentState.copy(isLoadingAllSongs = true)

        viewModelScope.launch {
            val result = getArtistAllSongsUseCase(playlistId)
            result.onSuccess { songs ->
                val combined = (currentState.allSongs + songs).distinctBy { it.id }
                _uiState.value = currentState.copy(
                    allSongs = combined,
                    isLoadingAllSongs = false,
                    hasLoadedAllSongs = true
                )
            }.onFailure {
                _uiState.value = currentState.copy(isLoadingAllSongs = false)
            }
        }
    }

    fun playSong(song: Song, contextSongs: List<Song>) {
        val queue = if (contextSongs.isNotEmpty()) contextSongs else listOf(song)
        val index = queue.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
        musicPlayerManager.playQueue(queue, index)
    }

    fun playAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            musicPlayerManager.playQueue(songs, 0)
        }
    }

    fun shuffleAll(songs: List<Song>) {
        if (songs.isNotEmpty()) {
            val shuffled = songs.shuffled()
            musicPlayerManager.playQueue(shuffled, 0)
        }
    }
}
