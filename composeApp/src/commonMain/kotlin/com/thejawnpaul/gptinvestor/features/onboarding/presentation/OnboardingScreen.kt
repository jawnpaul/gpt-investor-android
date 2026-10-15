package com.thejawnpaul.gptinvestor.features.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.clear_search
import com.thejawnpaul.gptinvestor.digest_sentiment_neutral
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSentimentSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefSkeleton
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyKeyNumberSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.companyBriefItems
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.previewCompanyBrief
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.OnboardingUiState
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.SuggestedStock
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.onboarding_body_value_prop
import com.thejawnpaul.gptinvestor.onboarding_brief_disclaimer
import com.thejawnpaul.gptinvestor.onboarding_cta_get_started
import com.thejawnpaul.gptinvestor.onboarding_cta_go_to_app
import com.thejawnpaul.gptinvestor.onboarding_cta_start_exploring
import com.thejawnpaul.gptinvestor.onboarding_cta_try_it_now
import com.thejawnpaul.gptinvestor.onboarding_error_message
import com.thejawnpaul.gptinvestor.onboarding_headline_how_it_works
import com.thejawnpaul.gptinvestor.onboarding_headline_stock_selection
import com.thejawnpaul.gptinvestor.onboarding_headline_value_prop
import com.thejawnpaul.gptinvestor.onboarding_loading_headline
import com.thejawnpaul.gptinvestor.onboarding_loading_subtitle
import com.thejawnpaul.gptinvestor.onboarding_search_placeholder
import com.thejawnpaul.gptinvestor.onboarding_skip_for_now
import com.thejawnpaul.gptinvestor.onboarding_step_of
import com.thejawnpaul.gptinvestor.onboarding_subtext_stock_selection
import com.thejawnpaul.gptinvestor.onboarding_success_body
import com.thejawnpaul.gptinvestor.onboarding_success_headline
import com.thejawnpaul.gptinvestor.onboarding_value_card_answer
import com.thejawnpaul.gptinvestor.onboarding_value_card_question
import com.thejawnpaul.gptinvestor.onboarding_value_follow_up_body
import com.thejawnpaul.gptinvestor.onboarding_value_follow_up_title
import com.thejawnpaul.gptinvestor.onboarding_value_plain_english_body
import com.thejawnpaul.gptinvestor.onboarding_value_plain_english_title
import com.thejawnpaul.gptinvestor.onboarding_value_real_time_body
import com.thejawnpaul.gptinvestor.onboarding_value_real_time_title
import com.thejawnpaul.gptinvestor.skip
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBrandMark
import com.thejawnpaul.gptinvestor.theme.components.GiBrandMarkSize
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiFillScrollColumn
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileTone
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiListRow
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiSearchField
import com.thejawnpaul.gptinvestor.theme.components.GiSentiment
import com.thejawnpaul.gptinvestor.theme.components.GiSentimentPill
import com.thejawnpaul.gptinvestor.theme.components.GiStepDots
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiUserMessage
import com.thejawnpaul.gptinvestor.theme.components.GiWaveIllustration
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)

private const val STEP_COUNT = 3
private const val PICK_STEP = 2
private const val BRIEF_SCREEN = 3
private const val STEP_FADE_OUT_MS = 90
private const val STEP_FADE_IN_MS = 210
private const val MAX_SEARCH_RESULTS = 20
private const val SOURCE_SUGGESTED = "suggested"
private const val SOURCE_SEARCH = "search"

@Immutable
private data class LogoTransition(val sharedScope: SharedTransitionScope, val flowScope: AnimatedVisibilityScope)

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    searchResults: Flow<PagingData<CompanyPresentation>>,
    onNextScreen: () -> Unit,
    onBack: () -> Unit,
    onSkip: (Int) -> Unit,
    onSelectStock: (ticker: String, companyName: String, source: String, logoUrl: String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onBackToStockSelection: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberSharedAxisTransition()
    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = state.currentScreen > 0,
        onBackCompleted = {
            if (state.currentScreen == BRIEF_SCREEN) onBackToStockSelection() else onBack()
        }
    )
    SharedTransitionLayout(modifier = modifier) {
        val sharedScope = this
        AnimatedContent(
            targetState = state.currentScreen == BRIEF_SCREEN,
            transitionSpec = { transition(targetState) },
            label = "onboardingFlow"
        ) { showBrief ->
            val logoTransition = LogoTransition(sharedScope = sharedScope, flowScope = this)
            if (showBrief) {
                BriefScreen(
                    state = state,
                    logoTransition = logoTransition,
                    onBack = onBackToStockSelection,
                    onFinish = onFinish
                )
            } else {
                OnboardingSteps(
                    state = state,
                    searchResults = searchResults,
                    logoTransition = logoTransition,
                    onNextScreen = onNextScreen,
                    onBack = onBack,
                    onSkip = onSkip,
                    onSelectStock = onSelectStock,
                    onSearchQueryChange = onSearchQueryChange
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun sharedLogoModifier(
    key: String,
    sharedScope: SharedTransitionScope,
    visibilityScope: AnimatedVisibilityScope
): Modifier {
    if (isReducedMotionEnabled()) return Modifier
    return with(sharedScope) {
        Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(key = key),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ -> GiMotion.smooth() }
        )
    }
}

private fun pickLogoKey(ticker: String) = "onboarding-pick-logo-$ticker"

private fun briefLogoKey(ticker: String) = "onboarding-brief-logo-$ticker"

@Composable
private fun rememberSharedAxisTransition(): (forward: Boolean) -> ContentTransform {
    val reducedMotion = isReducedMotionEnabled()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val offset = with(LocalDensity.current) { GiSpace.x8.roundToPx() }
    return remember(reducedMotion, isRtl, offset) {
        { forward ->
            if (reducedMotion) {
                fadeIn(tween(GiMotion.FAST)) togetherWith fadeOut(tween(GiMotion.FAST))
            } else {
                val direction = if (forward != isRtl) 1 else -1
                slideInHorizontally(GiMotion.smooth()) { offset * direction } +
                    fadeIn(tween(STEP_FADE_IN_MS, delayMillis = STEP_FADE_OUT_MS, easing = EaseOutQuart)) togetherWith
                    slideOutHorizontally(GiMotion.smooth()) { -offset * direction } +
                    fadeOut(tween(STEP_FADE_OUT_MS, easing = LinearEasing))
            }
        }
    }
}

@Composable
private fun OnboardingSteps(
    state: OnboardingUiState,
    searchResults: Flow<PagingData<CompanyPresentation>>,
    logoTransition: LogoTransition,
    onNextScreen: () -> Unit,
    onBack: () -> Unit,
    onSkip: (Int) -> Unit,
    onSelectStock: (ticker: String, companyName: String, source: String, logoUrl: String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val step = state.currentScreen.coerceAtMost(PICK_STEP)
    val transition = rememberSharedAxisTransition()
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        bottomBar = { OnboardingFooter(step = step, onNext = onNextScreen, onSkip = { onSkip(step) }) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = step,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
            transitionSpec = { transition(targetState > initialState) },
            label = "onboardingStep"
        ) { current ->
            when (current) {
                0 -> WelcomeContent()
                1 -> ValuePropsContent()
                else -> PickStockContent(
                    state = state,
                    searchResults = searchResults,
                    logoTransition = logoTransition,
                    onBack = onBack,
                    onSkip = { onSkip(PICK_STEP) },
                    onSelectStock = onSelectStock,
                    onSearchQueryChange = onSearchQueryChange
                )
            }
        }
    }
}

@Composable
private fun OnboardingFooter(step: Int, onNext: () -> Unit, onSkip: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3, bottom = GiSpace.x4),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingStepDots(current = step)
        AnimatedVisibility(
            visible = step < PICK_STEP,
            enter = expandVertically(GiMotion.smooth()) + fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)),
            exit = shrinkVertically(GiMotion.smooth()) + fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
        ) {
            Column(
                modifier = Modifier.padding(top = GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                GiButton(
                    text = stringResource(
                        if (step == 0) Res.string.onboarding_cta_get_started else Res.string.onboarding_cta_try_it_now
                    ),
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
                GiButton(
                    text = stringResource(Res.string.onboarding_skip_for_now),
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth(),
                    size = GiButtonSize.Medium,
                    variant = GiButtonVariant.TextMuted
                )
            }
        }
    }
}

@Composable
private fun WelcomeContent(modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    GiFillScrollColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = GiSpace.x3)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x10),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
        ) {
            GiBrandMark(modifier = Modifier.padding(bottom = GiSpace.x3))
            Text(
                text = stringResource(Res.string.onboarding_headline_value_prop),
                color = colors.ink,
                textAlign = TextAlign.Center,
                style = type.hero
            )
            Text(
                text = stringResource(Res.string.onboarding_body_value_prop),
                color = colors.muted,
                textAlign = TextAlign.Center,
                style = type.input
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GiWaveIllustration()
    }
}

@Composable
private fun ValuePropsContent(modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    GiFillScrollColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x3)
    ) {
        ValuePreviewCard(modifier = Modifier.padding(top = GiSpace.x6))
        Column(
            modifier = Modifier.padding(top = GiSpace.x8, bottom = GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x6)
        ) {
            Text(
                text = stringResource(Res.string.onboarding_headline_how_it_works),
                color = colors.ink,
                style = type.display
            )
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
                ValuePropRow(
                    title = Res.string.onboarding_value_plain_english_title,
                    body = Res.string.onboarding_value_plain_english_body
                )
                ValuePropRow(
                    title = Res.string.onboarding_value_real_time_title,
                    body = Res.string.onboarding_value_real_time_body
                )
                ValuePropRow(
                    title = Res.string.onboarding_value_follow_up_title,
                    body = Res.string.onboarding_value_follow_up_body
                )
            }
        }
    }
}

@Composable
private fun ValuePreviewCard(modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    GiCard(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {},
        contentPadding = PaddingValues(GiSpace.x5)
    ) {
        GiUserMessage(text = stringResource(Res.string.onboarding_value_card_question))
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = GiIcons.mark,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.ink
            )
            Text(text = stringResource(Res.string.gpt_investor), color = colors.muted, style = GiTheme.type.link)
            Spacer(modifier = Modifier.weight(1f))
            GiSentimentPill(
                sentiment = GiSentiment.Neutral,
                label = stringResource(Res.string.digest_sentiment_neutral)
            )
        }
        Text(
            text = stringResource(Res.string.onboarding_value_card_answer),
            color = colors.body,
            style = GiTheme.type.body
        )
    }
}

@Composable
private fun ValuePropRow(title: StringResource, body: StringResource, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        GiIconTile(icon = GiIcons.check, size = GiIconTileSize.Small, tone = GiIconTileTone.Gain)
        Column {
            Text(text = stringResource(title), color = colors.ink, style = type.label)
            Text(text = stringResource(body), color = colors.muted, style = type.meta)
        }
    }
}

@Composable
private fun OnboardingStepDots(current: Int, modifier: Modifier = Modifier) {
    GiStepDots(
        count = STEP_COUNT,
        current = current,
        modifier = modifier,
        contentDescription = stringResource(Res.string.onboarding_step_of, current + 1, STEP_COUNT)
    )
}

@Composable
private fun PickStockContent(
    state: OnboardingUiState,
    searchResults: Flow<PagingData<CompanyPresentation>>,
    logoTransition: LogoTransition,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onSelectStock: (ticker: String, companyName: String, source: String, logoUrl: String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val pagingItems = searchResults.collectAsLazyPagingItems()
    val results = if (state.searchQuery.isBlank()) {
        emptyList()
    } else {
        pagingItems.itemSnapshotList.items.take(MAX_SEARCH_RESULTS)
    }
    val scrollState = rememberScrollState()
    var tappedLogo by remember { mutableStateOf<String?>(null) }
    val selectStock: (ticker: String, name: String, source: String, logoUrl: String?) -> Unit =
        { ticker, name, source, logoUrl ->
            tappedLogo = "$source:$ticker"
            onSelectStock(ticker, name, source, logoUrl)
        }
    Column(modifier = modifier.fillMaxSize()) {
        GiTopBar(
            title = null,
            onBack = onBack,
            backContentDescription = stringResource(Res.string.back),
            scrolled = scrollState.canScrollBackward
        ) {
            GiButton(
                text = stringResource(Res.string.skip),
                onClick = onSkip,
                size = GiButtonSize.Medium,
                variant = GiButtonVariant.TextMuted
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x4)
        ) {
            Column(
                modifier = Modifier.padding(top = GiSpace.x6),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_headline_stock_selection),
                    color = colors.ink,
                    style = type.display
                )
                Text(
                    text = stringResource(Res.string.onboarding_subtext_stock_selection),
                    color = colors.muted,
                    style = type.input
                )
            }
            SuggestedStocksGrid(
                stocks = state.suggestedStocks,
                onSelectStock = {
                    selectStock(it.ticker, it.name, SOURCE_SUGGESTED, it.logoUrl.ifBlank { null })
                },
                logoModifierFor = { ticker ->
                    if (tappedLogo == "$SOURCE_SUGGESTED:$ticker") {
                        sharedLogoModifier(
                            pickLogoKey(ticker),
                            logoTransition.sharedScope,
                            logoTransition.flowScope
                        )
                    } else {
                        Modifier
                    }
                },
                modifier = Modifier.padding(top = GiSpace.x6)
            )
            GiSearchField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = stringResource(Res.string.onboarding_search_placeholder),
                clearContentDescription = stringResource(Res.string.clear_search),
                modifier = Modifier.padding(top = GiSpace.x5)
            )
            if (results.isNotEmpty()) {
                GiListGroup(modifier = Modifier.padding(top = GiSpace.x3)) {
                    results.forEachIndexed { index, company ->
                        if (index > 0) GiDivider()
                        SearchResultRow(
                            company = company,
                            onClick = {
                                selectStock(
                                    company.ticker,
                                    company.name,
                                    SOURCE_SEARCH,
                                    company.logo.ifBlank {
                                        null
                                    }
                                )
                            },
                            logoModifier = if (tappedLogo == "$SOURCE_SEARCH:${company.ticker}") {
                                sharedLogoModifier(
                                    pickLogoKey(company.ticker),
                                    logoTransition.sharedScope,
                                    logoTransition.flowScope
                                )
                            } else {
                                Modifier
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    company: CompanyPresentation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoModifier: Modifier = Modifier
) {
    val logo = tickerStock(
        ticker = company.ticker,
        title = company.name,
        subtitle = company.ticker,
        percent = null,
        imageUrl = company.logo.ifBlank { null }
    )
    GiListRow(
        title = company.name,
        modifier = modifier,
        subtitle = company.ticker,
        leading = {
            GiLogoTile(
                initials = logo.initials,
                brandColor = logo.brandColor,
                modifier = logoModifier,
                imageUrl = logo.imageUrl
            )
        },
        onClick = onClick,
        trailing = {
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = GiTheme.colors.muted
            )
        }
    )
}

@Composable
private fun SuggestedStocksGrid(
    stocks: List<SuggestedStock>,
    onSelectStock: (SuggestedStock) -> Unit,
    logoModifierFor: @Composable (ticker: String) -> Modifier,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        stocks.chunked(STEP_COUNT).forEach { rowStocks ->
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                rowStocks.forEach { stock ->
                    SuggestedStockTile(
                        stock = stock,
                        onClick = { onSelectStock(stock) },
                        modifier = Modifier.weight(1f),
                        logoModifier = logoModifierFor(stock.ticker)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedStockTile(
    stock: SuggestedStock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoModifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val haptics = LocalHapticFeedback.current
    val logo = tickerStock(
        ticker = stock.ticker,
        title = stock.name,
        subtitle = stock.ticker,
        percent = null,
        imageUrl = stock.logoUrl.ifBlank { null }
    )
    GiCard(
        modifier = modifier,
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        contentPadding = PaddingValues(horizontal = GiSpace.x2, vertical = GiSpace.x4)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiLogoTile(
                initials = logo.initials,
                brandColor = logo.brandColor,
                modifier = logoModifier,
                imageUrl = logo.imageUrl
            )
            Text(text = stock.ticker, color = colors.ink, style = type.label)
            Text(
                text = stock.name,
                color = colors.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = type.meta
            )
        }
    }
}

@Composable
private fun BriefScreen(
    state: OnboardingUiState,
    logoTransition: LogoTransition,
    onBack: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val reducedMotion = isReducedMotionEnabled()
    val briefRise = with(LocalDensity.current) { GiSpace.x4.roundToPx() }
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = null,
                onBack = onBack,
                backContentDescription = stringResource(Res.string.back),
                scrolled = state.briefView is BriefView.Success && listState.canScrollBackward
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.briefView,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentKey = { it::class },
            transitionSpec = {
                val enter = fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart))
                val rise = if (reducedMotion || targetState !is BriefView.Success) {
                    EnterTransition.None
                } else {
                    slideInVertically(tween(GiMotion.SLOW, easing = EaseOutQuart)) { briefRise }
                }
                (enter + rise) togetherWith fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
            },
            label = "briefView"
        ) { briefView ->
            val briefScope = this
            when (briefView) {
                is BriefView.Loading -> BriefLoading(
                    state = state,
                    logoTransition = logoTransition,
                    briefScope = briefScope
                )
                is BriefView.Success -> BriefContent(
                    state = state,
                    briefView = briefView,
                    listState = listState,
                    onFinish = onFinish,
                    sharedScope = logoTransition.sharedScope,
                    briefScope = briefScope
                )
                is BriefView.Error -> BriefError(onFinish = onFinish)
            }
        }
    }
}

@Composable
private fun BriefLoading(
    state: OnboardingUiState,
    logoTransition: LogoTransition,
    briefScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val companyName = state.selectedCompanyName.orEmpty()
    val ticker = state.selectedTicker.orEmpty()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x1),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        GiCard(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Polite },
            contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiBrandMark(size = GiBrandMarkSize.Small, spinning = true)
                Column {
                    Text(
                        text = stringResource(Res.string.onboarding_loading_headline, companyName),
                        color = colors.ink,
                        style = GiTheme.type.label
                    )
                    Text(
                        text = stringResource(Res.string.onboarding_loading_subtitle),
                        color = colors.muted,
                        style = GiTheme.type.meta
                    )
                }
            }
        }
        CompanyBriefSkeleton(
            header = {
                GiStockSummary(
                    stock = tickerStock(
                        ticker = ticker,
                        title = companyName,
                        subtitle = ticker,
                        percent = null,
                        imageUrl = state.selectedLogoUrl
                    ),
                    size = GiStockSummarySize.Header,
                    logoModifier = sharedLogoModifier(
                        pickLogoKey(ticker),
                        logoTransition.sharedScope,
                        logoTransition.flowScope
                    ).then(sharedLogoModifier(briefLogoKey(ticker), logoTransition.sharedScope, briefScope))
                )
            }
        )
    }
}

@Composable
private fun BriefContent(
    state: OnboardingUiState,
    briefView: BriefView.Success,
    listState: LazyListState,
    onFinish: () -> Unit,
    sharedScope: SharedTransitionScope,
    briefScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val brief = briefView.brief
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    var showSentimentSheet by remember { mutableStateOf(false) }
    val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val heroLogoModifier = sharedLogoModifier(briefLogoKey(brief.ticker), sharedScope, briefScope)

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = GiSpace.gutter,
            end = GiSpace.gutter,
            top = GiSpace.x1,
            bottom = GiSpace.x6 + navigationBarBottom
        ),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        item(key = "title", contentType = "title") {
            Text(
                text = stringResource(Res.string.onboarding_success_headline, state.selectedCompanyName.orEmpty()),
                color = colors.ink,
                style = type.pageTitle
            )
        }
        companyBriefItems(
            brief = brief,
            onVerdictClick = { showSentimentSheet = true },
            onKeyNumberClick = { selectedKeyNumber = it },
            logoModifier = heroLogoModifier
        )
        item(key = "footer", contentType = "footer") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = GiSpace.x2),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
            ) {
                Text(
                    text = stringResource(Res.string.onboarding_success_body),
                    color = colors.muted,
                    textAlign = TextAlign.Center,
                    style = type.body
                )
                GiButton(
                    text = stringResource(Res.string.onboarding_cta_start_exploring),
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(Res.string.onboarding_brief_disclaimer),
                    color = colors.muted,
                    textAlign = TextAlign.Center,
                    style = type.meta
                )
            }
        }
    }

    selectedKeyNumber?.let { keyNumber ->
        CompanyKeyNumberSheet(keyNumber = keyNumber, onDismiss = { selectedKeyNumber = null })
    }
    val sentiment = brief.sentiment
    if (showSentimentSheet && sentiment != null) {
        CompanyBriefSentimentSheet(
            sentiment = sentiment,
            summary = brief.sentimentSummary,
            onDismiss = { showSentimentSheet = false }
        )
    }
}

@Composable
private fun BriefError(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.gutter),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x6, Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(Res.string.onboarding_error_message),
            color = GiTheme.colors.body,
            textAlign = TextAlign.Center,
            style = GiTheme.type.body
        )
        GiButton(
            text = stringResource(Res.string.onboarding_cta_go_to_app),
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun OnboardingScreenPreview(state: OnboardingUiState) {
    GPTInvestorTheme {
        OnboardingScreen(
            state = state,
            searchResults = flowOf(PagingData.from(emptyList())),
            onNextScreen = {},
            onBack = {},
            onSkip = {},
            onSelectStock = { _, _, _, _ -> },
            onSearchQueryChange = {},
            onBackToStockSelection = {},
            onFinish = {}
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun OnboardingWelcomePreview() {
    OnboardingScreenPreview(state = OnboardingUiState(currentScreen = 0))
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun OnboardingValuePropsPreview() {
    OnboardingScreenPreview(state = OnboardingUiState(currentScreen = 1))
}

@PreviewLightDark
@Composable
private fun OnboardingPickStockPreview() {
    OnboardingScreenPreview(state = OnboardingUiState(currentScreen = 2))
}

@PreviewLightDark
@Composable
private fun OnboardingBriefLoadingPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(currentScreen = 3, selectedCompanyName = "Tesla", briefView = BriefView.Loading)
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun OnboardingBriefPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(
            currentScreen = 3,
            selectedCompanyName = "Tesla",
            briefView = BriefView.Success(previewCompanyBrief)
        )
    )
}

@PreviewLightDark
@Composable
private fun OnboardingBriefErrorPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(currentScreen = 3, briefView = BriefView.Error("Something went wrong."))
    )
}
