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
import com.example.p5.domain.model.MoodTag
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun MoodTagsRow(
    moodTags: List<MoodTag>,
    onSelectMoodTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (moodTags.isEmpty()) return

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(moodTags) { tag ->
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .pressedScale(
                        targetScale = 0.94f,
                        interactionSource = interactionSource
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassFillLight)
                    .glassBorder(
                        shape = RoundedCornerShape(20.dp),
                        strokeWidth = 1.dp,
                        startColor = GlassBorderBright.copy(alpha = 0.35f),
                        endColor = GlassBorderSubtle
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onSelectMoodTag(tag.query) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = tag.label,
                    color = ToscaPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
