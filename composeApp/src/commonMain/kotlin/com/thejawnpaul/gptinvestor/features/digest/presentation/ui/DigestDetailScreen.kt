package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.couldn_t_load_your_brief
import com.thejawnpaul.gptinvestor.digest_detail_empty_action
import com.thejawnpaul.gptinvestor.digest_detail_empty_body
import com.thejawnpaul.gptinvestor.digest_detail_empty_title
import com.thejawnpaul.gptinvestor.digest_locked_heading
import com.thejawnpaul.gptinvestor.every_stock_every_morning
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.DigestSkeleton
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.latest_digest
import com.thejawnpaul.gptinvestor.locked
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiPremiumBanner
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.today_s_digest
import com.thejawnpaul.gptinvestor.unlock
import com.thejawnpaul.gptinvestor.unlock_full_summaries
import org.jetbrains.compose.resources.stringResource

@Composable
fun DigestDetailScreen(
    digestView: DailyDigestView,
    onBack: () -> Unit,
    onStockClick: (String) -> Unit,
    onUnlockPremium: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scrolled by remember(listState) { derivedStateOf { listState.canScrollBackward } }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg)
    ) {
        GiTopBar(
            title = stringResource(if (digestView.isStale) Res.string.latest_digest else Res.string.today_s_digest),
            onBack = onBack,
            backContentDescription = stringResource(Res.string.back),
            subtitle = digestView.briefDateLabel,
            scrolled = scrolled
        )
        LazyColumn(
            modifier = Modifier.navigationBarsPadding(),
            state = listState,
            contentPadding = PaddingValues(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x2,
                bottom = GiSpace.x10
            ),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            when {
                digestView.loading -> {
                    item(key = "loading") { DigestSkeleton() }
                    return@LazyColumn
                }
                digestView.error -> {
                    item(key = "error") {
                        GiBanner(
                            title = stringResource(Res.string.couldn_t_load_your_brief),
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            subtitle = stringResource(Res.string.check_connection_try_again),
                            tone = GiBannerTone.Error
                        ) {
                            GiButton(
                                text = stringResource(Res.string.retry),
                                onClick = onRetry,
                                size = GiButtonSize.Small,
                                variant = GiButtonVariant.Secondary
                            )
                        }
                    }
                    return@LazyColumn
                }
                digestView.stocks.isEmpty() && digestView.lockedStocks.isEmpty() -> {
                    item(key = "empty") {
                        GiEmptyState(
                            icon = GiIcons.calendarBlank,
                            title = stringResource(Res.string.digest_detail_empty_title),
                            body = stringResource(Res.string.digest_detail_empty_body),
                            actionText = stringResource(Res.string.digest_detail_empty_action),
                            onAction = onBack,
                            modifier = Modifier.padding(top = GiSpace.x10)
                        )
                    }
                    return@LazyColumn
                }
            }
            items(digestView.stocks, key = { it.ticker }) { stock ->
                BriefStockCard(
                    stock = stock,
                    updatedLabel = stock.updatedLabel,
                    onClick = { onStockClick(stock.ticker) },
                    summaryMaxLines = Int.MAX_VALUE
                )
            }
            if (digestView.lockedStocks.isNotEmpty()) {
                item(key = "locked_heading") {
                    GiEyebrow(
                        text = stringResource(Res.string.digest_locked_heading, digestView.lockedStocks.size),
                        modifier = Modifier.semantics { heading() }
                    )
                }
                item(key = "locked_stocks") {
                    LockedStocks(lockedStocks = digestView.lockedStocks, onUnlockPremium = onUnlockPremium)
                }
                item(key = "unlock_banner") {
                    GiPremiumBanner(
                        title = stringResource(Res.string.unlock_full_summaries),
                        subtitle = stringResource(Res.string.every_stock_every_morning),
                        actionText = stringResource(Res.string.unlock),
                        onActionClick = onUnlockPremium
                    )
                }
            }
        }
    }
}

@Composable
private fun LockedStocks(lockedStocks: List<LockedStockDigestPresentation>, onUnlockPremium: () -> Unit) {
    val lockedDescription = stringResource(Res.string.locked)
    GiListGroup {
        lockedStocks.forEachIndexed { index, stock ->
            if (index > 0) GiDivider()
            GiStockRow(
                stock = tickerStock(
                    ticker = stock.ticker,
                    title = stock.name,
                    subtitle = stock.ticker,
                    percent = stock.priceChange
                ),
                onClick = onUnlockPremium,
                locked = true,
                lockedContentDescription = lockedDescription
            )
        }
    }
}

private val previewStock = StockDigestPresentation(
    ticker = "AAPL",
    name = "Apple Inc.",
    summary = "Apple rose 0.1% to $341.27 on 13 Oct, while revenue grew 6.4% year over year. " +
        "The modest gain reflects steady growth in its core business.",
    keyEvent = "John Ternus took the next big swing as Apple CEO with the HomeView Tablet on 14 Oct.",
    sentiment = DigestSentiment.POSITIVE,
    priceChange = 0.1,
    updatedLabel = "Tue 23:01"
)

private val previewLockedStocks = listOf(
    LockedStockDigestPresentation("NVDA", "NVIDIA", DigestSentiment.POSITIVE, 1.0),
    LockedStockDigestPresentation("MSFT", "Microsoft", DigestSentiment.POSITIVE, 0.4),
    LockedStockDigestPresentation("GOOGL", "Alphabet", DigestSentiment.POSITIVE, 0.3),
    LockedStockDigestPresentation("AMZN", "Amazon", DigestSentiment.NEUTRAL, -0.4)
)

@Composable
private fun DigestDetailPreviewHost(view: DailyDigestView) {
    GPTInvestorTheme {
        DigestDetailScreen(digestView = view, onBack = {}, onStockClick = {}, onUnlockPremium = {}, onRetry = {})
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DigestDetailLockedPreview() {
    DigestDetailPreviewHost(
        DailyDigestView(
            stocks = listOf(previewStock),
            briefDateLabel = "Tue 13 Oct",
            lockedStocks = previewLockedStocks
        )
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DigestDetailStalePreview() {
    DigestDetailPreviewHost(
        DailyDigestView(
            stocks = listOf(previewStock),
            isStale = true,
            briefDateLabel = "Mon 12 Oct",
            lockedStocks = previewLockedStocks
        )
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DigestDetailUnlockedPreview() {
    DigestDetailPreviewHost(
        DailyDigestView(
            stocks = listOf(previewStock, previewStock.copy(ticker = "TSLA", name = "Tesla, Inc.", priceChange = -1.2)),
            briefDateLabel = "Tue 13 Oct"
        )
    )
}

@PreviewLightDark
@Composable
private fun DigestDetailLoadingPreview() {
    DigestDetailPreviewHost(DailyDigestView(loading = true))
}

@PreviewLightDark
@Composable
private fun DigestDetailErrorPreview() {
    DigestDetailPreviewHost(DailyDigestView(error = true))
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DigestDetailEmptyPreview() {
    DigestDetailPreviewHost(DailyDigestView(status = DailyDigestStatus.EMPTY))
}
