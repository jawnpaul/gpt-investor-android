package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiSentiment { Positive, Neutral, Negative }

@Composable
fun GiSentimentPill(sentiment: GiSentiment, label: String, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val pillColors = when (sentiment) {
        GiSentiment.Positive -> GiSentimentColors(container = colors.gainSoft, content = colors.gain)
        GiSentiment.Neutral -> GiSentimentColors(container = colors.tile, content = colors.muted)
        GiSentiment.Negative -> GiSentimentColors(container = colors.lossSoft, content = colors.loss)
    }
    val content = pillColors.content
    Row(
        modifier = modifier
            .heightIn(min = 24.dp)
            .clip(GiRadius.xs)
            .background(pillColors.container)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (sentiment) {
            GiSentiment.Positive -> GiDeltaTriangle(up = true, color = content)
            GiSentiment.Negative -> GiDeltaTriangle(up = false, color = content)
            GiSentiment.Neutral -> Box(
                modifier = Modifier
                    .size(width = 8.dp, height = 2.dp)
                    .clip(GiRadius.full)
                    .background(content)
            )
        }
        GiText(text = label, style = GiTextStyle.Tag, color = content, maxLines = 1)
    }
}

private data class GiSentimentColors(val container: Color, val content: Color)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiSentimentPillPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSentimentPill(sentiment = GiSentiment.Positive, label = "Bullish")
            GiSentimentPill(sentiment = GiSentiment.Neutral, label = "Neutral")
            GiSentimentPill(sentiment = GiSentiment.Negative, label = "Bearish")
        }
    }
}
