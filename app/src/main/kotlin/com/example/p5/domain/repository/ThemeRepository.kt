package com.example.p5.domain.repository

import com.example.p5.domain.model.AppThemeId
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {
    val currentTheme: StateFlow<AppThemeId>
    suspend fun setTheme(themeId: AppThemeId)
}
