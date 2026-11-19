package com.example.p5.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.ScalePressedButton

@Composable
fun SettingsAboutCard(
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val githubUrl = stringResource(R.string.settings_about_github_url)
    val linkSource = remember { MutableInteractionSource() }

    SettingsSectionCard(
        icon = Icons.Rounded.Info,
        title = stringResource(R.string.settings_about_title),
        subtitle = stringResource(R.string.settings_about_desc),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_about_developer),
                    color = appColors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.settings_about_developer_name),
                    color = appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = linkSource)
                    .clip(RoundedCornerShape(CornerRadiusSm))
                    .clickable(
                        interactionSource = linkSource,
                        indication = null,
                        onClick = { onOpenUrl(githubUrl) }
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_about_github),
                    color = appColors.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "MuhammadKrisna18",
                        color = appColors.accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
