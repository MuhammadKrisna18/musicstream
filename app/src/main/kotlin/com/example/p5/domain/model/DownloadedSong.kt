package com.example.p5.domain.model

data class DownloadedSong(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val durationFormatted: String,
    val thumbnailUrl: String,
    val localFilePath: String,
    val fileSizeBytes: Long,
    val downloadedAt: Long
) {
    fun toSong(): Song {
        return Song(
            id = id,
            title = title,
            artist = artist,
            durationSeconds = durationSeconds,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = "file://$localFilePath"
        )
    }
}
