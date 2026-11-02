package com.example.p5.ui.library

import com.example.p5.domain.model.DownloadedSong
import com.example.p5.domain.model.Song

data class LibraryUiState(
    val downloadedSongs: List<DownloadedSong> = emptyList(),
    val favoriteSongs: List<Song> = emptyList(),
    val downloadingIds: Set<String> = emptySet(),
    val message: String? = null,
    val cacheSizeBytes: Long = 0L
)
