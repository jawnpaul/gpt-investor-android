package com.thejawnpaul.gptinvestor.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.offline_opening_saved
import com.thejawnpaul.gptinvestor.splash_tagline
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private const val SPLASH_DURATION_MS = 2000L
private const val WORDMARK_DELAY_MS = 150
private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
private val SplashMarkSize = 88.dp
private val SplashHaloSize = 220.dp
private val SplashGlowSize = 300.dp
private val SplashInnerRingSize = 148.dp
private val SplashRingDash = 3.dp
private const val GLOW_ALPHA_LIGHT = 0.28f
private const val GLOW_ALPHA_DARK = 0.38f
private const val RING_ALPHA_LIGHT = 0.18f
private const val RING_ALPHA_DARK = 0.24f

@Composable
fun AnimatedSplashScreen(onSplashFinish: () -> Unit, modifier: Modifier = Modifier, isOffline: Boolean = false) {
    val colors = GiTheme.colors
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
        SplashMark()
        Column(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val belowMark = (SplashHaloSize / 2 + GiSpace.x7).roundToPx()
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
            GiText(
                text = stringResource(Res.string.gpt_investor),
                style = GiTextStyle.ScreenTitle,
                textAlign = TextAlign.Center
            )
            // 15/22 regular: labelRegular's metrics at body weight.
            GiText(
                text = stringResource(Res.string.splash_tagline),
                style = GiTextStyle.LabelRegular,
                color = colors.muted,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
        if (isOffline) {
            SplashOfflineStatus(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

// LiveOfflineSplash: a quiet status line pinned to the bottom while the app opens cached data.
@Composable
private fun SplashOfflineStatus(modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x4)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.wifiSlash,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.sm),
            tint = colors.muted
        )
        GiText(
            text = stringResource(Res.string.offline_opening_saved),
            style = GiTextStyle.Meta,
            color = colors.muted,
            textAlign = TextAlign.Center
        )
    }
}

// Mark inside a soft violet glow with a dashed outer ring and a solid inner ring.
@Composable
private fun SplashMark(modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val glow = colors.violet.copy(alpha = if (colors.isDark) GLOW_ALPHA_DARK else GLOW_ALPHA_LIGHT)
    val ring = colors.violet.copy(alpha = if (colors.isDark) RING_ALPHA_DARK else RING_ALPHA_LIGHT)
    Box(
        modifier = modifier
            .size(SplashHaloSize)
            .drawWithCache {
                val glowRadius = SplashGlowSize.toPx() / 2f
                val glowBrush = Brush.radialGradient(
                    colors = listOf(glow, glow.copy(alpha = 0f)),
                    radius = glowRadius
                )
                val strokeWidth = GiEdge.width.toPx()
                val dash = SplashRingDash.toPx()
                val dashed =
                    Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)))
                val solid = Stroke(width = strokeWidth)
                val outerRadius = (size.minDimension - strokeWidth) / 2f
                val innerRadius = (SplashInnerRingSize.toPx() - strokeWidth) / 2f
                onDrawBehind {
                    drawCircle(brush = glowBrush, radius = glowRadius)
                    drawCircle(color = ring, radius = outerRadius, style = dashed)
                    drawCircle(color = ring, radius = innerRadius, style = solid)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(SplashMarkSize),
            tint = colors.ink
        )
    }
}

@PreviewLightDark
@Composable
private fun AnimatedSplashScreenPreview() {
    GPTInvestorTheme {
        AnimatedSplashScreen(onSplashFinish = {})
    }
}

@PreviewLightDark
@Composable
private fun AnimatedSplashScreenOfflinePreview() {
    GPTInvestorTheme {
        AnimatedSplashScreen(onSplashFinish = {}, isOffline = true)
    }
}
