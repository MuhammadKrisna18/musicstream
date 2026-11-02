package com.example.p5.data.downloader

import android.content.Context
import com.example.p5.domain.downloader.MusicDownloader
import com.example.p5.domain.model.DownloadedSong
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class MusicDownloaderImpl(
    private val context: Context,
    private val musicRepository: MusicRepository,
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicDownloader {

    override suspend fun download(song: Song): Result<DownloadedSong> = withContext(ioDispatcher) {
        runCatching {
            val streamResponse = musicRepository.getAudioStream(song.id).getOrThrow()
            val downloadUrl = streamResponse.streamUrl

            val downloadDir = File(context.filesDir, "downloads")
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }

            val fileExtension = streamResponse.format ?: "m4a"
            val targetFile = File(downloadDir, "${song.id}.$fileExtension")

            val request = Request.Builder()
                .url(downloadUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Download HTTP failed with code: ${response.code}")
            }

            val body = response.body ?: throw IllegalStateException("Empty response body from stream")

            body.byteStream().use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            DownloadedSong(
                id = song.id,
                title = song.title,
                artist = song.artist,
                durationSeconds = song.durationSeconds,
                durationFormatted = song.durationFormatted,
                thumbnailUrl = song.thumbnailUrl,
                localFilePath = targetFile.absolutePath,
                fileSizeBytes = targetFile.length(),
                downloadedAt = System.currentTimeMillis()
            )
        }
    }
}
