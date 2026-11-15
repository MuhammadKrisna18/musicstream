package com.example.p5.ui.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
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
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeMd
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun LibraryTopHeader(
    selectedTab: Int,
    favoriteCount: Int,
    savedCount: Int,
    historyCount: Int,
    onCheckForUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.library_title),
                color = TextPrimary,
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
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        val updateSource = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(ActionButtonSizeMd)
                .pressedScale(targetScale = ScalePressedButton, interactionSource = updateSource)
                .clip(CircleShape)
                .background(GlassFillLight)
                .glassBorder(
                    shape = CircleShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                    endColor = GlassBorderSubtle
                )
                .clickable(
                    interactionSource = updateSource,
                    indication = null,
                    onClick = onCheckForUpdate
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.SystemUpdate,
                contentDescription = stringResource(R.string.library_check_update_desc),
                tint = ToscaPrimary,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}
