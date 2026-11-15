package com.example.p5.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun SearchHistorySection(
    searchHistory: List<String>,
    onSelectQuery: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (searchHistory.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = ToscaPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.search_recent_title),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = stringResource(R.string.search_clear_all),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onClearAll)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(searchHistory) { historyQuery ->
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
                        ) { onSelectQuery(historyQuery) }
                        .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = historyQuery,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .clickable { onDeleteItem(historyQuery) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.search_remove_item_desc),
                                tint = TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
