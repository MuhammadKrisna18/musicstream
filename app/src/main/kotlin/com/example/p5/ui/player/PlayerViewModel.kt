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

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        musicPlayerManager.playQueue(songs, startIndex)
    }

    fun playSong(song: Song) {
        musicPlayerManager.playSong(song)
    }

    fun addToQueueNext(song: Song): Boolean {
        return musicPlayerManager.addToQueueNext(song)
    }

    fun playNext() {
        musicPlayerManager.playNext()
    }

    fun playPrevious() {
        musicPlayerManager.playPrevious()
    }

    fun toggleShuffle() {
        musicPlayerManager.toggleShuffle()
    }

    fun toggleRepeatMode() {
        musicPlayerManager.toggleRepeatMode()
    }

    fun playQueueIndex(index: Int) {
        musicPlayerManager.playQueueIndex(index)
    }

    fun togglePlayPause() {
        musicPlayerManager.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        musicPlayerManager.seekTo(positionMs)
    }

    fun setSleepTimerMinutes(minutes: Int) {
        musicPlayerManager.setSleepTimerMinutes(minutes)
    }

    fun setSleepTimerEndOfTrack() {
        musicPlayerManager.setSleepTimerEndOfTrack()
    }

    fun cancelSleepTimer() {
        musicPlayerManager.cancelSleepTimer()
    }

    fun retryFetchLyrics() {
        musicPlayerManager.retryFetchLyrics()
    }

    fun toggleFavoriteCurrentSong() {
        musicPlayerManager.toggleFavoriteCurrentSong()
    }

    fun stop() {
        musicPlayerManager.stop()
    }
}
