package com.example.p5.domain.model

enum class PlaylistType {
    GENRE,
    ARTIST,
    RANDOM
}

data class Playlist(
    val id: String,
    val title: String,
    val subtitle: String,
    val thumbnailUrl: String,
    val type: PlaylistType,
    val query: String,
    val tracks: List<Song> = emptyList()
)
