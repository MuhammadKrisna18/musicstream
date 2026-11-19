package com.example.p5.ui.library

import com.example.p5.domain.model.Song

data class LibraryUiState(
    val favoriteSongs: List<Song> = emptyList(),
    val savedSongs: List<Song> = emptyList(),
    val downloadedSongs: List<Song> = emptyList(),
    val historySongs: List<Song> = emptyList(),
    val activeDownloadIds: Set<String> = emptySet(),
    val message: String? = null
)
