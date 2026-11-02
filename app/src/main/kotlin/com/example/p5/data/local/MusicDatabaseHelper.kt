package com.example.p5.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MusicDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "music_offline.db"
        const val DATABASE_VERSION = 2

        const val TABLE_NAME = "downloaded_songs"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_DURATION_FORMATTED = "duration_formatted"
        const val COLUMN_THUMBNAIL_URL = "thumbnail_url"
        const val COLUMN_LOCAL_FILE_PATH = "local_file_path"
        const val COLUMN_FILE_SIZE_BYTES = "file_size_bytes"
        const val COLUMN_DOWNLOADED_AT = "downloaded_at"

        const val TABLE_FAVORITES = "favorite_songs"
        const val COLUMN_FAV_ID = "id"
        const val COLUMN_FAV_TITLE = "title"
        const val COLUMN_FAV_ARTIST = "artist"
        const val COLUMN_FAV_DURATION = "duration"
        const val COLUMN_FAV_DURATION_FORMATTED = "duration_formatted"
        const val COLUMN_FAV_THUMBNAIL_URL = "thumbnail_url"
        const val COLUMN_FAV_WEB_URL = "web_url"
        const val COLUMN_FAV_LIKED_AT = "liked_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_ARTIST TEXT NOT NULL,
                $COLUMN_DURATION INTEGER NOT NULL,
                $COLUMN_DURATION_FORMATTED TEXT NOT NULL,
                $COLUMN_THUMBNAIL_URL TEXT NOT NULL,
                $COLUMN_LOCAL_FILE_PATH TEXT NOT NULL,
                $COLUMN_FILE_SIZE_BYTES INTEGER NOT NULL,
                $COLUMN_DOWNLOADED_AT INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createTableQuery)

        val createFavoritesQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_FAVORITES (
                $COLUMN_FAV_ID TEXT PRIMARY KEY,
                $COLUMN_FAV_TITLE TEXT NOT NULL,
                $COLUMN_FAV_ARTIST TEXT NOT NULL,
                $COLUMN_FAV_DURATION INTEGER NOT NULL,
                $COLUMN_FAV_DURATION_FORMATTED TEXT NOT NULL,
                $COLUMN_FAV_THUMBNAIL_URL TEXT NOT NULL,
                $COLUMN_FAV_WEB_URL TEXT NOT NULL,
                $COLUMN_FAV_LIKED_AT INTEGER NOT NULL
            );
        """.trimIndent()
        db.execSQL(createFavoritesQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            val createFavoritesQuery = """
                CREATE TABLE IF NOT EXISTS $TABLE_FAVORITES (
                    $COLUMN_FAV_ID TEXT PRIMARY KEY,
                    $COLUMN_FAV_TITLE TEXT NOT NULL,
                    $COLUMN_FAV_ARTIST TEXT NOT NULL,
                    $COLUMN_FAV_DURATION INTEGER NOT NULL,
                    $COLUMN_FAV_DURATION_FORMATTED TEXT NOT NULL,
                    $COLUMN_FAV_THUMBNAIL_URL TEXT NOT NULL,
                    $COLUMN_FAV_WEB_URL TEXT NOT NULL,
                    $COLUMN_FAV_LIKED_AT INTEGER NOT NULL
                );
            """.trimIndent()
            db.execSQL(createFavoritesQuery)
        }
    }
}
