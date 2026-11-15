package com.example.p5.data.remote.playlist

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.innertube.InnerTubeFormatter
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import java.util.concurrent.ConcurrentHashMap

class PlaylistCurator(
    private val youTubeDataSource: YouTubeNativeDataSource
) {
    private val playlistTrackCache = ConcurrentHashMap<String, List<Song>>()

    suspend fun getCuratedPlaylists(): List<Playlist> {
        val feedPlaylists = youTubeDataSource.getHomeFeed(refresh = false).playlists
        if (feedPlaylists.isNotEmpty()) {
            return feedPlaylists
        }
        return youTubeDataSource.searchPlaylists("Top Hits Indonesia", limit = 6)
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
            song.durationSeconds in 90..480 && !InnerTubeFormatter.isExcludedCompilation(song.title)
        }

        val result = if (cleanSongs.isNotEmpty()) cleanSongs else validTracks
        if (result.isNotEmpty()) {
            playlistTrackCache[playlist.id] = result
        }
        return result
    }

    suspend fun searchPlaylists(query: String): List<Playlist> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()
        return youTubeDataSource.searchPlaylists(trimmed, limit = 5)
    }
}
