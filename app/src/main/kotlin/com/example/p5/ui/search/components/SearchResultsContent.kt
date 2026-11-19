package com.example.p5.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.p5.R
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.SearchFilter
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.bottomScrollPaddingWithInsets
import kotlinx.coroutines.flow.Flow
import java.util.Locale

@Composable
fun SearchResultsContent(
    query: String,
    songs: List<Song>,
    playlists: List<Playlist>,
    searchFilter: SearchFilter,
    playerState: PlayerState,
    onSelectFilter: (SearchFilter) -> Unit,
    onSongSelected: (Song, List<Song>, Int) -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    onArtistClick: (channelId: String, artistName: String) -> Unit,
    modifier: Modifier = Modifier,
    songsPagingFlow: Flow<PagingData<Song>>? = null
) {
    val pagedSongs = songsPagingFlow?.collectAsLazyPagingItems()

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
                        onClick = { onSongSelected(song, songs, index) },
                        onMoreOptionsClick = onMoreOptionsClick
                    )
                }
            }

            SearchFilter.SONGS -> {
                if (pagedSongs != null) {
                    when (val refreshState = pagedSongs.loadState.refresh) {
                        is LoadState.Loading -> {
                            if (pagedSongs.itemCount == 0) {
                                item(key = "paging_songs_refresh_loading") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = AppTheme.colors.accent,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                        is LoadState.Error -> {
                            if (pagedSongs.itemCount == 0) {
                                item(key = "paging_songs_refresh_error") {
                                    SearchErrorState(
                                        message = refreshState.error.localizedMessage ?: "",
                                        onRetry = { pagedSongs.retry() },
                                        modifier = Modifier.padding(vertical = 32.dp)
                                    )
                                }
                            }
                        }
                        is LoadState.NotLoading -> {
                            if (pagedSongs.itemCount == 0) {
                                item(key = "paging_songs_empty") {
                                    SearchEmptyState(
                                        query = "Lagu untuk \"$query\"",
                                        modifier = Modifier.padding(vertical = 32.dp)
                                    )
                                }
                            }
                        }
                    }

                    items(
                        count = pagedSongs.itemCount,
                        key = { index ->
                            pagedSongs[index]?.id ?: "paged_song_$index"
                        }
                    ) { index ->
                        val song = pagedSongs[index]
                        if (song != null) {
                            val isSelected = playerState.currentSong?.id == song.id
                            SongItem(
                                song = song,
                                isPlaying = playerState.isPlaying,
                                isSelected = isSelected,
                                onClick = {
                                    val currentLoadedList = (0 until pagedSongs.itemCount).mapNotNull { pagedSongs[it] }
                                    val effectiveIndex = currentLoadedList.indexOfFirst { it.id == song.id }.takeIf { it >= 0 } ?: index
                                    onSongSelected(song, currentLoadedList, effectiveIndex)
                                },
                                onMoreOptionsClick = onMoreOptionsClick
                            )
                        }
                    }

                    when (pagedSongs.loadState.append) {
                        is LoadState.Loading -> {
                            item(key = "paging_songs_append_loading") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = AppTheme.colors.accent,
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item(key = "paging_songs_append_error") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { pagedSongs.retry() }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.action_retry),
                                        color = AppTheme.colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        is LoadState.NotLoading -> Unit
                    }
                } else if (songs.isNotEmpty()) {
                    itemsIndexed(
                        items = songs,
                        key = { _, item -> item.id }
                    ) { index, song ->
                        val isSelected = playerState.currentSong?.id == song.id
                        SongItem(
                            song = song,
                            isPlaying = playerState.isPlaying,
                            isSelected = isSelected,
                            onClick = { onSongSelected(song, songs, index) },
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
