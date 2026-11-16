package com.example.p5.ui.components.player

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ActionButtonSizeLg
import com.example.p5.ui.theme.CornerRadiusModal
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.IconSizeLg
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ScaleAlbumPaused
import com.example.p5.ui.theme.ScaleAlbumPlaying
import com.example.p5.ui.theme.ScalePressedIcon
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlayerAlbumView(
    song: Song,
    isPlaying: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    accentColor: Color = ToscaPrimary,
    glowColor: Color = ToscaPrimary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val albumScale by animateFloatAsState(
            targetValue = if (isPlaying) ScaleAlbumPlaying else ScaleAlbumPaused,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = 300f),
            label = "album_scale"
        )

        val albumShape = remember { RoundedCornerShape(CornerRadiusModal) }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .scale(albumScale)
                .clip(albumShape)
                .background(ObsidianCardHigh)
                .glassBorder(
                    shape = albumShape,
                    strokeWidth = StrokeWidthBorder,
                    startColor = glowColor.copy(alpha = 0.55f),
                    endColor = GlassBorderSubtle
                )
        ) {
            AsyncImage(
                model = song.thumbnailUrl,
                contentDescription = song.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(CornerRadiusModal))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = song.title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = song.artist,
                    color = accentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = SpacingXs)
                )
            }

            val favSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(ActionButtonSizeLg)
                    .pressedScale(targetScale = ScalePressedIcon, interactionSource = favSource)
                    .clip(CircleShape)
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = CircleShape,
                        strokeWidth = StrokeWidthBorder,
                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                        endColor = GlassBorderSubtle
                    )
                    .clickable(
                        interactionSource = favSource,
                        indication = null,
                        onClick = onToggleFavorite
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) stringResource(R.string.library_remove_favorite_desc) else stringResource(R.string.library_add_favorite_desc),
                    tint = if (isFavorite) Color(0xFFFF3B30) else TextMuted,
                    modifier = Modifier.size(IconSizeLg)
                )
            }
        }
    }
}
