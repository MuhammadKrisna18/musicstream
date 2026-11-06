package com.example.p5.data.repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(
    private val dbHelper: MusicDatabaseHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SearchHistoryRepository {

    private val updatesTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getSearchHistory(limit: Int): Flow<List<String>> {
        return updatesTrigger
            .onStart { emit(Unit) }
            .map { fetchSearchHistoryInternal(limit) }
            .distinctUntilChanged()
    }

    private fun fetchSearchHistoryInternal(limit: Int): List<String> {
        val list = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            MusicDatabaseHelper.TABLE_SEARCH_HISTORY,
            arrayOf(MusicDatabaseHelper.COLUMN_SH_QUERY),
            null,
            null,
            null,
            null,
            "${MusicDatabaseHelper.COLUMN_SH_SEARCHED_AT} DESC",
            limit.toString()
        )

        cursor.use { c ->
            val queryIdx = c.getColumnIndexOrThrow(MusicDatabaseHelper.COLUMN_SH_QUERY)
            while (c.moveToNext()) {
                val q = c.getString(queryIdx)
                if (!q.isNullOrBlank()) {
                    list.add(q)
                }
            }
        }
        return list
    }

    override suspend fun addSearchQuery(query: String) = withContext(ioDispatcher) {
        val trimmed = query.trim()
        if (trimmed.length < 2) return@withContext

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MusicDatabaseHelper.COLUMN_SH_QUERY, trimmed)
            put(MusicDatabaseHelper.COLUMN_SH_SEARCHED_AT, System.currentTimeMillis())
        }

        db.insertWithOnConflict(
            MusicDatabaseHelper.TABLE_SEARCH_HISTORY,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        // Keep maximum 20 recent queries
        db.execSQL(
            """
            DELETE FROM ${MusicDatabaseHelper.TABLE_SEARCH_HISTORY}
            WHERE ${MusicDatabaseHelper.COLUMN_SH_QUERY} NOT IN (
                SELECT ${MusicDatabaseHelper.COLUMN_SH_QUERY}
                FROM ${MusicDatabaseHelper.TABLE_SEARCH_HISTORY}
                ORDER BY ${MusicDatabaseHelper.COLUMN_SH_SEARCHED_AT} DESC
                LIMIT 20
            )
            """.trimIndent()
        )

        updatesTrigger.tryEmit(Unit)
        Unit
    }

    override suspend fun deleteSearchQuery(query: String) = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(
            MusicDatabaseHelper.TABLE_SEARCH_HISTORY,
            "${MusicDatabaseHelper.COLUMN_SH_QUERY} = ?",
            arrayOf(query)
        )
        updatesTrigger.tryEmit(Unit)
        Unit
    }

    override suspend fun clearSearchHistory() = withContext(ioDispatcher) {
        val db = dbHelper.writableDatabase
        db.delete(MusicDatabaseHelper.TABLE_SEARCH_HISTORY, null, null)
        updatesTrigger.tryEmit(Unit)
        Unit
    }
}

