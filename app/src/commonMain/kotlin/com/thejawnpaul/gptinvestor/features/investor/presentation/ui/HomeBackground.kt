package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.interlaced
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.interlaced),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        ),
                        start = Offset(0.0f, 1500.0f),
                        end = Offset(0.0f, 00.0f)
                    )
                )
                .fillMaxSize()
        )
    }
}

@Preview
@Composable
fun HomeBackgroundPreview(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        HomeBackground(modifier = Modifier.fillMaxSize())
    }
}
