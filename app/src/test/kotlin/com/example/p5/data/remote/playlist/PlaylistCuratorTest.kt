package com.example.p5.data.remote.playlist

import com.example.p5.data.remote.YouTubeNativeDataSource
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
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
    fun searchPlaylists_emptyQuery_returnsEmptyList() = runTest {
        val results = curator.searchPlaylists("   ")
        assertTrue(results.isEmpty())
    }
}
