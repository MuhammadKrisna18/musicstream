package com.example.p5

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.p5.ui.artist.ArtistProfileScreen
import com.example.p5.ui.main.MainScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Main> {
                MainScreen(
                    onNavigateToArtist = { channelId, artistName ->
                        backStack.add(Artist(channelId = channelId, artistName = artistName))
                    }
                )
            }
            entry<Artist> { key ->
                ArtistProfileScreen(
                    channelId = key.channelId,
                    artistNameFallback = key.artistName,
                    onBackClick = { backStack.removeLastOrNull() },
                    onPlaylistClick = { playlist ->
                        backStack.removeLastOrNull()
                    },
                    onNavigateToArtist = { channelId, artistName ->
                        backStack.add(Artist(channelId = channelId, artistName = artistName))
                    }
                )
            }
        }
    )
}
