package com.example.p5.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.example.p5.domain.model.SearchFilter
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme

@Composable
fun SearchFilterBar(
    selectedFilter: SearchFilter,
    onSelectFilter: (SearchFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val filterOptions = remember {
        listOf(
            SearchFilter.ALL to "Semua",
            SearchFilter.SONGS to "Lagu",
            SearchFilter.ALBUMS to "Album",
            SearchFilter.PLAYLISTS to "Playlist",
            SearchFilter.ARTISTS to "Artis"
        )
    }

    val chipShape = remember { RoundedCornerShape(16.dp) }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(
            items = filterOptions,
            key = { it.first.name }
        ) { (filter, label) ->
            val isSelected = selectedFilter == filter
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .pressedScale(
                        targetScale = 0.94f,
                        interactionSource = interactionSource
                    )
                    .clip(chipShape)
                    .background(if (isSelected) appColors.accent else appColors.glassFillLight)
                    .then(
                        if (!isSelected) {
                            Modifier.glassBorder(
                                shape = chipShape,
                                strokeWidth = 1.dp,
                                startColor = appColors.glassBorderBright.copy(alpha = 0.35f),
                                endColor = appColors.glassBorderSubtle
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelectFilter(filter) }
                    )
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) appColors.onAccent else appColors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
