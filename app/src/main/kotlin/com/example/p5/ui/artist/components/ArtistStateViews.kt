package com.example.p5.ui.artist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p5.ui.theme.AppTheme

@Composable
fun ArtistThemeBackground(
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    val bgRes = appColors.backgroundImageRes ?: return

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.20f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            appColors.background.copy(alpha = 0.70f),
                            Color.Transparent,
                            appColors.background.copy(alpha = 0.90f)
                        )
                    )
                )
        )
    }
}

@Composable
fun ArtistLoadingView(
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = appColors.accent,
            strokeWidth = 3.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun ArtistErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = appColors.textMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.accent,
                    contentColor = appColors.onAccent
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Coba Lagi",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ArtistAllReleasesHeader(
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Semua Rilisan",
            color = appColors.textPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ArtistLoadMoreSection(
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = AppTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = appColors.accent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(28.dp)
            )
        } else {
            OutlinedButton(
                onClick = onLoadMore,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Muat Lebih Banyak Lagu",
                    color = appColors.textPrimary,
                    fontSize = 13.sp
                )
            }
        }
    }
}
