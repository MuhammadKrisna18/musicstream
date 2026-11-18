package com.example.p5.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun SettingsSectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val appColors = AppTheme.colors
    val cardShape = remember { RoundedCornerShape(CornerRadiusCard) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(appColors.card)
            .glassBorder(
                shape = cardShape,
                strokeWidth = StrokeWidthBorder,
                startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                endColor = appColors.glassBorderSubtle
            )
            .padding(SpacingMd)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(appColors.glassFillMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(IconSizeStandard)
                    )
                }

                Spacer(modifier = Modifier.width(SpacingSm))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = appColors.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = appColors.textSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}
