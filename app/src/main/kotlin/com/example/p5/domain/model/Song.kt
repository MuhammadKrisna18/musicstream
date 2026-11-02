package com.example.p5.domain.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val durationFormatted: String,
    val thumbnailUrl: String,
    val webUrl: String
)
