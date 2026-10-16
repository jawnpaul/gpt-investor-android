package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.offset
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

@Composable
fun GiComposerBar(modifier: Modifier = Modifier, docked: Boolean = false, content: @Composable () -> Unit) {
    val colors = GiTheme.colors
    val bg = colors.bg
    val line = colors.line
    val fade = remember(bg) { Brush.verticalGradient(0f to bg.copy(alpha = 0f), FADE_END to bg) }
    val reducedMotion = isReducedMotionEnabled()
    val dock = animateFloatAsState(
        targetValue = if (docked) 1f else 0f,
        animationSpec = if (reducedMotion) snap() else tween(GiMotion.BASE, easing = EaseOutQuart)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val progress = dock.value
                if (progress < 1f) drawRect(fade, alpha = 1f - progress)
                if (progress > 0f) {
                    drawRect(bg, alpha = progress)
                    val stroke = GiEdge.width.toPx()
                    drawLine(
                        color = line,
                        start = Offset(0f, stroke / 2),
                        end = Offset(size.width, stroke / 2),
                        strokeWidth = stroke,
                        alpha = progress
                    )
                }
            }
            .then(modifier)
            .layout { measurable, constraints ->
                val top = lerp(GiSpace.x6, GiSpace.x3, dock.value).roundToPx()
                val placeable = measurable.measure(constraints.offset(vertical = -top))
                layout(placeable.width, placeable.height + top) { placeable.place(0, top) }
            }
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x4)
    ) {
        content()
    }
}

private const val FADE_END = 0.32f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiComposerBarPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiComposerBar {
                GiAskComposer(
                    value = "",
                    onValueChange = {},
                    onSend = {},
                    placeholder = "Ask anything about Johnson & Johnson",
                    sendContentDescription = "Send",
                    largeTextMaxLines = 3
                )
            }
            GiComposerBar(docked = true) {
                GiAskComposer(
                    value = "",
                    onValueChange = {},
                    onSend = {},
                    placeholder = "Ask about Johnson & Johnson…",
                    sendContentDescription = "Send",
                    floating = false,
                    largeTextMaxLines = 3
                )
            }
        }
    }
}
