package com.example.p5.domain.player

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.LyricsData
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.RepeatMode
import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val playerState: StateFlow<PlayerState>
    fun setOnPlaybackEndedListener(listener: () -> Unit)
    fun prepareSong(song: Song)
    fun play(song: Song, stream: AudioStream)
    fun updateQueueState(
        queue: List<Song>,
        currentIndex: Int,
        isShuffleEnabled: Boolean,
        repeatMode: RepeatMode
    )
    fun updateSleepTimerState(secondsRemaining: Long?, isEndOfTrack: Boolean)
    fun updateLyricsState(lyrics: LyricsData?, isLoading: Boolean)
    fun updateFavoriteState(isFavorite: Boolean)
    fun setError(message: String)
    fun togglePlayPause()
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun stop()
    fun release()
}
