package com.thejawnpaul.gptinvestor.features.news.domain.model

data class NewsFeedMeta(val topics: List<NewsTopic>, val latestPublishedAt: Long, val personalised: Boolean)

data class NewsTopic(val id: String, val label: String)

data class Story(
    val id: String,
    val title: String,
    val summary: String?,
    val link: String,
    val publisher: String,
    val publishedAt: Long,
    val imageUrl: String?,
    val topics: List<NewsTopic>,
    val watchedTickers: List<String>,
    val connectedCount: Int,
    val movedCount: Int,
    val movesBasis: StoryPriceBasis,
    val moved: List<StoryStock>,
    val barelyMoved: List<StoryStock>,
    val explainer: ExplainerRef?
)

data class StoryStock(
    val ticker: String,
    val name: String,
    val exchange: String?,
    val logoUrl: String?,
    val reason: String,
    val connection: String?,
    val source: StorySource?,
    val isOnWatchlist: Boolean,
    val price: StoryPrice
)

data class StoryPrice(
    val basis: StoryPriceBasis,
    val atStory: Double?,
    val atStoryTime: Long?,
    val now: Double?,
    val nowTime: Long?,
    val changePercent: Double?,
    val status: StoryPriceStatus,
    val nextOpenTime: Long?,
    val bigMove: Boolean
)

enum class StoryPriceBasis {
    STORY_TIME,
    PREVIOUS_CLOSE,
    LAST_CLOSE,
    STORY_DATE;

    companion object {
        fun of(value: String?) = when (value) {
            "story_time" -> STORY_TIME
            "previous_close" -> PREVIOUS_CLOSE
            "story_date" -> STORY_DATE
            else -> LAST_CLOSE
        }
    }
}

enum class StoryPriceStatus {
    LIVE,
    MARKET_CLOSED,
    UNAVAILABLE,
    HALTED;

    companion object {
        fun of(value: String?) = when (value) {
            "live" -> LIVE
            "market_closed" -> MARKET_CLOSED
            "halted" -> HALTED
            else -> UNAVAILABLE
        }
    }
}

data class StorySource(val title: String, val detail: String?, val url: String?)

data class ExplainerRef(val id: String, val title: String, val readMinutes: Int)

data class Explainer(
    val id: String,
    val title: String,
    val author: String,
    val storyLink: String,
    val readMinutes: Int,
    val updatedAt: Long,
    val sections: List<ExplainerSection>,
    val sources: List<StorySource>,
    val stocks: List<StoryStock>,
    val isLiked: Boolean,
    val isBookmarked: Boolean
)

data class ExplainerSection(val heading: String, val body: String)
