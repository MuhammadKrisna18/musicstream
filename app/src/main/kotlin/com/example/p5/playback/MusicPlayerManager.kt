package com.example.p5.playback

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.domain.player.AudioPlayer
import com.example.p5.domain.repository.DownloadRepository
import com.example.p5.domain.usecase.GetStreamAudioUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class MusicPlayerManager(
    private val audioPlayer: AudioPlayer,
    private val getStreamAudioUseCase: GetStreamAudioUseCase,
    private val downloadRepository: DownloadRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    val playerState: StateFlow<PlayerState> = audioPlayer.playerState

    fun playSong(song: Song) {
        audioPlayer.prepareSong(song)
        scope.launch {
            val offlineSong = downloadRepository.getDownloadedSongById(song.id)
            if (offlineSong != null) {
                val file = File(offlineSong.localFilePath)
                if (file.exists() && file.length() > 0) {
                    val localStream = AudioStream(
                        id = offlineSong.id,
                        title = offlineSong.title,
                        artist = offlineSong.artist,
                        streamUrl = file.toURI().toString(),
                        durationSeconds = offlineSong.durationSeconds,
                        durationFormatted = offlineSong.durationFormatted,
                        thumbnailUrl = offlineSong.thumbnailUrl,
                        format = "local",
                        bitrate = null
                    )
                    audioPlayer.play(song, localStream)
                    return@launch
                }
            }

            getStreamAudioUseCase(song.id)
                .onSuccess { stream ->
                    audioPlayer.play(song, stream)
                }
                .onFailure { error ->
                    audioPlayer.setError("Failed to play: ${error.localizedMessage ?: "Network error"}")
                }
        }
    }

    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun stop() {
        audioPlayer.stop()
    }

    fun release() {
        audioPlayer.release()
    }
}
