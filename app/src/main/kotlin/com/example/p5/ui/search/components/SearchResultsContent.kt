package com.example.p5.ui.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchFilter
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.bottomScrollPaddingWithInsets
import java.util.Locale

@Composable
fun SearchResultsContent(
    query: String,
    songs: List<Song>,
    playlists: List<Playlist>,
    searchFilter: SearchFilter,
    playerState: PlayerState,
    onSelectFilter: (SearchFilter) -> Unit,
    onSongSelected: (Song, Int) -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    onArtistClick: (channelId: String, artistName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val albums = remember(playlists) {
        playlists.filter { isAlbumPlaylist(it) }
    }

    val playlistsOnly = remember(playlists) {
        playlists.filter { !isAlbumPlaylist(it) }
    }

    val matchingArtists = remember(songs, playlists) {
        extractMatchingArtists(songs, playlists)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomScrollPaddingWithInsets),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item(key = "search_filter_bar") {
            SearchFilterBar(
                selectedFilter = searchFilter,
                onSelectFilter = onSelectFilter,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        when (searchFilter) {
            SearchFilter.ALL -> {
                if (playlists.isNotEmpty()) {
                    item(key = "search_playlists_row") {
                        PlaylistRowSection(
                            title = stringResource(R.string.search_playlists_title),
                            subtitle = stringResource(R.string.search_playlists_subtitle),
                            playlists = playlists,
                            onPlaylistClick = onPlaylistSelected,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                itemsIndexed(
                    items = songs,
                    key = { _, item -> item.id }
                ) { index, song ->
                    val isSelected = playerState.currentSong?.id == song.id
                    SongItem(
                        song = song,
                        isPlaying = playerState.isPlaying,
                        isSelected = isSelected,
                        onClick = { onSongSelected(song, index) },
                        onMoreOptionsClick = onMoreOptionsClick
                    )
                }
            }

            SearchFilter.SONGS -> {
                if (songs.isNotEmpty()) {
                    itemsIndexed(
                        items = songs,
                        key = { _, item -> item.id }
                    ) { index, song ->
                        val isSelected = playerState.currentSong?.id == song.id
                        SongItem(
                            song = song,
                            isPlaying = playerState.isPlaying,
                            isSelected = isSelected,
                            onClick = { onSongSelected(song, index) },
                            onMoreOptionsClick = onMoreOptionsClick
                        )
                    }
                } else {
                    item {
                        SearchEmptyState(
                            query = "Lagu untuk \"$query\"",
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }

            SearchFilter.ALBUMS -> {
                if (albums.isNotEmpty()) {
                    items(
                        items = albums,
                        key = { it.id }
                    ) { album ->
                        SearchPlaylistItem(
                            playlist = album,
                            categoryBadge = "Album",
                            onClick = { onPlaylistSelected(album) }
                        )
                    }
                } else {
                    item {
                        SearchEmptyState(
                            query = "Album untuk \"$query\"",
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }

            SearchFilter.PLAYLISTS -> {
                if (playlistsOnly.isNotEmpty()) {
                    items(
                        items = playlistsOnly,
                        key = { it.id }
                    ) { playlist ->
                        SearchPlaylistItem(
                            playlist = playlist,
                            categoryBadge = "Playlist",
                            onClick = { onPlaylistSelected(playlist) }
                        )
                    }
                } else {
                    item {
                        SearchEmptyState(
                            query = "Playlist untuk \"$query\"",
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }

            SearchFilter.ARTISTS -> {
                if (matchingArtists.isNotEmpty()) {
                    items(
                        items = matchingArtists,
                        key = { it.name }
                    ) { artistItem ->
                        SearchArtistItem(
                            artist = artistItem,
                            onClick = { onArtistClick(artistItem.channelId, artistItem.name) }
                        )
                    }
                } else {
                    item {
                        SearchEmptyState(
                            query = "Artis untuk \"$query\"",
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun isAlbumPlaylist(playlist: Playlist): Boolean {
    return playlist.id.startsWith("MPRE") ||
        playlist.subtitle.contains("album", ignoreCase = true) ||
        playlist.title.contains("album", ignoreCase = true)
}

private fun extractMatchingArtists(
    songs: List<Song>,
    playlists: List<Playlist>
): List<ArtistSearchItem> {
    val artistList = mutableListOf<ArtistSearchItem>()
    val seenNames = mutableSetOf<String>()

    for (song in songs) {
        val name = song.artist.trim()
        if (name.isNotBlank() && name != "YouTube Music" && seenNames.add(name.lowercase(Locale.ROOT))) {
            artistList.add(
                ArtistSearchItem(
                    name = name,
                    channelId = song.channelId ?: "",
                    thumbnailUrl = song.thumbnailUrl
                )
            )
        }
    }

    for (pl in playlists) {
        val name = pl.subtitle.trim()
        if (name.isNotBlank() && name != "YouTube Music" && seenNames.add(name.lowercase(Locale.ROOT))) {
            artistList.add(
                ArtistSearchItem(
                    name = name,
                    channelId = if (pl.id.startsWith("UC")) pl.id else "",
                    thumbnailUrl = pl.thumbnailUrl
                )
            )
        }
    }

    return artistList
}
