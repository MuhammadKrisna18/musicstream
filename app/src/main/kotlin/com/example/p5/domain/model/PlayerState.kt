package com.example.p5.domain.model

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class PlayerState(
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val errorMessage: String? = null,
    val queue: List<Song> = emptyList(),
    val currentQueueIndex: Int = -1,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val sleepTimerSecondsRemaining: Long? = null,
    val isSleepTimerEndOfTrack: Boolean = false,
    val currentLyrics: LyricsData? = null,
    val isLyricsLoading: Boolean = false,
    val isCurrentSongFavorite: Boolean = false
) {
    val hasNext: Boolean
        get() = if (repeatMode != RepeatMode.OFF) queue.isNotEmpty() else currentQueueIndex < queue.lastIndex

    val hasPrevious: Boolean
        get() = if (repeatMode != RepeatMode.OFF) queue.isNotEmpty() else currentQueueIndex > 0 || currentPositionMs > 3000L

    val isSleepTimerActive: Boolean
        get() = sleepTimerSecondsRemaining != null || isSleepTimerEndOfTrack
}
