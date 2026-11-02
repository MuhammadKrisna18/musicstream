package com.example.p5.data.repository

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepositoryImpl(
    private val youTubeDataSource: YouTubeNativeDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicRepository {

    override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.search(query, limit)
        }
    }

    override suspend fun getAudioStream(videoId: String): Result<AudioStream> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.getAudioStream(videoId)
        }
    }

    override suspend fun getTrending(limit: Int): Result<List<Song>> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.getTrending(limit)
        }
    }

    override suspend fun getHomeCategories(): Result<HomeCategories> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.getCategories()
        }
    }

    override suspend fun getHomeFeed(refresh: Boolean): Result<HomeFeed> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.getHomeFeed(refresh)
        }
    }
}
