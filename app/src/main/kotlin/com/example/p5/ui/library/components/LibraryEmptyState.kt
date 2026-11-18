package com.example.p5.ui.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.ActionButtonSizeSm
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun LibraryEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(appColors.glassFillLight)
                .glassBorder(
                    shape = CircleShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                    endColor = appColors.glassBorderSubtle
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(ActionButtonSizeSm)
            )
        }

        Spacer(modifier = Modifier.height(SpacingLg))

        Text(
            text = title,
            color = appColors.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = description,
            color = appColors.textSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp)
        )
    }
}
