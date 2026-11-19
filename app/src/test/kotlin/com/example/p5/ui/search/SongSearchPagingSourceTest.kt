package com.example.p5.ui.search

import androidx.paging.PagingSource
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
import com.example.p5.domain.usecase.SearchMusicUseCase
import com.example.p5.ui.search.paging.SongSearchPagingSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SongSearchPagingSourceTest {

    private class FakeSearchMusicRepository(
        private val resultProvider: (String, Int) -> Result<List<Song>>
    ) : MusicRepository {
        override suspend fun searchSongs(query: String, limit: Int): Result<List<Song>> = resultProvider(query, limit)
        override suspend fun searchSongsAndPlaylists(query: String, limit: Int): Result<SearchResult> = Result.failure(UnsupportedOperationException())
        override suspend fun getAudioStream(videoId: String): Result<AudioStream> = Result.failure(UnsupportedOperationException())
        override suspend fun getTrending(limit: Int): Result<List<Song>> = Result.failure(UnsupportedOperationException())
        override suspend fun getHomeCategories(): Result<HomeCategories> = Result.failure(UnsupportedOperationException())
        override suspend fun getHomeFeed(refresh: Boolean): Result<HomeFeed> = Result.failure(UnsupportedOperationException())
        override suspend fun getGenreFeed(category: Category): Result<List<HomeSection>> = Result.failure(UnsupportedOperationException())
        override suspend fun getCuratedPlaylists(): Result<List<Playlist>> = Result.failure(UnsupportedOperationException())
        override suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>> = Result.failure(UnsupportedOperationException())
        override suspend fun searchPlaylists(query: String): Result<List<Playlist>> = Result.failure(UnsupportedOperationException())
        override suspend fun getArtistProfile(channelId: String): Result<ArtistProfile> = Result.failure(UnsupportedOperationException())
        override suspend fun getArtistAllSongs(playlistId: String): Result<List<Song>> = Result.failure(UnsupportedOperationException())
    }

    private fun createSong(id: String, title: String): Song {
        return Song(
            id = id,
            title = title,
            artist = "Artist",
            durationSeconds = 180,
            durationFormatted = "3:00",
            thumbnailUrl = "thumb",
            webUrl = "web"
        )
    }

    @Test
    fun load_whenQueryIsBlank_returnsEmptyPage() = runTest {
        val repo = FakeSearchMusicRepository { _, _ -> Result.success(emptyList()) }
        val useCase = SearchMusicUseCase(repo)
        val pagingSource = SongSearchPagingSource(useCase, "   ")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertEquals(null, page.prevKey)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun load_whenSuccessful_returnsPageWithCorrectKeys() = runTest {
        val songs = listOf(createSong("1", "Song 1"), createSong("2", "Song 2"))
        val repo = FakeSearchMusicRepository { _, _ -> Result.success(songs) }
        val useCase = SearchMusicUseCase(repo)
        val pagingSource = SongSearchPagingSource(useCase, "Radiohead")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(2, page.data.size)
        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun load_filtersDuplicatesAcrossConsecutiveLoads() = runTest {
        val songsPage1 = listOf(createSong("1", "Song 1"), createSong("2", "Song 2"))
        val songsPage2 = listOf(createSong("2", "Song 2"), createSong("3", "Song 3"))
        var callCount = 0

        val repo = FakeSearchMusicRepository { _, _ ->
            callCount++
            if (callCount == 1) Result.success(songsPage1) else Result.success(songsPage2)
        }
        val useCase = SearchMusicUseCase(repo)
        val pagingSource = SongSearchPagingSource(useCase, "Coldplay")

        val result1 = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = 1, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        val result2 = pagingSource.load(
            PagingSource.LoadParams.Append(key = 2, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(2, result1.data.size)
        assertEquals(1, result2.data.size)
        assertEquals("3", result2.data[0].id)
    }

    @Test
    fun load_whenRepositoryFails_returnsErrorResult() = runTest {
        val exception = RuntimeException("Network timeout")
        val repo = FakeSearchMusicRepository { _, _ -> Result.failure(exception) }
        val useCase = SearchMusicUseCase(repo)
        val pagingSource = SongSearchPagingSource(useCase, "Muse")

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = result as PagingSource.LoadResult.Error
        assertEquals(exception, error.throwable)
    }
}
