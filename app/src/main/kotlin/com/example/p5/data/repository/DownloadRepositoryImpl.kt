package com.example.p5.data.repository

import android.content.Context
import android.database.Cursor
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.p5.data.download.SongDownloadWorker
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.DownloadRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File

class DownloadRepositoryImpl(
    private val context: Context,
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DownloadRepository {

    private val workManager by lazy { WorkManager.getInstance(context) }

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    fun notifyDownloadChanged() {
        updatesTrigger.tryEmit(Unit)
    }

    override fun getAllDownloadedSongs(): Flow<List<Song>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchAllDownloadedInternal() }
            .distinctUntilChanged()
    }

    private fun fetchAllDownloadedInternal(): List<Song> {
        val list = mutableListOf<Song>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_DOWNLOADED_AT} DESC"
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_ID)
            val titleIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DURATION)
            val durationFormattedIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DURATION_FORMATTED)
            val thumbnailIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_THUMBNAIL_URL)
            val pathIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH)

            while (c.moveToNext()) {
                val path = c.getString(pathIndex)
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    list.add(
                        Song(
                            id = c.getString(idIndex),
                            title = c.getString(titleIndex),
                            artist = c.getString(artistIndex),
                            durationSeconds = c.getInt(durationIndex),
                            durationFormatted = c.getString(durationFormattedIndex),
                            thumbnailUrl = c.getString(thumbnailIndex),
                            webUrl = path,
                            localFilePath = path
                        )
                    )
                }
            }
        }

        return list
    }

    override suspend fun isDownloaded(songId: String): Boolean = withContext(ioDispatcher) {
        val path = getDownloadedFilePath(songId)
        !path.isNullOrBlank() && File(path).exists()
    }

    override fun getActiveDownloadIds(): Flow<Set<String>> {
        return workManager.getWorkInfosByTagFlow(SongDownloadWorker.TAG_DOWNLOAD)
            .map { workInfos ->
                workInfos.filter {
                    it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
                }.mapNotNull { workInfo ->
                    workInfo.tags.firstOrNull { it.startsWith("song_") }?.removePrefix("song_")
                }.toSet()
            }
            .distinctUntilChanged()
    }

    override fun enqueueDownload(song: Song) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = workDataOf(
            SongDownloadWorker.KEY_ID to song.id,
            SongDownloadWorker.KEY_TITLE to song.title,
            SongDownloadWorker.KEY_ARTIST to song.artist,
            SongDownloadWorker.KEY_DURATION to song.durationSeconds,
            SongDownloadWorker.KEY_DURATION_FORMATTED to song.durationFormatted,
            SongDownloadWorker.KEY_THUMBNAIL_URL to song.thumbnailUrl,
            SongDownloadWorker.KEY_WEB_URL to song.webUrl
        )

        val workRequest = OneTimeWorkRequestBuilder<SongDownloadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(SongDownloadWorker.TAG_DOWNLOAD)
            .addTag(SongDownloadWorker.tagForSong(song.id))
            .build()

        workManager.enqueueUniqueWork(
            SongDownloadWorker.workName(song.id),
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    override suspend fun deleteDownload(songId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            workManager.cancelUniqueWork(SongDownloadWorker.workName(songId))

            val path = getDownloadedFilePath(songId)
            if (!path.isNullOrBlank()) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }

            val db = dbHelper.writableDatabase
            db.delete(
                MusicDatabaseHelper.TABLE_NAME,
                "${MusicDatabaseHelper.COLUMN_ID} = ?",
                arrayOf(songId)
            )

            notifyDownloadChanged()
        }
    }

    override suspend fun getDownloadedFilePath(songId: String): String? = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            MusicDatabaseHelper.TABLE_NAME,
            arrayOf(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH),
            "${MusicDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(songId),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val path = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH))
                val file = File(path)
                if (file.exists() && file.length() > 0) {
                    path
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
}
