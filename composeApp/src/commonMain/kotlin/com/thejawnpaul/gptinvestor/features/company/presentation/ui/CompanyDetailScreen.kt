package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add_to_watchlist
import com.thejawnpaul.gptinvestor.ask_about_company
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.brief_footer_sources
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.company_context_title
import com.thejawnpaul.gptinvestor.copy
import com.thejawnpaul.gptinvestor.couldn_t_load_brief
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.domain.model.NewsBrief
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSentimentSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSkeleton
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefTip
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyKeyNumberSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.companyBriefItems
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.questionRes
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailEvent
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.AnswerMarkdown
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.RemoveFromWatchlistDialog
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.inaccurate
import com.thejawnpaul.gptinvestor.not_actionable
import com.thejawnpaul.gptinvestor.not_financial_advice
import com.thejawnpaul.gptinvestor.not_helpful
import com.thejawnpaul.gptinvestor.others
import com.thejawnpaul.gptinvestor.reading_company_data
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.share
import com.thejawnpaul.gptinvestor.share_brief
import com.thejawnpaul.gptinvestor.show_less
import com.thejawnpaul.gptinvestor.show_more
import com.thejawnpaul.gptinvestor.stop_answering
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAnswerActionLabels
import com.thejawnpaul.gptinvestor.theme.components.GiAnswerActions
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiAssistantMessage
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiComposerStop
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiFeedback
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.theme.components.GiThinkingIndicator
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import com.thejawnpaul.gptinvestor.theme.components.GiUserMessage
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.too_complex
import kotlinx.coroutines.delay
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
    val density = LocalDensity.current
    val listState = rememberLazyListState()
    var showSentimentSheet by remember { mutableStateOf(false) }
    var showRemoveFromWatchlistDialog by remember { mutableStateOf(false) }
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    var composerFocused by remember { mutableStateOf(false) }
    var composerBarHeight by remember { mutableStateOf(0.dp) }
    val brief = state.brief
    val isWatched = brief?.isWatched == true
    val messages = remember(state.conversation) {
        (state.conversation as? StructuredConversation)
            ?.messageList
            ?.filterIsInstance<GenAiTextMessage>()
            .orEmpty()
    }
    val askMode = composerFocused || messages.isNotEmpty() || state.pendingQuery != null
    val heroScrolledAway by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val showPinnedRow = askMode && brief != null && heroScrolledAway
    val companyName = state.companyName.ifEmpty { state.ticker }
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
                            contentDescription = stringResource(
                                if (isWatched) Res.string.remove_from_watchlist else Res.string.add_to_watchlist
                            ),
                            enabled = brief != null,
                            loading = state.watchlistPending
                        )
                    }
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            onClick = { onEvent(CompanyDetailEvent.SignUpClicked) },
                            modifier = Modifier.padding(
                                start = GiSpace.gutter,
                                end = GiSpace.gutter,
                                bottom = GiSpace.x3
                            )
                        )
                    }
                    AnimatedVisibility(
                        visible = showPinnedRow,
                        enter = fadeIn(tween(GiMotion.BASE)) + expandVertically(tween(GiMotion.BASE)),
                        exit = fadeOut(tween(GiMotion.FAST)) + shrinkVertically(tween(GiMotion.FAST))
                    ) {
                        if (brief != null) {
                            Column {
                                GiStockSummary(
                                    stock = tickerStock(
                                        ticker = brief.ticker,
                                        title = brief.name,
                                        subtitle = brief.ticker,
                                        percent = brief.change.toDouble(),
                                        imageUrl = brief.logoUrl.ifBlank { null }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(GiTheme.colors.bg)
                                        .padding(
                                            start = GiSpace.gutter,
                                            end = GiSpace.gutter,
                                            top = GiSpace.x2,
                                            bottom = GiSpace.x3
                                        ),
                                    size = GiStockSummarySize.Header
                                )
                                GiDivider()
                            }
                        }
                    }
                }
            },
            bottomBar = {
                GiComposerBar(
                    modifier = Modifier
                        .onSizeChanged { composerBarHeight = with(density) { it.height.toDp() } }
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
                                onStop = { onEvent(CompanyDetailEvent.StopAnswering) }
                            )
                        } else {
                            null
                        },
                        largeTextMaxLines = COMPOSER_LARGE_TEXT_LINES,
                        onFocusChange = { composerFocused = it }
                    )
                }
            }
        ) { innerPadding ->
            val display = when {
                brief != null -> CompanyDisplay.Brief
                state.error != null && !state.loading -> CompanyDisplay.Error
                else -> CompanyDisplay.Loading
            }
            AnimatedContent(
                targetState = display,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                transitionSpec = {
                    fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) togetherWith
                        fadeOut(tween(GiMotion.FAST, easing = LinearEasing))
                }
            ) { current ->
                when (current) {
                    CompanyDisplay.Loading -> Column(
                        modifier = Modifier.padding(horizontal = GiSpace.gutter, vertical = GiSpace.x1)
                    ) {
                        CompanyBriefSkeleton()
                    }
                    CompanyDisplay.Error -> Column(
                        modifier = Modifier.padding(horizontal = GiSpace.gutter, vertical = GiSpace.x1)
                    ) {
                        CompanyBriefError(onRetry = { onEvent(CompanyDetailEvent.RetryLoad) })
                    }
                    CompanyDisplay.Brief -> if (brief != null) {
                        CompanyDetailBriefContent(
                            brief = brief,
                            state = state,
                            messages = messages,
                            listState = listState,
                            bottomPadding = innerPadding.calculateBottomPadding(),
                            onEvent = onEvent,
                            onAction = onAction,
                            onVerdictClick = { sentiment ->
                                showSentimentSheet = true
                                onEvent(CompanyDetailEvent.BriefSentimentViewed(sentiment))
                            },
                            onKeyNumberClick = { keyNumber ->
                                selectedKeyNumber = keyNumber
                                onEvent(CompanyDetailEvent.BriefKeyNumberExpanded(keyNumber.key))
                            }
                        )
                    }
                }
            }
        }

        if (showSentimentSheet && brief?.sentiment != null) {
            CompanyBriefSentimentSheet(
                sentiment = brief.sentiment,
                summary = brief.sentimentSummary,
                onDismiss = { showSentimentSheet = false }
            )
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
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = composerBarHeight + GiSpace.x4)
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
private fun CompanyBriefError(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    GiBanner(
        title = stringResource(Res.string.couldn_t_load_brief),
        modifier = modifier,
        subtitle = stringResource(Res.string.check_connection_try_again),
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

@Composable
private fun CompanyDetailBriefContent(
    brief: CompanyBrief,
    state: SingleCompanyView,
    messages: List<GenAiTextMessage>,
    listState: LazyListState,
    bottomPadding: Dp,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    onVerdictClick: (BriefSentiment) -> Unit,
    onKeyNumberClick: (KeyNumber) -> Unit
) {
    val footer = if (brief.news.isNotEmpty()) {
        pluralStringResource(Res.plurals.brief_footer_sources, brief.news.size, brief.news.size)
    } else {
        stringResource(Res.string.not_financial_advice)
    }
    val companyName = state.companyName.ifEmpty { brief.name }
    val expanded = remember { mutableStateMapOf<Long, Boolean>() }
    var showReasonsFor by remember { mutableStateOf<Long?>(null) }
    val showPending = state.pendingQuery != null
    val questionCount = messages.size + if (showPending) 1 else 0
    val pendingNow by rememberUpdatedState(showPending)
    var scrolledQuestionCount by remember { mutableIntStateOf(questionCount) }
    LaunchedEffect(questionCount) {
        val grew = questionCount > scrolledQuestionCount
        scrolledQuestionCount = questionCount
        if (grew) {
            withFrameNanos { }
            val itemsAfterBubble = if (pendingNow) 1 else 2
            listState.animateScrollToItem((listState.layoutInfo.totalItemsCount - itemsAfterBubble).coerceAtLeast(0))
        }
    }
    LaunchedEffect(showReasonsFor) {
        if (showReasonsFor != null) {
            delay(REASONS_TIMEOUT_MS)
            showReasonsFor = null
        }
    }
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
            onVerdictClick = { brief.sentiment?.let(onVerdictClick) },
            onKeyNumberClick = onKeyNumberClick,
            onNewsClick = { onAction(CompanyDetailAction.OnNavigateToWebView(it)) }
        )
        item(key = "footer", contentType = "footer") {
            Text(text = footer, color = GiTheme.colors.muted, style = GiTheme.type.meta)
        }
        messages.forEachIndexed { index, message ->
            val isLatest = index == messages.lastIndex && !showPending
            item(key = "q-${message.id}", contentType = "question") {
                GiUserMessage(text = message.query)
            }
            if (message.loading && message.response.isNullOrBlank()) {
                item(key = "t-${message.id}", contentType = "thinking") {
                    GiThinkingIndicator(status = stringResource(Res.string.reading_company_data, companyName))
                }
            } else {
                item(key = "a-${message.id}", contentType = "answer") {
                    CompanyAnswer(
                        message = message,
                        collapsed = !(expanded[message.id] ?: isLatest),
                        onToggleCollapse = { expanded[message.id] = !(expanded[message.id] ?: isLatest) },
                        isGuest = state.isGuestSession,
                        showReasons = showReasonsFor == message.id,
                        onEvent = onEvent,
                        onNotHelpful = { showReasonsFor = message.id },
                        onReasonPick = { showReasonsFor = null }
                    )
                }
            }
        }
        if (state.pendingQuery != null) {
            item(key = "pending", contentType = "pending") {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
                    GiUserMessage(text = state.pendingQuery)
                    GiThinkingIndicator(status = stringResource(Res.string.reading_company_data, companyName))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompanyAnswer(
    message: GenAiTextMessage,
    collapsed: Boolean,
    onToggleCollapse: () -> Unit,
    isGuest: Boolean,
    showReasons: Boolean,
    onEvent: (CompanyDetailEvent) -> Unit,
    onNotHelpful: () -> Unit,
    onReasonPick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val response = message.response.orEmpty()
    val labels = GiAnswerActionLabels(
        copy = stringResource(Res.string.copy),
        helpful = stringResource(Res.string.helpful),
        notHelpful = stringResource(Res.string.not_helpful),
        share = stringResource(Res.string.share)
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
        GiAssistantMessage(
            name = stringResource(Res.string.gpt_investor),
            streaming = message.loading,
            disclaimer = stringResource(Res.string.not_financial_advice),
            collapsed = collapsed,
            onToggleCollapse = onToggleCollapse,
            showMoreText = stringResource(Res.string.show_more),
            showLessText = stringResource(Res.string.show_less),
            actions = {
                GiAnswerActions(
                    labels = labels,
                    onCopy = { onEvent(CompanyDetailEvent.CopyToClipboard(response)) },
                    onShare = { onEvent(CompanyDetailEvent.ShareAnswer(response)) },
                    feedback = when (message.feedbackStatus) {
                        1 -> GiFeedback.Helpful
                        -1 -> GiFeedback.NotHelpful
                        else -> GiFeedback.None
                    },
                    onHelpful = if (isGuest) {
                        null
                    } else {
                        { onEvent(CompanyDetailEvent.SendFeedback(message.id, 1, null)) }
                    },
                    onNotHelpful = if (isGuest) {
                        null
                    } else {
                        {
                            onEvent(CompanyDetailEvent.SendFeedback(message.id, -1, null))
                            onNotHelpful()
                        }
                    }
                )
            }
        ) {
            AnswerMarkdown(text = response)
        }
        if (showReasons) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                DislikeReasons.forEach { reasonRes ->
                    val reason = stringResource(reasonRes)
                    GiButton(
                        text = reason,
                        onClick = {
                            onEvent(CompanyDetailEvent.SendFeedback(message.id, -1, reason))
                            onReasonPick()
                        },
                        size = GiButtonSize.Small,
                        variant = GiButtonVariant.Secondary
                    )
                }
            }
        }
    }
}

private val DislikeReasons = listOf(
    Res.string.too_complex,
    Res.string.inaccurate,
    Res.string.not_actionable,
    Res.string.others
)

private const val REASONS_TIMEOUT_MS = 15_000L
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
