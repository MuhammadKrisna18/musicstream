package com.example.p5.playback

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.LyricsData
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.RepeatMode
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song
import com.example.p5.domain.player.AudioPlayer
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.LyricsRepository
import com.example.p5.domain.repository.MusicRepository
import com.example.p5.domain.usecase.GetStreamAudioUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QueueReorderTest {

    private lateinit var fakeAudioPlayer: FakeAudioPlayer
    private lateinit var manager: MusicPlayerManager

    private val song1 = Song("1", "Song 1", "Artist 1", 180, "3:00", "", "")
    private val song2 = Song("2", "Song 2", "Artist 2", 200, "3:20", "", "")
    private val song3 = Song("3", "Song 3", "Artist 3", 220, "3:40", "", "")
    private val song4 = Song("4", "Song 4", "Artist 4", 240, "4:00", "", "")

    @Before
    fun setUp() {
        fakeAudioPlayer = FakeAudioPlayer()
        val dummyRepo = object : MusicRepository {
            override suspend fun searchSongs(query: String, limit: Int) = Result.success(emptyList<Song>())
            override suspend fun searchSongsAndPlaylists(query: String, limit: Int) = Result.success(SearchResult(emptyList(), emptyList()))
            override suspend fun getAudioStream(videoId: String) = Result.failure<AudioStream>(Exception())
            override suspend fun getTrending(limit: Int) = Result.success(emptyList<Song>())
            override suspend fun getHomeCategories() = Result.success(HomeCategories(emptyList(), emptyList()))
            override suspend fun getHomeFeed(refresh: Boolean) = Result.success(HomeFeed(emptyList(), emptyList(), emptyList()))
            override suspend fun getGenreFeed(category: Category) = Result.success(emptyList<HomeSection>())
            override suspend fun getCuratedPlaylists() = Result.success(emptyList<Playlist>())
            override suspend fun getPlaylistTracks(playlist: Playlist) = Result.success(emptyList<Song>())
            override suspend fun searchPlaylists(query: String) = Result.success(emptyList<Playlist>())
            override suspend fun getArtistProfile(channelId: String) = Result.success(ArtistProfile(id = channelId, name = "Dummy Artist", avatarUrl = ""))
            override suspend fun getArtistAllSongs(playlistId: String) = Result.success(emptyList<Song>())
        }
        val dummyGetStream = GetStreamAudioUseCase(dummyRepo)
        val dummyLyrics = object : LyricsRepository {
            override suspend fun getLyrics(song: Song) = Result.failure<LyricsData>(Exception())
        }
        val dummyFav = object : FavoriteRepository {
            override fun getAllFavoriteSongs(): Flow<List<Song>> = emptyFlow()
            override suspend fun isFavorite(songId: String) = false
            override suspend fun toggleFavorite(song: Song) = false
            override suspend fun removeFavorite(songId: String): Result<Unit> = Result.success(Unit)
        }
        val dummyHistory = object : HistoryRepository {
            override fun getRecentlyPlayed(limit: Int): Flow<List<Song>> = emptyFlow()
            override fun getTopPlayedSongs(limit: Int): Flow<List<Song>> = emptyFlow()
            override fun getTopArtists(limit: Int): Flow<List<String>> = emptyFlow()
            override suspend fun recordPlay(song: Song) {}
            override suspend fun clearHistory(): Result<Unit> = Result.success(Unit)
        }

        manager = MusicPlayerManager(
            audioPlayer = fakeAudioPlayer,
            getStreamAudioUseCase = dummyGetStream,
            lyricsRepository = dummyLyrics,
            favoriteRepository = dummyFav,
            historyRepository = dummyHistory,
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        )
    }

    @Test
    fun moveQueueItem_movingItemDown_shiftsPlayingIndexCorrectly() {
        manager.playQueue(listOf(song1, song2, song3, song4), startIndex = 2)

        assertEquals(2, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("3", fakeAudioPlayer.lastQueue[2].id)

        manager.moveQueueItem(fromIndex = 0, toIndex = 3)

        assertEquals(listOf("2", "3", "4", "1"), fakeAudioPlayer.lastQueue.map { it.id })
        assertEquals(1, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("3", fakeAudioPlayer.lastQueue[1].id)
    }

    @Test
    fun moveQueueItem_movingItemUp_shiftsPlayingIndexCorrectly() {
        manager.playQueue(listOf(song1, song2, song3, song4), startIndex = 1)

        assertEquals(1, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("2", fakeAudioPlayer.lastQueue[1].id)

        manager.moveQueueItem(fromIndex = 3, toIndex = 0)

        assertEquals(listOf("4", "1", "2", "3"), fakeAudioPlayer.lastQueue.map { it.id })
        assertEquals(2, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("2", fakeAudioPlayer.lastQueue[2].id)
    }

    @Test
    fun moveQueueItem_movingCurrentPlayingItem_updatesPlayingIndexToTarget() {
        manager.playQueue(listOf(song1, song2, song3, song4), startIndex = 1)

        manager.moveQueueItem(fromIndex = 1, toIndex = 3)

        assertEquals(listOf("1", "3", "4", "2"), fakeAudioPlayer.lastQueue.map { it.id })
        assertEquals(3, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("2", fakeAudioPlayer.lastQueue[3].id)
    }

    @Test
    fun moveQueueItem_invalidIndices_areIgnored() {
        manager.playQueue(listOf(song1, song2), startIndex = 0)

        manager.moveQueueItem(fromIndex = -1, toIndex = 1)
        manager.moveQueueItem(fromIndex = 0, toIndex = 5)
        manager.moveQueueItem(fromIndex = 1, toIndex = 1)

        assertEquals(listOf("1", "2"), fakeAudioPlayer.lastQueue.map { it.id })
        assertEquals(0, fakeAudioPlayer.lastCurrentIndex)
    }

    @Test
    fun removeQueueItem_removesItemAndMaintainsPlayingIndex() {
        manager.playQueue(listOf(song1, song2, song3), startIndex = 2)

        val removed = manager.removeQueueItem(0)

        assertTrue(removed)
        assertEquals(listOf("2", "3"), fakeAudioPlayer.lastQueue.map { it.id })
        assertEquals(1, fakeAudioPlayer.lastCurrentIndex)
        assertEquals("3", fakeAudioPlayer.lastQueue[1].id)
    }

    @Test
    fun removeQueueItem_doesNotRemoveLastRemainingItem() {
        manager.playQueue(listOf(song1), startIndex = 0)

        val removed = manager.removeQueueItem(0)

        assertFalse(removed)
        assertEquals(1, fakeAudioPlayer.lastQueue.size)
    }

    private class FakeAudioPlayer : AudioPlayer {
        var lastQueue: List<Song> = emptyList()
        var lastCurrentIndex: Int = -1

        private val _state = MutableStateFlow(PlayerState())
        override val playerState: StateFlow<PlayerState> = _state

        override fun setOnPlaybackEndedListener(listener: () -> Unit) {}
        override fun prepareSong(song: Song) {}
        override fun play(song: Song, stream: AudioStream) {}
        override fun updateQueueState(
            queue: List<Song>,
            currentIndex: Int,
            isShuffleEnabled: Boolean,
            repeatMode: RepeatMode
        ) {
            lastQueue = queue
            lastCurrentIndex = currentIndex
        }

        override fun updateSleepTimerState(secondsRemaining: Long?, isEndOfTrack: Boolean) {}
        override fun updateLyricsState(lyrics: LyricsData?, isLoading: Boolean) {}
        override fun updateFavoriteState(isFavorite: Boolean) {}
        override fun setError(message: String) {}
        override fun togglePlayPause() {}
        override fun pause() {}
        override fun resume() {}
        override fun seekTo(positionMs: Long) {}
        override fun stop() {}
        override fun release() {}
    }
}
