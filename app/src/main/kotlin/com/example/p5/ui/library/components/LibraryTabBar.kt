package com.example.p5.ui.library.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusTab
import com.example.p5.ui.theme.DurationFast
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun LibraryTabBar(
    selectedTab: Int,
    favoriteCount: Int,
    savedCount: Int,
    historyCount: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val tabContainerShape = remember { RoundedCornerShape(CornerRadiusPill) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(tabContainerShape)
            .background(appColors.glassFillLight)
            .glassBorder(
                shape = tabContainerShape,
                strokeWidth = StrokeWidthBorder,
                startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                endColor = appColors.glassBorderSubtle
            )
            .padding(SpacingXs),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val tabs = listOf(
            stringResource(R.string.library_tab_favorites, favoriteCount),
            stringResource(R.string.library_tab_saved, savedCount),
            stringResource(R.string.library_tab_history, historyCount)
        )

        tabs.forEachIndexed { index, title ->
            val selected = selectedTab == index
            val itemShape = remember { RoundedCornerShape(CornerRadiusTab) }
            val itemSource = remember { MutableInteractionSource() }

            val itemBg by animateColorAsState(
                targetValue = if (selected) appColors.accent.copy(alpha = 0.16f) else Color.Transparent,
                animationSpec = tween(DurationFast),
                label = "tab_bg"
            )

            val textColor by animateColorAsState(
                targetValue = if (selected) appColors.accent else appColors.textSecondary,
                animationSpec = tween(DurationFast),
                label = "tab_text"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pressedScale(targetScale = ScalePressedCard, interactionSource = itemSource)
                    .clip(itemShape)
                    .background(itemBg)
                    .then(
                        if (selected) {
                            Modifier.glassBorder(
                                shape = itemShape,
                                strokeWidth = StrokeWidthBorder,
                                startColor = appColors.accent.copy(alpha = 0.4f),
                                endColor = appColors.glassBorderSubtle
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clickable(
                        interactionSource = itemSource,
                        indication = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
