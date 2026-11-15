package com.example.p5.ui.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.domain.model.LibrarySortOrder
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun LibraryFilterBar(
    sortOrder: LibrarySortOrder,
    onSortOrderSelected: (LibrarySortOrder) -> Unit,
    artists: List<String>,
    selectedArtist: String?,
    onArtistSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val sortOptions = remember {
        listOf(
            LibrarySortOrder.LATEST_ADDED to "Terbaru",
            LibrarySortOrder.TITLE_AZ to "Judul A-Z",
            LibrarySortOrder.ARTIST_AZ to "Artis A-Z",
            LibrarySortOrder.DURATION to "Durasi"
        )
    }

    val chipShape = remember { RoundedCornerShape(16.dp) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(
                items = sortOptions,
                key = { it.first.name }
            ) { (order, label) ->
                val isSelected = sortOrder == order
                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .pressedScale(
                            targetScale = 0.94f,
                            interactionSource = interactionSource
                        )
                        .clip(chipShape)
                        .background(if (isSelected) ToscaPrimary else GlassFillLight)
                        .then(
                            if (!isSelected) {
                                Modifier.glassBorder(
                                    shape = chipShape,
                                    strokeWidth = 1.dp,
                                    startColor = GlassBorderBright.copy(alpha = 0.35f),
                                    endColor = GlassBorderSubtle
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onSortOrderSelected(order) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) ObsidianBlack else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        if (artists.size > 1) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item(key = "artist_all") {
                    val isAllSelected = selectedArtist == null
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .pressedScale(
                                targetScale = 0.94f,
                                interactionSource = interactionSource
                            )
                            .clip(chipShape)
                            .background(if (isAllSelected) ToscaPrimary else GlassFillLight)
                            .then(
                                if (!isAllSelected) {
                                    Modifier.glassBorder(
                                        shape = chipShape,
                                        strokeWidth = 1.dp,
                                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                                        endColor = GlassBorderSubtle
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onArtistSelected(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Semua Artis",
                            color = if (isAllSelected) ObsidianBlack else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }

                items(
                    items = artists,
                    key = { it }
                ) { artist ->
                    val isSelected = selectedArtist.equals(artist, ignoreCase = true)
                    val interactionSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .pressedScale(
                                targetScale = 0.94f,
                                interactionSource = interactionSource
                            )
                            .clip(chipShape)
                            .background(if (isSelected) ToscaPrimary else GlassFillLight)
                            .then(
                                if (!isSelected) {
                                    Modifier.glassBorder(
                                        shape = chipShape,
                                        strokeWidth = 1.dp,
                                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                                        endColor = GlassBorderSubtle
                                    )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                if (isSelected) {
                                    onArtistSelected(null)
                                } else {
                                    onArtistSelected(artist)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = artist,
                            color = if (isSelected) ObsidianBlack else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
