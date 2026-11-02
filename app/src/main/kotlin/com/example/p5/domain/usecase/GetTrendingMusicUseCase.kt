package com.example.p5.domain.usecase

import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository

class GetTrendingMusicUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(limit: Int = 15): Result<List<Song>> {
        return musicRepository.getTrending(limit)
    }
}
