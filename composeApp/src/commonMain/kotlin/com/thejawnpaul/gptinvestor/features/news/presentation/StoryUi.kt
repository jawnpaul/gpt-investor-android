package com.thejawnpaul.gptinvestor.features.news.presentation

import com.thejawnpaul.gptinvestor.features.news.domain.model.ExplainerSection
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPrice
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceBasis
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceStatus
import com.thejawnpaul.gptinvestor.features.news.domain.model.StorySource
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.formatPrice
import kotlinx.datetime.TimeZone

private const val LOGO_STACK_MAX = 3
private const val CHART_MIN_POINTS = 2

sealed interface StoryDisclaimer {
    data class SinceStoryTime(val time: String) : StoryDisclaimer
    data object SinceLastClose : StoryDisclaimer
}

data class BarelyMovedUi(val count: Int, val logos: List<StoryStock>, val names: String)

sealed interface WatchAllUi {
    val enabled: Boolean

    data class AddRest(val count: Int, override val enabled: Boolean) : WatchAllUi
    data class AddAll(val count: Int, override val enabled: Boolean) : WatchAllUi
    data class AllWatched(val count: Int) : WatchAllUi {
        override val enabled = false
    }
}

sealed interface AtStoryLabel {
    data class AtTime(val time: String) : AtStoryLabel
    data class PreviousClose(val weekday: String) : AtStoryLabel
    data class StoryDate(val date: String) : AtStoryLabel
    data object LastClose : AtStoryLabel
}

sealed interface NowLabel {
    data class AtTime(val time: String) : NowLabel
    data class SinceDate(val date: String) : NowLabel
    data object MarketClosed : NowLabel
    data object Now : NowLabel
}

sealed interface PriceBadge {
    data class Change(val percent: Double) : PriceBadge
    data object Halted : PriceBadge
}

sealed interface PriceNote {
    data class NextOpen(val time: String) : PriceNote
    data class UnavailableSince(val time: String) : PriceNote
    data object Unavailable : PriceNote
    data object BigMove : PriceNote
}

data class PriceTilesUi(
    val atStoryLabel: AtStoryLabel,
    val atStoryValue: String?,
    val nowLabel: NowLabel,
    val nowValue: String?,
    val badge: PriceBadge?,
    val note: PriceNote?
)

data class StockSheetUi(
    val ticker: String,
    val name: String,
    val logoUrl: String?,
    val tickerLine: String,
    val watched: Boolean,
    val pending: Boolean,
    val connection: String,
    val source: StorySource?,
    val price: PriceTilesUi,
    val chart: StoryChartUi?
)

data class StoryChartUi(val closes: List<Double>, val storyIndex: Int, val storyDay: String, val rising: Boolean) {
    val days: Int get() = closes.size
}

data class SharePreviewUi(val imageUrl: String, val preparing: Boolean)

data class ExplainerSectionUi(val heading: String, val paragraphs: List<String>)

fun Story.disclaimer(timeZone: TimeZone = TimeZone.currentSystemDefault()): StoryDisclaimer =
    if (movesBasis == StoryPriceBasis.STORY_TIME) {
        StoryDisclaimer.SinceStoryTime(newsClock(publishedAt, timeZone))
    } else {
        StoryDisclaimer.SinceLastClose
    }

fun Story.barelyMovedUi(): BarelyMovedUi? = barelyMoved.takeIf { it.isNotEmpty() }?.let { stocks ->
    BarelyMovedUi(
        count = stocks.size,
        logos = stocks.take(LOGO_STACK_MAX),
        names = stocks.joinToString(", ") {
            it.name
        }
    )
}

fun watchAllUi(stocks: List<StoryStock>, watch: StockWatchState): WatchAllUi? {
    if (stocks.isEmpty()) return null
    val unwatched = watch.unwatched(stocks).size
    val enabled = stocks.none { watch.isPending(it.ticker) }
    return when (unwatched) {
        0 -> WatchAllUi.AllWatched(stocks.size)
        stocks.size -> WatchAllUi.AddAll(stocks.size, enabled)
        else -> WatchAllUi.AddRest(unwatched, enabled)
    }
}

fun StoryStock.sheetUi(story: Story, watch: StockWatchState, timeZone: TimeZone = TimeZone.currentSystemDefault()) =
    StockSheetUi(
        ticker = ticker,
        name = name,
        logoUrl = logoUrl,
        tickerLine = listOfNotNull(ticker, exchange).joinToString(" · "),
        watched = watch.isWatched(ticker),
        pending = watch.isPending(ticker),
        connection = connection ?: "$reason: “${story.title}”",
        source = source,
        price = price.tilesUi(timeZone),
        chart = chartUi()
    )

fun StoryStock.chartUi(): StoryChartUi? {
    val index = historyStoryIndex ?: return null
    if (history.size < CHART_MIN_POINTS || index !in history.indices) return null
    return StoryChartUi(
        closes = history.map { it.close },
        storyIndex = index,
        storyDay = newsDayMonth(history[index].date),
        rising = (price.changePercent ?: 0.0) >= 0.0
    )
}

fun StoryPrice.tilesUi(timeZone: TimeZone = TimeZone.currentSystemDefault()): PriceTilesUi {
    val closed = status == StoryPriceStatus.MARKET_CLOSED
    return PriceTilesUi(
        atStoryLabel = atStoryLabel(timeZone),
        atStoryValue = atStory?.let(::formatPrice),
        nowLabel = nowLabel(timeZone),
        nowValue = now?.takeIf { !closed }?.let(::formatPrice),
        badge = when {
            status == StoryPriceStatus.HALTED -> PriceBadge.Halted
            status == StoryPriceStatus.LIVE && changePercent != null -> PriceBadge.Change(changePercent)
            else -> null
        },
        note = when {
            closed && nextOpenTime != null -> PriceNote.NextOpen(newsClock(nextOpenTime, timeZone))
            status == StoryPriceStatus.UNAVAILABLE && nowTime != null ->
                PriceNote.UnavailableSince(newsClock(nowTime, timeZone))
            status == StoryPriceStatus.UNAVAILABLE -> PriceNote.Unavailable
            bigMove -> PriceNote.BigMove
            else -> null
        }
    )
}

private fun StoryPrice.atStoryLabel(timeZone: TimeZone): AtStoryLabel {
    val time = atStoryTime ?: return AtStoryLabel.LastClose
    return when (basis) {
        StoryPriceBasis.STORY_TIME -> AtStoryLabel.AtTime(newsClock(time, timeZone))
        StoryPriceBasis.PREVIOUS_CLOSE -> AtStoryLabel.PreviousClose(newsWeekday(time, timeZone))
        StoryPriceBasis.STORY_DATE -> AtStoryLabel.StoryDate(newsDayMonth(time, timeZone))
        StoryPriceBasis.LAST_CLOSE -> AtStoryLabel.LastClose
    }
}

private fun StoryPrice.nowLabel(timeZone: TimeZone): NowLabel {
    val time = nowTime
    val storyTime = atStoryTime
    return when {
        status == StoryPriceStatus.MARKET_CLOSED -> NowLabel.MarketClosed
        status == StoryPriceStatus.UNAVAILABLE || time == null -> NowLabel.Now
        basis == StoryPriceBasis.STORY_DATE && storyTime != null -> NowLabel.SinceDate(
            newsDayMonth(storyTime, timeZone)
        )
        else -> NowLabel.AtTime(newsClock(time, timeZone))
    }
}

fun ExplainerSection.toUi() = ExplainerSectionUi(
    heading = heading,
    paragraphs = body.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }
)
