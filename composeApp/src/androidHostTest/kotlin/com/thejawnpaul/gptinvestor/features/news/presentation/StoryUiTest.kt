package com.thejawnpaul.gptinvestor.features.news.presentation

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.news.domain.model.ExplainerSection
import com.thejawnpaul.gptinvestor.features.news.domain.model.PricePoint
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceBasis
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceStatus
import kotlinx.datetime.TimeZone
import org.junit.Test

class StoryUiTest {

    private val utc = TimeZone.UTC
    private val storyTime = 1_761_059_520L
    private val nowTime = 1_761_067_800L
    private val price = testStock("DAL").price.copy(atStoryTime = storyTime, nowTime = nowTime, bigMove = false)

    @Test
    fun `should label a live move from the story time`() {
        val ui = price.tilesUi("$", utc)

        assertThat(ui.atStoryLabel).isEqualTo(AtStoryLabel.AtTime("15:12"))
        assertThat(ui.nowLabel).isEqualTo(NowLabel.AtTime("17:30"))
        assertThat(ui.atStoryValue).isEqualTo("$10.00")
        assertThat(ui.badge).isEqualTo(PriceBadge.Change(10.0))
        assertThat(ui.note).isNull()
    }

    @Test
    fun `should format prices in the stock's own currency`() {
        val ui = price.copy(atStory = 1234.5, now = 1300.0).tilesUi("₦", utc)

        assertThat(ui.atStoryValue).isEqualTo("₦1,234.50")
        assertThat(ui.nowValue).isEqualTo("₦1,300.00")
    }

    @Test
    fun `should show market closed with the next open and no current price`() {
        val ui = price.copy(status = StoryPriceStatus.MARKET_CLOSED, nextOpenTime = nowTime).tilesUi("$", utc)

        assertThat(ui.nowLabel).isEqualTo(NowLabel.MarketClosed)
        assertThat(ui.nowValue).isNull()
        assertThat(ui.badge).isNull()
        assertThat(ui.note).isEqualTo(PriceNote.NextOpen("17:30"))
    }

    @Test
    fun `should say when an unavailable price last updated`() {
        val ui = price.copy(status = StoryPriceStatus.UNAVAILABLE, now = null).tilesUi("$", utc)

        assertThat(ui.nowLabel).isEqualTo(NowLabel.Now)
        assertThat(ui.note).isEqualTo(PriceNote.UnavailableSince("17:30"))
    }

    @Test
    fun `should show halted instead of a change`() {
        assertThat(price.copy(status = StoryPriceStatus.HALTED).tilesUi("$", utc).badge).isEqualTo(PriceBadge.Halted)
    }

    @Test
    fun `should name the dates for an older story and the weekday for a previous close`() {
        val older = price.copy(basis = StoryPriceBasis.STORY_DATE).tilesUi("$", utc)
        val beforeOpen = price.copy(basis = StoryPriceBasis.PREVIOUS_CLOSE).tilesUi("$", utc)

        assertThat(older.atStoryLabel).isEqualTo(AtStoryLabel.StoryDate("21 Oct"))
        assertThat(older.nowLabel).isEqualTo(NowLabel.SinceDate("21 Oct"))
        assertThat(beforeOpen.atStoryLabel).isEqualTo(AtStoryLabel.PreviousClose("Tue"))
    }

    @Test
    fun `should remind on big moves`() {
        assertThat(price.copy(bigMove = true).tilesUi("$", utc).note).isEqualTo(PriceNote.BigMove)
    }

    @Test
    fun `should pick the disclaimer from the backend basis`() {
        val story = testStory("s1").copy(publishedAt = storyTime)

        assertThat(story.disclaimer(utc)).isEqualTo(StoryDisclaimer.SinceStoryTime("15:12"))
        assertThat(story.copy(movesBasis = StoryPriceBasis.LAST_CLOSE).disclaimer(utc))
            .isEqualTo(StoryDisclaimer.SinceLastClose)
    }

    @Test
    fun `should word add all by how many are already watched`() {
        val stocks = listOf(testStock("DAL"), testStock("XOM"))

        assertThat(
            watchAllUi(stocks, StockWatchState())
        ).isEqualTo(WatchAllUi.AddAll(count = 2, enabled = true, loading = false))
        assertThat(watchAllUi(stocks, StockWatchState(watched = setOf("DAL"))))
            .isEqualTo(WatchAllUi.AddRest(count = 1, enabled = true, loading = false))
        assertThat(watchAllUi(stocks, StockWatchState(watched = setOf("DAL", "XOM"))))
            .isEqualTo(WatchAllUi.AllWatched(count = 2))
        val oneStar = StockWatchState(pending = setOf("XOM"))
        val twoStars = StockWatchState(pending = setOf("DAL", "XOM"))
        val addingAll = StockWatchState(pending = setOf("DAL", "XOM"), addingAll = true)
        assertThat(watchAllUi(stocks, oneStar)?.enabled).isTrue()
        assertThat(watchAllUi(stocks, twoStars)?.loading).isFalse()
        assertThat(watchAllUi(stocks, addingAll)?.loading).isTrue()
        assertThat(watchAllUi(stocks, addingAll)?.enabled).isFalse()
        assertThat(addingAll.showsRowSpinner("XOM")).isFalse()
        assertThat(oneStar.showsRowSpinner("XOM")).isTrue()
        assertThat(watchAllUi(stocks.take(1), StockWatchState())).isNull()
        assertThat(watchAllUi(emptyList(), StockWatchState())).isNull()
    }

    @Test
    fun `should fall back to the reason when the backend sends no connection`() {
        val story = testStory("s1")
        val sheet = testStock("DAL").sheetUi(story, StockWatchState(watched = setOf("DAL")), utc)

        assertThat(sheet.connection).isEqualTo("Named in this story: “Story s1”")
        assertThat(sheet.tickerLine).isEqualTo("DAL · NYSE")
        assertThat(sheet.watched).isTrue()
    }

    @Test
    fun `should summarise barely moved stocks`() {
        val story =
            testStory("s1", barelyMoved = listOf(testStock("KO"), testStock("FDX"), testStock("DOW"), testStock("UPS")))

        val ui = story.barelyMovedUi()

        assertThat(ui?.count).isEqualTo(4)
        assertThat(ui?.logos?.map { it.ticker }).containsExactly("KO", "FDX", "DOW").inOrder()
        assertThat(ui?.names).isEqualTo("KO, FDX, DOW, UPS")
        assertThat(testStory("s2").barelyMovedUi()).isNull()
    }

    @Test
    fun `should split explainer sections into paragraphs`() {
        val ui = ExplainerSection(heading = "What happened", body = "One.\n\n  Two.  \n\n\n").toUi()

        assertThat(ui.paragraphs).containsExactly("One.", "Two.").inOrder()
    }

    private val history = listOf(
        PricePoint("2026-10-20", 10.0),
        PricePoint("2026-10-21", 11.0),
        PricePoint("2026-10-22", 12.0)
    )

    @Test
    fun `should mark the story day the backend gives on the chart`() {
        val ui = testStock("DAL").copy(history = history, historyStoryIndex = 1).chartUi()

        assertThat(ui?.storyIndex).isEqualTo(1)
        assertThat(ui?.storyDay).isEqualTo("21 Oct")
        assertThat(ui?.days).isEqualTo(3)
        assertThat(ui?.rising).isTrue()
    }

    @Test
    fun `should draw no chart without enough history or a valid story day`() {
        assertThat(testStock("DAL").copy(history = history.take(1), historyStoryIndex = 0).chartUi()).isNull()
        assertThat(testStock("DAL").copy(history = history, historyStoryIndex = null).chartUi()).isNull()
        assertThat(testStock("DAL").copy(history = history, historyStoryIndex = 5).chartUi()).isNull()
    }

    @Test
    fun `should colour the chart by the line it draws, not the move since the story`() {
        val upSinceStory = testStock("XOM").let { it.copy(price = it.price.copy(changePercent = 0.4)) }
        val falling = history.reversed()

        assertThat(upSinceStory.copy(history = falling, historyStoryIndex = 0).chartUi()?.rising).isFalse()
    }

    @Test
    fun `should show a malformed chart date as it came`() {
        val odd = listOf(PricePoint("2026-11", 10.0), PricePoint("", 11.0))

        assertThat(testStock("DAL").copy(history = odd, historyStoryIndex = 0).chartUi()?.storyDay).isEqualTo("2026-11")
        assertThat(testStock("DAL").copy(history = odd, historyStoryIndex = 1).chartUi()?.storyDay).isEqualTo("")
    }
}
