package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiComposerBar(modifier: Modifier = Modifier, docked: Boolean = false, content: @Composable () -> Unit) {
    val colors = GiTheme.colors
    val bg = colors.bg
    val surface = if (docked) {
        val line = colors.line
        Modifier
            .background(bg)
            .drawBehind {
                val stroke = GiEdge.width.toPx()
                drawLine(line, Offset(0f, stroke / 2), Offset(size.width, stroke / 2), stroke)
            }
    } else {
        val fade = remember(bg) { Brush.verticalGradient(0f to bg.copy(alpha = 0f), FADE_END to bg) }
        Modifier.background(fade)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(surface)
            .then(modifier)
            .padding(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = if (docked) GiSpace.x3 else GiSpace.x6,
                bottom = GiSpace.x4
            )
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
