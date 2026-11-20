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
import com.example.p5.domain.model.Category
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppTheme

@Composable
fun GenreFilterRow(
    genres: List<Category>,
    selectedGenre: Category?,
    onSelectGenre: (Category?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (genres.isEmpty()) return

    val appColors = AppTheme.colors
    val chipShape = remember { RoundedCornerShape(20.dp) }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        item(key = "genre_chip_all") {
            val isAllSelected = selectedGenre == null
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .pressedScale(
                        targetScale = 0.94f,
                        interactionSource = interactionSource
                    )
                    .clip(chipShape)
                    .background(if (isAllSelected) appColors.accent else appColors.glassFillLight)
                    .glassBorder(
                        shape = chipShape,
                        strokeWidth = 1.dp,
                        startColor = if (isAllSelected) appColors.accentLight.copy(alpha = 0.45f) else appColors.glassBorderBright.copy(alpha = 0.35f),
                        endColor = if (isAllSelected) appColors.accentDark.copy(alpha = 0.2f) else appColors.glassBorderSubtle
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onSelectGenre(null) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Semua",
                    color = if (isAllSelected) appColors.onAccent else appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }

        items(
            items = genres,
            key = { it.id }
        ) { genre ->
            val isSelected = selectedGenre?.id == genre.id
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .pressedScale(
                        targetScale = 0.94f,
                        interactionSource = interactionSource
                    )
                    .clip(chipShape)
                    .background(if (isSelected) appColors.accent else appColors.glassFillLight)
                    .glassBorder(
                        shape = chipShape,
                        strokeWidth = 1.dp,
                        startColor = if (isSelected) appColors.accentLight.copy(alpha = 0.45f) else appColors.glassBorderBright.copy(alpha = 0.35f),
                        endColor = if (isSelected) appColors.accentDark.copy(alpha = 0.2f) else appColors.glassBorderSubtle
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (isSelected) {
                            onSelectGenre(null)
                        } else {
                            onSelectGenre(genre)
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = genre.title,
                    color = if (isSelected) appColors.onAccent else appColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
