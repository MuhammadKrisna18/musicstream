package com.example.p5.data.remote.playlist

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.domain.model.PlaylistType
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PlaylistCuratorTest {

    private lateinit var curator: PlaylistCurator

    @Before
    fun setUp() {
        val dummyClient = OkHttpClient()
        val dataSource = YouTubeNativeDataSource(dummyClient)
        curator = PlaylistCurator(dataSource)
    }

    @Test
    fun getCuratedPlaylists_containsAllThreeCategories() {
        val playlists = curator.getCuratedPlaylists()
        assertFalse(playlists.isEmpty())

        val hasGenre = playlists.any { it.type == PlaylistType.GENRE }
        val hasArtist = playlists.any { it.type == PlaylistType.ARTIST }
        val hasRandom = playlists.any { it.type == PlaylistType.RANDOM }

        assertTrue(hasGenre)
        assertTrue(hasArtist)
        assertTrue(hasRandom)
    }

    @Test
    fun searchPlaylists_matchingExistingPlaylist_returnsCuratedMatch() {
        val results = curator.searchPlaylists("tulus")
        assertFalse(results.isEmpty())
        assertTrue(results.any { it.title.contains("Tulus", ignoreCase = true) })
    }

    @Test
    fun searchPlaylists_unknownArtist_generatesDynamicArtistPlaylist() {
        val results = curator.searchPlaylists("Feast")
        assertEquals(1, results.size)
        val playlist = results.first()
        assertEquals(PlaylistType.ARTIST, playlist.type)
        assertEquals("This is Feast", playlist.title)
        assertTrue(playlist.query.contains("Feast", ignoreCase = true))
    }

    @Test
    fun searchPlaylists_emptyQuery_returnsEmptyList() {
        val results = curator.searchPlaylists("   ")
        assertTrue(results.isEmpty())
    }
}
