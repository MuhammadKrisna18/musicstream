package com.example.p5.domain.downloader

import com.example.p5.domain.model.DownloadedSong
import com.example.p5.domain.model.Song

interface MusicDownloader {
    suspend fun download(song: Song): Result<DownloadedSong>
}
