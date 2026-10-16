package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiComposerBar(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val bg = GiTheme.colors.bg
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(0f to bg.copy(alpha = 0f), FADE_END to bg))
            .then(modifier)
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x6, bottom = GiSpace.x4)
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
    }
}
