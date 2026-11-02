package com.example.p5.domain.model

data class AudioStream(
    val id: String,
    val title: String,
    val artist: String,
    val streamUrl: String,
    val durationSeconds: Int,
    val durationFormatted: String,
    val thumbnailUrl: String,
    val format: String?,
    val bitrate: Double?
)
