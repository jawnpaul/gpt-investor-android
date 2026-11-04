package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsTopic
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedContent
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFeedState
import com.thejawnpaul.gptinvestor.features.news.presentation.NewsFilter
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryCardUi
import com.thejawnpaul.gptinvestor.news_add_stocks
import com.thejawnpaul.gptinvestor.news_cold_start_body
import com.thejawnpaul.gptinvestor.news_cold_start_title
import com.thejawnpaul.gptinvestor.news_connected_moved
import com.thejawnpaul.gptinvestor.news_empty_body
import com.thejawnpaul.gptinvestor.news_empty_title
import com.thejawnpaul.gptinvestor.news_error_title
import com.thejawnpaul.gptinvestor.news_filter_all
import com.thejawnpaul.gptinvestor.news_filter_empty_body
import com.thejawnpaul.gptinvestor.news_filter_empty_title
import com.thejawnpaul.gptinvestor.news_filter_for_you
import com.thejawnpaul.gptinvestor.news_for_you_strip
import com.thejawnpaul.gptinvestor.news_loading_more
import com.thejawnpaul.gptinvestor.news_manage
import com.thejawnpaul.gptinvestor.news_more
import com.thejawnpaul.gptinvestor.news_more_error
import com.thejawnpaul.gptinvestor.news_none_moved_yet
import com.thejawnpaul.gptinvestor.news_paste_link_body
import com.thejawnpaul.gptinvestor.news_paste_link_title
import com.thejawnpaul.gptinvestor.news_popular_now
import com.thejawnpaul.gptinvestor.news_see_all
import com.thejawnpaul.gptinvestor.news_show_all
import com.thejawnpaul.gptinvestor.news_top_story_eyebrow
import com.thejawnpaul.gptinvestor.news_top_story_title
import com.thejawnpaul.gptinvestor.news_watch_reason
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiSpinner
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.try_again
import org.jetbrains.compose.resources.stringResource

private val StoryThumbSize = 72.dp
private val StackLogoSize = 24.dp
private val StackOverlap = 6.dp
private const val SKELETON_CARDS = 3

@Composable
fun NewsTab(
    state: NewsFeedState,
    stories: LazyPagingItems<StoryCardUi>,
    onEvent: (NewsFeedEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val reducedMotion = isReducedMotionEnabled()
    val content = state.content(refresh = stories.loadState.refresh, storyCount = stories.itemCount)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.sectionGap),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        item(key = "news-filters", contentType = "news-filters") {
            FilterRow(
                filters = state.filters,
                selected = state.filter,
                onSelect = { onEvent(NewsFeedEvent.SelectFilter(it)) }
            )
        }
        when (content) {
            NewsFeedContent.Loading -> items(
                SKELETON_CARDS,
                key = { "news-skeleton-$it" },
                contentType = { "news-skeleton" }
            ) {
                StoryCardSkeleton(modifier = newsItemMotion(reducedMotion))
            }

            NewsFeedContent.Error -> item(key = "news-error", contentType = "news-state") {
                GiErrorState(
                    title = stringResource(Res.string.news_error_title),
                    body = stringResource(Res.string.check_connection_try_again),
                    actionText = stringResource(Res.string.try_again),
                    onAction = stories::retry,
                    modifier = newsItemMotion(reducedMotion).padding(top = GiSpace.x6)
                )
            }

            NewsFeedContent.Empty -> item(key = "news-empty", contentType = "news-state") {
                GiEmptyState(
                    icon = GiIcons.newspaper,
                    title = stringResource(Res.string.news_empty_title),
                    body = stringResource(Res.string.news_empty_body),
                    actionText = stringResource(Res.string.try_again),
                    onAction = stories::refresh,
                    modifier = newsItemMotion(reducedMotion).padding(top = GiSpace.x6)
                )
            }

            NewsFeedContent.FilterEmpty -> item(key = "news-filter-empty", contentType = "news-state") {
                GiEmptyState(
                    icon = GiIcons.newspaper,
                    title = stringResource(Res.string.news_filter_empty_title),
                    body = stringResource(Res.string.news_filter_empty_body),
                    actionText = stringResource(Res.string.news_show_all),
                    onAction = { onEvent(NewsFeedEvent.ShowAll) },
                    modifier = newsItemMotion(reducedMotion).padding(top = GiSpace.x6)
                )
            }

            is NewsFeedContent.ColdStart -> {
                item(key = "news-cold-start", contentType = "news-cold-start") {
                    ColdStartCard(
                        onAddStocks = { onEvent(NewsFeedEvent.AddStocks) },
                        modifier = newsItemMotion(reducedMotion)
                    )
                }
                if (content.showPopularHeader) {
                    item(key = "news-popular", contentType = "news-header") {
                        GiEyebrow(
                            text = stringResource(Res.string.news_popular_now),
                            modifier = newsItemMotion(reducedMotion).semantics { heading() }
                        )
                    }
                }
                storyItems(stories, reducedMotion, onEvent)
            }

            is NewsFeedContent.Stories -> {
                if (content.showForYouStrip) {
                    item(key = "news-for-you-strip", contentType = "news-strip") {
                        ForYouStrip(
                            onManage = { onEvent(NewsFeedEvent.ManageWatchlist) },
                            modifier = newsItemMotion(reducedMotion)
                        )
                    }
                }
                storyItems(stories, reducedMotion, onEvent)
            }
        }
        appendFooter(stories)
        item(key = "news-paste-link", contentType = "news-paste-link") {
            PasteLinkCard(
                onClick = { onEvent(NewsFeedEvent.PasteLink) },
                modifier = newsItemMotion(reducedMotion)
            )
        }
    }
}

private fun LazyListScope.storyItems(
    stories: LazyPagingItems<StoryCardUi>,
    reducedMotion: Boolean,
    onEvent: (NewsFeedEvent) -> Unit
) {
    items(
        count = stories.itemCount,
        key = stories.itemKey { "story-${it.id}" },
        contentType = stories.itemContentType { "news-story" }
    ) { index ->
        val card = stories[index] ?: return@items
        StoryCard(
            card = card,
            onClick = { onEvent(NewsFeedEvent.OpenStory(card.id)) },
            modifier = newsItemMotion(reducedMotion)
        )
    }
}

private fun LazyListScope.appendFooter(stories: LazyPagingItems<StoryCardUi>) {
    when (stories.loadState.append) {
        is LoadState.Loading -> item(key = "news-append-loading", contentType = "news-append") {
            val description = stringResource(Res.string.news_loading_more)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = GiSpace.x4)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = description
                    },
                contentAlignment = Alignment.Center
            ) {
                GiSpinner()
            }
        }

        is LoadState.Error -> item(key = "news-append-error", contentType = "news-append") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiText(
                    text = stringResource(Res.string.news_more_error),
                    style = GiTextStyle.BodySmall,
                    modifier = Modifier.weight(1f)
                )
                GiButton(
                    text = stringResource(Res.string.try_again),
                    onClick = stories::retry,
                    size = GiButtonSize.Medium,
                    variant = GiButtonVariant.Text
                )
            }
        }

        is LoadState.NotLoading -> Unit
    }
}

private fun LazyItemScope.newsItemMotion(reducedMotion: Boolean): Modifier = if (reducedMotion) {
    Modifier.animateItem(fadeInSpec = null, placementSpec = null, fadeOutSpec = null)
} else {
    Modifier.animateItem(
        fadeInSpec = tween(GiMotion.BASE, easing = GiEasing.emphasised),
        placementSpec = GiMotion.smooth(),
        fadeOutSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised)
    )
}

@Composable
private fun FilterRow(filters: List<NewsFilter>, selected: NewsFilter, onSelect: (NewsFilter) -> Unit) {
    Row(
        modifier = Modifier
            .bleedHorizontally()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = GiSpace.gutter),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        filters.forEach { filter ->
            GiChip(
                text = filter.label(),
                onClick = { onSelect(filter) },
                selected = filter == selected,
                leadingIcon = filter.icon,
                singleChoice = true
            )
        }
    }
}

@Composable
private fun ForYouStrip(onManage: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiText(
            text = stringResource(Res.string.news_for_you_strip),
            style = GiTextStyle.BodySmall,
            modifier = Modifier.weight(1f)
        )
        GiButton(
            text = stringResource(Res.string.news_manage),
            onClick = onManage,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Text
        )
    }
}

@Composable
private fun ColdStartCard(onAddStocks: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth()) {
        Icon(
            imageVector = GiIcons.sparkle,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.md),
            tint = GiTheme.colors.violet
        )
        GiText(
            text = stringResource(Res.string.news_cold_start_title),
            style = GiTextStyle.Label,
            modifier = Modifier.semantics { heading() }
        )
        GiText(text = stringResource(Res.string.news_cold_start_body), style = GiTextStyle.BodySmall)
        GiButton(
            text = stringResource(Res.string.news_add_stocks),
            onClick = onAddStocks,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary,
            leadingIcon = GiIcons.plus
        )
    }
}

@Composable
internal fun StoryCard(card: StoryCardUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x4), verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
                GiText(text = card.meta, style = GiTextStyle.Meta)
                GiText(
                    text = card.title,
                    style = GiTextStyle.Body,
                    color = GiTheme.colors.ink,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (card.hasWhyTags) WhyTags(watchedTicker = card.watchedTicker, topic = card.topic)
            }
            StoryThumb(imageUrl = card.imageUrl, size = StoryThumbSize)
        }
        MoversLine(card = card, modifier = Modifier.padding(top = GiSpace.x1))
    }
}

@Composable
private fun WhyTags(watchedTicker: String?, topic: NewsTopic?) {
    FlowRow(
        modifier = Modifier.padding(top = GiSpace.x1),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1_5),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        watchedTicker?.let {
            NewsTag(text = stringResource(Res.string.news_watch_reason, it), icon = GiIcons.starFill, accent = true)
        }
        topic?.let { NewsTag(text = it.label, icon = GiIcons.newspaper) }
    }
}

@Composable
private fun NewsTag(text: String, icon: ImageVector, accent: Boolean = false) {
    val colors = GiTheme.colors
    Row(
        modifier = Modifier
            .clip(GiRadius.xs)
            .background(if (accent) colors.violetSoft else colors.tile)
            .padding(horizontal = GiSpace.x1_5, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = if (accent) colors.violet else colors.muted
        )
        GiText(text = text, style = GiTextStyle.Tag, color = if (accent) colors.violet else colors.body)
    }
}

@Composable
private fun MoversLine(card: StoryCardUi, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(GiTheme.colors.line)
        )
        Row(
            modifier = Modifier.padding(top = GiSpace.x3),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.hasMovers) LogoStack(stocks = card.movers)
            Column(modifier = Modifier.weight(1f)) {
                if (card.hasMovers) {
                    GiText(
                        text = stringResource(Res.string.news_connected_moved, card.movedCount, card.connectedCount),
                        style = GiTextStyle.BodySmall,
                        color = GiTheme.colors.ink
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
                        GiText(
                            text = card.moverNames,
                            style = GiTextStyle.Meta,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (card.moreMovers > 0) {
                            GiText(
                                text = stringResource(Res.string.news_more, card.moreMovers),
                                style = GiTextStyle.Meta
                            )
                        }
                    }
                } else {
                    GiText(text = stringResource(Res.string.news_none_moved_yet), style = GiTextStyle.Meta)
                }
            }
        }
    }
}

@Composable
internal fun LogoStack(stocks: List<StoryStock>, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clearAndSetSemantics {}) {
        stocks.forEachIndexed { index, stock ->
            GiLogoTile(
                initials = stock.ticker.take(2),
                imageUrl = stock.logoUrl,
                size = StackLogoSize,
                modifier = Modifier
                    .offset(x = (StackLogoSize - StackOverlap) * index)
                    .border(2.dp, GiTheme.colors.raised, GiRadius.sm)
            )
        }
        Box(
            Modifier.size(
                width = StackLogoSize + (StackLogoSize - StackOverlap) * (stocks.size - 1),
                height = StackLogoSize
            )
        )
    }
}

@Composable
internal fun StoryThumb(imageUrl: String?, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(GiRadius.sm)
            .background(GiTheme.colors.violetSoft)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GiIcons.mark,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.lg),
            tint = GiTheme.colors.violet
        )
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun PasteLinkCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick, contentPadding = PaddingValues(GiSpace.x4)) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = GiIcons.chatText,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.md),
                tint = GiTheme.colors.violet
            )
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = stringResource(Res.string.news_paste_link_title), style = GiTextStyle.Label)
                GiText(text = stringResource(Res.string.news_paste_link_body), style = GiTextStyle.Meta)
            }
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = GiTheme.colors.muted
            )
        }
    }
}

@Composable
private fun StoryCardSkeleton(modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth().height(16.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
            }
            GiSkeleton(modifier = Modifier.size(StoryThumbSize))
        }
        GiSkeleton(modifier = Modifier.fillMaxWidth(0.8f).height(14.dp))
    }
}

@Composable
fun HomeTopStory(card: StoryCardUi, onOpen: () -> Unit, onSeeAll: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
        GiSectionHeader(
            title = stringResource(Res.string.news_top_story_title),
            eyebrow = stringResource(Res.string.news_top_story_eyebrow),
            eyebrowIcon = GiIcons.newspaper,
            trailingText = stringResource(Res.string.news_see_all),
            onTrailingClick = onSeeAll
        )
        StoryCard(card = card, onClick = onOpen)
    }
}

@Composable
private fun NewsFilter.label(): String = when (this) {
    NewsFilter.All -> stringResource(Res.string.news_filter_all)
    NewsFilter.ForYou -> stringResource(Res.string.news_filter_for_you)
    is NewsFilter.Topic -> topic.label
}

private fun Modifier.bleedHorizontally(): Modifier = layout { measurable, constraints ->
    val bleed = GiSpace.gutter.roundToPx()
    val placeable = measurable.measure(
        constraints.copy(minWidth = constraints.maxWidth + bleed * 2, maxWidth = constraints.maxWidth + bleed * 2)
    )
    layout(constraints.maxWidth, placeable.height) { placeable.place(-bleed, 0) }
}

private val NewsFilter.icon: ImageVector?
    get() = if (this == NewsFilter.ForYou) GiIcons.sparkle else null
