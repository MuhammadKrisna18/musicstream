package com.example.p5.domain.repository

import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface SavedSongRepository {
    fun getAllSavedSongs(): Flow<List<Song>>
    suspend fun isSaved(songId: String): Boolean
    suspend fun toggleSaveSong(song: Song): Boolean
    suspend fun removeSavedSong(songId: String): Result<Unit>
}

