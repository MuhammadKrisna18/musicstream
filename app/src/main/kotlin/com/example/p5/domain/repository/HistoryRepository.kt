package com.example.p5.domain.repository

import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getRecentlyPlayed(limit: Int = 50): Flow<List<Song>>
    fun getTopArtists(limit: Int = 5): Flow<List<String>>
    suspend fun recordPlay(song: Song)
    suspend fun clearHistory(): Result<Unit>
}

