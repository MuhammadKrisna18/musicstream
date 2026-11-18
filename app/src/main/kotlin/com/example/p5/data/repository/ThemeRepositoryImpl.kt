package com.example.p5.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.p5.domain.model.AppThemeId
import com.example.p5.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeRepositoryImpl(
    context: Context,
    private val prefsName: String = "musicstream_theme_prefs"
) : ThemeRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadSavedTheme())
    override val currentTheme: StateFlow<AppThemeId> = _currentTheme.asStateFlow()

    private fun loadSavedTheme(): AppThemeId {
        val savedName = prefs.getString(KEY_THEME_ID, AppThemeId.DEFAULT_DARK.name)
        return try {
            if (savedName != null) AppThemeId.valueOf(savedName) else AppThemeId.DEFAULT_DARK
        } catch (e: IllegalArgumentException) {
            AppThemeId.DEFAULT_DARK
        }
    }

    override suspend fun setTheme(themeId: AppThemeId) {
        prefs.edit().putString(KEY_THEME_ID, themeId.name).apply()
        _currentTheme.value = themeId
    }

    companion object {
        private const val KEY_THEME_ID = "selected_app_theme_id"
    }
}
