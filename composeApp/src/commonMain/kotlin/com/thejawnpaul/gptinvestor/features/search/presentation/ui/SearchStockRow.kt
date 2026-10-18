package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow

@Composable
fun SearchStockList(stocks: List<StockItem>, onClick: (StockItem) -> Unit, modifier: Modifier = Modifier) {
    GiListGroup(modifier = modifier) {
        stocks.forEachIndexed { index, stock ->
            if (index > 0) GiDivider()
            SearchStockRow(stock = stock, onClick = { onClick(stock) })
        }
    }
}

@Composable
fun SearchStockRow(stock: StockItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiStockRow(
        stock = tickerStock(
            ticker = stock.ticker,
            title = stock.name,
            subtitle = stock.ticker,
            percent = null,
            imageUrl = stock.logoUrl.ifBlank { null }
        ),
        modifier = modifier,
        onClick = onClick,
        showCaret = true
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchStockListPreview() {
    GPTInvestorTheme {
        Column(modifier = Modifier.fillMaxWidth().padding(GiSpace.gutter), verticalArrangement = Arrangement.Top) {
            SearchStockList(
                stocks = listOf(
                    StockItem("NVDA", "NVIDIA Corporation", ""),
                    StockItem("INVH", "Invitation Homes Inc.", "")
                ),
                onClick = {}
            )
        }
    }
}
