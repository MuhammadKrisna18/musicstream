package com.example.p5.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.ui.settings.components.SettingsAboutCard
import com.example.p5.ui.settings.components.SettingsStorageCard
import com.example.p5.ui.settings.components.SettingsThemeCard
import com.example.p5.ui.settings.components.SettingsTopHeader
import com.example.p5.ui.settings.components.SettingsUpdateCard
import com.example.p5.ui.settings.components.ThemeSelectorBottomSheet
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.SpacingLg

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    isCheckingUpdate: Boolean,
    onCheckForUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var isThemeSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.refreshCacheSize(context)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = SpacingLg),
        contentPadding = PaddingValues(top = 14.dp, bottom = BottomScrollPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingsTopHeader()
        }

        item {
            SettingsThemeCard(
                currentTheme = uiState.currentTheme,
                onOpenThemeSelector = { isThemeSheetVisible = true }
            )
        }

        item {
            SettingsStorageCard(
                formattedCacheSize = uiState.formattedCacheSize,
                cacheSizeBytes = uiState.cacheSizeBytes,
                isClearingCache = uiState.isClearingCache,
                onClearCache = { settingsViewModel.clearCache(context) }
            )
        }

        item {
            SettingsUpdateCard(
                currentVersion = uiState.currentVersion,
                isCheckingUpdate = isCheckingUpdate,
                onCheckForUpdate = onCheckForUpdate
            )
        }

        item {
            SettingsAboutCard(
                onOpenUrl = uriHandler::openUri
            )
        }
    }

    if (isThemeSheetVisible) {
        ThemeSelectorBottomSheet(
            selectedTheme = uiState.currentTheme,
            onThemeSelected = settingsViewModel::selectTheme,
            onDismiss = { isThemeSheetVisible = false }
        )
    }
}
