package com.example.p5.domain.usecase

import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository

class SearchMusicUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 20): Result<List<Song>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return Result.success(emptyList())
        }
        return repository.searchSongs(trimmed, limit)
    }
}
