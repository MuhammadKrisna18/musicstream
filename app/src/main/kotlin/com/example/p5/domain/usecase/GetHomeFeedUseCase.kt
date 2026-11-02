package com.example.p5.domain.usecase

import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.repository.MusicRepository

class GetHomeFeedUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(refresh: Boolean = false): Result<HomeFeed> {
        return musicRepository.getHomeFeed(refresh)
    }
}
