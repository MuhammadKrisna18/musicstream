package com.example.p5.domain.repository

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.Song

interface MusicRepository {
    suspend fun searchSongs(query: String, limit: Int = 20): Result<List<Song>>
    suspend fun getAudioStream(videoId: String): Result<AudioStream>
    suspend fun getTrending(limit: Int = 15): Result<List<Song>>
    suspend fun getHomeCategories(): Result<HomeCategories>
    suspend fun getHomeFeed(refresh: Boolean = false): Result<HomeFeed>
}
