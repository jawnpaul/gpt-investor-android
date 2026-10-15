package com.thejawnpaul.gptinvestor.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.financial_clarity_powered_by_ai
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val SPLASH_DURATION_MS = 2000L
private const val WORDMARK_DELAY_MS = 150
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val SplashMarkSize = 80.dp

@Composable
fun AnimatedSplashScreen(onSplashFinish: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val reducedMotion = isReducedMotionEnabled()
    val reveal = remember { Animatable(if (reducedMotion) 1f else 0f) }
    val rise = with(LocalDensity.current) { GiSpace.x2.toPx() }

    LaunchedEffect(onSplashFinish) {
        reveal.animateTo(
            targetValue = 1f,
            animationSpec = tween(GiMotion.SLOW, delayMillis = WORDMARK_DELAY_MS, easing = EaseOutQuart)
        )
        delay(SPLASH_DURATION_MS - GiMotion.SLOW - WORDMARK_DELAY_MS)
        onSplashFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(SplashMarkSize),
            tint = colors.ink
        )
        Column(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val belowMark = (SplashMarkSize / 2 + GiSpace.x6).roundToPx()
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, placeable.height / 2 + belowMark)
                    }
                }
                .padding(horizontal = GiSpace.gutter)
                .graphicsLayer {
                    alpha = reveal.value
                    translationY = (1f - reveal.value) * rise
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
        ) {
            Text(
                text = stringResource(Res.string.gpt_investor),
                color = colors.ink,
                textAlign = TextAlign.Center,
                style = type.screenTitle
            )
            Text(
                text = stringResource(Res.string.financial_clarity_powered_by_ai),
                color = colors.muted,
                textAlign = TextAlign.Center,
                style = type.body
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AnimatedSplashScreenPreview() {
    GPTInvestorTheme {
        AnimatedSplashScreen(onSplashFinish = {})
    }
}
