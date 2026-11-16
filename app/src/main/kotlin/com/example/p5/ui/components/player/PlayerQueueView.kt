package com.example.p5.ui.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.p5.R
import com.example.p5.domain.model.Song
import com.example.p5.ui.components.glass.MiniEqualizer
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.ArtworkQueueThumbSize
import com.example.p5.ui.theme.CornerRadiusMd
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.IconSizeLg
import com.example.p5.ui.theme.ScalePressedGlass
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.StrokeWidthStandard
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaGlassBorder
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun PlayerQueueView(
    queue: List<Song>,
    currentQueueIndex: Int,
    isPlaying: Boolean,
    onPlayQueueIndex: (Int) -> Unit,
    accentColor: Color = ToscaPrimary,
    modifier: Modifier = Modifier
) {
    val queueShape = remember { RoundedCornerShape(CornerRadiusPill) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(queueShape)
            .background(GlassFillLight)
            .glassBorder(
                shape = queueShape,
                strokeWidth = StrokeWidthBorder,
                startColor = GlassBorderBright.copy(alpha = 0.35f),
                endColor = GlassBorderSubtle
            )
            .padding(14.dp)
    ) {
        Text(
            text = stringResource(R.string.player_queue_title, queue.size),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = SpacingSm, vertical = 6.dp)
        )

        if (queue.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.player_queue_empty),
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(queue) { index, song ->
                    val isCurrent = index == currentQueueIndex
                    val rowShape = remember { RoundedCornerShape(CornerRadiusMd) }
                    val rowSource = remember { MutableInteractionSource() }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressedScale(targetScale = ScalePressedGlass, interactionSource = rowSource)
                            .clip(rowShape)
                            .background(if (isCurrent) accentColor.copy(alpha = 0.14f) else GlassFillSubtle)
                            .then(
                                if (isCurrent) {
                                    Modifier.glassBorder(
                                        shape = rowShape,
                                        strokeWidth = StrokeWidthBorder,
                                        startColor = accentColor.copy(alpha = 0.40f),
                                        endColor = GlassBorderSubtle
                                    )
                                } else {
                                    Modifier.glassBorder(
                                        shape = rowShape,
                                        strokeWidth = StrokeWidthStandard,
                                        startColor = GlassBorderBright.copy(alpha = 0.2f),
                                        endColor = Color.Transparent
                                    )
                                }
                            )
                            .clickable(
                                interactionSource = rowSource,
                                indication = null
                            ) { onPlayQueueIndex(index) }
                            .padding(horizontal = 10.dp, vertical = SpacingSm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(IconSizeLg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent && isPlaying) {
                                MiniEqualizer(isPlaying = true, barColor = accentColor)
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isCurrent) accentColor else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(SpacingSm))

                        AsyncImage(
                            model = song.thumbnailUrl,
                            contentDescription = song.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(ArtworkQueueThumbSize)
                                .clip(RoundedCornerShape(CornerRadiusSm))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                color = if (isCurrent) accentColor else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = song.artist,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(SpacingSm))

                        Text(
                            text = song.durationFormatted,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
