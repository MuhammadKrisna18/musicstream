package com.example.p5.domain.player

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayer {
    val playerState: StateFlow<PlayerState>
    fun prepareSong(song: Song)
    fun play(song: Song, stream: AudioStream)
    fun setError(message: String)
    fun togglePlayPause()
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun stop()
    fun release()
}
