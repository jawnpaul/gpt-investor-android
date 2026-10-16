package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.digest_sentiment_negative
import com.thejawnpaul.gptinvestor.digest_sentiment_neutral
import com.thejawnpaul.gptinvestor.digest_sentiment_positive
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.sentiment
import com.thejawnpaul.gptinvestor.theme.components.GiDigestCard
import com.thejawnpaul.gptinvestor.theme.components.GiSentiment
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
        sentiment = stock.sentiment?.toGiSentiment(),
        sentimentLabel = stock.sentiment?.let { digestSentimentLabel(it) },
        sentimentCaption = stringResource(Res.string.sentiment),
        keyEventLabel = stringResource(Res.string.key_event),
        keyEvent = stock.keyEvent,
        updatedText = updatedLabel?.let { stringResource(Res.string.updated_at, it) },
        summaryMaxLines = summaryMaxLines
    )
}

@Composable
internal fun digestSentimentLabel(sentiment: DigestSentiment): String = stringResource(
    when (sentiment) {
        DigestSentiment.POSITIVE -> Res.string.digest_sentiment_positive
        DigestSentiment.NEUTRAL -> Res.string.digest_sentiment_neutral
        DigestSentiment.NEGATIVE -> Res.string.digest_sentiment_negative
    }
)

private fun DigestSentiment.toGiSentiment(): GiSentiment = when (this) {
    DigestSentiment.POSITIVE -> GiSentiment.Positive
    DigestSentiment.NEUTRAL -> GiSentiment.Neutral
    DigestSentiment.NEGATIVE -> GiSentiment.Negative
}
