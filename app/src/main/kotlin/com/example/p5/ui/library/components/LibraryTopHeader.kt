package com.example.p5.ui.library.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.theme.AppTheme

@Composable
fun LibraryTopHeader(
    selectedTab: Int,
    favoriteCount: Int,
    savedCount: Int,
    historyCount: Int,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.library_title),
            color = appColors.textPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = when (selectedTab) {
                0 -> stringResource(R.string.library_subtitle_favorites, favoriteCount)
                1 -> stringResource(R.string.library_subtitle_saved, savedCount)
                else -> stringResource(R.string.library_subtitle_history, historyCount)
            },
            color = appColors.textSecondary,
            fontSize = 12.sp
        )
    }
}
