package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiSpace

@Composable
fun GiMoverCard(stock: GiStock, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(
        modifier = modifier.width(if (isLargeText()) 280.dp else 248.dp),
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    ) {
        GiStockSummary(stock = stock)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiMoverCardPreview() {
    GiPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiMoverCard(stock = previewStock.copy(title = "NVDA", subtitle = "NVIDIA Corporation"), onClick = {})
            GiMoverCard(
                stock = GiStock(
                    initials = "SK",
                    brandColor = Color(0xFF7A2E0E),
                    title = "SKYD",
                    subtitle = "Skydance Corporation",
                    percent = -23.84
                ),
                onClick = {}
            )
        }
    }
}
