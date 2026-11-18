package com.example.p5.ui.library.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.CornerRadiusXs
import com.example.p5.ui.theme.SpacingSm

@Composable
fun LibrarySongList(
    songs: List<Song>,
    currentSongId: String?,
    isPlaying: Boolean,
    onPlayQueue: (List<Song>, Int) -> Unit,
    onMoreOptionsClick: (Song) -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxSize()) {
        headerContent?.invoke()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = BottomScrollPadding),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(
                items = songs,
                key = { _, item -> item.id }
            ) { index, song ->
                SongItem(
                    song = song,
                    isSelected = currentSongId == song.id,
                    isPlaying = isPlaying,
                    onClick = { onPlayQueue(songs, index) },
                    onMoreOptionsClick = onMoreOptionsClick
                )
            }
        }
    }
}

@Composable
fun LibraryHistoryHeader(
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = SpacingSm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.library_history_last_listened),
            color = appColors.textSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = stringResource(R.string.library_history_clear),
            color = appColors.accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadiusXs))
                .clickable(onClick = onClearHistory)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
