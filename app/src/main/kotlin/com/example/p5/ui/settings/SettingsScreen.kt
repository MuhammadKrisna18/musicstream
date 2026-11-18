package com.example.p5.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.p5.R
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.settings.components.SettingsSectionCard
import com.example.p5.ui.settings.components.SettingsTopHeader
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.BottomScrollPadding
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillMedium
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthThick
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

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
            SettingsSectionCard(
                icon = Icons.Rounded.Storage,
                title = stringResource(R.string.settings_storage_title),
                subtitle = stringResource(R.string.settings_storage_subtitle)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.settings_storage_cache_size),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadiusPill))
                            .background(GlassFillMedium)
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusPill),
                                strokeWidth = StrokeWidthBorder,
                                startColor = ToscaGlassBorder,
                                endColor = Color.Transparent
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.formattedCacheSize,
                            color = ToscaPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpacingSm))

                Text(
                    text = stringResource(R.string.settings_storage_desc),
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                val clearSource = remember { MutableInteractionSource() }
                val isClearEnabled = uiState.cacheSizeBytes > 0 && !uiState.isClearingCache

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
                        .background(if (isClearEnabled) GlassFillLight else GlassFillMedium.copy(alpha = 0.5f))
                        .glassBorder(
                            shape = RoundedCornerShape(CornerRadiusSm),
                            strokeWidth = StrokeWidthBorder,
                            startColor = if (isClearEnabled) GlassBorderBright.copy(alpha = 0.35f) else Color.Transparent,
                            endColor = GlassBorderSubtle
                        )
                        .clickable(
                            interactionSource = clearSource,
                            indication = null,
                            enabled = isClearEnabled,
                            onClick = { settingsViewModel.clearCache(context) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isClearingCache) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(SpacingSm)
                        ) {
                            CircularProgressIndicator(
                                color = ToscaPrimary,
                                strokeWidth = StrokeWidthThick,
                                modifier = Modifier.size(IconSizeSm)
                            )
                            Text(
                                text = stringResource(R.string.settings_storage_clearing),
                                color = TextSecondary,
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
                                tint = if (isClearEnabled) ToscaPrimary else TextMuted,
                                modifier = Modifier.size(IconSizeSm)
                            )
                            Text(
                                text = stringResource(R.string.settings_storage_clear),
                                color = if (isClearEnabled) TextPrimary else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionCard(
                icon = Icons.Rounded.SystemUpdate,
                title = stringResource(R.string.settings_update_title),
                subtitle = stringResource(R.string.settings_update_subtitle)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.settings_update_current_version),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadiusPill))
                            .background(GlassFillMedium)
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusPill),
                                strokeWidth = StrokeWidthBorder,
                                startColor = GlassBorderBright.copy(alpha = 0.25f),
                                endColor = Color.Transparent
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "v${uiState.currentVersion}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val updateSource = remember { MutableInteractionSource() }

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
                        .background(GlassFillLight)
                        .glassBorder(
                            shape = RoundedCornerShape(CornerRadiusSm),
                            strokeWidth = StrokeWidthBorder,
                            startColor = GlassBorderBright.copy(alpha = 0.35f),
                            endColor = GlassBorderSubtle
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
                                color = ToscaPrimary,
                                strokeWidth = StrokeWidthThick,
                                modifier = Modifier.size(IconSizeSm)
                            )
                            Text(
                                text = stringResource(R.string.settings_update_checking),
                                color = TextSecondary,
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
                                tint = ToscaPrimary,
                                modifier = Modifier.size(IconSizeSm)
                            )
                            Text(
                                text = stringResource(R.string.settings_update_check_button),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            val githubUrl = stringResource(R.string.settings_about_github_url)

            SettingsSectionCard(
                icon = Icons.Rounded.Info,
                title = stringResource(R.string.settings_about_title),
                subtitle = stringResource(R.string.settings_about_desc)
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
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.settings_about_developer_name),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    val linkSource = remember { MutableInteractionSource() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressedScale(targetScale = ScalePressedButton, interactionSource = linkSource)
                            .clip(RoundedCornerShape(CornerRadiusSm))
                            .clickable(
                                interactionSource = linkSource,
                                indication = null,
                                onClick = { uriHandler.openUri(githubUrl) }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.settings_about_github),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "MuhammadKrisna18",
                                color = ToscaPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                contentDescription = null,
                                tint = ToscaPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
