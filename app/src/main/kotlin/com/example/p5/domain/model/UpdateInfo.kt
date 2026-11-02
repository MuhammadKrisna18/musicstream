package com.example.p5.domain.model

data class UpdateInfo(
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val apkFileName: String,
    val apkSize: Long
)

