package com.example.p5.ui.search.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.p5.ui.theme.DurationShimmer
import com.example.p5.ui.theme.ObsidianSurfaceVariant
import com.example.p5.ui.theme.ToscaPrimary

@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = DurationShimmer, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = listOf(
            ObsidianSurfaceVariant.copy(alpha = 0.5f),
            ObsidianSurfaceVariant.copy(alpha = 0.85f),
            ToscaPrimary.copy(alpha = 0.16f),
            ObsidianSurfaceVariant.copy(alpha = 0.85f),
            ObsidianSurfaceVariant.copy(alpha = 0.5f)
        ),
        start = Offset(translateAnim, translateAnim),
        end = Offset(translateAnim + 260f, translateAnim + 260f)
    )
}

fun LazyListScope.searchShimmerSkeleton(shimmerBrush: Brush) {
    items(3) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(shimmerBrush)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(4) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(210.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(shimmerBrush)
                    )
                }
            }
        }
    }
}
