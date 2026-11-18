package com.example.p5.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.R
import com.example.p5.domain.model.AppThemeId
import com.example.p5.domain.model.ThemeCategory
import com.example.p5.ui.components.glass.glassBorder
import com.example.p5.ui.components.glass.pressedScale
import com.example.p5.ui.theme.AppThemePalette
import com.example.p5.ui.theme.CornerRadiusCard
import com.example.p5.ui.theme.CornerRadiusPill
import com.example.p5.ui.theme.CornerRadiusSm
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillLight
import com.example.p5.ui.theme.GlassFillMedium
import com.example.p5.ui.theme.IconSizeStandard
import com.example.p5.ui.theme.ObsidianBlack
import com.example.p5.ui.theme.ObsidianFloating
import com.example.p5.ui.theme.ScalePressedCard
import com.example.p5.ui.theme.SpacingMd
import com.example.p5.ui.theme.SpacingSm
import com.example.p5.ui.theme.SpacingXs
import com.example.p5.ui.theme.StrokeWidthBorder
import com.example.p5.ui.theme.TextMuted
import com.example.p5.ui.theme.TextPrimary
import com.example.p5.ui.theme.TextSecondary
import com.example.p5.ui.theme.ToscaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectorBottomSheet(
    selectedTheme: AppThemeId,
    onThemeSelected: (AppThemeId) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf<ThemeCategory?>(null) }

    val categories = listOf(
        null to stringResource(R.string.theme_tab_all),
        ThemeCategory.DEFAULT to stringResource(R.string.theme_tab_default),
        ThemeCategory.ANIME to stringResource(R.string.theme_tab_anime),
        ThemeCategory.MINIMALIST to stringResource(R.string.theme_tab_minimalist)
    )

    val displayedThemes = remember(selectedCategory) {
        if (selectedCategory == null) {
            AppThemeId.entries
        } else {
            AppThemeId.entries.filter { it.category == selectedCategory }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianFloating,
        scrimColor = ObsidianBlack.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = SpacingMd)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingMd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.theme_sheet_title),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.dialog_close),
                        tint = TextSecondary,
                        modifier = Modifier.size(IconSizeStandard)
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingSm))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = SpacingMd),
                horizontalArrangement = Arrangement.spacedBy(SpacingSm)
            ) {
                items(categories) { (category, label) ->
                    val isSelected = selectedCategory == category
                    val chipSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .pressedScale(targetScale = 0.95f, interactionSource = chipSource)
                            .clip(RoundedCornerShape(CornerRadiusPill))
                            .background(if (isSelected) ToscaPrimary.copy(alpha = 0.18f) else GlassFillLight)
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusPill),
                                strokeWidth = StrokeWidthBorder,
                                startColor = if (isSelected) ToscaPrimary else GlassBorderBright.copy(alpha = 0.25f),
                                endColor = GlassBorderSubtle
                            )
                            .clickable(
                                interactionSource = chipSource,
                                indication = null,
                                onClick = { selectedCategory = category }
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) ToscaPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentPadding = PaddingValues(horizontal = SpacingMd, vertical = SpacingXs),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedThemes) { theme ->
                    val isSelected = theme == selectedTheme
                    val palette = AppThemePalette.getPalette(theme)
                    val itemSource = remember { MutableInteractionSource() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pressedScale(targetScale = ScalePressedCard, interactionSource = itemSource)
                            .clip(RoundedCornerShape(CornerRadiusCard))
                            .background(if (isSelected) palette.accent.copy(alpha = 0.12f) else GlassFillMedium)
                            .glassBorder(
                                shape = RoundedCornerShape(CornerRadiusCard),
                                strokeWidth = StrokeWidthBorder,
                                startColor = if (isSelected) palette.accent else GlassBorderBright.copy(alpha = 0.30f),
                                endColor = GlassBorderSubtle,
                                accentGlow = if (isSelected) palette.accentGlow else null
                            )
                            .clickable(
                                interactionSource = itemSource,
                                indication = null,
                                onClick = {
                                    onThemeSelected(theme)
                                    onDismiss()
                                }
                            )
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(CornerRadiusSm))
                                        .background(palette.surfaceVariant)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = theme.category.name,
                                        color = palette.accentLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = palette.accent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = theme.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(palette.background)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(palette.surface)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(palette.accent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
