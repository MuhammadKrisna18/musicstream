package com.example.p5.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.ui.components.glass.ambientGlow
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillMedium
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianFloating
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaGlowSubtle
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun FloatingDock(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    favoriteCount: Int,
    modifier: Modifier = Modifier
) {
    val dockShape = remember { RoundedCornerShape(32.dp) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .ambientGlow(
                    color = ToscaGlowSubtle,
                    alpha = 0.5f,
                    shape = dockShape
                )
                .clip(dockShape)
                .background(ObsidianFloating)
                .glassBorder(
                    shape = dockShape,
                    strokeWidth = 1.dp,
                    startColor = GlassBorderBright.copy(alpha = 0.45f),
                    endColor = GlassBorderSubtle
                )
                .padding(horizontal = 10.dp, vertical = 7.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingDockItem(
                    label = "Beranda",
                    icon = Icons.Rounded.Home,
                    isSelected = selectedTab == 0,
                    badgeCount = 0,
                    onClick = { onTabSelected(0) }
                )

                FloatingDockItem(
                    label = "Koleksi",
                    icon = Icons.Rounded.LibraryMusic,
                    isSelected = selectedTab == 1,
                    badgeCount = favoriteCount,
                    onClick = { onTabSelected(1) }
                )
            }
        }
    }
}

@Composable
private fun FloatingDockItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val itemShape = remember { RoundedCornerShape(24.dp) }

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) ToscaPrimary else TextSecondary,
        animationSpec = tween(durationMillis = 200),
        label = "dock_item_content_color"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) ToscaPrimary.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "dock_item_bg_color"
    )

    val itemPaddingHorizontal by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 14.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "dock_item_padding"
    )

    Row(
        modifier = modifier
            .pressedScale(targetScale = 0.94f, interactionSource = interactionSource)
            .clip(itemShape)
            .background(backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.glassBorder(
                        shape = itemShape,
                        strokeWidth = 1.dp,
                        startColor = ToscaGlassBorder,
                        endColor = GlassBorderSubtle
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = itemPaddingHorizontal, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = ToscaPrimary,
                        contentColor = ObsidianBlack,
                        modifier = Modifier.size(16.dp)
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }

        if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

