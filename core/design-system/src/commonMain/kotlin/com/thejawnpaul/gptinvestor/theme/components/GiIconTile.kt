package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiIconTileSize(internal val tile: Dp, internal val icon: Dp) {
    Small(tile = 32.dp, icon = 16.dp),
    Medium(tile = 40.dp, icon = 20.dp),
    Large(tile = 48.dp, icon = 24.dp)
}

enum class GiIconTileTone { Violet, Gain, Loss, Neutral }

@Composable
fun GiIconTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: GiIconTileSize = GiIconTileSize.Medium,
    tone: GiIconTileTone = GiIconTileTone.Violet
) {
    val colors = GiTheme.colors
    val tileColors = when (tone) {
        GiIconTileTone.Violet -> GiIconTileColors(container = colors.violetSoft, content = colors.violet)
        GiIconTileTone.Gain -> GiIconTileColors(container = colors.gainSoft, content = colors.gain)
        GiIconTileTone.Loss -> GiIconTileColors(container = colors.lossSoft, content = colors.loss)
        GiIconTileTone.Neutral -> GiIconTileColors(container = colors.tile, content = colors.muted)
    }
    Box(
        modifier = modifier
            .size(size.tile)
            .clip(GiRadius.sm)
            .background(tileColors.container)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size.icon),
            tint = tileColors.content
        )
    }
}

private data class GiIconTileColors(val container: Color, val content: Color)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiIconTilePreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                GiIconTileTone.entries.forEach { GiIconTile(icon = GiIcons.check, size = GiIconTileSize.Small, tone = it) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                GiIconTileSize.entries.forEach { GiIconTile(icon = GiIcons.warning, size = it) }
            }
        }
    }
}
