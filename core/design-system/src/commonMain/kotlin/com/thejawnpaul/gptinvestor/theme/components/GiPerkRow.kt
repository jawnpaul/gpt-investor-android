package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiPerkRow(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiIconTile(icon = icon, size = GiIconTileSize.Medium, tone = GiIconTileTone.Violet)
        Column(modifier = Modifier.weight(1f)) {
            GiText(text = title, style = GiTextStyle.Label, color = GiTheme.colors.ink)
            GiText(text = subtitle, style = GiTextStyle.BodySmall, color = GiTheme.colors.body)
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiPerkRowPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiPerkRow(
                icon = GiIcons.infinity,
                title = "Unlimited AI questions",
                subtitle = "Ask anything, as many times as you want"
            )
            GiPerkRow(
                icon = GiIcons.newspaper,
                title = "Full brief coverage",
                subtitle = "Every stock in your watchlist, unlocked"
            )
            GiPerkRow(icon = GiIcons.star, title = "Premium research", subtitle = "Top picks, tidbits and curated insights")
        }
    }
}
