package com.example.p5.ui.library

import com.example.p5.domain.model.Song

data class LibraryUiState(
    val favoriteSongs: List<Song> = emptyList(),
    val savedSongs: List<Song> = emptyList(),
    val historySongs: List<Song> = emptyList(),
    val message: String? = null,
    val cacheSizeBytes: Long = 0L
)
