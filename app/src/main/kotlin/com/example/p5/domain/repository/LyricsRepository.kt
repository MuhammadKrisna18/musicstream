package com.example.p5.domain.repository

import com.example.p5.domain.model.LyricsData
import com.example.p5.domain.model.Song

interface LyricsRepository {
    suspend fun getLyrics(song: Song): Result<LyricsData>
}

