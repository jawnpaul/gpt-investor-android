package com.thejawnpaul.gptinvestor.features.company.presentation.ui

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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_anything_about
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.domain.model.NewsBrief
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.BriefSummaryCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefHeader
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSkeleton
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefTopBar
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.KeyNumbersCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.RiskOpportunityCard
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.SentimentBadge
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.WhatsHappeningCard
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailEvent
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.SingleStructuredResponse
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.WaitlistBottomSheetContent
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
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
    var showSourcesSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    CompanyBriefTopBar(
                        onBack = { onAction(CompanyDetailAction.OnGoBack) },
                        onFavorite = {},
                        onMore = {}
                    )
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEvent(CompanyDetailEvent.SignUpClicked) }
                        )
                    }
                }
            },
            bottomBar = {
                QuestionInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(insets = WindowInsets.ime),
                    onSendClick = {
                        keyboardController?.hide()
                        onEvent(CompanyDetailEvent.SendClick)
                    },
                    hint = stringResource(Res.string.ask_anything_about, state.companyName),
                    onTextChange = { onEvent(CompanyDetailEvent.QueryInputChanged(it)) },
                    text = state.inputQuery,
                    availableModels = state.availableModels,
                    selectedModel = state.selectedModel,
                    onModelChange = { model ->
                        if (model.canUpgrade) {
                            onEvent(CompanyDetailEvent.UpgradeModel(showBottomSheet = true, modelId = model.modelId))
                        } else {
                            onEvent(CompanyDetailEvent.ModelChange(model))
                        }
                    }
                )
            }
        ) { innerPadding ->
            CompanyDetailBody(
                state = state,
                contentPadding = innerPadding,
                onEvent = onEvent,
                onAction = onAction,
                onShowSources = { showSourcesSheet = true }
            )
        }

        if (showSourcesSheet) {
            GptInvestorBottomSheet(
                modifier = Modifier,
                onDismiss = { showSourcesSheet = false }
            ) {
                SourcesSheetContent(brief = state.brief)
            }
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
    }
}

@Composable
private fun CompanyDetailBody(
    state: SingleCompanyView,
    contentPadding: PaddingValues,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    onShowSources: () -> Unit
) {
    val brief = state.brief
    val messages = (state.conversation as? StructuredConversation)
        ?.messageList
        ?.filterIsInstance<GenAiTextMessage>()
        .orEmpty()

    if (state.loading && brief == null) {
        CompanyDetailLoadingContent(contentPadding = contentPadding)
    } else if (brief != null) {
        CompanyDetailBriefContent(
            brief = brief,
            messages = messages,
            genText = state.genText,
            isGuestSession = state.isGuestSession,
            contentPadding = contentPadding,
            onEvent = onEvent,
            onAction = onAction,
            onShowSources = onShowSources
        )
    }
}

@Composable
private fun CompanyDetailLoadingContent(contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        item(key = "skeleton") {
            CompanyBriefSkeleton(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun CompanyDetailBriefContent(
    brief: CompanyBrief,
    messages: List<GenAiTextMessage>,
    genText: String,
    isGuestSession: Boolean,
    contentPadding: PaddingValues,
    onEvent: (CompanyDetailEvent) -> Unit,
    onAction: (CompanyDetailAction) -> Unit,
    onShowSources: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") { CompanyBriefHeader(brief = brief) }

        brief.sentiment?.let { sentiment ->
            item(key = "sentiment") {
                SentimentBadge(
                    sentiment = sentiment,
                    summary = brief.sentimentSummary
                )
            }
        }

        brief.summary?.let { summary ->
            item(key = "summary") { BriefSummaryCard(summary = summary) }
        }

        if (brief.keyNumbers.isNotEmpty()) {
            item(key = "keyNumbers") { KeyNumbersCard(keyNumbers = brief.keyNumbers) }
        }

        if (brief.news.isNotEmpty()) {
            item(key = "news") {
                WhatsHappeningCard(
                    news = brief.news,
                    onNewsClick = { url ->
                        onAction(CompanyDetailAction.OnNavigateToWebView(url))
                    }
                )
            }
        }

        if (brief.risk != null || brief.opportunity != null) {
            item(key = "riskOpportunity") {
                RiskOpportunityCard(
                    risk = brief.risk,
                    opportunity = brief.opportunity
                )
            }
        }

        items(
            items = messages,
            key = { it.id },
            contentType = { "conversationMessage" }
        ) { message ->
            SingleStructuredResponse(
                genAiMessage = message,
                text = genText,
                onClickNews = { url -> onAction(CompanyDetailAction.OnNavigateToWebView(url)) },
                onClickFeedback = { messageId, status, reason ->
                    onEvent(CompanyDetailEvent.SendFeedback(messageId, status, reason))
                },
                onCopy = { onEvent(CompanyDetailEvent.CopyToClipboard(it)) },
                onClickSource = onShowSources,
                isGuest = isGuestSession
            )
        }

        item(key = "footerSpace") {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SourcesSheetContent(brief: CompanyBrief?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.sources),
            style = MaterialTheme.typography.labelMedium
        )
        brief?.news?.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = "",
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = item.publisher,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            if (brief.news.isNotEmpty() && index != brief.news.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CompanyDetailScreenPreview() {
    GPTInvestorTheme {
        CompanyDetailScreen(
            modifier = Modifier,
            state = SingleCompanyView(
                companyName = "Apple Inc.",
                brief = CompanyBrief(
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
