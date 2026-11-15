package com.example.p5.ui.search.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Nightlife
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.Category
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.CategoryCardHeight
import com.example.p5.ui.theme.CornerRadiusCardLg
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.IconSizeHero
import com.example.p5.ui.theme.ObsidianCardHigh
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun CategoryGridSection(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.categories_title),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
        )

        categories.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GenreCategoryCard(
                    category = pair[0],
                    onClick = { onCategoryClick(pair[0]) },
                    modifier = Modifier.weight(1f)
                )
                if (pair.size > 1) {
                    GenreCategoryCard(
                        category = pair[1],
                        onClick = { onCategoryClick(pair[1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun GenreCategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = parseHexColor(category.startColor, ToscaPrimary)
    val icon = getCategoryIcon(category.icon)
    val cardShape = remember { RoundedCornerShape(CornerRadiusCardLg) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(CategoryCardHeight)
            .pressedScale(targetScale = ScalePressedCard, interactionSource = interactionSource)
            .clip(cardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0.3f),
                        ObsidianCardHigh
                    )
                )
            )
            .glassBorder(
                shape = cardShape,
                strokeWidth = StrokeWidthBorder,
                startColor = baseColor.copy(alpha = 0.45f),
                endColor = GlassBorderSubtle
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = baseColor.copy(alpha = 0.55f),
            modifier = Modifier
                .size(IconSizeHero)
                .align(Alignment.BottomEnd)
        )

        Text(
            text = category.title,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 19.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth(0.72f)
        )
    }
}

private fun parseHexColor(hexString: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexString))
    } catch (_: Exception) {
        fallback
    }
}

private fun getCategoryIcon(iconKey: String): ImageVector {
    return when (iconKey.lowercase()) {
        "fire", "whatshot" -> Icons.Rounded.Whatshot
        "electric_bolt", "rock" -> Icons.Rounded.ElectricBolt
        "nightlife", "chill" -> Icons.Rounded.Nightlife
        "music_note", "pop" -> Icons.Rounded.MusicNote
        "headphones", "indie" -> Icons.Rounded.Headphones
        "bolt", "workout", "edm" -> Icons.Rounded.Bolt
        "library_music", "acoustic" -> Icons.Rounded.LibraryMusic
        "graphic_eq", "jazz" -> Icons.Rounded.GraphicEq
        else -> Icons.Rounded.MusicNote
    }
}
