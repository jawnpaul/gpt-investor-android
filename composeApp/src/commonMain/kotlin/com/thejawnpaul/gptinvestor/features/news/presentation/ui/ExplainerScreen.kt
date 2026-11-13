package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.news.domain.model.StorySource
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerContent
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.ExplainerState
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.news_explainer_byline
import com.thejawnpaul.gptinvestor.news_explainer_disclaimer
import com.thejawnpaul.gptinvestor.news_explainer_error_title
import com.thejawnpaul.gptinvestor.news_explainer_label
import com.thejawnpaul.gptinvestor.news_explainer_save
import com.thejawnpaul.gptinvestor.news_explainer_share
import com.thejawnpaul.gptinvestor.news_explainer_stocks
import com.thejawnpaul.gptinvestor.news_read_story
import com.thejawnpaul.gptinvestor.news_story_unavailable_body
import com.thejawnpaul.gptinvestor.news_story_unavailable_title
import com.thejawnpaul.gptinvestor.remove_from_saved
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiSpinner
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.try_again
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExplainerScreen(
    state: ExplainerState,
    onEvent: (ExplainerEvent) -> Unit,
    toastState: GiToastState,
    modifier: Modifier = Modifier
) {
    val explainer = state.explainer
    val reducedMotion = isReducedMotionEnabled()
    val listState = rememberLazyListState()
    val scrolled by remember(listState) { derivedStateOf { listState.canScrollBackward } }
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = null,
                onBack = { onEvent(ExplainerEvent.Back) },
                backContentDescription = stringResource(Res.string.back),
                scrolled = scrolled
            )
        },
        snackbarHost = { GiToastHost(state = toastState, modifier = Modifier.padding(horizontal = GiSpace.gutter)) },
        bottomBar = { if (explainer != null) ExplainerActions(state = state, onEvent = onEvent) }
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding).fillMaxSize()
        AnimatedContent(
            targetState = state.content,
            contentKey = { it::class },
            transitionSpec = {
                if (reducedMotion) {
                    EnterTransition.None togetherWith ExitTransition.None
                } else {
                    fadeIn(tween(GiMotion.BASE, easing = GiEasing.emphasised)) togetherWith
                        fadeOut(tween(GiMotion.FAST, easing = GiEasing.emphasised))
                }
            },
            label = "explainer_content"
        ) { content ->
            when (content) {
                is ExplainerContent.Loaded -> ExplainerBody(
                    explainer = content.explainer,
                    state = state,
                    listState = listState,
                    onEvent = onEvent,
                    modifier = contentModifier
                )

                ExplainerContent.Loading -> Box(contentModifier, contentAlignment = Alignment.Center) { GiSpinner() }

                ExplainerContent.Unavailable -> GiEmptyState(
                    icon = GiIcons.newspaper,
                    title = stringResource(Res.string.news_story_unavailable_title),
                    body = stringResource(Res.string.news_story_unavailable_body),
                    actionText = stringResource(Res.string.back),
                    onAction = { onEvent(ExplainerEvent.Back) },
                    modifier = contentModifier.padding(GiSpace.gutter)
                )

                ExplainerContent.Error -> GiErrorState(
                    title = stringResource(Res.string.news_explainer_error_title),
                    body = stringResource(Res.string.check_connection_try_again),
                    actionText = stringResource(Res.string.try_again),
                    onAction = { onEvent(ExplainerEvent.Retry) },
                    modifier = contentModifier.padding(GiSpace.gutter)
                )
            }
        }
    }
}

@Composable
private fun ExplainerBody(
    explainer: Explainer,
    state: ExplainerState,
    listState: LazyListState,
    onEvent: (ExplainerEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(
            start = GiSpace.gutter,
            end = GiSpace.gutter,
            top = GiSpace.x2,
            bottom = GiSpace.sectionGap
        ),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x5)
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiText(
                    text = explainer.title,
                    style = GiTextStyle.PageTitle,
                    modifier = Modifier.semantics { heading() }
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiText(
                        text = stringResource(
                            Res.string.news_explainer_byline,
                            explainer.author,
                            explainer.readMinutes,
                            state.updatedTime
                        ),
                        style = GiTextStyle.Meta,
                        modifier = Modifier.weight(1f)
                    )
                    ExplainerLabel()
                }
            }
        }
        itemsIndexed(
            state.sections,
            key = { index, _ -> "section-$index" },
            contentType = { _, _ -> "explainer-section" }
        ) { _, section ->
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiText(
                    text = section.heading,
                    style = GiTextStyle.SectionTitle,
                    color = GiTheme.colors.ink,
                    modifier = Modifier.semantics { heading() }
                )
                section.paragraphs.forEach { paragraph -> GiText(text = paragraph, style = GiTextStyle.Body) }
            }
        }
        if (explainer.sources.isNotEmpty()) {
            item(key = "sources") {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                    GiEyebrow(
                        text = stringResource(Res.string.sources),
                        modifier = Modifier.semantics { heading() }
                    )
                    explainer.sources.forEach { source ->
                        SourceRow(source = source, onOpen = { onEvent(ExplainerEvent.OpenSource(it)) })
                    }
                }
            }
        }
        if (state.stocks.isNotEmpty()) {
            item(key = "stocks") {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                    GiEyebrow(
                        text = stringResource(Res.string.news_explainer_stocks),
                        modifier = Modifier.semantics { heading() }
                    )
                    ConnectedStockList(
                        stocks = state.stocks,
                        watch = state.watch,
                        onTap = { onEvent(ExplainerEvent.OpenStock(it)) },
                        onToggleWatch = { onEvent(ExplainerEvent.ToggleWatch(it)) }
                    )
                    state.watchAll?.let { WatchAllButton(ui = it, onClick = { onEvent(ExplainerEvent.WatchAll) }) }
                }
            }
        }
        item(key = "disclaimer") {
            GiText(text = stringResource(Res.string.news_explainer_disclaimer), style = GiTextStyle.Meta)
        }
    }
}

@Composable
private fun ExplainerLabel() {
    GiText(
        text = stringResource(Res.string.news_explainer_label),
        style = GiTextStyle.Tag,
        color = GiTheme.colors.violet,
        modifier = Modifier
            .clip(GiRadius.xs)
            .background(GiTheme.colors.violetSoft)
            .padding(horizontal = GiSpace.x2, vertical = 2.dp)
    )
}

@Composable
private fun SourceRow(source: StorySource, onOpen: (String) -> Unit) {
    val url = source.url
    GiCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = url?.let { { onOpen(it) } },
        contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = source.title, style = GiTextStyle.Label)
                source.detail?.let { GiText(text = it, style = GiTextStyle.Meta) }
            }
            if (url != null) {
                Icon(
                    imageVector = GiIcons.arrowSquareOut,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.sm),
                    tint = GiTheme.colors.muted
                )
            }
        }
    }
}

@Composable
private fun ExplainerActions(state: ExplainerState, onEvent: (ExplainerEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GiTheme.colors.bg)
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!state.isGuestSession) {
            GiIconToggleButton(
                checked = state.isLiked,
                onCheckedChange = { onEvent(ExplainerEvent.SetHelpful(it)) },
                icon = GiIcons.thumbsUp,
                checkedIcon = GiIcons.thumbsUpFill,
                contentDescription = stringResource(Res.string.helpful)
            )
            GiIconToggleButton(
                checked = state.isBookmarked,
                onCheckedChange = { onEvent(ExplainerEvent.SetSaved(it)) },
                icon = GiIcons.bookmarkSimple,
                checkedIcon = GiIcons.bookmarkSimpleFill,
                contentDescription = stringResource(
                    if (state.isBookmarked) Res.string.remove_from_saved else Res.string.news_explainer_save
                )
            )
        }
        GiIconButton(
            icon = GiIcons.shareNetwork,
            contentDescription = stringResource(Res.string.news_explainer_share),
            onClick = { onEvent(ExplainerEvent.Share) }
        )
        GiButton(
            text = stringResource(Res.string.news_read_story),
            onClick = { onEvent(ExplainerEvent.ReadStory) },
            variant = GiButtonVariant.Secondary,
            leadingIcon = GiIcons.newspaper,
            modifier = Modifier.weight(1f)
        )
    }
}
