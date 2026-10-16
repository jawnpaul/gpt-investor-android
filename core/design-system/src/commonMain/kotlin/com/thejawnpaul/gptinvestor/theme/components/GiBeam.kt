package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

enum class GiBeamStyle { Travel, Pulse }

@Composable
fun Modifier.giBeam(
    active: Boolean,
    shape: Shape,
    strength: Float = DEFAULT_STRENGTH,
    style: GiBeamStyle = GiBeamStyle.Travel
): Modifier {
    if (isReducedMotionEnabled()) return this
    val presence = animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = if (active) {
            tween(GiMotion.BASE, easing = EaseOutQuart)
        } else {
            tween(GiMotion.FAST, easing = LinearEasing)
        }
    )
    val visible by remember { derivedStateOf { presence.value > 0f } }
    if (!visible) return this
    val transition = rememberInfiniteTransition()
    return when (style) {
        GiBeamStyle.Travel -> {
            val angle = transition.animateFloat(
                initialValue = 0f,
                targetValue = FULL_TURN,
                animationSpec = infiniteRepeatable(tween(durationMillis = LAP_MS, easing = LinearEasing))
            )
            beamRings(shape = shape, alpha = { strength * presence.value }, rotation = { angle.value }, brush = ::travelBrush)
        }
        GiBeamStyle.Pulse -> {
            val alpha = transition.animateFloat(
                initialValue = PULSE_FLOOR * strength,
                targetValue = strength,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = PULSE_HALF_MS, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                )
            )
            beamRings(shape = shape, alpha = { alpha.value * presence.value }, rotation = null, brush = ::pulseBrush)
        }
    }
}

private fun Modifier.beamRings(shape: Shape, alpha: () -> Float, rotation: (() -> Float)?, brush: (Size) -> Brush): Modifier =
    drawWithCache {
        val ring = ringPath(shape, BEAM_WIDTH.toPx())
        val halo = HALO_STEPS.map { ringPath(shape, it.toPx()) }
        val fill = brush(size)
        val reach = hypot(size.width, size.height)
        onDrawWithContent {
            drawContent()
            val beamAlpha = alpha()
            val haloAlpha = beamAlpha * HALO_ALPHA / HALO_STEPS.size
            if (rotation == null) {
                halo.fastForEach { drawPath(path = it, brush = fill, alpha = haloAlpha) }
                drawPath(path = ring, brush = fill, alpha = beamAlpha)
            } else {
                val degrees = rotation()
                halo.fastForEach { drawSwept(it, fill, haloAlpha, degrees, reach) }
                drawSwept(ring, fill, beamAlpha, degrees, reach)
            }
        }
    }

private fun DrawScope.drawSwept(path: Path, brush: Brush, alpha: Float, degrees: Float, radius: Float) {
    clipPath(path) {
        rotate(degrees) { drawCircle(brush = brush, radius = radius, center = center, alpha = alpha) }
    }
}

private fun CacheDrawScope.ringPath(shape: Shape, outset: Float): Path {
    val outer = Path().apply {
        addOutline(shape.createOutline(Size(size.width + outset * 2, size.height + outset * 2), layoutDirection, this@ringPath))
        translate(Offset(-outset, -outset))
    }
    val inner = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@ringPath)) }
    return Path.combine(PathOperation.Difference, outer, inner)
}

private fun travelBrush(size: Size): Brush = Brush.sweepGradient(
    0f to Color.Transparent,
    TRAVEL_FADE to Color.Transparent,
    TRAVEL_STOP_1 to BEAM_BLUE,
    TRAVEL_STOP_2 to BEAM_VIOLET,
    TRAVEL_STOP_3 to BEAM_LILAC,
    1f to Color.Transparent,
    center = size.center
)

private fun pulseBrush(size: Size): Brush {
    val radians = PULSE_ANGLE * PI.toFloat() / HALF_TURN
    val direction = Offset(sin(radians), -cos(radians))
    val half = (abs(size.width * direction.x) + abs(size.height * direction.y)) / 2f
    return Brush.linearGradient(
        colors = listOf(BEAM_BLUE, BEAM_VIOLET, BEAM_LILAC),
        start = size.center - direction * half,
        end = size.center + direction * half
    )
}

private const val DEFAULT_STRENGTH = 0.6f
private const val LAP_MS = 1_960
private const val PULSE_HALF_MS = 1_600
private const val PULSE_FLOOR = 0.3f
private const val PULSE_ANGLE = 120f
private const val HALO_ALPHA = 0.55f
private const val FULL_TURN = 360f
private const val HALF_TURN = 180f
private const val TRAVEL_FADE = 220f / FULL_TURN
private const val TRAVEL_STOP_1 = 285f / FULL_TURN
private const val TRAVEL_STOP_2 = 320f / FULL_TURN
private const val TRAVEL_STOP_3 = 348f / FULL_TURN
private val BEAM_WIDTH = 1.5.dp
private val HALO_STEPS = listOf(2.5.dp, 5.dp, 7.5.dp, 10.dp)
private val BEAM_BLUE = Color(0xFF5B7CFF)
private val BEAM_VIOLET = Color(0xFF8B5CF6)
private val BEAM_LILAC = Color(0xFFA994FF)

@PreviewLightDark
@Composable
private fun GiBeamPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x6)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .giBeam(active = true, shape = GiRadius.md)
                    .clip(GiRadius.md)
                    .background(GiTheme.colors.raised)
                    .border(GiEdge.width, GiTheme.colors.line, GiRadius.md)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .giBeam(active = true, shape = GiRadius.full, style = GiBeamStyle.Pulse)
                    .clip(GiRadius.full)
                    .background(GiTheme.colors.primary)
            )
        }
    }
}
