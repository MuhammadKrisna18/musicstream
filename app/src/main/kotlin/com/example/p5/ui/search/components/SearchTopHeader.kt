package com.example.p5.ui.search.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.AppTheme

@Composable
fun SearchTopHeader(
    isRefreshing: Boolean,
    isPlaying: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val refreshTransition = rememberInfiniteTransition(label = "refresh_angle")
    val refreshAngle by refreshTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_spin"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.app_title_music),
                    color = appColors.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = stringResource(R.string.app_title_stream),
                    color = appColors.accent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            }
            Text(
                text = stringResource(R.string.home_subtitle),
                color = appColors.textSecondary,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColors.accent.copy(alpha = 0.12f))
                        .glassBorder(
                            shape = RoundedCornerShape(12.dp),
                            strokeWidth = 1.dp,
                            startColor = appColors.accent.copy(alpha = 0.35f),
                            endColor = appColors.glassBorderSubtle
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    MiniEqualizer(isPlaying = true)
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(appColors.glassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = 1.dp,
                        startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                        endColor = appColors.glassBorderSubtle
                    )
                    .clickable(
                        enabled = !isRefreshing,
                        onClick = onRefresh
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = stringResource(R.string.home_refresh_content_desc),
                    tint = if (isRefreshing) appColors.accent else appColors.textSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isRefreshing) refreshAngle else 0f)
                )
            }
        }
    }
}
