package com.example.p5.domain.usecase

import com.example.p5.domain.model.Playlist
import com.example.p5.domain.repository.MusicRepository

class GetCuratedPlaylistsUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): Result<List<Playlist>> {
        return musicRepository.getCuratedPlaylists()
    }
}
