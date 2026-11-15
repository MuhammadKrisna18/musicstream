package com.example.p5.ui.components.player

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeMd
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeAction
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.ScalePressedRound
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlayerTopBar(
    centerView: PlayerCenterView,
    isSleepTimerActive: Boolean,
    onDismiss: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onToggleLyrics: () -> Unit,
    onToggleQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = ActionButtonSizeMd, height = SpacingXs)
                .clip(CircleShape)
                .background(GlassBorderBright.copy(alpha = 0.45f))
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dismissSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(ActionButtonSizeMd)
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = dismissSource)
                    .clip(CircleShape)
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                        endColor = GlassBorderSubtle
                    )
                    .clickable(
                        interactionSource = dismissSource,
                        indication = null,
                        onClick = onDismiss
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.player_dismiss_desc),
                    tint = TextPrimary,
                    modifier = Modifier.size(IconSizeAction)
                )
            }

            Text(
                text = when (centerView) {
                    PlayerCenterView.ALBUM -> stringResource(R.string.player_header_now_playing)
                    PlayerCenterView.QUEUE -> stringResource(R.string.player_header_queue)
                    PlayerCenterView.LYRICS -> stringResource(R.string.player_header_lyrics)
                },
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PlayerTopActionIcon(
                    icon = Icons.Rounded.Bedtime,
                    isActive = isSleepTimerActive,
                    onClick = onOpenSleepTimer
                )

                PlayerTopActionIcon(
                    icon = Icons.Rounded.Lyrics,
                    isActive = centerView == PlayerCenterView.LYRICS,
                    onClick = onToggleLyrics
                )

                PlayerTopActionIcon(
                    icon = Icons.AutoMirrored.Rounded.QueueMusic,
                    isActive = centerView == PlayerCenterView.QUEUE,
                    onClick = onToggleQueue
                )
            }
        }
    }
}

@Composable
private fun PlayerTopActionIcon(
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(ActionButtonSizeSm)
            .pressedScale(targetScale = ScalePressedRound, interactionSource = interactionSource)
            .clip(CircleShape)
            .background(if (isActive) ToscaPrimary.copy(alpha = 0.16f) else GlassFillLight)
            .then(
                if (isActive) {
                    Modifier.glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle
                    )
                } else {
                    Modifier.glassBorder(
                        shape = CircleShape,
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) ToscaPrimary else TextSecondary,
            modifier = Modifier.size(19.dp)
        )
    }
}
