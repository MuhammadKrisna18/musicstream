package com.example.p5.domain.usecase

import android.net.Uri
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.repository.DownloadRepository
import com.example.p5.domain.repository.MusicRepository
import java.io.File

class GetStreamAudioUseCase(
    private val repository: MusicRepository,
    private val downloadRepository: DownloadRepository? = null
) {
    suspend operator fun invoke(videoId: String): Result<AudioStream> {
        val trimmed = videoId.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Video ID cannot be blank"))
        }

        val localPath = downloadRepository?.getDownloadedFilePath(trimmed)
        if (!localPath.isNullOrBlank()) {
            val file = File(localPath)
            if (file.exists() && file.length() > 0) {
                return Result.success(
                    AudioStream(
                        id = trimmed,
                        title = "",
                        artist = "",
                        streamUrl = Uri.fromFile(file).toString(),
                        durationSeconds = 0,
                        durationFormatted = "",
                        thumbnailUrl = "",
                        format = "m4a",
                        bitrate = 128.0
                    )
                )
            }
        }

        return repository.getAudioStream(trimmed)
    }
}
