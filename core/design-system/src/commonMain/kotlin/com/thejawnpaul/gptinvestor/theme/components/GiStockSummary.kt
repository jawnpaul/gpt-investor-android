package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiSpace

@Immutable
data class GiStock(
    val initials: String,
    val brandColor: Color,
    val title: String,
    val subtitle: String,
    val percent: Double?,
    val imageUrl: String? = null
)

enum class GiStockSummarySize { Default, Header }

@Composable
fun GiStockSummary(
    stock: GiStock,
    modifier: Modifier = Modifier,
    logoModifier: Modifier = Modifier,
    size: GiStockSummarySize = GiStockSummarySize.Default
) {
    val header = size == GiStockSummarySize.Header
    if (isLargeText()) {
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiLogoTile(
                    initials = stock.initials,
                    brandColor = stock.brandColor,
                    modifier = logoModifier,
                    imageUrl = stock.imageUrl
                )
                Column(modifier = Modifier.weight(1f)) {
                    GiText(
                        text = stock.title,
                        style = if (header) GiTextStyle.CompanyName else GiTextStyle.Label
                    )
                    GiText(text = stock.subtitle, style = GiTextStyle.Meta)
                }
            }
            if (stock.percent != null) {
                GiDeltaPill(percent = stock.percent, modifier = Modifier.padding(start = LOGO_TILE_SIZE + GiSpace.x3))
            }
        }
        return
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiLogoTile(
            initials = stock.initials,
            brandColor = stock.brandColor,
            modifier = logoModifier,
            imageUrl = stock.imageUrl
        )
        Column(modifier = Modifier.weight(1f)) {
            GiText(
                text = stock.title,
                style = if (header) GiTextStyle.CompanyName else GiTextStyle.Label,
                maxLines = if (header) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis
            )
            GiText(
                text = stock.subtitle,
                style = GiTextStyle.Meta,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (stock.percent != null) {
            GiDeltaPill(percent = stock.percent)
        }
    }
}

private val LOGO_TILE_SIZE = 40.dp

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
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiStockSummary(stock = previewStock)
            GiStockSummary(stock = previewStock.copy(percent = null), size = GiStockSummarySize.Header)
        }
    }
}
