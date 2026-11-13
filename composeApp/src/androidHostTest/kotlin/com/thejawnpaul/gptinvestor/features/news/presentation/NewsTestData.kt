package com.thejawnpaul.gptinvestor.features.news.presentation

import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPrice
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceBasis
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceStatus
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock

internal fun testStock(ticker: String, watched: Boolean = false) = StoryStock(
    ticker = ticker,
    name = ticker,
    exchange = "NYSE",
    logoUrl = null,
    reason = "Named in this story",
    connection = null,
    source = null,
    isOnWatchlist = watched,
    currencySymbol = "$",
    price = StoryPrice(
        basis = StoryPriceBasis.STORY_TIME,
        atStory = 10.0,
        atStoryTime = 1_000L,
        now = 11.0,
        nowTime = 2_000L,
        changePercent = 10.0,
        status = StoryPriceStatus.LIVE,
        nextOpenTime = null,
        bigMove = true
    )
)

internal fun testStory(
    id: String,
    moved: List<StoryStock> = listOf(testStock("DAL")),
    barelyMoved: List<StoryStock> = emptyList()
) = Story(
    id = id,
    title = "Story $id",
    summary = null,
    link = "https://news/$id",
    publisher = "Reuters",
    publishedAt = 1_000L,
    imageUrl = null,
    topics = listOf(NewsTopic(id = "energy", label = "Energy")),
    watchedTickers = emptyList(),
    connectedCount = moved.size + barelyMoved.size,
    movedCount = moved.size,
    movesBasis = StoryPriceBasis.STORY_TIME,
    moved = moved,
    barelyMoved = barelyMoved,
    explainer = null
)
