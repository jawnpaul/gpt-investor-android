package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.theme.components.GiDigestCard
import com.thejawnpaul.gptinvestor.updated_at
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BriefStockCard(
    stock: StockDigestPresentation,
    updatedLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    summaryMaxLines: Int = 4
) {
    GiDigestCard(
        stock = tickerStock(
            ticker = stock.ticker,
            title = stock.name,
            subtitle = stock.exchange?.takeIf { it.isNotBlank() }?.let { "${stock.ticker} · $it" } ?: stock.ticker,
            percent = stock.priceChange
        ),
        summary = stock.summary,
        onClick = onClick,
        modifier = modifier,
        keyEventLabel = stringResource(Res.string.key_event),
        keyEvent = stock.keyEvent,
        updatedText = updatedLabel?.let { stringResource(Res.string.updated_at, it) },
        summaryMaxLines = summaryMaxLines
    )
}
