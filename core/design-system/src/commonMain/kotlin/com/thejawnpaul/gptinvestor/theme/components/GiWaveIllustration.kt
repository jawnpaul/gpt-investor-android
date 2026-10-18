package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiTheme

private const val WAVE_WIDTH = 390f
private const val WAVE_HEIGHT = 210f
private const val WAVE_COUNT = 7

@Composable
fun GiWaveIllustration(modifier: Modifier = Modifier) {
    val lilac = GiTheme.colors.lilac
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(WAVE_WIDTH / WAVE_HEIGHT)
            .clearAndSetSemantics {}
            .drawWithCache {
                val scale = size.width / WAVE_WIDTH
                val stroke = Stroke(width = 1.dp.toPx())
                val waves = List(WAVE_COUNT) { k ->
                    val y = 40f + 22f * k
                    val a = 10f + 2f * k
                    Path().apply {
                        moveTo(-10f * scale, y * scale)
                        cubicTo(60f * scale, (y - a) * scale, 120f * scale, (y + a) * scale, 195f * scale, y * scale)
                        cubicTo(
                            270f * scale,
                            (y - a) * scale,
                            330f * scale,
                            (y - a) * scale,
                            400f * scale,
                            (y + 4f) * scale
                        )
                    }
                }
                onDrawBehind {
                    waves.forEachIndexed { k, path ->
                        drawPath(path = path, color = lilac.copy(alpha = 0.18f + 0.05f * k), style = stroke)
                    }
                }
            }
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiWaveIllustrationPreview() {
    GiPreviewSurface {
        GiWaveIllustration()
    }
}
