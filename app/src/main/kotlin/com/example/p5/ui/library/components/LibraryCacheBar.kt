package com.example.p5.ui.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
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
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.CornerRadiusDefault
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.IconSizeSm
import com.example.p5.ui.theme.ScalePressedButton
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary
import java.util.Locale

@Composable
fun LibraryCacheBar(
    cacheSizeBytes: Long,
    onClearCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cacheShape = remember { RoundedCornerShape(CornerRadiusDefault) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cacheShape)
            .background(GlassFillSubtle)
            .glassBorder(
                shape = cacheShape,
                strokeWidth = StrokeWidthStandard,
                startColor = GlassBorderBright.copy(alpha = 0.25f),
                endColor = Color.Transparent
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Storage,
                contentDescription = null,
                tint = ToscaPrimary,
                modifier = Modifier.size(IconSizeSm)
            )
            Spacer(modifier = Modifier.width(SpacingSm))
            Text(
                text = stringResource(R.string.library_cache_label, formatCacheBytes(cacheSizeBytes)),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (cacheSizeBytes > 0) {
            val clearSource = remember { MutableInteractionSource() }
            Text(
                text = stringResource(R.string.library_cache_clear),
                color = ToscaPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .pressedScale(targetScale = ScalePressedButton, interactionSource = clearSource)
                    .clip(RoundedCornerShape(CornerRadiusSm))
                    .clickable(
                        interactionSource = clearSource,
                        indication = null,
                        onClick = onClearCache
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

private fun formatCacheBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format(Locale.getDefault(), "%.1f MB", mb)
    } else {
        String.format(Locale.getDefault(), "%.1f KB", kb)
    }
}
