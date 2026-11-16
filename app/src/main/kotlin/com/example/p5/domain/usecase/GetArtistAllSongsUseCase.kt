package com.example.p5.domain.usecase

import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository

class GetArtistAllSongsUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(playlistId: String): Result<List<Song>> {
        val trimmed = playlistId.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Playlist ID cannot be blank"))
        }
        return musicRepository.getArtistAllSongs(trimmed)
    }
}
