package com.example.p5.domain.usecase

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.repository.MusicRepository

class GetArtistProfileUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(channelId: String): Result<ArtistProfile> {
        val trimmed = channelId.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Channel ID cannot be blank"))
        }
        return musicRepository.getArtistProfile(trimmed)
    }
}
