package com.example.p5.domain.usecase

import com.example.p5.domain.repository.DownloadRepository
import java.io.File

class DeleteDownloadedSongUseCase(
    private val downloadRepository: DownloadRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val song = downloadRepository.getDownloadedSongById(id)
        if (song != null) {
            val file = File(song.localFilePath)
            if (file.exists()) {
                file.delete()
            }
        }
        return downloadRepository.deleteDownloadedSong(id)
    }
}
