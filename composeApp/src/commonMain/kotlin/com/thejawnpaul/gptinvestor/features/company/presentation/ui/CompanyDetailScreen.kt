package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add_to_watchlist
import com.thejawnpaul.gptinvestor.ask_about_company
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.brief_footer_sources
import com.thejawnpaul.gptinvestor.company_context_title
import com.thejawnpaul.gptinvestor.couldn_t_load_brief
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.domain.model.NewsBrief
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSkeleton
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefTip
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyKeyNumberSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.briefChartMotion
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.companyBriefItems
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.questionRes
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailEvent
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.AnswerStopSignal
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ScrollToNewQuestionEffect
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.ThreadMessage
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.answerStrings
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.answerThreadItems
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.rememberAnswerThreadState
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.rememberThreadMessages
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.toppick.presentation.ui.PriceChartSection
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.RemoveFromWatchlistDialog
import com.thejawnpaul.gptinvestor.loading_brief
import com.thejawnpaul.gptinvestor.loading_company_brief
import com.thejawnpaul.gptinvestor.not_financial_advice
import com.thejawnpaul.gptinvestor.reading_company_data
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.share_brief
import com.thejawnpaul.gptinvestor.stop_answering
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiComposerStop
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    state: SingleCompanyView,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    toastState: GiToastState,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    var showRemoveFromWatchlistDialog by remember { mutableStateOf(false) }
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    var composerFocused by remember { mutableStateOf(false) }
    val composerBarHeight = remember { mutableIntStateOf(0) }
    val brief = state.brief
    val isWatched = brief?.isWatched == true
    val messages = rememberThreadMessages((state.conversation as? StructuredConversation)?.messageList.orEmpty())
    val askMode = composerFocused || messages.isNotEmpty() || state.pendingQuery != null
    val heroScrolledAway by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val showPinnedRow = askMode && brief != null && heroScrolledAway
    val companyName = state.companyName.ifEmpty { state.ticker }
    val display = when {
        brief != null -> CompanyDisplay.Brief
        state.error != null && !state.loading -> CompanyDisplay.Error
        else -> CompanyDisplay.Loading
    }
    val onWatchlistClick = {
        if (isWatched) {
            showRemoveFromWatchlistDialog = true
        } else {
            onEvent(CompanyDetailEvent.AddToWatchlist)
        }
    }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = GiTheme.colors.bg,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                Column {
                    GiTopBar(
                        title = if (askMode) {
                            stringResource(Res.string.company_context_title, companyName, brief?.ticker ?: state.ticker)
                        } else {
                            null
                        },
                        onBack = { onAction(CompanyDetailAction.OnGoBack) },
                        backContentDescription = stringResource(Res.string.back),
                        scrolled = !showPinnedRow && listState.canScrollBackward,
                        titleStyle = if (askMode) GiTopBarTitleStyle.Centered else GiTopBarTitleStyle.Start
                    ) {
                        GiIconButton(
                            icon = GiIcons.shareNetwork,
                            contentDescription = stringResource(Res.string.share_brief),
                            onClick = { onEvent(CompanyDetailEvent.ShareBrief) },
                            enabled = brief != null
                        )
                        GiIconToggleButton(
                            checked = isWatched,
                            onCheckedChange = { onWatchlistClick() },
                            icon = GiIcons.star,
                            checkedIcon = GiIcons.starFill,
                            checkedHaptic = null,
                            contentDescription = stringResource(
                                if (isWatched) Res.string.remove_from_watchlist else Res.string.add_to_watchlist
                            ),
                            enabled = brief != null,
                            loading = state.watchlistPending
                        )
                    }
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            onClick = { onEvent(CompanyDetailEvent.LoginClicked) },
                            modifier = Modifier.padding(
                                start = GiSpace.gutter,
                                end = GiSpace.gutter,
                                bottom = GiSpace.x3
                            )
                        )
                    }
                }
            },
            bottomBar = {
                if (display != CompanyDisplay.Error) {
                    GiComposerBar(
                        modifier = Modifier
                            .onSizeChanged { composerBarHeight.intValue = it.height }
                            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime)),
                        docked = askMode
                    ) {
                        GiAskComposer(
                            value = state.inputQuery,
                            onValueChange = { onEvent(CompanyDetailEvent.QueryInputChanged(it)) },
                            onSend = {
                                keyboardController?.hide()
                                onEvent(CompanyDetailEvent.SendClick)
                            },
                            placeholder = if (askMode) {
                                stringResource(Res.string.ask_about_company, companyName)
                            } else {
                                stringResource(Res.string.ask_anything_about, companyName)
                            },
                            sendContentDescription = stringResource(Res.string.send),
                            floating = !askMode,
                            stop = if (state.isAnswering) {
                                GiComposerStop(
                                    contentDescription = stringResource(Res.string.stop_answering),
                                    onStop = {
                                        AnswerStopSignal.count++
                                        onEvent(CompanyDetailEvent.StopAnswering)
                                    }
                                )
                            } else {
                                null
                            },
                            largeTextMaxLines = COMPOSER_LARGE_TEXT_LINES,
                            onFocusChange = { composerFocused = it }
                        )
                    }
                }
            }
        ) { innerPadding ->
            val loadingStatus = if (companyName.isNotBlank()) {
                stringResource(Res.string.loading_company_brief, companyName)
            } else {
                stringResource(Res.string.loading_brief)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                AnimatedContent(
                    targetState = display,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) togetherWith
                            fadeOut(tween(GiMotion.FAST, easing = LinearEasing))
                    }
                ) { current ->
                    when (current) {
                        CompanyDisplay.Loading -> Column(
                            modifier = Modifier
                                .semantics {
                                    liveRegion = LiveRegionMode.Polite
                                    contentDescription = loadingStatus
                                }
                                .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x1)
                        ) {
                            CompanyBriefSkeleton(announce = false)
                        }
                        CompanyDisplay.Error -> GiErrorState(
                            title = stringResource(Res.string.couldn_t_load_brief),
                            body = stringResource(state.errorBody),
                            actionText = stringResource(Res.string.try_again),
                            onAction = { onEvent(CompanyDetailEvent.RetryLoad) },
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                        CompanyDisplay.Brief -> if (brief != null) {
                            CompanyDetailBriefContent(
                                brief = brief,
                                state = state,
                                messages = messages,
                                listState = listState,
                                bottomPadding = innerPadding.calculateBottomPadding(),
                                onEvent = onEvent,
                                onAction = onAction,
                                onKeyNumberClick = { keyNumber ->
                                    selectedKeyNumber = keyNumber
                                    onEvent(CompanyDetailEvent.BriefKeyNumberExpanded(keyNumber.key))
                                }
                            )
                        }
                    }
                }
                CompanyPinnedRow(visible = showPinnedRow, brief = brief)
            }
        }

        selectedKeyNumber?.let { keyNumber ->
            val question = stringResource(
                keyNumber.key.questionRes(),
                keyNumber.value,
                state.companyName.ifEmpty { state.ticker }
            )
            CompanyKeyNumberSheet(
                keyNumber = keyNumber,
                onDismiss = { selectedKeyNumber = null },
                onAsk = {
                    selectedKeyNumber = null
                    onEvent(CompanyDetailEvent.AskAboutKeyNumber(keyNumber.key, question))
                }
            )
        }

        if (state.showWaitListBottomSheet) {
            GptInvestorBottomSheet(
                modifier = Modifier,
                onDismiss = { onEvent(CompanyDetailEvent.UpgradeModel(showBottomSheet = false)) }
            ) {
                WaitlistBottomSheetContent(
                    modifier = Modifier,
                    options = state.waitlistAvailableOptions,
                    selectedOptions = state.selectedWaitlistOptions,
                    onSelectOption = { onEvent(CompanyDetailEvent.SelectWaitlistOption(it)) },
                    onJoinWaitList = { onEvent(CompanyDetailEvent.JoinWaitList) },
                    onDismiss = {
                        onEvent(CompanyDetailEvent.UpgradeModel(showBottomSheet = false))
                    }
                )
            }
        }

        if (showRemoveFromWatchlistDialog) {
            brief?.let {
                RemoveFromWatchlistDialog(
                    ticker = it.ticker,
                    onConfirm = {
                        showRemoveFromWatchlistDialog = false
                        onEvent(CompanyDetailEvent.RemoveFromWatchlist)
                    },
                    onDismiss = { showRemoveFromWatchlistDialog = false }
                )
            }
        }

        GiToastHost(
            state = toastState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = GiSpace.gutter)
                .offset { IntOffset(0, -(composerBarHeight.intValue + GiSpace.x4.roundToPx())) }
        )

        CompanyBriefTip(
            visible = state.showSpotlight && brief != null,
            onDismiss = { onEvent(CompanyDetailEvent.DismissSpotlight) },
            onShare = { onEvent(CompanyDetailEvent.ShareBrief) },
            onWatchlist = onWatchlistClick,
            isWatched = isWatched
        )
    }
}

private enum class CompanyDisplay { Loading, Error, Brief }

@Composable
private fun CompanyDetailBriefContent(
    brief: CompanyBrief,
    state: SingleCompanyView,
    messages: List<ThreadMessage>,
    listState: LazyListState,
    bottomPadding: Dp,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    onKeyNumberClick: (KeyNumber) -> Unit
) {
    val footer = if (brief.news.isNotEmpty()) {
        pluralStringResource(Res.plurals.brief_footer_sources, brief.news.size, brief.news.size)
    } else {
        stringResource(Res.string.not_financial_advice)
    }
    val companyName = state.companyName.ifEmpty { brief.name }
    val strings = answerStrings()
    val thinkingStatus = stringResource(Res.string.reading_company_data, companyName)
    val thread = rememberAnswerThreadState()
    val scope = rememberCoroutineScope()
    val reducedMotion = isReducedMotionEnabled()
    val itemSizes = remember { mutableMapOf<Int, Int>() }
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { items -> items.forEach { itemSizes[it.index] = it.size } }
    }
    ScrollToNewQuestionEffect(
        listState = listState,
        questionCount = messages.size + if (state.pendingQuery != null) 1 else 0
    )
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = GiSpace.gutter,
            end = GiSpace.gutter,
            top = GiSpace.x1,
            bottom = bottomPadding + GiSpace.x6
        ),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        companyBriefItems(
            brief = brief,
            onKeyNumberClick = onKeyNumberClick,
            onNewsClick = { onAction(CompanyDetailAction.OnNavigateToWebView(it)) },
            beforeSections = {
                if (state.priceHistory.isNotEmpty()) {
                    item(key = "chart", contentType = "chart") {
                        PriceChartSection(
                            companyName = brief.name,
                            history = state.priceHistory,
                            modifier = briefChartMotion().padding(vertical = GiSpace.x2)
                        )
                    }
                }
            }
        )
        item(key = "footer", contentType = "footer") {
            Text(text = footer, color = GiTheme.colors.muted, style = GiTheme.type.meta)
        }
        answerThreadItems(
            messages = messages,
            pendingQuery = state.pendingQuery,
            thinkingStatus = thinkingStatus,
            strings = strings,
            thread = thread,
            listState = listState,
            isGuest = state.isGuestSession,
            offlineAnswerIds = state.offlineAnswerIds,
            onCompanyClick = { ticker ->
                if (ticker.equals(brief.ticker, ignoreCase = true)) {
                    scope.launch { listState.scrollToTop(itemSizes, animate = !reducedMotion) }
                } else {
                    onAction(CompanyDetailAction.OnOpenCompany(ticker))
                }
            },
            onCopy = { onEvent(CompanyDetailEvent.CopyToClipboard(it)) },
            onShare = { onEvent(CompanyDetailEvent.ShareAnswer(it)) },
            onFeedback = { messageId, status, reason ->
                onEvent(CompanyDetailEvent.SendFeedback(messageId, status, reason))
            },
            onRetry = { onEvent(CompanyDetailEvent.RetryAnswer(it)) },
            extraGap = GiSpace.x1
        )
    }
}

@Composable
private fun BoxScope.CompanyPinnedRow(visible: Boolean, brief: CompanyBrief?) {
    val reducedMotion = isReducedMotionEnabled()
    val enterFade = fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart))
    val exitFade = fadeOut(tween(GiMotion.FAST, easing = EaseOutQuart))
    AnimatedVisibility(
        visible = visible && brief != null,
        modifier = Modifier.align(Alignment.TopCenter),
        enter = if (reducedMotion) {
            enterFade
        } else {
            enterFade + slideInVertically(tween(GiMotion.BASE, easing = EaseOutQuart)) { -it / PINNED_SLIDE_DIVISOR }
        },
        exit = if (reducedMotion) {
            exitFade
        } else {
            exitFade + slideOutVertically(tween(GiMotion.FAST, easing = EaseOutQuart)) { -it / PINNED_SLIDE_DIVISOR }
        }
    ) {
        if (brief != null) {
            Column(modifier = Modifier.background(GiTheme.colors.bg)) {
                GiStockSummary(
                    stock = tickerStock(
                        ticker = brief.ticker,
                        title = brief.name,
                        subtitle = brief.ticker,
                        percent = brief.change?.toDouble(),
                        imageUrl = brief.logoUrl.ifBlank { null }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2, bottom = GiSpace.x3),
                    size = GiStockSummarySize.Header
                )
                GiDivider()
            }
        }
    }
}

private suspend fun LazyListState.scrollToTop(itemSizes: Map<Int, Int>, animate: Boolean) {
    if (!animate) {
        scrollToItem(0)
        return
    }
    val visible = layoutInfo.visibleItemsInfo
    val averageSize = if (visible.isEmpty()) 0 else visible.sumOf { it.size } / visible.size
    val spacing = layoutInfo.mainAxisItemSpacing
    val distance = (0 until firstVisibleItemIndex).sumOf { (itemSizes[it] ?: averageSize) + spacing } +
        firstVisibleItemScrollOffset
    animateScrollBy(-distance.toFloat(), GiMotion.smooth())
    if (firstVisibleItemIndex != 0 || firstVisibleItemScrollOffset != 0) animateScrollToItem(0)
}

private const val PINNED_SLIDE_DIVISOR = 4
private const val COMPOSER_LARGE_TEXT_LINES = 3

@PreviewLightDark
@Composable
private fun CompanyDetailScreenPreview() {
    GPTInvestorTheme {
        CompanyDetailScreen(
            modifier = Modifier,
            state = SingleCompanyView(
                companyName = "Apple Inc.",
                brief = CompanyBrief(
                    id = "",
                    ticker = "AAPL",
                    name = "Apple Inc.",
                    logoUrl = "",
                    price = 182.30f,
                    change = 1.42f,
                    currencySymbol = "$",
                    sentiment = BriefSentiment.Bullish,
                    sentimentSummary = "Services growth and steady cash flow keep the long-term outlook strong.",
                    summary = "Apple is the world's largest consumer-electronics company, best known for the iPhone. " +
                        "Hardware sales are flat year-over-year, but its services business — App Store, " +
                        "iCloud, Apple Music — is steadily growing.",
                    keyNumbers = listOf(
                        KeyNumber(
                            key = KeyNumberType.MarketCap,
                            value = "$2.83T",
                            insight = "Among the largest in the world",
                            tone = BriefTone.Neutral
                        ),
                        KeyNumber(
                            key = KeyNumberType.PeRatio,
                            value = "29.4",
                            insight = "Fairly valued",
                            tone = BriefTone.Neutral
                        ),
                        KeyNumber(
                            key = KeyNumberType.RevenueGrowth,
                            value = "+4.2%",
                            insight = "Growing steadily",
                            tone = BriefTone.Positive
                        ),
                        KeyNumber(
                            key = KeyNumberType.DividendYield,
                            value = "0.51%",
                            insight = "Small but reliable",
                            tone = BriefTone.Neutral
                        )
                    ),
                    news = listOf(
                        NewsBrief(
                            id = "1",
                            publisher = "Reuters",
                            publishedRelative = "2h ago",
                            title = "Apple unveils on-device AI features for the iPhone, rolling out in iOS 19",
                            whatItMeans = "Could give people a reason to upgrade their phones, " +
                                "lifting revenue later this year.",
                            tone = BriefTone.Positive,
                            link = ""
                        ),
                        NewsBrief(
                            id = "2",
                            publisher = "Bloomberg",
                            publishedRelative = "Yesterday",
                            title = "EU regulators open new probe into App Store fees",
                            whatItMeans = "If Apple has to lower its 30% cut, services revenue would " +
                                "take a small but real hit.",
                            tone = BriefTone.Negative,
                            link = ""
                        )
                    ),
                    risk = BriefSection(
                        title = "The Risk",
                        body = "Most revenue still comes from one product — the iPhone. A weak upgrade cycle " +
                            "in any year noticeably dents the entire business and weighs on the stock."
                    ),
                    opportunity = BriefSection(
                        title = "The Opportunity",
                        body = "Services now grow more than twice as fast as hardware, and have higher profit " +
                            "margins, which steadily lifts overall earnings power."
                    )
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
private fun CompanyDetailScreenLoadingPreview() {
    GPTInvestorTheme {
        CompanyDetailScreen(
            modifier = Modifier,
            state = SingleCompanyView(
                loading = true
            ),
            onEvent = {},
            onAction = {},
            toastState = rememberGiToastState()
        )
    }
}
