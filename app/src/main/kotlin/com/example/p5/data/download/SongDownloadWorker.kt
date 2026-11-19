package com.example.p5.data.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.core.app.NotificationCompat
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
        const val NOTIFICATION_CHANNEL_ID = "music_download_channel"

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
        val notificationId = songId.hashCode()

        createNotificationChannel()
        showProgressNotification(title, artist, 0, notificationId)

        val app = applicationContext as? MusicApplication ?: run {
            dismissNotification(notificationId)
            return@withContext Result.failure()
        }
        val musicRepo = app.container.musicRepository
        val dbHelper = MusicDatabaseHelper(applicationContext)

        val streamResult = musicRepo.getAudioStream(songId)
        if (streamResult.isFailure) {
            dismissNotification(notificationId)
            return@withContext if (runAttemptCount < 2) Result.retry() else Result.failure()
        }

        val audioStream = streamResult.getOrNull()
        val streamUrl = audioStream?.streamUrl
        if (streamUrl.isNullOrBlank()) {
            dismissNotification(notificationId)
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
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 15_7_3) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/26.0 Safari/605.1.15"
                )
                .header("Origin", "https://music.youtube.com")
                .header("Referer", "https://music.youtube.com/")
                .header("Range", "bytes=0-")
                .build()

            val response = app.container.okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                response.close()
                dismissNotification(notificationId)
                return@withContext if (runAttemptCount < 2) Result.retry() else Result.failure()
            }

            val body = response.body ?: run {
                dismissNotification(notificationId)
                return@withContext Result.failure()
            }

            val headerContentLength = body.contentLength()
            val totalLength = if (headerContentLength > 0) {
                headerContentLength
            } else {
                response.header("Content-Range")?.substringAfterLast('/')?.toLongOrNull() ?: -1L
            }

            var downloadedBytes = 0L
            var lastReportedProgress = -1

            body.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(32768)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        if (isStopped) {
                            tempFile.delete()
                            dismissNotification(notificationId)
                            return@withContext Result.failure()
                        }
                        output.write(buffer, 0, read)
                        downloadedBytes += read

                        if (totalLength > 0) {
                            val progress = ((downloadedBytes * 100) / totalLength).toInt().coerceIn(0, 100)
                            if (progress != lastReportedProgress) {
                                lastReportedProgress = progress
                                setProgress(workDataOf(KEY_PROGRESS to progress))
                                showProgressNotification(title, artist, progress, notificationId)
                            }
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
                showCompleteNotification(title, artist, notificationId)
                Result.success()
            } else {
                tempFile.delete()
                dismissNotification(notificationId)
                Result.failure()
            }
        } catch (e: Exception) {
            tempFile.delete()
            dismissNotification(notificationId)
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Unduhan Musik",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifikasi status dan progres unduhan musik offline"
            }
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun showProgressNotification(title: String, artist: String, progress: Int, notificationId: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(if (progress > 0) "$artist • $progress%" else "$artist • Menyiapkan audio...")
            .setProgress(100, progress.coerceIn(0, 100), progress <= 0)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        manager.notify(notificationId, notification)
    }

    private fun showCompleteNotification(title: String, artist: String, notificationId: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText("$artist • Unduhan Selesai")
            .setProgress(0, 0, false)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }

    private fun dismissNotification(notificationId: Int) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        manager.cancel(notificationId)
    }
}
