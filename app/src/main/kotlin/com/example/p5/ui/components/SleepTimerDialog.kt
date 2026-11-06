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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.p5.domain.model.SleepTimerPresets
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.CornerRadiusModal
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeHero
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaContainer
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlowSubtle
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun SleepTimerDialog(
    secondsRemaining: Long?,
    isEndOfTrack: Boolean,
    onSetMinutes: (Int) -> Unit,
    onSetEndOfTrack: () -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    val isTimerActive = secondsRemaining != null || isEndOfTrack
    val dialogShape = remember { RoundedCornerShape(CornerRadiusModal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .ambientGlow(
                color = ToscaGlowSubtle,
                alpha = 0.5f,
                shape = dialogShape
            )
            .clip(dialogShape)
            .glassBorder(
                shape = dialogShape,
                strokeWidth = StrokeWidthBorder,
                startColor = GlassBorderBright.copy(alpha = 0.45f),
                endColor = GlassBorderSubtle
            ),
        containerColor = ObsidianCardHigh,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingMd)
            ) {
                Box(
                    modifier = Modifier
                        .size(IconSizeHero)
                        .clip(CircleShape)
                        .background(ToscaContainer)
                        .glassBorder(
                            shape = CircleShape,
                            strokeWidth = StrokeWidthBorder,
                            startColor = ToscaGlassBorder,
                            endColor = Color.Transparent
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bedtime,
                        contentDescription = null,
                        tint = ToscaPrimary,
                        modifier = Modifier.size(IconSizeStandard)
                    )
                }

                Column {
                    Text(
                        text = stringResource(R.string.sleep_timer_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isTimerActive) {
                            if (isEndOfTrack) stringResource(R.string.sleep_timer_subtitle_end_of_track)
                            else stringResource(R.string.sleep_timer_subtitle_countdown, formatCountdown(secondsRemaining ?: 0L))
                        } else stringResource(R.string.sleep_timer_subtitle_idle),
                        fontSize = 12.sp,
                        color = if (isTimerActive) ToscaPrimary else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpacingSm)
            ) {
                val presets = SleepTimerPresets.defaultMinutes

                presets.forEach { minutes ->
                    val isSelected = secondsRemaining != null && (secondsRemaining / 60) == minutes.toLong()
                    TimerOptionItem(
                        title = stringResource(R.string.sleep_timer_minutes_option, minutes),
                        isSelected = isSelected,
                        onClick = {
                            onSetMinutes(minutes)
                            onDismiss()
                        }
                    )
                }

                TimerOptionItem(
                    title = stringResource(R.string.sleep_timer_end_of_track_option),
                    isSelected = isEndOfTrack,
                    onClick = {
                        onSetEndOfTrack()
                        onDismiss()
                    }
                )

                if (isTimerActive) {
                    Spacer(modifier = Modifier.height(SpacingXs))
                    val cancelSource = remember { MutableInteractionSource() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressedScale(targetScale = ScalePressedCard, interactionSource = cancelSource)
                            .clip(RoundedCornerShape(CornerRadiusCard))
                            .background(ToscaPrimary.copy(alpha = 0.12f))
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusCard),
                                strokeWidth = StrokeWidthBorder,
                                startColor = ToscaGlassBorder,
                                endColor = GlassBorderSubtle
                            )
                            .clickable(
                                interactionSource = cancelSource,
                                indication = null
                            ) {
                                onCancelTimer()
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.sleep_timer_cancel),
                            color = ToscaPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.sleep_timer_cancel_icon_desc),
                            tint = ToscaPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(CornerRadiusMd)
            ) {
                Text(stringResource(R.string.dialog_close), color = TextSecondary)
            }
        }
    )
}

@Composable
private fun TimerOptionItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = remember { RoundedCornerShape(CornerRadiusCard) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pressedScale(targetScale = ScalePressedCard, interactionSource = interactionSource)
            .clip(itemShape)
            .background(if (isSelected) ToscaPrimary.copy(alpha = 0.16f) else GlassFillLight)
            .then(
                if (isSelected) {
                    Modifier.glassBorder(
                        shape = itemShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle
                    )
                } else {
                    Modifier.glassBorder(
                        shape = itemShape,
                        strokeWidth = StrokeWidthStandard,
                        startColor = GlassBorderBright.copy(alpha = 0.25f),
                        endColor = Color.Transparent
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = SpacingMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = if (isSelected) ToscaPrimary else TextPrimary,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = ToscaPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun formatCountdown(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
