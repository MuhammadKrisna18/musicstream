package com.example.p5.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthThick

@Composable
fun SettingsStorageCard(
    formattedCacheSize: String,
    cacheSizeBytes: Long,
    isClearingCache: Boolean,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val clearSource = remember { MutableInteractionSource() }
    val isClearEnabled = cacheSizeBytes > 0 && !isClearingCache

    SettingsSectionCard(
        icon = Icons.Rounded.Storage,
        title = stringResource(R.string.settings_storage_title),
        subtitle = stringResource(R.string.settings_storage_subtitle),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_storage_cache_size),
                color = appColors.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadiusPill))
                    .background(appColors.glassFillMedium)
                    .glassBorder(
                        shape = RoundedCornerShape(CornerRadiusPill),
                        strokeWidth = StrokeWidthBorder,
                        startColor = appColors.accentLight.copy(alpha = 0.45f),
                        endColor = Color.Transparent
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedCacheSize,
                    color = appColors.accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(SpacingSm))

        Text(
            text = stringResource(R.string.settings_storage_desc),
            color = appColors.textMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ActionButtonSizeSm)
                .then(
                    if (isClearEnabled) {
                        Modifier.pressedScale(
                            targetScale = ScalePressedButton,
                            interactionSource = clearSource
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(CornerRadiusSm))
                .background(if (isClearEnabled) appColors.glassFillLight else appColors.glassFillMedium.copy(alpha = 0.5f))
                .glassBorder(
                    shape = RoundedCornerShape(CornerRadiusSm),
                    strokeWidth = StrokeWidthBorder,
                    startColor = if (isClearEnabled) appColors.glassBorderBright.copy(alpha = 0.35f) else Color.Transparent,
                    endColor = appColors.glassBorderSubtle
                )
                .clickable(
                    interactionSource = clearSource,
                    indication = null,
                    enabled = isClearEnabled,
                    onClick = onClearCache
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isClearingCache) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingSm)
                ) {
                    CircularProgressIndicator(
                        color = appColors.accent,
                        strokeWidth = StrokeWidthThick,
                        modifier = Modifier.size(IconSizeSm)
                    )
                    Text(
                        text = stringResource(R.string.settings_storage_clearing),
                        color = appColors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpacingXs)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = null,
                        tint = if (isClearEnabled) appColors.accent else appColors.textMuted,
                        modifier = Modifier.size(IconSizeSm)
                    )
                    Text(
                        text = stringResource(R.string.settings_storage_clear),
                        color = if (isClearEnabled) appColors.textPrimary else appColors.textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
