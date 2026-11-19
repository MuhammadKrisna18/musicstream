package com.example.p5.data.remote.deezer

data class DeezerTrack(
    val id: Long,
    val title: String,
    val artistName: String,
    val albumTitle: String,
    val coverUrl: String
)

data class DeezerArtist(
    val id: Long,
    val name: String,
    val pictureUrl: String
)

data class DeezerPlaylist(
    val id: Long,
    val title: String,
    val pictureUrl: String,
    val trackCount: Int
)

data class DeezerGenre(
    val id: Long,
    val name: String,
    val pictureUrl: String
)

data class DeezerChartData(
    val tracks: List<DeezerTrack> = emptyList(),
    val artists: List<DeezerArtist> = emptyList(),
    val playlists: List<DeezerPlaylist> = emptyList()
)
