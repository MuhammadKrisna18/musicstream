package com.example.p5.domain.usecase

import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.repository.MusicRepository

class GetHomeCategoriesUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): Result<HomeCategories> {
        return musicRepository.getHomeCategories()
    }
}
