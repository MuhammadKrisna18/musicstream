package com.example.p5.domain.usecase

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.repository.MusicRepository

class GetStreamAudioUseCase(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(videoId: String): Result<AudioStream> {
        val trimmed = videoId.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Video ID cannot be blank"))
        }
        return repository.getAudioStream(trimmed)
    }
}
