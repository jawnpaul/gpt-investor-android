package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

enum class GiBrandMarkSize(internal val box: Dp, internal val mark: Dp, internal val glow: Dp) {
    Hero(box = 120.dp, mark = 64.dp, glow = 180.dp),
    Small(box = 32.dp, mark = 24.dp, glow = 48.dp)
}

private const val SPIN_PERIOD_MS = 2400
private val SpinEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

@Composable
fun GiBrandMark(
    modifier: Modifier = Modifier,
    size: GiBrandMarkSize = GiBrandMarkSize.Hero,
    spinning: Boolean = false
) {
    val colors = GiTheme.colors
    val glow = colors.violet.copy(alpha = if (colors.isDark) 0.32f else 0.22f)
    val rotation = if (spinning && !isReducedMotionEnabled()) spinRotation() else null
    Box(
        modifier = modifier
            .size(size.box)
            .clearAndSetSemantics {}
            .drawWithCache {
                val radius = size.glow.toPx() / 2f
                val brush = Brush.radialGradient(
                    colors = listOf(glow, glow.copy(alpha = 0f)),
                    radius = radius
                )
                onDrawBehind { drawCircle(brush = brush, radius = radius) }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier
                .size(size.mark)
                .graphicsLayer { rotationZ = rotation?.value ?: 0f },
            tint = colors.ink
        )
    }
}

@Composable
private fun spinRotation(): State<Float> {
    val transition = rememberInfiniteTransition()
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SPIN_PERIOD_MS, easing = SpinEasing),
            repeatMode = RepeatMode.Restart
        )
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiBrandMarkPreview() {
    GiPreviewSurface {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x10),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiBrandMark()
            GiBrandMark(size = GiBrandMarkSize.Small, spinning = true)
        }
    }
}
