package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add_to_watchlist
import com.thejawnpaul.gptinvestor.added_to_watchlist_success
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.couldn_t_load_brief
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyKeyNumberSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.briefChartMotion
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.briefItemMotion
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.companyBriefItems
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.questionRes
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.ContentBookmarkFill
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickEvent
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.RemoveFromWatchlistDialog
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.loading_brief
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiDeltaPill
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiPriceDisplay
import com.thejawnpaul.gptinvestor.theme.components.GiSheetSubtitle
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.today
import com.thejawnpaul.gptinvestor.top_pick_disclaimer
import com.thejawnpaul.gptinvestor.top_pick_error_title
import com.thejawnpaul.gptinvestor.top_pick_label
import com.thejawnpaul.gptinvestor.top_pick_loading
import com.thejawnpaul.gptinvestor.top_pick_save
import com.thejawnpaul.gptinvestor.top_pick_saved_to_picks
import com.thejawnpaul.gptinvestor.top_pick_score
import com.thejawnpaul.gptinvestor.top_pick_share
import com.thejawnpaul.gptinvestor.top_pick_sources_subtitle
import com.thejawnpaul.gptinvestor.try_again
import com.thejawnpaul.gptinvestor.view
import com.thejawnpaul.gptinvestor.yahoo_finance
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPickDetailScreen(
    state: TopPickDetailView,
    onEvent: (TopPickEvent) -> Unit,
    onAction: (TopPickAction) -> Unit,
    toastState: GiToastState,
    modifier: Modifier = Modifier,
    onOpenSavedList: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptics = LocalHapticFeedback.current
    val pick = state.topPick
    val company = state.companyPresentation
    val brief = state.brief
    val isWatched = brief?.isWatched == true
    val signedIn = state.isLoggedIn && !state.isGuestSession
    val companyName = brief?.name ?: company?.name ?: pick?.companyName.orEmpty()
    val savedMessage = stringResource(Res.string.top_pick_saved_to_picks)
    val watchedMessage = stringResource(Res.string.added_to_watchlist_success)
    val viewLabel = stringResource(Res.string.view)
    var isLiked by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    val openUrl: (String) -> Unit = { url -> runCatching { uriHandler.openUri(url) } }

    var lastWatched by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(brief?.isWatched) {
        val now = brief?.isWatched ?: return@LaunchedEffect
        val added = lastWatched == false && now
        lastWatched = now
        if (added) {
            haptics.performHapticFeedback(GiHaptics.Confirm)
            toastState.show(message = watchedMessage, icon = GiIcons.checkCircle)
        }
    }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colors.bg,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                Column {
                    GiTopBar(
                        title = null,
                        onBack = { onAction(TopPickAction.OnGoBack) },
                        backContentDescription = stringResource(Res.string.back),
                        scrolled = listState.canScrollBackward
                    ) {
                        GiIconButton(
                            icon = GiIcons.shareNetwork,
                            contentDescription = stringResource(Res.string.top_pick_share),
                            onClick = { onEvent(TopPickEvent.ShareTopPick) },
                            enabled = pick != null
                        )
                        if (signedIn) {
                            GiIconToggleButton(
                                checked = isWatched,
                                onCheckedChange = {
                                    if (isWatched) showRemoveDialog = true else onEvent(TopPickEvent.AddToWatchlist)
                                },
                                checkedHaptic = null,
                                icon = GiIcons.star,
                                checkedIcon = GiIcons.starFill,
                                contentDescription = stringResource(
                                    if (isWatched) Res.string.remove_from_watchlist else Res.string.add_to_watchlist
                                ),
                                enabled = brief != null,
                                loading = state.watchlistPending
                            )
                        }
                    }
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.bg)
                                .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x3),
                            onClick = { onEvent(TopPickEvent.GoToSignUp) }
                        )
                    }
                }
            },
            bottomBar = {
                if (signedIn && pick != null) {
                    GiComposerBar(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
                        docked = false
                    ) {
                        GiAskComposer(
                            value = query,
                            onValueChange = { query = it },
                            onSend = {
                                keyboardController?.hide()
                                onEvent(TopPickEvent.AskAboutPick(query))
                                query = ""
                            },
                            placeholder = stringResource(Res.string.ask_anything_about, companyName),
                            sendContentDescription = stringResource(Res.string.send),
                            floating = true
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
            ) {
                when {
                    pick != null -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = GiSpace.gutter,
                            end = GiSpace.gutter,
                            top = GiSpace.x1,
                            bottom = innerPadding.calculateBottomPadding() + GiSpace.x6
                        ),
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
                    ) {
                        val pickItems: LazyListScope.() -> Unit = {
                            item(key = "pick", contentType = "pick") {
                                PickCard(
                                    pick = pick,
                                    isLiked = isLiked,
                                    showActions = signedIn,
                                    onLike = {
                                        onEvent(if (it) TopPickEvent.LikeTopPick else TopPickEvent.RemoveLikeTopPick)
                                        isLiked = it
                                    },
                                    onSave = { save ->
                                        val event = if (save) {
                                            TopPickEvent.BookmarkTopPick
                                        } else {
                                            TopPickEvent.RemoveBookmarkTopPick
                                        }
                                        onEvent(event)
                                        if (save) {
                                            scope.launch {
                                                val result = toastState.show(
                                                    message = savedMessage,
                                                    icon = GiIcons.check,
                                                    actionText = if (onOpenSavedList != null) viewLabel else null
                                                )
                                                if (result == GiToastResult.ActionPerformed) onOpenSavedList?.invoke()
                                            }
                                        }
                                    },
                                    onReadMore = if (state.isLoggedIn) {
                                        null
                                    } else {
                                        { onEvent(TopPickEvent.Authenticate(showDialog = true)) }
                                    }
                                )
                            }
                            val history = company?.historicalData.orEmpty()
                            if (signedIn && history.isNotEmpty()) {
                                item(key = "chart", contentType = "chart") {
                                    PriceChartSection(
                                        companyName = companyName,
                                        history = history,
                                        modifier = briefChartMotion().padding(vertical = GiSpace.x2)
                                    )
                                }
                            }
                        }
                        val footer: @Composable ColumnScope.() -> Unit = {
                            PickFooter(
                                company = if (signedIn) company else null,
                                onSources = { onEvent(TopPickEvent.ClickNewsSources(show = true)) }
                            )
                        }
                        if (signedIn && brief != null) {
                            companyBriefItems(
                                brief = brief,
                                onKeyNumberClick = { selectedKeyNumber = it },
                                onNewsClick = openUrl,
                                footer = footer,
                                afterHero = pickItems,
                                showVerdict = false
                            )
                        } else {
                            item(key = "hero", contentType = "hero") { PickHeader(pick = pick, company = company) }
                            pickItems()
                            if (signedIn && state.briefFailed) {
                                item(key = "briefError", contentType = "state") {
                                    BriefLoadError(
                                        onRetry = { onEvent(TopPickEvent.RetryBrief) },
                                        modifier = briefItemMotion()
                                    )
                                }
                            } else if (signedIn) {
                                item(key = "briefLoading", contentType = "state") {
                                    BriefSectionsSkeleton(modifier = briefItemMotion())
                                }
                            }
                            item(key = "footer", contentType = "footer") {
                                Column(modifier = briefItemMotion()) { footer() }
                            }
                        }
                    }

                    state.loading -> PickSkeleton()

                    else -> GiErrorState(
                        title = stringResource(Res.string.top_pick_error_title),
                        body = stringResource(Res.string.check_connection_try_again),
                        actionText = stringResource(Res.string.try_again),
                        onAction = { onEvent(TopPickEvent.Retry) }
                    )
                }
            }
        }

        GiToastHost(
            state = toastState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = GiSpace.gutter, vertical = PickToastLift)
        )

        if (state.showNewsSourcesBottomSheet && company != null) {
            GiBottomSheet(
                onDismissRequest = { onEvent(TopPickEvent.ClickNewsSources(show = false)) },
                title = stringResource(Res.string.sources),
                subtitle = stringResource(Res.string.top_pick_sources_subtitle),
                subtitleStyle = GiSheetSubtitle.Caption,
                closeContentDescription = stringResource(Res.string.close)
            ) {
                val yahooName = stringResource(Res.string.yahoo_finance)
                SourceRows(
                    sources = remember(company, yahooName) { pickSources(company, yahooName) },
                    onOpen = openUrl
                )
            }
        }

        selectedKeyNumber?.let { keyNumber ->
            val question = stringResource(keyNumber.key.questionRes(), keyNumber.value, companyName)
            CompanyKeyNumberSheet(
                keyNumber = keyNumber,
                onDismiss = { selectedKeyNumber = null },
                onAsk = {
                    selectedKeyNumber = null
                    onEvent(TopPickEvent.AskAboutPick(question))
                }
            )
        }

        if (showRemoveDialog && brief != null) {
            RemoveFromWatchlistDialog(
                ticker = brief.ticker,
                onConfirm = {
                    showRemoveDialog = false
                    onEvent(TopPickEvent.RemoveFromWatchlist)
                },
                onDismiss = { showRemoveDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PickHeader(
    pick: TopPickPresentation,
    company: CompanyDetailRemoteResponse?,
    modifier: Modifier = Modifier
) {
    val price = (company?.price ?: pick.currentPrice).toDouble()
    val change = company?.change?.toDouble()
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            GiLogoTile(
                initials = pickInitials(pick.ticker),
                imageUrl = company?.formattedImageUrl?.takeIf { it.isNotBlank() } ?: pick.imageUrl
            )
            Column(modifier = Modifier.weight(1f)) {
                GiText(
                    text = company?.name ?: pick.companyName,
                    modifier = Modifier.semantics { heading() },
                    style = GiTextStyle.CompanyName
                )
                GiText(text = pick.ticker, style = GiTextStyle.MetaRelaxed)
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            GiPriceDisplay(price = price)
            if (change != null) GiDeltaPill(percent = change, suffix = stringResource(Res.string.today))
        }
    }
}

@Composable
private fun PickCard(
    pick: TopPickPresentation,
    isLiked: Boolean,
    showActions: Boolean,
    onLike: (Boolean) -> Unit,
    onSave: (Boolean) -> Unit,
    onReadMore: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.md)
            .background(colors.violetSoft)
            .padding(GiSpace.x4),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = GiIcons.mark,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = colors.violet
            )
            GiText(
                text = stringResource(Res.string.top_pick_label),
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                style = GiTextStyle.Label,
                color = colors.violet
            )
            GiText(
                text = stringResource(Res.string.top_pick_score, pick.confidenceScore),
                style = GiTextStyle.Label,
                color = colors.violet
            )
        }
        ClampedText(
            text = pick.rationale,
            maxLines = RATIONALE_LINES,
            onReadMore = onReadMore,
            trailing = if (showActions) {
                {
                    Row(
                        modifier = Modifier.offset(x = GiSpace.x3),
                        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1)
                    ) {
                        GiIconToggleButton(
                            checked = isLiked,
                            onCheckedChange = onLike,
                            icon = GiIcons.thumbsUp,
                            checkedIcon = GiIcons.thumbsUpFill,
                            contentDescription = stringResource(Res.string.helpful),
                            variant = GiIconButtonVariant.Ghost,
                            iconColor = colors.ink
                        )
                        GiIconToggleButton(
                            checked = pick.isSaved,
                            onCheckedChange = onSave,
                            icon = GiIcons.bookmarkSimple,
                            checkedIcon = ContentBookmarkFill,
                            contentDescription = stringResource(Res.string.top_pick_save),
                            variant = GiIconButtonVariant.Ghost,
                            iconColor = colors.ink
                        )
                    }
                }
            } else {
                null
            }
        )
    }
}

@Composable
private fun PickFooter(company: CompanyDetailRemoteResponse?, onSources: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
        if (company != null) {
            val yahooName = stringResource(Res.string.yahoo_finance)
            val sourceCount = remember(company, yahooName) { pickSources(company, yahooName).size }
            SourcesRow(count = sourceCount, onClick = onSources)
        }
        GiText(text = stringResource(Res.string.top_pick_disclaimer), style = GiTextStyle.Meta)
    }
}

@Composable
private fun BriefSectionsSkeleton(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.loading_brief)
    GiListGroup(
        modifier = modifier.clearAndSetSemantics {
            liveRegion = LiveRegionMode.Polite
            contentDescription = description
        }
    ) {
        listOf(SKEL_SECTION_SHORT, SKEL_NAME).forEachIndexed { index, titleWidth ->
            if (index > 0) GiDivider()
            Column(
                modifier = Modifier.padding(GiSpace.cardPad),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                GiSkeleton(modifier = Modifier.width(titleWidth).height(SKEL_TITLE))
                listOf(1f, 1f, 0.6f).forEach { GiSkeleton(modifier = Modifier.fillMaxWidth(it).height(SKEL_LINE)) }
            }
        }
    }
}

@Composable
private fun BriefLoadError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    GiBanner(
        title = stringResource(Res.string.couldn_t_load_brief),
        subtitle = stringResource(Res.string.check_connection_try_again),
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        tone = GiBannerTone.Error
    ) {
        GiButton(
            text = stringResource(Res.string.retry),
            onClick = onRetry,
            size = GiButtonSize.Small,
            variant = GiButtonVariant.Secondary
        )
    }
}

private const val RATIONALE_LINES = 4
private val PickToastLift = 88.dp

internal fun pickSources(company: CompanyDetailRemoteResponse, yahooName: String): List<PickSource> {
    val news = company.news.orEmpty().distinctBy { it.publisher }.map { PickSource(name = it.publisher, url = it.link) }
    return news + PickSource(name = yahooName, url = "https://finance.yahoo.com/quote/${company.ticker}")
}

@Composable
private fun PickSkeleton() {
    val colors = GiTheme.colors
    val loading = stringResource(Res.string.top_pick_loading)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clearAndSetSemantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            }
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x1),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiSkeleton(modifier = Modifier.size(GiIconSize.xl))
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                    GiSkeleton(modifier = Modifier.width(SKEL_NAME).height(GiSpace.x4))
                    GiSkeleton(modifier = Modifier.width(SKEL_TICKER).height(GiSpace.x3))
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiSkeleton(modifier = Modifier.width(SKEL_PRICE).height(GiSpace.x8))
                GiSkeleton(modifier = Modifier.width(SKEL_PILL).height(GiSpace.x6), shape = GiRadius.xs)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GiRadius.md)
                .background(colors.violetSoft)
                .padding(GiSpace.x4),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                GiSkeleton(modifier = Modifier.width(SKEL_SCORE).height(GiSpace.x4))
                GiSkeleton(modifier = Modifier.width(SKEL_SCORE).height(GiSpace.x4))
            }
            listOf(1f, 1f, 1f, 0.6f).forEach { GiSkeleton(modifier = Modifier.fillMaxWidth(it).height(SKEL_LINE)) }
        }
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(SKEL_RANGE), shape = GiRadius.full)
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(SKEL_CHART), shape = GiRadius.md)
        BriefSectionsSkeleton()
    }
}

private val SKEL_NAME = 120.dp
private val SKEL_TICKER = 150.dp
private val SKEL_PRICE = 128.dp
private val SKEL_PILL = 104.dp
private val SKEL_SECTION_SHORT = 100.dp
private val SKEL_SCORE = 80.dp
private val SKEL_TITLE = 22.dp
private val SKEL_LINE = 14.dp
private val SKEL_RANGE = 42.dp
private val SKEL_CHART = 140.dp

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun TopPickDetailScreenPreview() {
    GPTInvestorTheme {
        TopPickDetailScreen(
            state = TopPickDetailView(
                isLoggedIn = true,
                topPick = previewPick,
                companyPresentation = CompanyDetailRemoteResponse(
                    ticker = "GOOGL",
                    name = "Alphabet Inc.",
                    price = 353.13f,
                    change = -0.18f,
                    marketCap = 4_300_000_000_000,
                    peRatio = 17.72f,
                    revenue = 445_900_000_000,
                    about = "Alphabet Inc. offers various products and platforms in the United States, Europe, the " +
                        "Middle East, Africa, the Asia-Pacific, Canada, and Latin America."
                )
            ),
            onEvent = {},
            onAction = {},
            toastState = rememberGiToastState()
        )
    }
}

@PreviewLightDark
@Composable
private fun TopPickDetailLoadingPreview() {
    GPTInvestorTheme {
        TopPickDetailScreen(
            state = TopPickDetailView(loading = true),
            onEvent = {},
            onAction = {},
            toastState = rememberGiToastState()
        )
    }
}
