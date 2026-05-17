package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import com.thejawnpaul.gptinvestor.ic_caret_right
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.painterResource

@Composable
fun SearchStockRow(stock: StockItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape) {
            AsyncImage(
                model = stock.logoUrl,
                contentDescription = stock.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                color = LocalGPTInvestorColors.current.textColors.secondary50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(Res.drawable.ic_caret_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = LocalGPTInvestorColors.current.textColors.secondary50
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchStockRowPreview() {
    GPTInvestorTheme {
        Surface {
            SearchStockRow(
                stock = StockItem("AAPL", "Apple Inc.", ""),
                onClick = {}
            )
        }
    }
}
