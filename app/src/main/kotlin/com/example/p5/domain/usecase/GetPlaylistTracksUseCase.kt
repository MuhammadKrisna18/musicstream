package com.example.p5.domain.usecase

import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository

class GetPlaylistTracksUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(playlist: Playlist): Result<List<Song>> {
        return musicRepository.getPlaylistTracks(playlist)
    }
}
