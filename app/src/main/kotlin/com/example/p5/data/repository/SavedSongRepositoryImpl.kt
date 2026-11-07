package com.example.p5.data.repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.SavedSongRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class SavedSongRepositoryImpl(
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SavedSongRepository {

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getAllSavedSongs(): Flow<List<Song>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchAllSavedInternal() }
            .distinctUntilChanged()
    }

    private fun fetchAllSavedInternal(): List<Song> {
        val list = mutableListOf<Song>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_SAVED,
            null,
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_SAVED_AT} DESC"
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_ID)
            val titleIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_DURATION)
            val durationFormattedIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_DURATION_FORMATTED)
            val thumbnailIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_THUMBNAIL_URL)
            val webUrlIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SAVED_WEB_URL)

            while (c.moveToNext()) {
                list.add(
                    Song(
                        id = c.getString(idIndex),
                        title = c.getString(titleIndex),
                        artist = c.getString(artistIndex),
                        durationSeconds = c.getInt(durationIndex),
                        durationFormatted = c.getString(durationFormattedIndex),
                        thumbnailUrl = c.getString(thumbnailIndex),
                        webUrl = c.getString(webUrlIndex)
                    )
                )
            }
        }

        return list
    }

    override suspend fun isSaved(songId: String): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            MusicDatabaseHelper.TABLE_SAVED,
            arrayOf(MusicDatabaseHelper.COLUMN_SAVED_ID),
            "${MusicDatabaseHelper.COLUMN_SAVED_ID} = ?",
            arrayOf(songId),
            null,
            null,
            null
        )
        cursor.use { it.count > 0 }
    }

    override suspend fun toggleSaveSong(song: Song): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val exists = isSaved(song.id)

        if (exists) {
            db.delete(
                MusicDatabaseHelper.TABLE_SAVED,
                "${MusicDatabaseHelper.COLUMN_SAVED_ID} = ?",
                arrayOf(song.id)
            )
            updatesTrigger.tryEmit(Unit)
            false
        } else {
            val values = ContentValues().apply {
                put(MusicDatabaseHelper.COLUMN_SAVED_ID, song.id)
                put(MusicDatabaseHelper.COLUMN_SAVED_TITLE, song.title)
                put(MusicDatabaseHelper.COLUMN_SAVED_ARTIST, song.artist)
                put(MusicDatabaseHelper.COLUMN_SAVED_DURATION, song.durationSeconds)
                put(MusicDatabaseHelper.COLUMN_SAVED_DURATION_FORMATTED, song.durationFormatted)
                put(MusicDatabaseHelper.COLUMN_SAVED_THUMBNAIL_URL, song.thumbnailUrl)
                put(MusicDatabaseHelper.COLUMN_SAVED_WEB_URL, song.webUrl)
                put(MusicDatabaseHelper.COLUMN_SAVED_AT, System.currentTimeMillis())
            }
            db.insertWithOnConflict(
                MusicDatabaseHelper.TABLE_SAVED,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            updatesTrigger.tryEmit(Unit)
            true
        }
    }

    override suspend fun removeSavedSong(songId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val db = dbHelper.writableDatabase
            db.delete(
                MusicDatabaseHelper.TABLE_SAVED,
                "${MusicDatabaseHelper.COLUMN_SAVED_ID} = ?",
                arrayOf(songId)
            )
            updatesTrigger.tryEmit(Unit)
            Unit
        }
    }
}

