package com.example.p5.domain.usecase

import com.example.p5.domain.model.Playlist
import com.example.p5.domain.repository.MusicRepository

class SearchPlaylistsUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(query: String): Result<List<Playlist>> {
        return musicRepository.searchPlaylists(query)
    }
}
