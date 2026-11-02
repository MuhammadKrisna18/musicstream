package com.example.p5.ui.player

import androidx.lifecycle.ViewModel
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.playback.MusicPlayerManager
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(
    private val musicPlayerManager: MusicPlayerManager
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = musicPlayerManager.playerState

    fun playSong(song: Song) {
        musicPlayerManager.playSong(song)
    }

    fun togglePlayPause() {
        musicPlayerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        musicPlayerManager.seekTo(positionMs)
    }

    fun stop() {
        musicPlayerManager.stop()
    }
}
