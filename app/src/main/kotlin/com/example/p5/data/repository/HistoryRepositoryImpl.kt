package com.example.p5.data.repository

import android.content.ContentValues
import android.database.Cursor
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.HistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HistoryRepository {

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getRecentlyPlayed(limit: Int): Flow<List<Song>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchRecentlyPlayedInternal(limit) }
            .distinctUntilChanged()
    }

    override fun getTopPlayedSongs(limit: Int): Flow<List<Song>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchTopPlayedInternal(limit) }
            .distinctUntilChanged()
    }

    override fun getTopArtists(limit: Int): Flow<List<String>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchTopArtistsInternal(limit) }
            .distinctUntilChanged()
    }

    private fun fetchTopPlayedInternal(limit: Int): List<Song> {
        val list = mutableListOf<Song>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_HISTORY,
            null,
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_HIST_PLAY_COUNT} DESC, ${MusicDatabaseHelper.COLUMN_HIST_PLAYED_AT} DESC",
            limit.toString()
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_ID)
            val titleIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_DURATION)
            val durationFormattedIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_DURATION_FORMATTED)
            val thumbnailIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_THUMBNAIL_URL)

            while (c.moveToNext()) {
                list.add(
                    Song(
                        id = c.getString(idIndex),
                        title = c.getString(titleIndex),
                        artist = c.getString(artistIndex),
                        durationSeconds = c.getInt(durationIndex),
                        durationFormatted = c.getString(durationFormattedIndex),
                        thumbnailUrl = c.getString(thumbnailIndex),
                        webUrl = "https://www.youtube.com/watch?v=${c.getString(idIndex)}"
                    )
                )
            }
        }
        return list
    }

    private fun fetchRecentlyPlayedInternal(limit: Int): List<Song> {
        val list = mutableListOf<Song>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_HISTORY,
            null,
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_HIST_PLAYED_AT} DESC",
            limit.toString()
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_ID)
            val titleIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_ARTIST)
            val durationIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_DURATION)
            val durationFormattedIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_DURATION_FORMATTED)
            val thumbnailIndex = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_THUMBNAIL_URL)

            while (c.moveToNext()) {
                list.add(
                    Song(
                        id = c.getString(idIndex),
                        title = c.getString(titleIndex),
                        artist = c.getString(artistIndex),
                        durationSeconds = c.getInt(durationIndex),
                        durationFormatted = c.getString(durationFormattedIndex),
                        thumbnailUrl = c.getString(thumbnailIndex),
                        webUrl = "https://www.youtube.com/watch?v=${c.getString(idIndex)}"
                    )
                )
            }
        }
        return list
    }

    private fun fetchTopArtistsInternal(limit: Int): List<String> {
        val artists = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT ${MusicDatabaseHelper.COLUMN_HIST_ARTIST}, SUM(${MusicDatabaseHelper.COLUMN_HIST_PLAY_COUNT}) as total_plays
            FROM ${MusicDatabaseHelper.TABLE_HISTORY}
            GROUP BY ${MusicDatabaseHelper.COLUMN_HIST_ARTIST}
            ORDER BY total_plays DESC
            LIMIT ?
            """.trimIndent(),
            arrayOf(limit.toString())
        )

        cursor.use { c ->
            val artistIdx = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_HIST_ARTIST)
            while (c.moveToNext()) {
                val artist = c.getString(artistIdx)
                if (!artist.isNullOrBlank()) {
                    artists.add(artist)
                }
            }
        }
        return artists
    }

    override suspend fun recordPlay(song: Song) = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            MusicDatabaseHelper.TABLE_HISTORY,
            arrayOf(MusicDatabaseHelper.COLUMN_HIST_PLAY_COUNT),
            "${MusicDatabaseHelper.COLUMN_HIST_ID} = ?",
            arrayOf(song.id),
            null,
            null,
            null
        )

        var currentPlayCount = 0
        val exists = cursor.use {
            if (it.moveToFirst()) {
                currentPlayCount = it.getInt(0)
                true
            } else {
                false
            }
        }

        val values = ContentValues().apply {
            put(MusicDatabaseHelper.COLUMN_HIST_ID, song.id)
            put(MusicDatabaseHelper.COLUMN_HIST_TITLE, song.title)
            put(MusicDatabaseHelper.COLUMN_HIST_ARTIST, song.artist)
            put(MusicDatabaseHelper.COLUMN_HIST_DURATION, song.durationSeconds)
            put(MusicDatabaseHelper.COLUMN_HIST_DURATION_FORMATTED, song.durationFormatted)
            put(MusicDatabaseHelper.COLUMN_HIST_THUMBNAIL_URL, song.thumbnailUrl)
            put(MusicDatabaseHelper.COLUMN_HIST_PLAYED_AT, System.currentTimeMillis())
            put(MusicDatabaseHelper.COLUMN_HIST_PLAY_COUNT, currentPlayCount + 1)
        }

        if (exists) {
            db.update(
                MusicDatabaseHelper.TABLE_HISTORY,
                values,
                "${MusicDatabaseHelper.COLUMN_HIST_ID} = ?",
                arrayOf(song.id)
            )
        } else {
            db.insert(MusicDatabaseHelper.TABLE_HISTORY, null, values)
        }

        updatesTrigger.tryEmit(Unit)
        Unit
    }

    override suspend fun clearHistory(): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val db = dbHelper.writableDatabase
            db.delete(MusicDatabaseHelper.TABLE_HISTORY, null, null)
            updatesTrigger.tryEmit(Unit)
            Unit
        }
    }
}

