package com.example.p5.data.download

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.p5.MusicApplication
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.data.repository.DownloadRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class SongDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG_DOWNLOAD = "song_download"
        const val KEY_ID = "song_id"
        const val KEY_TITLE = "song_title"
        const val KEY_ARTIST = "song_artist"
        const val KEY_DURATION = "song_duration"
        const val KEY_DURATION_FORMATTED = "song_duration_formatted"
        const val KEY_THUMBNAIL_URL = "song_thumbnail_url"
        const val KEY_WEB_URL = "song_web_url"
        const val KEY_PROGRESS = "progress"

        fun tagForSong(songId: String): String = "song_$songId"
        fun workName(songId: String): String = "download_$songId"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val songId = inputData.getString(KEY_ID) ?: return@withContext Result.failure()
        val title = inputData.getString(KEY_TITLE) ?: ""
        val artist = inputData.getString(KEY_ARTIST) ?: ""
        val duration = inputData.getInt(KEY_DURATION, 0)
        val durationFormatted = inputData.getString(KEY_DURATION_FORMATTED) ?: ""
        val thumbnailUrl = inputData.getString(KEY_THUMBNAIL_URL) ?: ""

        val app = applicationContext as? MusicApplication ?: return@withContext Result.failure()
        val musicRepo = app.container.musicRepository
        val dbHelper = MusicDatabaseHelper(applicationContext)

        val streamResult = musicRepo.getAudioStream(songId)
        if (streamResult.isFailure) {
            return@withContext if (runAttemptCount < 2) Result.retry() else Result.failure()
        }

        val audioStream = streamResult.getOrNull()
        val streamUrl = audioStream?.streamUrl
        if (streamUrl.isNullOrBlank()) {
            return@withContext Result.failure()
        }

        val downloadDir = File(applicationContext.filesDir, "offline_audio")
        if (!downloadDir.exists()) {
            downloadDir.mkdirs()
        }

        val tempFile = File(downloadDir, "$songId.tmp")
        val targetFile = File(downloadDir, "$songId.m4a")

        try {
            val request = Request.Builder()
                .url(streamUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36"
                )
                .build()

            val response = app.container.okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                response.close()
                return@withContext if (runAttemptCount < 2) Result.retry() else Result.failure()
            }

            val body = response.body ?: return@withContext Result.failure()
            val contentLength = body.contentLength()
            var downloadedBytes = 0L

            body.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        if (isStopped) {
                            tempFile.delete()
                            return@withContext Result.failure()
                        }
                        output.write(buffer, 0, read)
                        downloadedBytes += read
                        if (contentLength > 0) {
                            val progress = ((downloadedBytes * 100) / contentLength).toInt().coerceIn(0, 100)
                            setProgress(workDataOf(KEY_PROGRESS to progress))
                        }
                    }
                    output.flush()
                }
            }

            if (tempFile.exists() && tempFile.length() > 0) {
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                tempFile.renameTo(targetFile)

                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put(MusicDatabaseHelper.COLUMN_ID, songId)
                    put(MusicDatabaseHelper.COLUMN_TITLE, title)
                    put(MusicDatabaseHelper.COLUMN_ARTIST, artist)
                    put(MusicDatabaseHelper.COLUMN_DURATION, duration)
                    put(MusicDatabaseHelper.COLUMN_DURATION_FORMATTED, durationFormatted)
                    put(MusicDatabaseHelper.COLUMN_THUMBNAIL_URL, thumbnailUrl)
                    put(MusicDatabaseHelper.COLUMN_LOCAL_FILE_PATH, targetFile.absolutePath)
                    put(MusicDatabaseHelper.COLUMN_FILE_SIZE_BYTES, targetFile.length())
                    put(MusicDatabaseHelper.COLUMN_DOWNLOADED_AT, System.currentTimeMillis())
                }
                db.insertWithOnConflict(
                    MusicDatabaseHelper.TABLE_NAME,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )

                (app.container.downloadRepository as? DownloadRepositoryImpl)?.notifyDownloadChanged()
                Result.success()
            } else {
                tempFile.delete()
                Result.failure()
            }
        } catch (e: Exception) {
            tempFile.delete()
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }
}
