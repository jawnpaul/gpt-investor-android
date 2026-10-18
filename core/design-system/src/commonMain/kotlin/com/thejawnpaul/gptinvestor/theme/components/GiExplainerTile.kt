package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiExplainerTile(label: String, text: String, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.sm)
            .background(colors.tile)
            .padding(GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = GiIcons.mark,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = colors.violet
            )
            GiText(text = label, style = GiTextStyle.Link, color = colors.ink)
        }
        GiText(text = text, style = GiTextStyle.BodySmall)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiExplainerTilePreview() {
    GiPreviewSurface {
        GiCard {
            GiExplainerTile(
                label = "What it means",
                text = "This news caused the stock price to drop because sales of medical devices were " +
                    "lower than expected."
            )
        }
    }
}
