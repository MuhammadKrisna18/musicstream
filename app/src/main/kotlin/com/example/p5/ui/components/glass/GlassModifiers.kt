package com.example.p5.ui.components.glass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.p5.ui.theme.GlassBorderBright
import com.example.p5.ui.theme.GlassBorderMedium
import com.example.p5.ui.theme.GlassBorderSubtle
import com.example.p5.ui.theme.GlassFillMedium
import com.example.p5.ui.theme.GlassFillSubtle
import com.example.p5.ui.theme.ToscaGlow
import com.example.p5.ui.theme.ToscaPrimary

fun Modifier.glassBackground(
    shape: Shape = RoundedCornerShape(16.dp),
    topColor: Color = GlassFillMedium,
    bottomColor: Color = GlassFillSubtle
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor)
        ),
        shape = shape
    )

fun Modifier.glassBorder(
    shape: Shape = RoundedCornerShape(16.dp),
    strokeWidth: Dp = 1.dp,
    startColor: Color = GlassBorderBright,
    endColor: Color = GlassBorderSubtle,
    accentGlow: Color? = null
): Modifier {
    val brush = if (accentGlow != null) {
        Brush.linearGradient(
            colors = listOf(
                accentGlow,
                startColor,
                endColor
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                startColor,
                endColor
            )
        )
    }
    return this.border(
        width = strokeWidth,
        brush = brush,
        shape = shape
    )
}

fun Modifier.glassCard(
    shape: Shape = RoundedCornerShape(16.dp),
    topColor: Color = GlassFillMedium,
    bottomColor: Color = GlassFillSubtle,
    strokeWidth: Dp = 1.dp,
    borderColorStart: Color = GlassBorderMedium,
    borderColorEnd: Color = GlassBorderSubtle,
    accentBorder: Color? = null
): Modifier = this
    .glassBackground(
        shape = shape,
        topColor = topColor,
        bottomColor = bottomColor
    )
    .glassBorder(
        shape = shape,
        strokeWidth = strokeWidth,
        startColor = borderColorStart,
        endColor = borderColorEnd,
        accentGlow = accentBorder
    )

fun Modifier.ambientGlow(
    color: Color = ToscaGlow,
    alpha: Float = 0.35f,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier = this.drawBehind {
    drawRoundRect(
        color = color.copy(alpha = alpha),
        size = size
    )
}

@Composable
fun Modifier.pressedScale(
    targetScale: Float = 0.97f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) targetScale else 1f,
        animationSpec = spring(
            dampingRatio = 0.75f,
            stiffness = 380f
        ),
        label = "glass_press_scale"
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    topColor: Color = GlassFillMedium,
    bottomColor: Color = GlassFillSubtle,
    strokeWidth: Dp = 1.dp,
    borderColorStart: Color = GlassBorderMedium,
    borderColorEnd: Color = GlassBorderSubtle,
    accentBorder: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.glassCard(
            shape = shape,
            topColor = topColor,
            bottomColor = bottomColor,
            strokeWidth = strokeWidth,
            borderColorStart = borderColorStart,
            borderColorEnd = borderColorEnd,
            accentBorder = accentBorder
        ),
        content = content
    )
}

@Composable
fun MiniEqualizer(
    modifier: Modifier = Modifier,
    barColor: Color = ToscaPrimary,
    isPlaying: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    val bar1Height by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar1"
    )

    val bar2Height by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar2"
    )

    val bar3Height by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 520, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bar3"
    )

    Row(
        modifier = modifier.size(width = 18.dp, height = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(if (isPlaying) bar1Height else 0.35f)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(if (isPlaying) bar2Height else 0.7f)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor)
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(if (isPlaying) bar3Height else 0.45f)
                .clip(RoundedCornerShape(2.dp))
                .background(barColor)
        )
    }
}
