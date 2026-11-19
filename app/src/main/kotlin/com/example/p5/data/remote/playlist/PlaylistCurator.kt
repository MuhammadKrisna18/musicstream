package com.example.p5.data.remote.playlist

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.innertube.MusicFilterHelper
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import java.util.concurrent.ConcurrentHashMap

class PlaylistCurator(
    private val youTubeDataSource: YouTubeNativeDataSource
) {
    private val playlistTrackCache = ConcurrentHashMap<String, List<Song>>()

    suspend fun getCuratedPlaylists(): List<Playlist> {
        val feedPlaylists = youTubeDataSource.getHomeFeed(refresh = false).playlists
            .filter { !MusicFilterHelper.isExcludedPlaylist(it.title) }
        if (feedPlaylists.isNotEmpty()) {
            return feedPlaylists
        }
        return searchPlaylists("Top Hits Indonesia", limit = 6)
    }

    suspend fun getPlaylistTracks(playlist: Playlist): List<Song> {
        val cached = playlistTrackCache[playlist.id]
        if (!cached.isNullOrEmpty()) {
            return cached
        }

        val directTracks = youTubeDataSource.getPlaylistTracks(playlist.id)
        val sourceTracks = if (directTracks.isNotEmpty()) {
            directTracks
        } else {
            val query = playlist.query.ifBlank { "Lagu ${playlist.title} official audio" }
            youTubeDataSource.search(query, limit = 25)
        }

        val cleanSongs = sourceTracks.filter { song ->
            MusicFilterHelper.isValidMusicTrack(song)
        }
        val result = MusicFilterHelper.deduplicateSongs(cleanSongs)
        if (result.isNotEmpty()) {
            playlistTrackCache[playlist.id] = result
        }
        return result
    }

    suspend fun searchPlaylists(query: String, limit: Int = 5): List<Playlist> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        val results = youTubeDataSource.searchPlaylists(trimmed, limit = 10)
        return results.filter { !MusicFilterHelper.isExcludedPlaylist(it.title) }.take(limit)
    }
}
