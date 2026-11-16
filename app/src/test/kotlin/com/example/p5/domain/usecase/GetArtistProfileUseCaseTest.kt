package com.example.p5.domain.usecase

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.repository.MusicRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetArtistProfileUseCaseTest {

    private class TestMusicRepository : MusicRepository {
        var requestedChannelId: String? = null
        var requestedPlaylistId: String? = null
        var shouldFail: Boolean = false

        override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> = Result.success(emptyList())
        override suspend fun searchSongsAndPlaylists(query: String, limit: Int): Result<SearchResult> = Result.success(SearchResult(emptyList(), emptyList()))
        override suspend fun getAudioStream(videoId: String): Result<AudioStream> = Result.failure(UnsupportedOperationException())
        override suspend fun getTrending(limit: Int): Result<List<Song>> = Result.success(emptyList())
        override suspend fun getHomeCategories(): Result<HomeCategories> = Result.success(HomeCategories(emptyList(), emptyList()))
        override suspend fun getHomeFeed(refresh: Boolean): Result<HomeFeed> = Result.success(HomeFeed(emptyList(), emptyList(), emptyList()))
        override suspend fun getGenreFeed(category: Category): Result<List<HomeSection>> = Result.success(emptyList())
        override suspend fun getCuratedPlaylists(): Result<List<Playlist>> = Result.success(emptyList())
        override suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>> = Result.success(emptyList())
        override suspend fun searchPlaylists(query: String): Result<List<Playlist>> = Result.success(emptyList())

        override suspend fun getArtistProfile(channelId: String): Result<ArtistProfile> {
            requestedChannelId = channelId
            return if (shouldFail) {
                Result.failure(IllegalStateException("Network error"))
            } else {
                Result.success(
                    ArtistProfile(
                        id = channelId,
                        name = "Denny Caknan",
                        avatarUrl = "https://example.com/avatar.jpg"
                    )
                )
            }
        }

        override suspend fun getArtistAllSongs(playlistId: String): Result<List<Song>> {
            requestedPlaylistId = playlistId
            return if (shouldFail) {
                Result.failure(IllegalStateException("Playlist not found"))
            } else {
                Result.success(
                    listOf(
                        Song(
                            id = "song1",
                            title = "Kalih Welasku",
                            artist = "Denny Caknan",
                            durationSeconds = 210,
                            durationFormatted = "3:30",
                            thumbnailUrl = "https://example.com/thumb.jpg",
                            webUrl = "https://youtube.com/watch?v=song1",
                            channelId = "UCrXRY"
                        )
                    )
                )
            }
        }
    }

    @Test
    fun invoke_withBlankChannelId_returnsFailureWithoutCallingRepository() = runBlocking {
        val repo = TestMusicRepository()
        val useCase = GetArtistProfileUseCase(repo)

        val result = useCase("   ")

        assertTrue(result.isFailure)
        assertEquals(null, repo.requestedChannelId)
    }

    @Test
    fun invoke_withValidChannelId_callsRepositoryAndReturnsSuccess() = runBlocking {
        val repo = TestMusicRepository()
        val useCase = GetArtistProfileUseCase(repo)

        val result = useCase("UCrXRY_7SVVmc6TykwhRGUNQ")

        assertTrue(result.isSuccess)
        assertEquals("UCrXRY_7SVVmc6TykwhRGUNQ", repo.requestedChannelId)
        val profile = result.getOrNull()
        assertEquals("Denny Caknan", profile?.name)
    }

    @Test
    fun invoke_whenRepositoryFails_returnsFailure() = runBlocking {
        val repo = TestMusicRepository().apply { shouldFail = true }
        val useCase = GetArtistProfileUseCase(repo)

        val result = useCase("UCrXRY_7SVVmc6TykwhRGUNQ")

        assertFalse(result.isSuccess)
    }

    @Test
    fun invoke_allSongs_withBlankPlaylistId_returnsFailure() = runBlocking {
        val repo = TestMusicRepository()
        val useCase = GetArtistAllSongsUseCase(repo)

        val result = useCase("")

        assertTrue(result.isFailure)
        assertEquals(null, repo.requestedPlaylistId)
    }

    @Test
    fun invoke_allSongs_withValidPlaylistId_returnsSongs() = runBlocking {
        val repo = TestMusicRepository()
        val useCase = GetArtistAllSongsUseCase(repo)

        val result = useCase("VLOLAK_test")

        assertTrue(result.isSuccess)
        assertEquals("VLOLAK_test", repo.requestedPlaylistId)
        val songs = result.getOrNull()
        assertEquals(1, songs?.size)
        assertEquals("Kalih Welasku", songs?.first()?.title)
    }
}
