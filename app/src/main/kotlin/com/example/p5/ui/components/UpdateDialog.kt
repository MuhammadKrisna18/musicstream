package com.example.p5.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.UpdateInfo
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaContainer
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlowSubtle
import com.example.p5.ui.theme.ToscaPrimary
import java.io.File

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Float,
    downloadedFile: File?,
    onDismiss: () -> Unit,
    onStartDownload: () -> Unit,
    onInstall: () -> Unit
) {
    val isDownloaded = downloadedFile != null
    val dialogShape = remember { RoundedCornerShape(26.dp) }

    AlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        },
        modifier = Modifier
            .ambientGlow(
                color = ToscaGlowSubtle,
                alpha = 0.5f,
                shape = dialogShape
            )
            .clip(dialogShape)
            .glassBorder(
                shape = dialogShape,
                strokeWidth = 1.dp,
                startColor = GlassBorderBright.copy(alpha = 0.45f),
                endColor = GlassBorderSubtle
            ),
        containerColor = ObsidianCardHigh,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ToscaContainer)
                        .glassBorder(
                            shape = CircleShape,
                            strokeWidth = 1.dp,
                            startColor = ToscaGlassBorder,
                            endColor = Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Rounded.CheckCircle else Icons.Rounded.SystemUpdate,
                        contentDescription = null,
                        tint = ToscaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = if (isDownloaded) stringResource(R.string.update_ready_title) else stringResource(R.string.update_available_title),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.update_version_label, updateInfo.latestVersion),
                        fontSize = 12.sp,
                        color = ToscaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (updateInfo.releaseNotes.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.update_release_notes_label),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassFillLight)
                            .glassBorder(
                                shape = RoundedCornerShape(12.dp),
                                strokeWidth = 0.75.dp,
                                startColor = GlassBorderBright.copy(alpha = 0.25f),
                                endColor = Color.Transparent
                            )
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = updateInfo.releaseNotes,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                if (isDownloading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = ToscaPrimary,
                            trackColor = ObsidianSurfaceVariant,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.update_downloading_progress, (downloadProgress * 100).toInt()),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                } else if (isDownloaded) {
                    Text(
                        text = stringResource(R.string.update_downloaded_message, updateInfo.apkFileName),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.update_available_message),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        confirmButton = {
            val confirmSource = remember { MutableInteractionSource() }
            if (isDownloaded) {
                Button(
                    onClick = onInstall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ToscaPrimary,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.pressedScale(targetScale = 0.94f, interactionSource = confirmSource)
                ) {
                    Text(stringResource(R.string.update_action_install), fontWeight = FontWeight.Bold)
                }
            } else if (isDownloading) {
                Button(
                    onClick = {},
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = ToscaContainer,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.update_action_downloading))
                }
            } else {
                Button(
                    onClick = onStartDownload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ToscaPrimary,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.pressedScale(targetScale = 0.94f, interactionSource = confirmSource)
                ) {
                    Text(stringResource(R.string.update_action_download), fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isDownloading) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isDownloaded) stringResource(R.string.dialog_close) else stringResource(R.string.update_action_later), color = TextSecondary)
                }
            }
        }
    )
}
