package com.example.p5.domain.model

data class SearchResult(
    val songs: List<Song>,
    val playlists: List<Playlist> = emptyList()
)
