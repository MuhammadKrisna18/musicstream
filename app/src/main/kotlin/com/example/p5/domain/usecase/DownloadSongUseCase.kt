package com.example.p5.domain.usecase

import com.example.p5.domain.downloader.MusicDownloader
import com.example.p5.domain.model.DownloadedSong
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.DownloadRepository

class DownloadSongUseCase(
    private val musicDownloader: MusicDownloader,
    private val downloadRepository: DownloadRepository
) {
    suspend operator fun invoke(song: Song): Result<DownloadedSong> {
        val downloadResult = musicDownloader.download(song)
        return downloadResult.mapCatching { downloadedSong ->
            downloadRepository.saveDownloadedSong(downloadedSong)
            downloadedSong
        }
    }
}
