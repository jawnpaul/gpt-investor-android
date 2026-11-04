package com.thejawnpaul.gptinvestor.features.news.presentation

import com.thejawnpaul.gptinvestor.core.utility.relativeTime
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock

private const val CARD_MOVERS = 3

sealed interface NewsFilter {
    val id: String

    data object All : NewsFilter {
        override val id = "all"
    }

    data object ForYou : NewsFilter {
        override val id = "for_you"
    }

    data class Topic(val topic: NewsTopic) : NewsFilter {
        override val id: String get() = topic.id
    }
}

data class StoryCardUi(
    val id: String,
    val meta: String,
    val title: String,
    val imageUrl: String?,
    val movers: List<StoryStock>,
    val moverNames: String,
    val moreMovers: Int,
    val movedCount: Int,
    val connectedCount: Int,
    val watchedTicker: String?,
    val topic: NewsTopic?
) {
    val hasMovers: Boolean get() = movers.isNotEmpty()

    val hasWhyTags: Boolean get() = watchedTicker != null || topic != null
}

fun storyMeta(publisher: String, publishedAt: Long, now: Long): String =
    listOf(publisher, relativeTime(publishedAt, now)).filter { it.isNotBlank() }.joinToString(" · ")

fun Story.toCard(now: Long, showWhy: Boolean): StoryCardUi {
    val shown = moved.take(CARD_MOVERS)
    return StoryCardUi(
        id = id,
        meta = storyMeta(publisher, publishedAt, now),
        title = title,
        imageUrl = imageUrl,
        movers = shown,
        moverNames = shown.joinToString(", ") { it.name },
        moreMovers = (movedCount - shown.size).coerceAtLeast(0),
        movedCount = movedCount,
        connectedCount = connectedCount,
        watchedTicker = if (showWhy) watchedTickers.firstOrNull() else null,
        topic = if (showWhy) topics.firstOrNull() else null
    )
}
