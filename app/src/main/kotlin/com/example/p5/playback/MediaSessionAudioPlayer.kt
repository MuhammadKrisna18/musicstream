package com.example.p5.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Song
import com.example.p5.domain.player.AudioPlayer
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaSessionAudioPlayer(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) : AudioPlayer {

    private val appContext = context.applicationContext
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var pendingPlay: Pair<Song, AudioStream>? = null

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private var progressJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(
            appContext,
            ComponentName(appContext, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                val controller = controllerFuture?.get()
                mediaController = controller
                setupPlayerListener(controller)
                pendingPlay?.let { (song, stream) ->
                    pendingPlay = null
                    play(song, stream)
                }
            } catch (e: Exception) {
                _playerState.update { it.copy(errorMessage = e.message) }
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener(player: Player?) {
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startProgressTracker()
                } else {
                    stopProgressTracker()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> _playerState.update { it.copy(isBuffering = true) }
                    Player.STATE_READY -> {
                        _playerState.update {
                            it.copy(
                                isBuffering = false,
                                durationMs = player.duration.coerceAtLeast(0L),
                                currentPositionMs = player.currentPosition.coerceAtLeast(0L)
                            )
                        }
                    }
                    Player.STATE_ENDED -> {
                        _playerState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
                        stopProgressTracker()
                    }
                    Player.STATE_IDLE -> _playerState.update { it.copy(isBuffering = false) }
                }
            }
        })
    }

    override fun prepareSong(song: Song) {
        _playerState.update {
            it.copy(
                currentSong = song,
                isBuffering = true,
                errorMessage = null
            )
        }
    }

    override fun play(song: Song, stream: AudioStream) {
        val controller = mediaController
        if (controller == null) {
            pendingPlay = Pair(song, stream)
            _playerState.update {
                it.copy(
                    currentSong = song,
                    isBuffering = true,
                    errorMessage = null,
                    durationMs = (stream.durationSeconds * 1000L).coerceAtLeast(0L)
                )
            }
            return
        }

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setArtworkUri(if (song.thumbnailUrl.isNotEmpty()) Uri.parse(song.thumbnailUrl) else null)
            .build()

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id)
            .setUri(Uri.parse(stream.streamUrl))
            .setMediaMetadata(mediaMetadata)
            .build()

        _playerState.update {
            it.copy(
                currentSong = song,
                isBuffering = true,
                errorMessage = null,
                durationMs = (stream.durationSeconds * 1000L).coerceAtLeast(0L)
            )
        }

        controller.setMediaItem(mediaItem)
        controller.prepare()
        controller.play()
    }

    override fun setError(message: String) {
        _playerState.update {
            it.copy(
                isBuffering = false,
                errorMessage = message
            )
        }
    }

    override fun togglePlayPause() {
        val controller = mediaController ?: return
        if (controller.isPlaying) {
            controller.pause()
        } else {
            controller.play()
        }
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun seekTo(positionMs: Long) {
        val controller = mediaController ?: return
        controller.seekTo(positionMs)
        _playerState.update { it.copy(currentPositionMs = positionMs) }
    }

    override fun stop() {
        mediaController?.stop()
        stopProgressTracker()
        _playerState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
    }

    override fun release() {
        stopProgressTracker()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    if (controller.isPlaying) {
                        _playerState.update {
                            it.copy(
                                currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                                durationMs = controller.duration.coerceAtLeast(0L)
                            )
                        }
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
