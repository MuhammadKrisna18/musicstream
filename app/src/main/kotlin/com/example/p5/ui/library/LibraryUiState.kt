package com.example.p5.ui.library

import com.example.p5.domain.model.ActiveDownloadItem
import com.example.p5.domain.model.Song

data class LibraryUiState(
    val favoriteSongs: List<Song> = emptyList(),
    val savedSongs: List<Song> = emptyList(),
    val downloadedSongs: List<Song> = emptyList(),
    val historySongs: List<Song> = emptyList(),
    val activeDownloads: List<ActiveDownloadItem> = emptyList(),
    val activeDownloadIds: Set<String> = emptySet(),
    val downloadProgress: Map<String, Int> = emptyMap(),
    val message: String? = null
)
