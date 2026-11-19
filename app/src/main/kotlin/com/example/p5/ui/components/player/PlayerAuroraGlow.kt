package com.example.p5.ui.components.player

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import com.example.p5.ui.theme.PlayerColorPalette

private const val AURORA_SHADER_SRC = """
    uniform float2 resolution;
    uniform float time;
    uniform float4 color1;
    uniform float4 color2;
    uniform float4 color3;
    uniform float4 colorBg;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / resolution;
        
        float wave1 = sin(uv.x * 2.5 + time * 0.45) * 0.22;
        float wave2 = cos(uv.y * 2.2 - time * 0.35) * 0.22;
        
        float dist1 = length(uv - float2(0.32 + wave1, 0.22 + wave2));
        float dist2 = length(uv - float2(0.68 - wave2, 0.42 + wave1));
        float dist3 = length(uv - float2(0.50 + wave2 * 0.4, 0.72 - wave1 * 0.4));
        
        float glow1 = smoothstep(0.85, 0.05, dist1);
        float glow2 = smoothstep(0.75, 0.05, dist2);
        float glow3 = smoothstep(0.80, 0.05, dist3);
        
        float4 col = colorBg;
        col = mix(col, color1, glow1 * 0.65);
        col = mix(col, color2, glow2 * 0.50);
        col = mix(col, color3, glow3 * 0.35);
        
        float fadeBottom = smoothstep(0.15, 0.95, uv.y);
        col = mix(col, colorBg, fadeBottom * 0.75);
        
        return half4(col);
    }
"""

@Composable
fun PlayerAuroraGlow(
    palette: PlayerColorPalette,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AgslPlayerAuroraGlow(
            palette = palette,
            backgroundColor = backgroundColor,
            modifier = modifier
        )
    } else {
        FallbackPlayerAuroraGlow(
            palette = palette,
            backgroundColor = backgroundColor,
            modifier = modifier
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AgslPlayerAuroraGlow(
    palette: PlayerColorPalette,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "agsl_aurora_time")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 62.83f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val shader = remember { RuntimeShader(AURORA_SHADER_SRC) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                shader.setFloatUniform("resolution", size.width, size.height)
                shader.setFloatUniform("time", time)
                shader.setFloatUniform(
                    "color1",
                    palette.darkVibrant.red,
                    palette.darkVibrant.green,
                    palette.darkVibrant.blue,
                    palette.darkVibrant.alpha
                )
                shader.setFloatUniform(
                    "color2",
                    palette.vibrant.red,
                    palette.vibrant.green,
                    palette.vibrant.blue,
                    palette.vibrant.alpha
                )
                shader.setFloatUniform(
                    "color3",
                    palette.lightVibrant.red,
                    palette.lightVibrant.green,
                    palette.lightVibrant.blue,
                    palette.lightVibrant.alpha
                )
                shader.setFloatUniform(
                    "colorBg",
                    backgroundColor.red,
                    backgroundColor.green,
                    backgroundColor.blue,
                    backgroundColor.alpha
                )
                val brush = ShaderBrush(shader)
                onDrawBehind {
                    drawRect(brush)
                }
            }
    )
}

@Composable
private fun FallbackPlayerAuroraGlow(
    palette: PlayerColorPalette,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fallback_aurora")
    val auroraOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            palette.darkVibrant.copy(alpha = 0.70f),
                            backgroundColor.copy(alpha = 0.88f),
                            backgroundColor
                        ),
                        startY = auroraOffset,
                        endY = 1600f
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            palette.vibrant.copy(alpha = 0.20f),
                            palette.lightVibrant.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        radius = 650f
                    )
                )
        )
    }
}
