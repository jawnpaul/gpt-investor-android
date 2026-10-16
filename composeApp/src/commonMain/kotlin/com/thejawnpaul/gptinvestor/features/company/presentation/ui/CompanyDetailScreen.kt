package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.add_to_watchlist
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.brief_footer_sources
import com.thejawnpaul.gptinvestor.check_connection_try_again
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
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.SingleStructuredResponse
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui.RemoveFromWatchlistDialog
import com.thejawnpaul.gptinvestor.not_financial_advice
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.share_brief
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiComposerBar
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(
    state: SingleCompanyView,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    var showSentimentSheet by remember { mutableStateOf(false) }
    var showRemoveFromWatchlistDialog by remember { mutableStateOf(false) }
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    val brief = state.brief
    val isWatched = brief?.isWatched == true
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
                        title = null,
                        onBack = { onAction(CompanyDetailAction.OnGoBack) },
                        backContentDescription = stringResource(Res.string.back),
                        scrolled = listState.canScrollBackward
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
                }
            },
            bottomBar = {
                GiComposerBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                ) {
                    GiAskComposer(
                        value = state.inputQuery,
                        onValueChange = { onEvent(CompanyDetailEvent.QueryInputChanged(it)) },
                        onSend = {
                            keyboardController?.hide()
                            onEvent(CompanyDetailEvent.SendClick)
                        },
                        placeholder = stringResource(
                            Res.string.ask_anything_about,
                            state.companyName.ifEmpty { state.ticker }
                        ),
                        sendContentDescription = stringResource(Res.string.send),
                        largeTextMaxLines = COMPOSER_LARGE_TEXT_LINES
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
    listState: LazyListState,
    bottomPadding: Dp,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    onVerdictClick: (BriefSentiment) -> Unit,
    onKeyNumberClick: (KeyNumber) -> Unit
) {
    val messages = remember(state.conversation) {
        (state.conversation as? StructuredConversation)
            ?.messageList
            ?.filterIsInstance<GenAiTextMessage>()
            .orEmpty()
    }
    val footer = if (brief.news.isNotEmpty()) {
        pluralStringResource(Res.plurals.brief_footer_sources, brief.news.size, brief.news.size)
    } else {
        stringResource(Res.string.not_financial_advice)
    }
    val messageCount by rememberUpdatedState(messages.size)
    LaunchedEffect(listState) {
        snapshotFlow { messageCount }
            .drop(1)
            .filter { it > 0 }
            .collect { listState.animateScrollToItem(listState.layoutInfo.totalItemsCount - 1) }
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
        items(
            items = messages,
            key = { it.id },
            contentType = { "conversationMessage" }
        ) { message ->
            SingleStructuredResponse(
                genAiMessage = message,
                text = state.genText,
                onClickNews = { url -> onAction(CompanyDetailAction.OnNavigateToWebView(url)) },
                onClickFeedback = { messageId, status, reason ->
                    onEvent(CompanyDetailEvent.SendFeedback(messageId, status, reason))
                },
                onCopy = { onEvent(CompanyDetailEvent.CopyToClipboard(it)) },
                onClickSource = {},
                isGuest = state.isGuestSession
            )
        }
    }
}

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
            onAction = {}
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
            onAction = {}
        )
    }
}
