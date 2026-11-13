package com.thejawnpaul.gptinvestor.features.news.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.news.domain.model.ExplainerRef
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import com.thejawnpaul.gptinvestor.features.news.domain.model.StorySource
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryStock
import com.thejawnpaul.gptinvestor.features.news.presentation.BarelyMovedUi
import com.thejawnpaul.gptinvestor.features.news.presentation.StockSheetUi
import com.thejawnpaul.gptinvestor.features.news.presentation.StockWatchState
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryContent
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryDisclaimer
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryEvent
import com.thejawnpaul.gptinvestor.features.news.presentation.StoryState
import com.thejawnpaul.gptinvestor.news_ask_about_story
import com.thejawnpaul.gptinvestor.news_barely_moved
import com.thejawnpaul.gptinvestor.news_connected_count
import com.thejawnpaul.gptinvestor.news_disclaimer
import com.thejawnpaul.gptinvestor.news_disclaimer_story_time
import com.thejawnpaul.gptinvestor.news_explainer_meta
import com.thejawnpaul.gptinvestor.news_info_disclaimer
import com.thejawnpaul.gptinvestor.news_moved_since
import com.thejawnpaul.gptinvestor.news_read_full_story
import com.thejawnpaul.gptinvestor.news_share_story
import com.thejawnpaul.gptinvestor.news_sheet_ask
import com.thejawnpaul.gptinvestor.news_sheet_connected
import com.thejawnpaul.gptinvestor.news_sheet_subtitle_watched
import com.thejawnpaul.gptinvestor.news_sheet_view_company
import com.thejawnpaul.gptinvestor.news_story_error_title
import com.thejawnpaul.gptinvestor.news_story_loading
import com.thejawnpaul.gptinvestor.news_story_unavailable_body
import com.thejawnpaul.gptinvestor.news_story_unavailable_title
import com.thejawnpaul.gptinvestor.source
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiImage
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun StoryScreen(
    state: StoryState,
    onEvent: (StoryEvent) -> Unit,
    toastState: GiToastState,
    modifier: Modifier = Modifier
) {
    val story = state.story
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
                onBack = { onEvent(StoryEvent.Back) },
                backContentDescription = stringResource(Res.string.back),
                scrolled = scrolled
            ) {
                if (story != null) {
                    GiIconButton(
                        icon = GiIcons.shareNetwork,
                        contentDescription = stringResource(Res.string.news_share_story),
                        onClick = { onEvent(StoryEvent.Share) }
                    )
                }
            }
        },
        snackbarHost = {
            GiToastHost(
                state = toastState,
                modifier = Modifier
                    .padding(horizontal = GiSpace.gutter)
                    .graphicsLayer { alpha = if (state.screenToastVisible) 1f else 0f }
            )
        },
        bottomBar = {
            if (story != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GiTheme.colors.bg)
                        .navigationBarsPadding()
                        .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x3)
                ) {
                    GiButton(
                        text = stringResource(Res.string.news_ask_about_story),
                        onClick = { onEvent(StoryEvent.AskAboutStory) },
                        leadingIcon = GiIcons.chatCircleDots,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
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
            label = "story_content"
        ) { content ->
            when (content) {
                is StoryContent.Loaded -> StoryBody(
                    state = state,
                    story = content.story,
                    listState = listState,
                    onEvent = onEvent,
                    modifier = contentModifier
                )

                StoryContent.Loading -> StorySkeleton(modifier = contentModifier)

                StoryContent.Unavailable -> GiEmptyState(
                    icon = GiIcons.newspaper,
                    title = stringResource(Res.string.news_story_unavailable_title),
                    body = stringResource(Res.string.news_story_unavailable_body),
                    actionText = stringResource(Res.string.back),
                    onAction = { onEvent(StoryEvent.Back) },
                    modifier = contentModifier.padding(GiSpace.gutter)
                )

                StoryContent.Error -> GiErrorState(
                    title = stringResource(Res.string.news_story_error_title),
                    body = stringResource(Res.string.check_connection_try_again),
                    actionText = stringResource(Res.string.try_again),
                    onAction = { onEvent(StoryEvent.Retry) },
                    modifier = contentModifier.padding(GiSpace.gutter)
                )
            }
        }
    }

    state.sheet?.let { StoryCompanySheet(sheet = it, toastState = toastState, onEvent = onEvent) }
    state.sharePreview?.let { preview ->
        SharePreviewSheet(
            preview = preview,
            onShare = { onEvent(StoryEvent.ConfirmShare) },
            onDismiss = { onEvent(StoryEvent.CloseShare) }
        )
    }
}

@Composable
private fun StoryBody(
    state: StoryState,
    story: Story,
    listState: LazyListState,
    onEvent: (StoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val reducedMotion = isReducedMotionEnabled()
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
            StoryHeader(story = story, meta = state.meta, onReadFull = { onEvent(StoryEvent.ReadFullStory) })
        }
        story.explainer?.let { explainer ->
            item(key = "explainer") {
                ExplainerCard(explainer = explainer, onClick = { onEvent(StoryEvent.OpenExplainer) })
            }
        }
        if (state.hasStocks) {
            item(key = "moved") {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                    if (state.hasMoved) {
                        GiSectionHeader(
                            title = stringResource(Res.string.news_moved_since),
                            trailingText = stringResource(
                                Res.string.news_connected_count,
                                story.movedCount,
                                story.connectedCount
                            )
                        )
                        StoryStockList(stocks = story.moved, watch = state.watch, onEvent = onEvent)
                    }
                    state.barelyMoved?.let { barelyMoved ->
                        BarelyMovedCard(
                            ui = barelyMoved,
                            expanded = state.showBarelyMoved,
                            onToggle = { onEvent(StoryEvent.ToggleBarelyMoved) }
                        )
                        AnimatedVisibility(
                            visible = state.showBarelyMoved,
                            enter = if (reducedMotion) {
                                EnterTransition.None
                            } else {
                                expandVertically(GiMotion.smooth(), expandFrom = Alignment.Top) +
                                    fadeIn(tween(GiMotion.BASE, easing = GiEasing.emphasised))
                            },
                            exit = if (reducedMotion) {
                                ExitTransition.None
                            } else {
                                shrinkVertically(GiMotion.smooth(), shrinkTowards = Alignment.Top) +
                                    fadeOut(tween(GiMotion.FAST, easing = GiEasing.emphasised))
                            }
                        ) {
                            StoryStockList(stocks = story.barelyMoved, watch = state.watch, onEvent = onEvent)
                        }
                    }
                    state.watchAll?.let { WatchAllButton(ui = it, onClick = { onEvent(StoryEvent.WatchAll) }) }
                }
            }
        }
        state.disclaimer?.let { disclaimer ->
            item(key = "disclaimer") {
                GiText(text = disclaimer.text(), style = GiTextStyle.Meta)
            }
        }
    }
}

@Composable
private fun StoryDisclaimer.text(): String = when (this) {
    is StoryDisclaimer.SinceStoryTime -> stringResource(Res.string.news_disclaimer_story_time, time)
    StoryDisclaimer.SinceLastClose -> stringResource(Res.string.news_disclaimer)
}

@Composable
private fun StoryStockList(stocks: List<StoryStock>, watch: StockWatchState, onEvent: (StoryEvent) -> Unit) {
    ConnectedStockList(
        stocks = stocks,
        watch = watch,
        onTap = { onEvent(StoryEvent.TapStock(it)) },
        onToggleWatch = { onEvent(StoryEvent.ToggleWatch(it)) }
    )
}

@Composable
private fun StoryHeader(story: Story, meta: String, onReadFull: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        if (story.imageUrl != null) {
            GiImage(
                url = story.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(GiRadius.md)
                    .background(GiTheme.colors.tile),
                shape = GiRadius.md
            )
        }
        GiText(
            text = meta,
            style = GiTextStyle.Meta,
            modifier = Modifier.padding(top = GiSpace.x1)
        )
        GiText(text = story.title, style = GiTextStyle.PageTitle, modifier = Modifier.semantics { heading() })
        story.summary?.let { GiText(text = it, style = GiTextStyle.Body) }
        GiButton(
            text = stringResource(Res.string.news_read_full_story),
            onClick = onReadFull,
            variant = GiButtonVariant.Text,
            leadingIcon = GiIcons.arrowSquareOut
        )
    }
}

@Composable
private fun StorySkeleton(modifier: Modifier = Modifier) {
    val loading = stringResource(Res.string.news_story_loading)
    Column(
        modifier = modifier
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2)
            .semantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(180.dp), shape = GiRadius.md)
        GiSkeleton(modifier = Modifier.padding(top = GiSpace.x1).fillMaxWidth(0.4f).height(12.dp))
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(GiSpace.x7))
        GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(GiSpace.x7))
        Spacer(modifier = Modifier.height(GiSpace.x1))
        listOf(1f, 1f, 1f, 0.62f).forEach { fraction ->
            GiSkeleton(modifier = Modifier.fillMaxWidth(fraction).height(14.dp))
        }
    }
}

@Composable
private fun ExplainerCard(explainer: ExplainerRef, onClick: () -> Unit) {
    GiCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = PaddingValues(GiSpace.x4),
        borderColor = GiTheme.colors.violet
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            GiIconTile(icon = GiIcons.bookOpen)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                GiText(
                    text = stringResource(Res.string.news_explainer_meta, explainer.readMinutes),
                    style = GiTextStyle.Meta
                )
                GiText(text = explainer.title, style = GiTextStyle.Label, color = GiTheme.colors.ink)
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
private fun BarelyMovedCard(ui: BarelyMovedUi, expanded: Boolean, onToggle: () -> Unit) {
    val caretRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = if (isReducedMotionEnabled()) snap() else GiMotion.snappy()
    )
    GiCard(modifier = Modifier.fillMaxWidth(), onClick = onToggle, contentPadding = PaddingValues(GiSpace.x4)) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            LogoStack(stocks = ui.logos)
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = stringResource(Res.string.news_barely_moved, ui.count), style = GiTextStyle.Label)
                GiText(
                    text = ui.names,
                    style = GiTextStyle.Meta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm).graphicsLayer { rotationZ = caretRotation },
                tint = GiTheme.colors.muted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryCompanySheet(sheet: StockSheetUi, toastState: GiToastState, onEvent: (StoryEvent) -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    var leaving by remember { mutableStateOf(false) }
    val hideThen: (StoryEvent) -> Unit = { event ->
        if (!leaving) {
            leaving = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { onEvent(event) }
        }
    }
    GiBottomSheet(
        onDismissRequest = { onEvent(StoryEvent.CloseSheet) },
        title = sheet.name,
        subtitle = if (sheet.watched) {
            stringResource(Res.string.news_sheet_subtitle_watched, sheet.tickerLine)
        } else {
            sheet.tickerLine
        },
        closeContentDescription = stringResource(Res.string.close),
        sheetState = sheetState,
        toastState = toastState,
        leading = { GiLogoTile(initials = sheet.ticker.take(2), imageUrl = sheet.logoUrl) },
        headerAction = {
            WatchStar(
                name = sheet.name,
                watched = sheet.watched,
                pending = sheet.pending,
                onToggle = { onEvent(StoryEvent.ToggleWatch(sheet.ticker)) }
            )
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
                GiEyebrow(text = stringResource(Res.string.news_sheet_connected))
                GiText(text = sheet.connection, style = GiTextStyle.Body)
            }
            sheet.source?.let { source -> SourceCard(source = source, onOpen = { onEvent(StoryEvent.OpenSource(it)) }) }
            StoryPriceTiles(ui = sheet.price)
            sheet.chart?.let { StoryPriceChart(ui = it, stockName = sheet.name, modifier = Modifier.fillMaxWidth()) }
            GiButton(
                text = stringResource(Res.string.news_sheet_view_company, sheet.name),
                onClick = { hideThen(StoryEvent.ViewCompany(sheet.ticker)) },
                modifier = Modifier.fillMaxWidth()
            )
            GiButton(
                text = stringResource(Res.string.news_sheet_ask),
                onClick = { hideThen(StoryEvent.AskFollowUp(sheet.ticker)) },
                variant = GiButtonVariant.Secondary,
                leadingIcon = GiIcons.chatCircleDots,
                modifier = Modifier.fillMaxWidth()
            )
            GiText(text = stringResource(Res.string.news_info_disclaimer), style = GiTextStyle.Meta)
        }
    }
}

@Composable
private fun SourceCard(source: StorySource, onOpen: (String) -> Unit) {
    val url = source.url
    GiCard(modifier = Modifier.fillMaxWidth(), onClick = url?.let { { onOpen(it) } }) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = GiIcons.fileText,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.md),
                tint = GiTheme.colors.muted
            )
            Column(modifier = Modifier.weight(1f)) {
                GiText(text = stringResource(Res.string.source), style = GiTextStyle.Meta)
                GiText(text = source.title, style = GiTextStyle.Label)
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
