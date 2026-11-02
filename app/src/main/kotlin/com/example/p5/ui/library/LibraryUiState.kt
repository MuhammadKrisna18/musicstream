package com.example.p5.ui.library

import com.example.p5.domain.model.DownloadedSong

data class LibraryUiState(
    val downloadedSongs: List<DownloadedSong> = emptyList(),
    val downloadingIds: Set<String> = emptySet(),
    val message: String? = null
)
