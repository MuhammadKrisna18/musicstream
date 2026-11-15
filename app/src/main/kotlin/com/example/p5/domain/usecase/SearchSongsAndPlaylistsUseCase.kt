package com.example.p5.domain.usecase

import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.repository.MusicRepository

class SearchSongsAndPlaylistsUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(query: String, limit: Int = 20): Result<SearchResult> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return Result.success(SearchResult(emptyList(), emptyList()))
        }
        return repository.searchSongsAndPlaylists(trimmed, limit)
    }
}
