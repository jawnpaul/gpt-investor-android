package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.math.hypot

// A short violet streak that travels around the edge, for the Ask composer while the AI is answering (and,
// per the spec, the one Pro element on a screen). Off with reduced motion.
// Colour and speed are placeholders until design shares the prototype's border-beam settings.
@Composable
fun Modifier.giBeam(active: Boolean, shape: Shape, strength: Float = DEFAULT_STRENGTH): Modifier {
    if (!active || isReducedMotionEnabled()) return this
    val head = GiTheme.colors.lilac.copy(alpha = strength)
    val tail = GiTheme.colors.violet.copy(alpha = strength)
    val transition = rememberInfiniteTransition()
    val angle by transition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_TURN,
        animationSpec = infiniteRepeatable(tween(durationMillis = LAP_MS, easing = LinearEasing))
    )
    return drawWithContent {
        drawContent()
        val stroke = BEAM_WIDTH.toPx()
        val outer = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawWithContent)) }
        val inner = Path().apply {
            addOutline(shape.createOutline(Size(size.width - stroke * 2, size.height - stroke * 2), layoutDirection, this@drawWithContent))
            translate(Offset(stroke, stroke))
        }
        val brush = Brush.sweepGradient(
            0f to Color.Transparent,
            STREAK_START to Color.Transparent,
            STREAK_BODY to tail,
            STREAK_HEAD to head,
            1f to Color.Transparent,
            center = center
        )
        clipPath(outer) {
            clipPath(inner, clipOp = ClipOp.Difference) {
                rotate(degrees = angle, pivot = center) {
                    drawCircle(brush = brush, radius = hypot(size.width, size.height), center = center)
                }
            }
        }
    }
}

private const val DEFAULT_STRENGTH = 0.6f
private const val LAP_MS = 2_500
private const val FULL_TURN = 360f
private const val STREAK_START = 0.72f
private const val STREAK_BODY = 0.92f
private const val STREAK_HEAD = 0.98f
private val BEAM_WIDTH = 1.5.dp

@PreviewLightDark
@Composable
private fun GiBeamPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(GiRadius.md)
                    .background(GiTheme.colors.raised)
                    .border(GiEdge.width, GiTheme.colors.line, GiRadius.md)
                    .giBeam(active = true, shape = GiRadius.md)
            )
        }
    }
}
