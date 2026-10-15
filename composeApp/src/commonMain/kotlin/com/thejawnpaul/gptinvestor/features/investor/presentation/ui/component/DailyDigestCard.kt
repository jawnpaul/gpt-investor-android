package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add
import com.thejawnpaul.gptinvestor.add_more_stocks
import com.thejawnpaul.gptinvestor.browse_all_companies
import com.thejawnpaul.gptinvestor.build_your_digest
import com.thejawnpaul.gptinvestor.digest_pending_description
import com.thejawnpaul.gptinvestor.digest_pending_description_at
import com.thejawnpaul.gptinvestor.digest_pending_description_premium
import com.thejawnpaul.gptinvestor.digest_pending_description_premium_at
import com.thejawnpaul.gptinvestor.digest_pending_notify_subtitle
import com.thejawnpaul.gptinvestor.digest_pending_notify_title
import com.thejawnpaul.gptinvestor.digest_pending_title
import com.thejawnpaul.gptinvestor.digest_sentiment_negative
import com.thejawnpaul.gptinvestor.digest_sentiment_neutral
import com.thejawnpaul.gptinvestor.digest_sentiment_positive
import com.thejawnpaul.gptinvestor.done
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment
import com.thejawnpaul.gptinvestor.features.digest.presentation.formatTickerList
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestStatus
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.DailyDigestView
import com.thejawnpaul.gptinvestor.features.digest.presentation.ui.StockDigestPresentation
import com.thejawnpaul.gptinvestor.full_summaries_with_premium
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.latest_digest
import com.thejawnpaul.gptinvestor.morning_brief
import com.thejawnpaul.gptinvestor.new_digest_on_the_way
import com.thejawnpaul.gptinvestor.next_digest_by
import com.thejawnpaul.gptinvestor.sentiment
import com.thejawnpaul.gptinvestor.stocks_locked
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBriefCard
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiListRow
import com.thejawnpaul.gptinvestor.theme.components.GiPremiumBanner
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiSentiment
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiSpinner
import com.thejawnpaul.gptinvestor.today_s_digest
import com.thejawnpaul.gptinvestor.tracking_stocks
import com.thejawnpaul.gptinvestor.unlock
import com.thejawnpaul.gptinvestor.updated_at
import com.thejawnpaul.gptinvestor.x_more_stocks_in_your_digest
import com.thejawnpaul.gptinvestor.you_re_not_tracking_any_companies_yet
import com.thejawnpaul.gptinvestor.your_digest
import org.jetbrains.compose.resources.stringResource

private enum class DigestDisplay { Loading, Empty, Pending, Ready }

@Composable
fun DailyDigestCard(
    view: DailyDigestView,
    onAddStock: (String) -> Unit,
    onBrowseAll: () -> Unit,
    onSeeFullDigest: () -> Unit,
    onUnlockPremiumPending: () -> Unit,
    onUnlockPremiumReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    val display = when {
        view.loading -> DigestDisplay.Loading
        view.status == DailyDigestStatus.PENDING -> DigestDisplay.Pending
        view.status == DailyDigestStatus.READY -> DigestDisplay.Ready
        else -> DigestDisplay.Empty
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)
    ) {
        GiSectionHeader(
            title = stringResource(
                when {
                    view.status == DailyDigestStatus.EMPTY -> Res.string.build_your_digest
                    view.status == DailyDigestStatus.PENDING -> Res.string.your_digest
                    view.isStale -> Res.string.latest_digest
                    else -> Res.string.today_s_digest
                }
            ),
            eyebrow = stringResource(Res.string.morning_brief),
            eyebrowIcon = GiIcons.calendarBlank,
            trailingText = view.briefDateLabel.takeIf { view.status == DailyDigestStatus.READY }
        )
        if (view.isStale && view.status == DailyDigestStatus.READY) {
            GiBanner(
                title = view.nextDigestLabel?.let { stringResource(Res.string.next_digest_by, it) }
                    ?: stringResource(Res.string.new_digest_on_the_way),
                icon = GiIcons.clock
            )
        }
        AnimatedContent(
            targetState = display,
            transitionSpec = {
                fadeIn(tween(GiMotion.BASE)) togetherWith fadeOut(tween(GiMotion.FAST))
            },
            label = "DailyDigestContent"
        ) { state ->
            when (state) {
                DigestDisplay.Loading -> DigestSkeleton()
                DigestDisplay.Empty -> EmptyDigest(view = view, onAddStock = onAddStock, onBrowseAll = onBrowseAll)
                DigestDisplay.Pending -> PendingDigest(
                    view = view,
                    onAddMoreStocks = onBrowseAll,
                    onUnlockPremium = onUnlockPremiumPending
                )
                DigestDisplay.Ready -> ReadyDigest(
                    view = view,
                    onSeeFullDigest = onSeeFullDigest,
                    onUnlockPremium = onUnlockPremiumReady
                )
            }
        }
    }
}

@Composable
private fun EmptyDigest(view: DailyDigestView, onAddStock: (String) -> Unit, onBrowseAll: () -> Unit) {
    GiCard {
        Text(
            text = stringResource(Res.string.you_re_not_tracking_any_companies_yet),
            style = GiTheme.type.body,
            color = GiTheme.colors.body
        )
        GiListGroup {
            view.suggestedStocks.forEachIndexed { index, stock ->
                if (index > 0) GiDivider()
                GiListRow(title = stock.ticker, subtitle = stock.companyName) {
                    AddStockAction(
                        isAdding = stock.ticker in view.addingTickers,
                        isAdded = stock.ticker in view.addedTickers,
                        onAdd = { onAddStock(stock.ticker) }
                    )
                }
            }
        }
        GiButton(
            text = stringResource(Res.string.browse_all_companies),
            onClick = onBrowseAll,
            modifier = Modifier.fillMaxWidth(),
            size = GiButtonSize.Medium,
            variant = GiButtonVariant.Secondary
        )
    }
}

@Composable
private fun AddStockAction(isAdding: Boolean, isAdded: Boolean, onAdd: () -> Unit) {
    when {
        isAdding -> GiSpinner()
        isAdded -> Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = GiIcons.check,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = GiTheme.colors.gain
            )
            Text(text = stringResource(Res.string.done), style = GiTheme.type.link, color = GiTheme.colors.gain)
        }
        else -> GiButton(
            text = stringResource(Res.string.add),
            onClick = onAdd,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary
        )
    }
}

@Composable
private fun PendingDigest(view: DailyDigestView, onAddMoreStocks: () -> Unit, onUnlockPremium: () -> Unit) {
    val nextDigestLabel = view.nextDigestLabel
    GiCard {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            Text(
                text = stringResource(Res.string.digest_pending_title),
                style = GiTheme.type.label,
                color = GiTheme.colors.ink
            )
            Text(
                text = when {
                    nextDigestLabel == null && view.isPremium ->
                        stringResource(Res.string.digest_pending_description_premium)
                    nextDigestLabel == null -> stringResource(Res.string.digest_pending_description)
                    view.isPremium -> stringResource(Res.string.digest_pending_description_premium_at, nextDigestLabel)
                    else -> stringResource(Res.string.digest_pending_description_at, nextDigestLabel)
                },
                style = GiTheme.type.body,
                color = GiTheme.colors.body
            )
        }
        if (view.trackedTickers.isNotEmpty()) {
            GiDivider()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.tracking_stocks, formatTickerList(view.trackedTickers)),
                    modifier = Modifier.weight(1f),
                    style = GiTheme.type.meta,
                    color = GiTheme.colors.muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                GiButton(
                    text = stringResource(Res.string.add_more_stocks),
                    onClick = onAddMoreStocks,
                    size = GiButtonSize.Small,
                    variant = GiButtonVariant.Text
                )
            }
        }
        if (!view.isPremium) {
            GiBanner(
                title = stringResource(Res.string.digest_pending_notify_title),
                subtitle = stringResource(Res.string.digest_pending_notify_subtitle),
                icon = GiIcons.bell,
                onClick = onUnlockPremium
            )
        }
    }
}

@Composable
private fun ReadyDigest(view: DailyDigestView, onSeeFullDigest: () -> Unit, onUnlockPremium: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        // Home previews the first stock; the rest are one tap away in Digest Detail.
        view.stocks.firstOrNull()?.let { stock ->
            BriefStockCard(stock = stock, updatedLabel = view.updatedLabel, onClick = onSeeFullDigest)
        }
        // Only unlocked stocks: locked ones get their own upsell below, so they aren't counted twice.
        val remainingStocks = view.stocks.size - 1
        if (remainingStocks > 0) {
            GiButton(
                text = stringResource(Res.string.x_more_stocks_in_your_digest, remainingStocks),
                onClick = onSeeFullDigest,
                modifier = Modifier.fillMaxWidth(),
                size = GiButtonSize.Medium,
                variant = GiButtonVariant.Secondary
            )
        }
        if (view.lockedStocksCount > 0) {
            GiPremiumBanner(
                title = view.lockedStocksSummary?.let { stringResource(Res.string.stocks_locked, it) }
                    ?: stringResource(Res.string.full_summaries_with_premium),
                subtitle = stringResource(Res.string.full_summaries_with_premium),
                actionText = stringResource(Res.string.unlock),
                onActionClick = onUnlockPremium
            )
        }
    }
}

@Composable
private fun BriefStockCard(stock: StockDigestPresentation, updatedLabel: String?, onClick: () -> Unit) {
    GiBriefCard(
        stock = tickerStock(
            ticker = stock.ticker,
            title = stock.name,
            subtitle = stock.ticker,
            percent = stock.priceChange
        ),
        summary = stock.summary,
        onClick = onClick,
        sentiment = stock.sentiment?.toGiSentiment(),
        sentimentLabel = stock.sentiment?.let {
            stringResource(
                when (it) {
                    DigestSentiment.POSITIVE -> Res.string.digest_sentiment_positive
                    DigestSentiment.NEUTRAL -> Res.string.digest_sentiment_neutral
                    DigestSentiment.NEGATIVE -> Res.string.digest_sentiment_negative
                }
            )
        },
        sentimentCaption = stringResource(Res.string.sentiment),
        keyEventLabel = stringResource(Res.string.key_event),
        keyEvent = stock.keyEvent,
        updatedText = updatedLabel?.let { stringResource(Res.string.updated_at, it) }
    )
}

private fun DigestSentiment.toGiSentiment(): GiSentiment = when (this) {
    DigestSentiment.POSITIVE -> GiSentiment.Positive
    DigestSentiment.NEUTRAL -> GiSentiment.Neutral
    DigestSentiment.NEGATIVE -> GiSentiment.Negative
}

@Composable
private fun DigestSkeleton() {
    GiCard {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            GiSkeleton(modifier = Modifier.size(40.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp))
            }
        }
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
        GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(14.dp))
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(56.dp))
    }
}

@Composable
private fun DailyDigestCardPreviewHost(view: DailyDigestView) {
    GPTInvestorTheme {
        DailyDigestCard(
            view = view,
            onAddStock = {},
            onBrowseAll = {},
            onSeeFullDigest = {},
            onUnlockPremiumPending = {},
            onUnlockPremiumReady = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ReadyDigestPreview() {
    DailyDigestCardPreviewHost(
        DailyDigestView(
            status = DailyDigestStatus.READY,
            stocks = listOf(
                StockDigestPresentation(
                    ticker = "AAPL",
                    name = "Apple Inc.",
                    summary = "Apple rose 0.1% to $341.27 on Oct 13, while revenue grew 6.4% year over year.",
                    keyEvent = "New HomeView tablet announced on Oct 14.",
                    sentiment = DigestSentiment.POSITIVE,
                    priceChange = 0.1
                ),
                StockDigestPresentation(ticker = "TSLA", name = "Tesla", summary = "")
            ),
            isStale = true,
            briefDateLabel = "Tue, Oct 13",
            updatedLabel = "Wed 7:01 AM",
            nextDigestLabel = "7 AM",
            lockedStocksCount = 4,
            lockedStocksSummary = "NVDA, MSFT, GOOGL & AMZN",
            isPremium = false
        )
    )
}

@PreviewLightDark
@Composable
private fun EmptyDigestPreview() {
    DailyDigestCardPreviewHost(
        DailyDigestView(
            status = DailyDigestStatus.EMPTY,
            suggestedStocks = listOf(
                RecommendedDigestCompany("AAPL", "Apple Inc."),
                RecommendedDigestCompany("NVDA", "NVIDIA Corp."),
                RecommendedDigestCompany("MSFT", "Microsoft")
            ),
            addingTickers = setOf("NVDA"),
            addedTickers = setOf("MSFT")
        )
    )
}

@PreviewLightDark
@Composable
private fun PendingDigestPreview() {
    DailyDigestCardPreviewHost(
        DailyDigestView(
            status = DailyDigestStatus.PENDING,
            nextDigestLabel = "7 AM",
            trackedTickers = listOf("AAPL", "NVDA"),
            isPremium = false
        )
    )
}

@PreviewLightDark
@Composable
private fun LoadingDigestPreview() {
    DailyDigestCardPreviewHost(DailyDigestView(loading = true))
}
