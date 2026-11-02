package com.example.p5.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.model.DownloadedSong
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

class DownloadRepositoryImpl(
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DownloadRepository {

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getAllDownloadedSongs(): Flow<List<DownloadedSong>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchAllSongsInternal() }
            .distinctUntilChanged()
    }

    private fun fetchAllSongsInternal(): List<DownloadedSong> {
        val list = mutableListOf<DownloadedSong>()
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
            val sizeIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FILE_SIZE_BYTES)
            val downloadedAtIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DOWNLOADED_AT)

            while (c.moveToNext()) {
                list.add(
                    DownloadedSong(
                        id = c.getString(idIndex),
                        title = c.getString(titleIndex),
                        artist = c.getString(artistIndex),
                        durationSeconds = c.getInt(durationIndex),
                        durationFormatted = c.getString(durationFormattedIndex),
                        thumbnailUrl = c.getString(thumbnailIndex),
                        localFilePath = c.getString(pathIndex),
                        fileSizeBytes = c.getLong(sizeIndex),
                        downloadedAt = c.getLong(downloadedAtIndex)
                    )
                )
            }
        }
        return list
    }

    override suspend fun getDownloadedSongById(id: String): DownloadedSong? = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            MusicDatabaseHelper.TABLE_NAME,
            null,
            "${MusicDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                DownloadedSong(
                    id = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_ID)),
                    title = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_TITLE)),
                    artist = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_ARTIST)),
                    durationSeconds = c.getInt(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DURATION)),
                    durationFormatted = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DURATION_FORMATTED)),
                    thumbnailUrl = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_THUMBNAIL_URL)),
                    localFilePath = c.getString(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH)),
                    fileSizeBytes = c.getLong(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FILE_SIZE_BYTES)),
                    downloadedAt = c.getLong(c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_DOWNLOADED_AT))
                )
            } else null
        }
    }

    override suspend fun saveDownloadedSong(song: DownloadedSong): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(MusicDatabaseHelper.COLUMN_ID, song.id)
                put(MusicDatabaseHelper.COLUMN_TITLE, song.title)
                put(MusicDatabaseHelper.COLUMN_ARTIST, song.artist)
                put(MusicDatabaseHelper.COLUMN_DURATION, song.durationSeconds)
                put(MusicDatabaseHelper.COLUMN_DURATION_FORMATTED, song.durationFormatted)
                put(MusicDatabaseHelper.COLUMN_THUMBNAIL_URL, song.thumbnailUrl)
                put(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH, song.localFilePath)
                put(MusicDatabaseHelper.COLUMN_FILE_SIZE_BYTES, song.fileSizeBytes)
                put(MusicDatabaseHelper.COLUMN_DOWNLOADED_AT, song.downloadedAt)
            }
            db.insertWithOnConflict(
                MusicDatabaseHelper.TABLE_NAME,
                null,
                values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
            )
            updatesTrigger.tryEmit(Unit)
            Unit
        }
    }

    override suspend fun deleteDownloadedSong(id: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val db = dbHelper.writableDatabase
            db.delete(MusicDatabaseHelper.TABLE_NAME, "${MusicDatabaseHelper.COLUMN_ID} = ?", arrayOf(id))
            updatesTrigger.tryEmit(Unit)
            Unit
        }
    }

    override suspend fun isSongDownloaded(id: String): Boolean = withContext(ioDispatcher) {
        getDownloadedSongById(id) != null
    }
}
