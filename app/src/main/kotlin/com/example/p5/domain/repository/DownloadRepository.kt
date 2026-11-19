package com.example.p5.domain.repository

import com.example.p5.domain.model.ActiveDownloadItem
import com.example.p5.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloadedSongs(): Flow<List<Song>>
    suspend fun isDownloaded(songId: String): Boolean
    fun getActiveDownloadIds(): Flow<Set<String>>
    fun getActiveDownloads(): Flow<List<ActiveDownloadItem>>
    fun getDownloadProgress(): Flow<Map<String, Int>>
    fun enqueueDownload(song: Song)
    suspend fun deleteDownload(songId: String): Result<Unit>
    suspend fun getDownloadedFilePath(songId: String): String?
}
