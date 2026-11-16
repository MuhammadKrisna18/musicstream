package com.example.p5

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable
data class Artist(
    val channelId: String,
    val artistName: String = ""
) : NavKey
