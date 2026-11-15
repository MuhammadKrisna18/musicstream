package com.example.p5.domain.usecase

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.repository.MusicRepository

class GetGenreFeedUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(category: Category): Result<List<HomeSection>> {
        return musicRepository.getGenreFeed(category)
    }
}
