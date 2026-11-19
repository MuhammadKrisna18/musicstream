package com.example.p5.ui.library.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.domain.model.ActiveDownloadItem
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.theme.AppTheme
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.StrokeWidthBorder

@Composable
fun ActiveDownloadsSection(
    downloads: List<ActiveDownloadItem>,
    onCancelDownload: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (downloads.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        downloads.forEach { item ->
            ActiveDownloadCard(
                item = item,
                onCancel = { onCancelDownload(item.song.id) }
            )
        }
    }
}

@Composable
private fun ActiveDownloadCard(
    item: ActiveDownloadItem,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val cardShape = remember { RoundedCornerShape(CornerRadiusMd) }
    val cancelSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(appColors.cardHigh)
            .glassBorder(
                shape = cardShape,
                strokeWidth = StrokeWidthBorder,
                startColor = appColors.accent.copy(alpha = 0.35f),
                endColor = appColors.glassBorderSubtle
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(appColors.surfaceVariant)
        ) {
            AsyncImage(
                model = item.song.thumbnailUrl,
                contentDescription = item.song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.song.title,
                color = appColors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.song.artist,
                    color = appColors.textSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Text(
                    text = " • ",
                    color = appColors.textMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = if (item.progress > 0) "${item.progress}%" else "Menyiapkan...",
                    color = if (item.progress > 0) appColors.accent else appColors.accentSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val progressFloat = item.progress.coerceIn(0, 100) / 100f
            LinearProgressIndicator(
                progress = { progressFloat },
                color = appColors.accent,
                trackColor = appColors.glassBorderSubtle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(appColors.glassFillLight)
                .clickable(
                    interactionSource = cancelSource,
                    indication = null,
                    onClick = onCancel
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Batal Unduh",
                tint = appColors.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
