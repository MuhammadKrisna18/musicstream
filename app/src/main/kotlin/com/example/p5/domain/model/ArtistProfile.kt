package com.example.p5.domain.model

data class ArtistProfile(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val bannerUrl: String = "",
    val description: String = "",
    val subscriberCount: String = "",
    val topSongs: List<Song> = emptyList(),
    val albums: List<Playlist> = emptyList(),
    val singles: List<Playlist> = emptyList(),
    val allSongsPlaylistId: String? = null
)
