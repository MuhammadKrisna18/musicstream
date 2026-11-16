package com.example.p5.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import com.example.p5.ui.artist.components.ArtistAlbumsSection
import com.example.p5.ui.artist.components.ArtistHeaderSection
import com.example.p5.ui.artist.components.ArtistTopSongsSection
import com.example.p5.ui.common.AppViewModelProvider
import com.example.p5.ui.components.SongItem
import com.example.p5.ui.theme.ObsidianBackground
import com.example.p5.ui.theme.ObsidianSurface
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.VelvetPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistProfileScreen(
    channelId: String,
    artistNameFallback: String = "",
    onBackClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArtistProfileViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onMoreOptionsClick: ((Song) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val currentSongId = playerState.currentSong?.id

    LaunchedEffect(channelId) {
        viewModel.loadArtist(channelId)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                title = {
                    val title = when (val state = uiState) {
                        is ArtistProfileUiState.Success -> state.profile.name
                        else -> artistNameFallback
                    }
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground.copy(alpha = 0.95f)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ArtistProfileUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = VelvetPrimary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                is ArtistProfileUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            color = TextMuted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadArtist(channelId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VelvetPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Coba Lagi",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(text = "Coba Lagi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is ArtistProfileUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        item(key = "header") {
                            ArtistHeaderSection(
                                profile = state.profile,
                                onPlayAll = { viewModel.playAll(state.allSongs) },
                                onShuffle = { viewModel.shuffleAll(state.allSongs) }
                            )
                        }

                        if (state.profile.topSongs.isNotEmpty()) {
                            item(key = "top_songs") {
                                ArtistTopSongsSection(
                                    songs = state.profile.topSongs,
                                    currentPlayingSongId = currentSongId,
                                    onSongClick = { viewModel.playSong(it, state.profile.topSongs) }
                                )
                            }
                        }

                        if (state.profile.albums.isNotEmpty()) {
                            item(key = "albums") {
                                ArtistAlbumsSection(
                                    title = "Album",
                                    playlists = state.profile.albums,
                                    onPlaylistClick = onPlaylistClick
                                )
                            }
                        }

                        if (state.profile.singles.isNotEmpty()) {
                            item(key = "singles") {
                                ArtistAlbumsSection(
                                    title = "Single & EP",
                                    playlists = state.profile.singles,
                                    onPlaylistClick = onPlaylistClick
                                )
                            }
                        }

                        if (state.allSongs.isNotEmpty()) {
                            item(key = "all_songs_header") {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Semua Rilisan",
                                        color = TextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            items(state.allSongs, key = { it.id }) { song ->
                                val isPlaying = song.id == currentSongId
                                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                                    SongItem(
                                        song = song,
                                        isPlaying = isPlaying,
                                        isSelected = isPlaying,
                                        onClick = { viewModel.playSong(song, state.allSongs) },
                                        onMoreOptionsClick = onMoreOptionsClick
                                    )
                                }
                            }

                            if (!state.hasLoadedAllSongs && !state.profile.allSongsPlaylistId.isNullOrBlank()) {
                                item(key = "load_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (state.isLoadingAllSongs) {
                                            CircularProgressIndicator(
                                                color = VelvetPrimary,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        } else {
                                            OutlinedButton(
                                                onClick = { viewModel.loadAllSongs() },
                                                shape = RoundedCornerShape(20.dp)
                                            ) {
                                                Text(
                                                    text = "Muat Lebih Banyak Lagu",
                                                    color = TextPrimary,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
