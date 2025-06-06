package com.thejawnpaul.gptinvestor.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import kotlinx.coroutines.delay

// Simulated duration for the splash screen
private const val SPLASH_DURATION_MS = 2000L
private const val FADE_IN_DURATION_MS = 1500
private const val GLOW_ANIMATION_DURATION_MS = 2000

@Composable
fun AnimatedSplashScreen(onSplashFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(0f) }

    // For the glowing effect on the logo
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlowTransition")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f, // How much the glow expands
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = GLOW_ANIMATION_DURATION_MS / 2,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f, // Base alpha of the glow
        targetValue = 1f, // Peak alpha of the glow
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = GLOW_ANIMATION_DURATION_MS / 2,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoGlowAlpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = FADE_IN_DURATION_MS)
        )
        delay(SPLASH_DURATION_MS - FADE_IN_DURATION_MS) // Wait for the remaining duration
        onSplashFinished()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        contentColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Wavy Background Image (Bottom)
            Image(
                painter = painterResource(id = R.drawable.wavy_background),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 160.dp)
                    .fillMaxHeight(0.4f) // Adjust height as needed
                    .align(Alignment.BottomCenter)
                    .alpha(0.5f) // Make it subtle

            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1.3f)) // Pushes content down a bit

                // Glowing Logo
                Box(contentAlignment = Alignment.Center) {
                    // Main Logo
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "GPT Investor Logo",
                        modifier = Modifier
                            .size(60.dp)
                            .graphicsLayer(
                                scaleX = glowScale,
                                scaleY = glowScale,
                                alpha = glowAlpha
                            )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.gpt_investor),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.weight(1f)) // Pushes the bottom text down

                Text(
                    text = "Your financial assistant",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AnimatedSplashScreenPreview() {
    GPTInvestorTheme {
        AnimatedSplashScreen(onSplashFinished = { })
    }
}
