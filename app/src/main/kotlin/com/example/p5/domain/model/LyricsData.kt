package com.example.p5.domain.model

data class LyricLine(
    val timestampMs: Long,
    val text: String
)

data class LyricsData(
    val trackId: String,
    val isSynced: Boolean,
    val lines: List<LyricLine> = emptyList(),
    val plainLyrics: String = "",
    val isInstrumental: Boolean = false
)

