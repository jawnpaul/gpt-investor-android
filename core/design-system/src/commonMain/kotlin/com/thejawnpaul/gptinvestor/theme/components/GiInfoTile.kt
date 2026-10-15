package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Inner tile for use inside a raised surface: tile fill, no edge.
@Composable
fun GiInfoTile(label: String, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.sm)
            .background(GiTheme.colors.tile)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        GiEyebrow(text = label)
        Text(text = text, style = GiTheme.type.label, color = GiTheme.colors.ink)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiInfoTilePreview() {
    GiPreviewSurface {
        GiCard {
            GiInfoTile(label = "Key event", text = "Q3 earnings on 22 Oct after the close")
        }
    }
}
