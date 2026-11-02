package com.example.p5.domain.repository

import com.example.p5.domain.model.DownloadedSong
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloadedSongs(): Flow<List<DownloadedSong>>
    suspend fun getDownloadedSongById(id: String): DownloadedSong?
    suspend fun saveDownloadedSong(song: DownloadedSong): Result<Unit>
    suspend fun deleteDownloadedSong(id: String): Result<Unit>
    suspend fun isSongDownloaded(id: String): Boolean
}
