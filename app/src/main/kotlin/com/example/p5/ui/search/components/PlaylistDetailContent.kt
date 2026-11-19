package com.example.p5.ui.search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.domain.model.PlayerState
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.bottomScrollPaddingWithInsets

@Composable
fun PlaylistDetailContent(
    playlist: Playlist,
    tracks: List<Song>,
    isLoading: Boolean,
    playerState: PlayerState,
    onBackClick: () -> Unit,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PlaylistDetailTopBar(
            title = playlist.title,
            trackCount = tracks.size,
            isLoading = isLoading,
            onBackClick = onBackClick
        )

        PlaylistHeroCard(playlist = playlist)

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = appColors.accent,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            }
        } else if (tracks.isNotEmpty()) {
            PlaylistActionButtonsRow(
                onPlayAll = { onPlayQueue(tracks, 0) },
                onShuffle = { onPlayQueue(tracks.shuffled(), 0) }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 4.dp, bottom = bottomScrollPaddingWithInsets),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(
                    items = tracks,
                    key = { _, song -> song.id }
                ) { index, song ->
                    val isSelected = playerState.currentSong?.id == song.id
                    SongItem(
                        song = song,
                        isPlaying = playerState.isPlaying,
                        isSelected = isSelected,
                        onClick = { onPlayQueue(tracks, index) },
                        onMoreOptionsClick = onMoreOptionsClick
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Daftar lagu kosong atau gagal dimuat.",
                    color = appColors.textMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}
