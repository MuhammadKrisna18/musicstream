package com.example.p5.ui.settings

import java.util.Locale

data class SettingsUiState(
    val cacheSizeBytes: Long = 0L,
    val isClearingCache: Boolean = false,
    val userMessage: String? = null,
    val currentVersion: String = ""
) {
    val formattedCacheSize: String
        get() {
            if (cacheSizeBytes <= 0L) return "0 B"
            val kb = cacheSizeBytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) {
                String.format(Locale.getDefault(), "%.1f MB", mb)
            } else {
                String.format(Locale.getDefault(), "%.1f KB", kb)
            }
        }
}
