package com.example.p5.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoriteRepository {

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getAllFavoriteSongs(): Flow<List<Song>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchAllFavoritesInternal() }
            .distinctUntilChanged()
    }

    private fun fetchAllFavoritesInternal(): List<Song> {
        val list = mutableListOf<Song>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_FAVORITES,
            null,
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_FAV_LIKED_AT} DESC"
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_ID)
            val titleIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_DURATION)
            val durationFormattedIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_DURATION_FORMATTED)
            val thumbnailIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_THUMBNAIL_URL)
            val webUrlIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_FAV_WEB_URL)

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

    override suspend fun isFavorite(songId: String): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            MusicDatabaseHelper.TABLE_FAVORITES,
            arrayOf(MusicDatabaseHelper.COLUMN_FAV_ID),
            "${MusicDatabaseHelper.COLUMN_FAV_ID} = ?",
            arrayOf(songId),
            null,
            null,
            null
        )
        cursor.use { it.count > 0 }
    }

    override suspend fun toggleFavorite(song: Song): Boolean = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val exists = isFavorite(song.id)

        if (exists) {
            db.delete(
                MusicDatabaseHelper.TABLE_FAVORITES,
                "${MusicDatabaseHelper.COLUMN_FAV_ID} = ?",
                arrayOf(song.id)
            )
            updatesTrigger.tryEmit(Unit)
            false
        } else {
            val values = ContentValues().apply {
                put(MusicDatabaseHelper.COLUMN_FAV_ID, song.id)
                put(MusicDatabaseHelper.COLUMN_FAV_TITLE, song.title)
                put(MusicDatabaseHelper.COLUMN_FAV_ARTIST, song.artist)
                put(MusicDatabaseHelper.COLUMN_FAV_DURATION, song.durationSeconds)
                put(MusicDatabaseHelper.COLUMN_FAV_DURATION_FORMATTED, song.durationFormatted)
                put(MusicDatabaseHelper.COLUMN_FAV_THUMBNAIL_URL, song.thumbnailUrl)
                put(MusicDatabaseHelper.COLUMN_FAV_WEB_URL, song.webUrl)
                put(MusicDatabaseHelper.COLUMN_FAV_LIKED_AT, System.currentTimeMillis())
            }
            db.insertWithOnConflict(
                MusicDatabaseHelper.TABLE_FAVORITES,
                null,
                values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
            )
            updatesTrigger.tryEmit(Unit)
            true
        }
    }

    override suspend fun removeFavorite(songId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val db = dbHelper.writableDatabase
            db.delete(
                MusicDatabaseHelper.TABLE_FAVORITES,
                "${MusicDatabaseHelper.COLUMN_FAV_ID} = ?",
                arrayOf(songId)
            )
            updatesTrigger.tryEmit(Unit)
            Unit
        }
    }
}

