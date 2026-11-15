package com.example.p5.domain.usecase

import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchSongsAndPlaylistsUseCaseTest {

    private class FakeMusicRepository : MusicRepository {
        var lastQuery: String? = null
        var lastLimit: Int? = null

        override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> =
            Result.success(emptyList())

        override suspend fun searchSongsAndPlaylists(query: String, limit: Int): Result<SearchResult> {
            lastQuery = query
            lastLimit = limit
            return Result.success(
                SearchResult(
                    songs = listOf(
                        Song(
                            id = "s1",
                            title = "Test Song",
                            artist = "Test Artist",
                            durationSeconds = 120,
                            durationFormatted = "02:00",
                            thumbnailUrl = "https://example.com/thumb.jpg",
                            webUrl = "https://youtube.com/watch?v=s1"
                        )
                    ),
                    playlists = listOf(
                        Playlist(
                            id = "p1",
                            title = "Test Playlist",
                            subtitle = "Test Subtitle",
                            thumbnailUrl = "https://example.com/playlist.jpg",
                            type = PlaylistType.GENRE,
                            query = "test"
                        )
                    )
                )
            )
        }

        override suspend fun getAudioStream(videoId: String): Result<AudioStream> =
            Result.failure(UnsupportedOperationException())

        override suspend fun getTrending(limit: Int): Result<List<Song>> =
            Result.success(emptyList())

        override suspend fun getHomeCategories(): Result<HomeCategories> =
            Result.success(HomeCategories(emptyList(), emptyList()))

        override suspend fun getHomeFeed(refresh: Boolean): Result<HomeFeed> =
            Result.success(HomeFeed(emptyList(), emptyList(), emptyList()))

        override suspend fun getGenreFeed(category: Category): Result<List<HomeSection>> =
            Result.success(emptyList())

        override suspend fun getCuratedPlaylists(): Result<List<Playlist>> =
            Result.success(emptyList())

        override suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>> =
            Result.success(emptyList())

        override suspend fun searchPlaylists(query: String): Result<List<Playlist>> =
            Result.success(emptyList())
    }

    @Test
    fun invoke_withEmptyQuery_returnsEmptySearchResultWithoutCallingRepository() = runBlocking {
        val repo = FakeMusicRepository()
        val useCase = SearchSongsAndPlaylistsUseCase(repo)

        val result = useCase("   ")

        assertTrue(result.isSuccess)
        val payload = result.getOrNull()
        assertEquals(0, payload?.songs?.size)
        assertEquals(0, payload?.playlists?.size)
        assertEquals(null, repo.lastQuery)
    }

    @Test
    fun invoke_withNonEmptyQuery_trimsAndCallsRepository() = runBlocking {
        val repo = FakeMusicRepository()
        val useCase = SearchSongsAndPlaylistsUseCase(repo)

        val result = useCase("  Tulus  ", 10)

        assertTrue(result.isSuccess)
        assertEquals("Tulus", repo.lastQuery)
        assertEquals(10, repo.lastLimit)
        val payload = result.getOrNull()
        assertEquals(1, payload?.songs?.size)
        assertEquals("s1", payload?.songs?.first()?.id)
        assertEquals(1, payload?.playlists?.size)
        assertEquals("p1", payload?.playlists?.first()?.id)
    }
}
