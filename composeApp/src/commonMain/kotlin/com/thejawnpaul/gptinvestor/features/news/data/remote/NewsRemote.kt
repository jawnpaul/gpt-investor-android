package com.thejawnpaul.gptinvestor.features.news.data.remote

import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.news.domain.model.ExplainerRef
import com.thejawnpaul.gptinvestor.features.news.domain.model.ExplainerSection
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPrice
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceBasis
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceStatus
import com.thejawnpaul.gptinvestor.features.news.domain.model.StorySource
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsFeedRemote(
    @SerialName("page") val page: Int = 1,
    @SerialName("page_size") val pageSize: Int = 0,
    @SerialName("topics") val topics: List<NewsTopicRemote> = emptyList(),
    @SerialName("latest_published_at") val latestPublishedAt: Long = 0L,
    @SerialName("personalised") val personalised: Boolean = true,
    @SerialName("stories") val stories: List<StoryRemote> = emptyList()
)

@Serializable
data class NewsTopicRemote(@SerialName("id") val id: String, @SerialName("label") val label: String)

@Serializable
data class TopStoryRemote(@SerialName("story") val story: StoryRemote? = null)

@Serializable
data class StoryRemote(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("summary") val summary: String? = null,
    @SerialName("link") val link: String,
    @SerialName("publisher") val publisher: String,
    @SerialName("published_at") val publishedAt: Long,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("topics") val topics: List<NewsTopicRemote> = emptyList(),
    @SerialName("watched_tickers") val watchedTickers: List<String> = emptyList(),
    @SerialName("connected_count") val connectedCount: Int = 0,
    @SerialName("moved_count") val movedCount: Int = 0,
    @SerialName("moves_basis") val movesBasis: String? = null,
    @SerialName("moved") val moved: List<StoryStockRemote> = emptyList(),
    @SerialName("barely_moved") val barelyMoved: List<StoryStockRemote> = emptyList(),
    @SerialName("explainer") val explainer: ExplainerRefRemote? = null
)

@Serializable
data class StoryStockRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("name") val name: String,
    @SerialName("exchange") val exchange: String? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("reason") val reason: String,
    @SerialName("connection") val connection: String? = null,
    @SerialName("source") val source: StorySourceRemote? = null,
    @SerialName("on_watchlist") val onWatchlist: Boolean = false,
    @SerialName("price") val price: StoryPriceRemote = StoryPriceRemote()
)

@Serializable
data class StoryPriceRemote(
    @SerialName("basis") val basis: String? = null,
    @SerialName("at_story") val atStory: Double? = null,
    @SerialName("at_story_time") val atStoryTime: Long? = null,
    @SerialName("now") val now: Double? = null,
    @SerialName("now_time") val nowTime: Long? = null,
    @SerialName("change_percent") val changePercent: Double? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("next_open_time") val nextOpenTime: Long? = null,
    @SerialName("big_move") val bigMove: Boolean = false
)

@Serializable
data class StorySourceRemote(
    @SerialName("title") val title: String,
    @SerialName("detail") val detail: String? = null,
    @SerialName("url") val url: String? = null
)

@Serializable
data class ExplainerRefRemote(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("read_minutes") val readMinutes: Int = 0
)

@Serializable
data class ExplainerRemote(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("story_link") val storyLink: String,
    @SerialName("read_minutes") val readMinutes: Int = 0,
    @SerialName("updated_at") val updatedAt: Long = 0L,
    @SerialName("sections") val sections: List<ExplainerSectionRemote> = emptyList(),
    @SerialName("sources") val sources: List<StorySourceRemote> = emptyList(),
    @SerialName("stocks") val stocks: List<StoryStockRemote> = emptyList(),
    @SerialName("is_liked") val isLiked: Boolean = false,
    @SerialName("is_bookmarked") val isBookmarked: Boolean = false
)

@Serializable
data class ExplainerSectionRemote(@SerialName("heading") val heading: String, @SerialName("body") val body: String)

fun NewsFeedRemote.toMeta() = NewsFeedMeta(
    topics = topics.map { it.toDomain() },
    latestPublishedAt = latestPublishedAt,
    personalised = personalised
)

fun StoryRemote.toDomain() = Story(
    id = id,
    title = title,
    summary = summary?.takeIf { it.isNotBlank() },
    link = link,
    publisher = publisher,
    publishedAt = publishedAt,
    imageUrl = imageUrl?.takeIf { it.isNotBlank() }?.toHttpsUrl(),
    topics = topics.map { it.toDomain() },
    watchedTickers = watchedTickers,
    connectedCount = connectedCount,
    movedCount = movedCount,
    movesBasis = StoryPriceBasis.of(movesBasis),
    moved = moved.map { it.toDomain() },
    barelyMoved = barelyMoved.map { it.toDomain() },
    explainer = explainer?.let { ExplainerRef(id = it.id, title = it.title, readMinutes = it.readMinutes) }
)

fun StoryStockRemote.toDomain() = StoryStock(
    ticker = ticker,
    name = name,
    exchange = exchange,
    logoUrl = logoUrl?.takeIf { it.isNotBlank() }?.toHttpsUrl(),
    reason = reason,
    connection = connection?.takeIf { it.isNotBlank() },
    source = source?.toDomain(),
    isOnWatchlist = onWatchlist,
    price = StoryPrice(
        basis = StoryPriceBasis.of(price.basis),
        atStory = price.atStory,
        atStoryTime = price.atStoryTime,
        now = price.now,
        nowTime = price.nowTime,
        changePercent = price.changePercent,
        status = StoryPriceStatus.of(price.status),
        nextOpenTime = price.nextOpenTime,
        bigMove = price.bigMove
    )
)

fun NewsTopicRemote.toDomain() = NewsTopic(id = id, label = label)

fun StorySourceRemote.toDomain() = StorySource(title = title, detail = detail, url = url)

fun ExplainerRemote.toDomain() = Explainer(
    id = id,
    title = title,
    author = author,
    storyLink = storyLink,
    readMinutes = readMinutes,
    updatedAt = updatedAt,
    sections = sections.map { ExplainerSection(heading = it.heading, body = it.body) },
    sources = sources.map { it.toDomain() },
    stocks = stocks.map { it.toDomain() },
    isLiked = isLiked,
    isBookmarked = isBookmarked
)
