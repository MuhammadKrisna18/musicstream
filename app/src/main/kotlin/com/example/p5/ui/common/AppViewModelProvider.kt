package com.example.p5.ui.common

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.p5.MusicApplication
import com.example.p5.ui.library.LibraryViewModel
import com.example.p5.ui.player.PlayerViewModel
import com.example.p5.ui.search.SearchViewModel
import com.example.p5.ui.update.UpdateViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            SearchViewModel(
                musicApplication().container.searchMusicUseCase,
                musicApplication().container.getTrendingMusicUseCase,
                musicApplication().container.getHomeCategoriesUseCase,
                musicApplication().container.getHomeFeedUseCase
            )
        }
        initializer {
            PlayerViewModel(
                musicApplication().container.musicPlayerManager
            )
        }
        initializer {
            LibraryViewModel(
                musicApplication().container.getDownloadedSongsUseCase,
                musicApplication().container.downloadSongUseCase,
                musicApplication().container.deleteDownloadedSongUseCase
            )
        }
        initializer {
            UpdateViewModel(
                musicApplication().container.updateRepository
            )
        }
    }
}

fun CreationExtras.musicApplication(): MusicApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MusicApplication)
