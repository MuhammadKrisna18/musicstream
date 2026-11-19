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
            .filter { !MusicFilterHelper.isExcludedContent(it.title) }
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
        val validTracks = if (directTracks.isNotEmpty()) {
            directTracks
        } else {
            val query = playlist.query.ifBlank { "Lagu ${playlist.title}" }
            youTubeDataSource.search(query, limit = 25)
        }

        val cleanSongs = validTracks.filter { song ->
            MusicFilterHelper.isValidMusicTrack(song)
        }
        val deduplicated = MusicFilterHelper.deduplicateSongs(if (cleanSongs.isNotEmpty()) cleanSongs else validTracks)
        val result = deduplicated.ifEmpty { validTracks }
        if (result.isNotEmpty()) {
            playlistTrackCache[playlist.id] = result
        }
        return result
    }

    suspend fun searchPlaylists(query: String, limit: Int = 5): List<Playlist> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        val results = youTubeDataSource.searchPlaylists(trimmed, limit = 10)
        return results.filter { !MusicFilterHelper.isExcludedContent(it.title) }.take(limit)
    }
}
