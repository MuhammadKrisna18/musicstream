package com.example.p5.domain.repository

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeCategories
import com.example.p5.domain.model.HomeFeed
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchResult
import com.example.p5.domain.model.Song

interface MusicRepository {
    suspend fun searchSongs(query: String, limit: Int = 20): Result<List<Song>>
    suspend fun searchSongsAndPlaylists(query: String, limit: Int = 20): Result<SearchResult>
    suspend fun getAudioStream(videoId: String): Result<AudioStream>
    suspend fun getTrending(limit: Int = 15): Result<List<Song>>
    suspend fun getHomeCategories(): Result<HomeCategories>
    suspend fun getHomeFeed(refresh: Boolean = false): Result<HomeFeed>
    suspend fun getGenreFeed(category: Category): Result<List<HomeSection>>
    suspend fun getCuratedPlaylists(): Result<List<Playlist>>
    suspend fun getPlaylistTracks(playlist: Playlist): Result<List<Song>>
    suspend fun searchPlaylists(query: String): Result<List<Playlist>>
    suspend fun getArtistProfile(channelId: String): Result<ArtistProfile>
    suspend fun getArtistAllSongs(playlistId: String): Result<List<Song>>
}
