package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Immutable
data class GiStock(
    val initials: String,
    val brandColor: Color,
    val title: String,
    val subtitle: String,
    val percent: Double?,
    val imageUrl: String? = null
)

// Logo tile, title over subtitle, delta pill. The delta pill never truncates; the text block does.
@Composable
fun GiStockSummary(stock: GiStock, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiLogoTile(initials = stock.initials, brandColor = stock.brandColor, imageUrl = stock.imageUrl)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.title,
                style = GiTheme.type.label,
                color = GiTheme.colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stock.subtitle,
                style = GiTheme.type.meta,
                color = GiTheme.colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (stock.percent != null) {
            GiDeltaPill(percent = stock.percent)
        }
    }
}

internal val previewStock = GiStock(
    initials = "NV",
    brandColor = Color(0xFF76B900),
    title = "NVIDIA Corporation",
    subtitle = "NVDA · NASDAQ",
    percent = 2.31
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiStockSummaryPreview() {
    GiPreviewSurface {
        GiStockSummary(stock = previewStock)
    }
}
