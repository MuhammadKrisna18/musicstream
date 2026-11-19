package com.example.p5.di

import android.content.Context
import com.example.p5.data.local.MusicDatabaseHelper
import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.data.remote.playlist.PlaylistCurator
import com.example.p5.data.repository.DownloadRepositoryImpl
import com.example.p5.data.repository.FavoriteRepositoryImpl
import com.example.p5.data.repository.HistoryRepositoryImpl
import com.example.p5.data.repository.LyricsRepositoryImpl
import com.example.p5.data.repository.MusicRepositoryImpl
import com.example.p5.data.repository.SavedSongRepositoryImpl
import com.example.p5.data.repository.SearchHistoryRepositoryImpl
import com.example.p5.data.repository.ThemeRepositoryImpl
import com.example.p5.data.repository.UpdateRepositoryImpl
import com.example.p5.domain.player.AudioPlayer
import com.example.p5.domain.repository.DownloadRepository
import com.example.p5.domain.repository.FavoriteRepository
import com.example.p5.domain.repository.HistoryRepository
import com.example.p5.domain.repository.LyricsRepository
import com.example.p5.domain.repository.MusicRepository
import com.example.p5.domain.repository.SavedSongRepository
import com.example.p5.domain.repository.SearchHistoryRepository
import com.example.p5.domain.repository.ThemeRepository
import com.example.p5.domain.repository.UpdateRepository
import com.example.p5.domain.usecase.GetArtistAllSongsUseCase
import com.example.p5.domain.usecase.GetArtistProfileUseCase
import com.example.p5.domain.usecase.GetCuratedPlaylistsUseCase
import com.example.p5.domain.usecase.GetGenreFeedUseCase
import com.example.p5.domain.usecase.GetHomeCategoriesUseCase
import com.example.p5.domain.usecase.GetHomeFeedUseCase
import com.example.p5.domain.usecase.GetPlaylistTracksUseCase
import com.example.p5.domain.usecase.GetStreamAudioUseCase
import com.example.p5.domain.usecase.GetTrendingMusicUseCase
import com.example.p5.domain.usecase.SearchMusicUseCase
import com.example.p5.domain.usecase.SearchPlaylistsUseCase
import com.example.p5.domain.usecase.SearchSongsAndPlaylistsUseCase
import com.example.p5.playback.MediaSessionAudioPlayer
import com.example.p5.playback.MusicPlayerManager
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

interface AppContainer {
    val musicRepository: MusicRepository
    val searchMusicUseCase: SearchMusicUseCase
    val getStreamAudioUseCase: GetStreamAudioUseCase
    val getTrendingMusicUseCase: GetTrendingMusicUseCase
    val getHomeCategoriesUseCase: GetHomeCategoriesUseCase
    val getHomeFeedUseCase: GetHomeFeedUseCase
    val getGenreFeedUseCase: GetGenreFeedUseCase
    val getCuratedPlaylistsUseCase: GetCuratedPlaylistsUseCase
    val getPlaylistTracksUseCase: GetPlaylistTracksUseCase
    val searchPlaylistsUseCase: SearchPlaylistsUseCase
    val searchSongsAndPlaylistsUseCase: SearchSongsAndPlaylistsUseCase
    val getArtistProfileUseCase: GetArtistProfileUseCase
    val getArtistAllSongsUseCase: GetArtistAllSongsUseCase

    val audioPlayer: AudioPlayer
    val musicPlayerManager: MusicPlayerManager
    val updateRepository: UpdateRepository
    val lyricsRepository: LyricsRepository
    val favoriteRepository: FavoriteRepository
    val savedSongRepository: SavedSongRepository
    val downloadRepository: DownloadRepository
    val historyRepository: HistoryRepository
    val searchHistoryRepository: SearchHistoryRepository
    val themeRepository: ThemeRepository
    val okHttpClient: OkHttpClient
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val dbHelper: MusicDatabaseHelper by lazy {
        MusicDatabaseHelper(context)
    }

    override val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val youTubeDataSource: YouTubeNativeDataSource by lazy {
        YouTubeNativeDataSource(okHttpClient)
    }

    private val playlistCurator: PlaylistCurator by lazy {
        PlaylistCurator(youTubeDataSource)
    }

    override val musicRepository: MusicRepository by lazy {
        MusicRepositoryImpl(youTubeDataSource, playlistCurator)
    }

    override val searchMusicUseCase: SearchMusicUseCase by lazy {
        SearchMusicUseCase(musicRepository)
    }

    override val getStreamAudioUseCase: GetStreamAudioUseCase by lazy {
        GetStreamAudioUseCase(musicRepository, downloadRepository)
    }

    override val getTrendingMusicUseCase: GetTrendingMusicUseCase by lazy {
        GetTrendingMusicUseCase(musicRepository)
    }

    override val getHomeCategoriesUseCase: GetHomeCategoriesUseCase by lazy {
        GetHomeCategoriesUseCase(musicRepository)
    }

    override val getHomeFeedUseCase: GetHomeFeedUseCase by lazy {
        GetHomeFeedUseCase(musicRepository)
    }

    override val getGenreFeedUseCase: GetGenreFeedUseCase by lazy {
        GetGenreFeedUseCase(musicRepository)
    }

    override val getCuratedPlaylistsUseCase: GetCuratedPlaylistsUseCase by lazy {
        GetCuratedPlaylistsUseCase(musicRepository)
    }

    override val getPlaylistTracksUseCase: GetPlaylistTracksUseCase by lazy {
        GetPlaylistTracksUseCase(musicRepository)
    }

    override val searchPlaylistsUseCase: SearchPlaylistsUseCase by lazy {
        SearchPlaylistsUseCase(musicRepository)
    }

    override val searchSongsAndPlaylistsUseCase: SearchSongsAndPlaylistsUseCase by lazy {
        SearchSongsAndPlaylistsUseCase(musicRepository)
    }

    override val getArtistProfileUseCase: GetArtistProfileUseCase by lazy {
        GetArtistProfileUseCase(musicRepository)
    }

    override val getArtistAllSongsUseCase: GetArtistAllSongsUseCase by lazy {
        GetArtistAllSongsUseCase(musicRepository)
    }

    override val audioPlayer: AudioPlayer by lazy {
        MediaSessionAudioPlayer(context)
    }

    override val lyricsRepository: LyricsRepository by lazy {
        LyricsRepositoryImpl(okHttpClient)
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepositoryImpl(dbHelper)
    }

    override val savedSongRepository: SavedSongRepository by lazy {
        SavedSongRepositoryImpl(dbHelper)
    }

    override val downloadRepository: DownloadRepository by lazy {
        DownloadRepositoryImpl(context, dbHelper)
    }

    override val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(dbHelper)
    }

    override val searchHistoryRepository: SearchHistoryRepository by lazy {
        SearchHistoryRepositoryImpl(dbHelper)
    }

    override val musicPlayerManager: MusicPlayerManager by lazy {
        MusicPlayerManager(audioPlayer, getStreamAudioUseCase, lyricsRepository, favoriteRepository, historyRepository)
    }

    override val updateRepository: UpdateRepository by lazy {
        UpdateRepositoryImpl(context, okHttpClient)
    }

    override val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(context)
    }
}
