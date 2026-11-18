package com.example.p5.ui.settings.components

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
fun SettingsTopHeader(
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.settings_title),
            color = appColors.textPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            color = appColors.textSecondary,
            fontSize = 12.sp
        )
    }
}
