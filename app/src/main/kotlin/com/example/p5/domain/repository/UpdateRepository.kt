package com.example.p5.domain.repository

import com.example.p5.domain.model.UpdateInfo
import java.io.File

interface UpdateRepository {
    val currentVersionName: String
    suspend fun checkLatestRelease(): UpdateInfo?
    suspend fun downloadApk(downloadUrl: String, onProgress: (Float) -> Unit): File
    fun installApk(apkFile: File)
}

