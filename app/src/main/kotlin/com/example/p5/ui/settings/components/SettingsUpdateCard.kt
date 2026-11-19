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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
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
fun SettingsUpdateCard(
    currentVersion: String,
    isCheckingUpdate: Boolean,
    onCheckForUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val updateSource = remember { MutableInteractionSource() }

    SettingsSectionCard(
        icon = Icons.Rounded.SystemUpdate,
        title = stringResource(R.string.settings_update_title),
        subtitle = stringResource(R.string.settings_update_subtitle),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_update_current_version),
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
                        startColor = appColors.glassBorderBright.copy(alpha = 0.25f),
                        endColor = Color.Transparent
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "v$currentVersion",
                    color = appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ActionButtonSizeSm)
                .then(
                    if (!isCheckingUpdate) {
                        Modifier.pressedScale(
                            targetScale = ScalePressedButton,
                            interactionSource = updateSource
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(CornerRadiusSm))
                .background(appColors.glassFillLight)
                .glassBorder(
                    shape = RoundedCornerShape(CornerRadiusSm),
                    strokeWidth = StrokeWidthBorder,
                    startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                    endColor = appColors.glassBorderSubtle
                )
                .clickable(
                    interactionSource = updateSource,
                    indication = null,
                    enabled = !isCheckingUpdate,
                    onClick = onCheckForUpdate
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCheckingUpdate) {
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
                        text = stringResource(R.string.settings_update_checking),
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
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(IconSizeSm)
                    )
                    Text(
                        text = stringResource(R.string.settings_update_check_button),
                        color = appColors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
