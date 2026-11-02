package com.example.p5.domain.usecase

import com.example.p5.domain.model.DownloadedSong
import com.example.p5.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow

class GetDownloadedSongsUseCase(
    private val downloadRepository: DownloadRepository
) {
    operator fun invoke(): Flow<List<DownloadedSong>> {
        return downloadRepository.getAllDownloadedSongs()
    }
}
