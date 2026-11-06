package com.example.p5.playback

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.RepeatMode
import com.example.p5.domain.model.Song
import com.example.p5.domain.player.AudioPlayer
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.LyricsRepository
import com.example.p5.domain.usecase.GetStreamAudioUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicPlayerManager(
    private val audioPlayer: AudioPlayer,
    private val getStreamAudioUseCase: GetStreamAudioUseCase,
    private val lyricsRepository: LyricsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    val playerState: StateFlow<PlayerState> = audioPlayer.playerState

    private var currentQueue: MutableList<Song> = mutableListOf()
    private var currentQueueIndex: Int = -1
    private var isShuffleEnabled: Boolean = false
    private var repeatMode: RepeatMode = RepeatMode.OFF
    private var shuffleOrder: MutableList<Int> = mutableListOf()

    private var loadSongJob: Job? = null
    private var lyricsJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var sleepTimerSecondsRemaining: Long? = null
    private var isSleepTimerEndOfTrack: Boolean = false

    init {
        audioPlayer.setOnPlaybackEndedListener {
            handlePlaybackEnded()
        }
    }

    fun playQueue(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        currentQueue = songs.toMutableList()
        currentQueueIndex = startIndex.coerceIn(0, songs.lastIndex)

        if (isShuffleEnabled) {
            rebuildShuffleOrder()
        }

        syncQueueState()
        loadAndPlay(currentQueue[currentQueueIndex])
    }

    fun playSong(song: Song) {
        val existingIndex = currentQueue.indexOfFirst { it.id == song.id }
        if (existingIndex != -1) {
            currentQueueIndex = existingIndex
        } else {
            currentQueue = mutableListOf(song)
            currentQueueIndex = 0
            shuffleOrder = mutableListOf(0)
        }

        syncQueueState()
        loadAndPlay(song)
    }

    fun playNext(isAutoPlay: Boolean = false) {
        if (currentQueue.isEmpty()) return

        if (isShuffleEnabled && shuffleOrder.isNotEmpty()) {
            val currentPosInShuffle = shuffleOrder.indexOf(currentQueueIndex)
            if (currentPosInShuffle != -1 && currentPosInShuffle < shuffleOrder.lastIndex) {
                currentQueueIndex = shuffleOrder[currentPosInShuffle + 1]
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else if (repeatMode == RepeatMode.ALL) {
                rebuildShuffleOrder()
                currentQueueIndex = shuffleOrder.firstOrNull() ?: 0
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else {
                if (!isAutoPlay) {
                    audioPlayer.seekTo(0L)
                }
            }
        } else {
            if (currentQueueIndex < currentQueue.lastIndex) {
                currentQueueIndex++
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else if (repeatMode == RepeatMode.ALL) {
                currentQueueIndex = 0
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else {
                if (!isAutoPlay) {
                    audioPlayer.seekTo(0L)
                }
            }
        }
    }

    fun playPrevious() {
        if (currentQueue.isEmpty()) return

        // If played more than 3 seconds, replay current song
        if (playerState.value.currentPositionMs > 3000L) {
            audioPlayer.seekTo(0L)
            return
        }

        if (isShuffleEnabled && shuffleOrder.isNotEmpty()) {
            val currentPosInShuffle = shuffleOrder.indexOf(currentQueueIndex)
            if (currentPosInShuffle > 0) {
                currentQueueIndex = shuffleOrder[currentPosInShuffle - 1]
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else if (repeatMode == RepeatMode.ALL) {
                currentQueueIndex = shuffleOrder.last()
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else {
                audioPlayer.seekTo(0L)
            }
        } else {
            if (currentQueueIndex > 0) {
                currentQueueIndex--
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else if (repeatMode == RepeatMode.ALL) {
                currentQueueIndex = currentQueue.lastIndex
                syncQueueState()
                loadAndPlay(currentQueue[currentQueueIndex])
            } else {
                audioPlayer.seekTo(0L)
            }
        }
    }

    fun playQueueIndex(index: Int) {
        if (index in currentQueue.indices) {
            currentQueueIndex = index
            syncQueueState()
            loadAndPlay(currentQueue[currentQueueIndex])
        }
    }

    fun toggleShuffle() {
        isShuffleEnabled = !isShuffleEnabled
        if (isShuffleEnabled) {
            rebuildShuffleOrder()
        }
        syncQueueState()
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        syncQueueState()
    }

    fun setSleepTimerMinutes(minutes: Int) {
        cancelSleepTimer()
        isSleepTimerEndOfTrack = false
        val totalSec = minutes * 60L
        sleepTimerSecondsRemaining = totalSec
        audioPlayer.updateSleepTimerState(totalSec, false)

        sleepTimerJob = scope.launch {
            var current = totalSec
            while (current > 0) {
                delay(1000L)
                current--
                sleepTimerSecondsRemaining = current
                audioPlayer.updateSleepTimerState(current, false)
            }
            audioPlayer.pause()
            cancelSleepTimer()
        }
    }

    fun setSleepTimerEndOfTrack() {
        cancelSleepTimer()
        isSleepTimerEndOfTrack = true
        audioPlayer.updateSleepTimerState(null, true)
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        sleepTimerSecondsRemaining = null
        isSleepTimerEndOfTrack = false
        audioPlayer.updateSleepTimerState(null, false)
    }

    fun retryFetchLyrics() {
        val current = playerState.value.currentSong ?: return
        loadLyrics(current)
    }

    fun toggleFavoriteCurrentSong() {
        val song = playerState.value.currentSong ?: return
        scope.launch {
            val isFav = favoriteRepository.toggleFavorite(song)
            audioPlayer.updateFavoriteState(isFav)
        }
    }

    private fun handlePlaybackEnded() {
        if (isSleepTimerEndOfTrack) {
            audioPlayer.pause()
            cancelSleepTimer()
            return
        }

        when (repeatMode) {
            RepeatMode.ONE -> {
                audioPlayer.seekTo(0L)
                audioPlayer.resume()
            }
            RepeatMode.ALL, RepeatMode.OFF -> {
                playNext(isAutoPlay = true)
            }
        }
    }

    private fun rebuildShuffleOrder() {
        if (currentQueue.isEmpty()) {
            shuffleOrder.clear()
            return
        }
        val remaining = (0 until currentQueue.size).filter { it != currentQueueIndex }.shuffled()
        shuffleOrder = (listOf(currentQueueIndex) + remaining).toMutableList()
    }

    private fun syncQueueState() {
        audioPlayer.updateQueueState(
            queue = currentQueue.toList(),
            currentIndex = currentQueueIndex,
            isShuffleEnabled = isShuffleEnabled,
            repeatMode = repeatMode
        )
    }

    private fun loadAndPlay(song: Song) {
        loadSongJob?.cancel()
        audioPlayer.prepareSong(song)
        loadLyrics(song)

        scope.launch {
            val isFav = favoriteRepository.isFavorite(song.id)
            audioPlayer.updateFavoriteState(isFav)
        }

        scope.launch {
            historyRepository.recordPlay(song)
        }

        loadSongJob = scope.launch {
            getStreamAudioUseCase(song.id)
                .onSuccess { stream ->
                    audioPlayer.play(song, stream)
                }
                .onFailure { error ->
                    audioPlayer.setError("Failed to play: ${error.localizedMessage ?: "Network error"}")
                }
        }
    }

    private fun loadLyrics(song: Song) {
        lyricsJob?.cancel()
        audioPlayer.updateLyricsState(lyrics = null, isLoading = true)
        lyricsJob = scope.launch {
            lyricsRepository.getLyrics(song)
                .onSuccess { lyrics ->
                    audioPlayer.updateLyricsState(lyrics = lyrics, isLoading = false)
                }
                .onFailure {
                    audioPlayer.updateLyricsState(lyrics = null, isLoading = false)
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
        cancelSleepTimer()
        audioPlayer.release()
    }
}
