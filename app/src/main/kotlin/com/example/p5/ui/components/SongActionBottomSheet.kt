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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ArtworkTrackSize
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.CornerRadiusModal
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeLg
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.SpacingLg
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthHairline
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongActionBottomSheet(
    song: Song?,
    isSaved: Boolean,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onToggleSave: (Song) -> Unit,
    onToggleFavorite: (Song) -> Unit,
    onAddToQueueNext: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetShape = remember {
        RoundedCornerShape(topStart = CornerRadiusModal, topEnd = CornerRadiusModal)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = sheetShape,
        containerColor = ObsidianCardHigh,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 6.dp)
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(TextMuted.copy(alpha = 0.45f))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = SpacingLg, vertical = SpacingSm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(ArtworkTrackSize)
                        .clip(RoundedCornerShape(CornerRadiusMd))
                        .background(ObsidianSurfaceVariant)
                        .glassBorder(
                            shape = RoundedCornerShape(CornerRadiusMd),
                            strokeWidth = StrokeWidthHairline,
                            startColor = GlassBorderMedium,
                            endColor = Color.Transparent
                        )
                ) {
                    AsyncImage(
                        model = song.thumbnailUrl,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(ArtworkTrackSize)
                    )
                }

                Spacer(modifier = Modifier.width(SpacingMd))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val subtitleText = if (song.durationFormatted.isNotBlank()) {
                        "${song.artist} • ${song.durationFormatted}"
                    } else {
                        song.artist
                    }

                    Text(
                        text = subtitleText,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMd))

            HorizontalDivider(
                color = GlassBorderSubtle.copy(alpha = 0.45f),
                thickness = StrokeWidthHairline
            )

            Spacer(modifier = Modifier.height(SpacingSm))

            ActionSheetRow(
                icon = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                iconTint = if (isSaved) ToscaPrimary else TextPrimary,
                title = if (isSaved) "Hapus dari Tersimpan" else "Simpan Lagu",
                subtitle = if (isSaved) "Keluarkan lagu dari daftar tersimpan" else "Simpan lagu ke koleksi tersimpan",
                onClick = {
                    onToggleSave(song)
                    onDismiss()
                }
            )

            ActionSheetRow(
                icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                iconTint = if (isFavorite) ToscaPrimary else TextPrimary,
                title = if (isFavorite) "Hapus dari Favorit" else "Masukin ke Favorit",
                subtitle = if (isFavorite) "Keluarkan dari daftar favorit kamu" else "Tambahkan ke daftar lagu favorit",
                onClick = {
                    onToggleFavorite(song)
                    onDismiss()
                }
            )

            ActionSheetRow(
                icon = Icons.AutoMirrored.Rounded.QueueMusic,
                iconTint = TextPrimary,
                title = "Masuk ke antrean berikutnya",
                subtitle = "Putar tepat setelah lagu saat ini selesai",
                onClick = {
                    onAddToQueueNext(song)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(SpacingSm))
        }
    }
}

@Composable
private fun ActionSheetRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val rowShape = remember { RoundedCornerShape(CornerRadiusMd) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressedScale(targetScale = ScalePressedCard, interactionSource = interactionSource)
            .clip(rowShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = SpacingSm, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GlassFillLight)
                .glassBorder(
                    shape = CircleShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = GlassBorderBright.copy(alpha = 0.25f),
                    endColor = GlassBorderSubtle
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(IconSizeLg)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

