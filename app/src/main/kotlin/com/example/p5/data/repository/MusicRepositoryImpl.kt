package com.example.p5.data.repository

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.playlist.PlaylistCurator
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicRepositoryImpl(
    private val youTubeDataSource: YouTubeNativeDataSource,
    private val playlistCurator: PlaylistCurator = PlaylistCurator(youTubeDataSource),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MusicRepository {

    override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.search(query, limit)
        }
    }

    override suspend fun searchSongsAndPlaylists(query: String, limit: Int): Result<SearchResult> = withContext(ioDispatcher) {
        runCatching {
            val (songs, playlists) = youTubeDataSource.searchWithPlaylists(query, limit)
            SearchResult(songs, playlists)
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

    override suspend fun getGenreFeed(category: Category): Result<List<HomeSection>> = withContext(ioDispatcher) {
        runCatching {
            youTubeDataSource.getGenreFeed(category)
        }
    }

    override suspend fun getCuratedPlaylists(): Result<List<Playlist>> = withContext(ioDispatcher) {
        runCatching {
            playlistCurator.getCuratedPlaylists()
        }
    }

    override suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>> = withContext(ioDispatcher) {
        runCatching {
            playlistCurator.getPlaylistTracks(playlist)
        }
    }

    override suspend fun searchPlaylists(query: String): Result<List<Playlist>> = withContext(ioDispatcher) {
        runCatching {
            playlistCurator.searchPlaylists(query)
        }
    }
}
