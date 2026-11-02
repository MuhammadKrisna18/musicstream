package com.example.p5.domain.repository

import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavoriteSongs(): Flow<List<Song>>
    suspend fun isFavorite(songId: String): Boolean
    suspend fun toggleFavorite(song: Song): Boolean
    suspend fun removeFavorite(songId: String): Result<Unit>
}

