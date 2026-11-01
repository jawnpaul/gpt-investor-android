package com.thejawnpaul.gptinvestor.features.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.clear_search
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyBriefLayout
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.CompanyKeyNumberSheet
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.companyBriefItems
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief.previewCompanyBrief
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.SuggestedStock
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.BriefView
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.state.OnboardingUiState
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.onboarding_body_value_prop
import com.thejawnpaul.gptinvestor.onboarding_brief_disclaimer
import com.thejawnpaul.gptinvestor.onboarding_brief_error_message
import com.thejawnpaul.gptinvestor.onboarding_brief_limit_message
import com.thejawnpaul.gptinvestor.onboarding_cta_get_started
import com.thejawnpaul.gptinvestor.onboarding_cta_go_to_app_short
import com.thejawnpaul.gptinvestor.onboarding_cta_start_exploring
import com.thejawnpaul.gptinvestor.onboarding_cta_try_it_now
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
import com.thejawnpaul.gptinvestor.sign_up
import com.thejawnpaul.gptinvestor.skip
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBrandMark
import com.thejawnpaul.gptinvestor.theme.components.GiBrandMarkSize
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiContentSheet
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
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiStepDots
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import com.thejawnpaul.gptinvestor.theme.components.GiUserMessage
import com.thejawnpaul.gptinvestor.theme.components.GiWaveIllustration
import com.thejawnpaul.gptinvestor.theme.components.giBeam
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.theme.rememberGiSharedAxisTransition
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)

private const val STEP_COUNT = 3
private const val GRID_COLUMNS = 3
private const val LARGE_TEXT_GRID_COLUMNS = 2
private const val LARGE_TEXT_FONT_SCALE = 1.5f
private const val BEAM_STRENGTH = 0.6f
private const val PICK_STEP = 2
private const val BRIEF_SCREEN = 3
private const val MAX_SEARCH_RESULTS = 20
private const val SOURCE_SUGGESTED = "suggested"
private const val NIGERIA_TICKER_SUFFIX = ".NG"
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
    onRetryBrief: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberGiSharedAxisTransition()
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
                    onRetry = onRetryBrief,
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
    val transition = rememberGiSharedAxisTransition()
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
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2, bottom = GiSpace.x4),
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
            GiText(
                text = stringResource(Res.string.onboarding_headline_value_prop),
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.Hero,
                textAlign = TextAlign.Center
            )
            GiText(
                text = stringResource(Res.string.onboarding_body_value_prop),
                style = GiTextStyle.Input,
                color = GiTheme.colors.muted,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        GiWaveIllustration()
    }
}

@Composable
private fun ValuePropsContent(modifier: Modifier = Modifier) {
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
            GiText(
                text = stringResource(Res.string.onboarding_headline_how_it_works),
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.Display
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
                modifier = Modifier.size(GiIconSize.md),
                tint = colors.ink
            )
            GiText(text = stringResource(Res.string.gpt_investor), style = GiTextStyle.Link, color = colors.muted)
        }
        GiText(text = stringResource(Res.string.onboarding_value_card_answer), style = GiTextStyle.Body)
    }
}

@Composable
private fun ValuePropRow(title: StringResource, body: StringResource, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        GiIconTile(icon = GiIcons.check, size = GiIconTileSize.Small, tone = GiIconTileTone.Gain)
        Column {
            GiText(text = stringResource(title), style = GiTextStyle.Label)
            GiText(text = stringResource(body), style = GiTextStyle.MetaRelaxed)
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
    val pagingItems = searchResults.collectAsLazyPagingItems()
    val results = if (state.searchQuery.isBlank()) {
        emptyList()
    } else {
        pagingItems.itemSnapshotList.items.take(MAX_SEARCH_RESULTS)
    }
    val scrollState = rememberScrollState()
    val scrolled by remember(scrollState) { derivedStateOf { scrollState.canScrollBackward } }
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
            scrolled = scrolled
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
                modifier = Modifier.padding(top = GiSpace.x3),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                GiText(
                    text = stringResource(Res.string.onboarding_headline_stock_selection),
                    modifier = Modifier.semantics { heading() },
                    style = GiTextStyle.Display
                )
                GiText(
                    text = stringResource(Res.string.onboarding_subtext_stock_selection),
                    style = GiTextStyle.Input,
                    color = GiTheme.colors.muted
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
                modifier = Modifier.padding(top = GiSpace.x7)
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
                modifier = Modifier.size(GiIconSize.sm),
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
    val columns = if (isLargeText()) LARGE_TEXT_GRID_COLUMNS else GRID_COLUMNS
    AnimatedContent(
        targetState = stocks,
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(GiMotion.BASE, easing = EaseOutQuart)) togetherWith
                fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
        },
        label = "suggestedStocks"
    ) { shownStocks ->
        SuggestedStocksRows(
            stocks = shownStocks,
            columns = columns,
            onSelectStock = onSelectStock,
            logoModifierFor = logoModifierFor
        )
    }
}

@Composable
private fun SuggestedStocksRows(
    stocks: List<SuggestedStock>,
    columns: Int,
    onSelectStock: (SuggestedStock) -> Unit,
    logoModifierFor: @Composable (ticker: String) -> Modifier
) {
    val rows = remember(stocks, columns) { stocks.chunked(columns) }
    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        rows.forEach { rowStocks ->
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                rowStocks.forEach { stock ->
                    SuggestedStockTile(
                        stock = stock,
                        onClick = { onSelectStock(stock) },
                        modifier = Modifier.weight(1f),
                        logoModifier = logoModifierFor(stock.ticker)
                    )
                }
                // Keep a short last row on the grid instead of stretching its tiles.
                repeat(columns - rowStocks.size) { Spacer(modifier = Modifier.weight(1f)) }
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
            GiText(
                text = stock.ticker.removeSuffix(NIGERIA_TICKER_SUFFIX),
                style = GiTextStyle.Label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            GiText(
                text = stock.name,
                style = GiTextStyle.Meta,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun BriefScreen(
    state: OnboardingUiState,
    logoTransition: LogoTransition,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val listScrolled by remember(listState) { derivedStateOf { listState.canScrollBackward } }
    val reducedMotion = isReducedMotionEnabled()
    val briefRise = with(LocalDensity.current) { GiSpace.x4.roundToPx() }
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                // The exchange isn't in the onboarding state yet; the board's "TSLA · NASDAQ" falls back to the ticker.
                title = state.briefTitle,
                onBack = onBack,
                backContentDescription = stringResource(Res.string.back),
                scrolled = state.briefView is BriefView.Success && listScrolled,
                titleStyle = GiTopBarTitleStyle.Centered
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
                is BriefView.Error -> BriefError(onRetry = onRetry, onFinish = onFinish)
                is BriefView.LimitReached -> BriefLimitReached(onSignUp = onFinish)
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
    val companyName = state.selectedCompanyName.orEmpty()
    val ticker = state.selectedTicker.orEmpty()
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(GiSpace.x5)) {
        Column(
            modifier = Modifier.padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x5)
        ) {
            GiCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .giBeam(active = true, shape = GiRadius.md, strength = BEAM_STRENGTH)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiBrandMark(size = GiBrandMarkSize.Small, spinning = true)
                    Column {
                        GiText(
                            text = stringResource(Res.string.onboarding_loading_headline, companyName),
                            style = GiTextStyle.Label
                        )
                        GiText(
                            text = stringResource(Res.string.onboarding_loading_subtitle),
                            style = GiTextStyle.MetaRelaxed
                        )
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
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
                GiSkeleton(modifier = Modifier.size(width = 150.dp, height = 32.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth().height(64.dp), shape = GiRadius.md)
            }
        }
        OnboardingSheetSkeleton(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun OnboardingSheetSkeleton(modifier: Modifier = Modifier) {
    GiContentSheet(
        modifier = modifier.clearAndSetSemantics {},
        contentPadding = PaddingValues(GiSpace.x5),
        verticalSpacing = GiSpace.x6
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiSkeleton(modifier = Modifier.size(width = 96.dp, height = 18.dp))
            listOf(1f, 1f, 0.64f).forEach { width ->
                GiSkeleton(modifier = Modifier.fillMaxWidth(width).height(12.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiSkeleton(modifier = Modifier.size(width = 120.dp, height = 18.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                repeat(3) { GiSkeleton(modifier = Modifier.weight(1f).height(76.dp)) }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiSkeleton(modifier = Modifier.size(width = 140.dp, height = 18.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth().height(14.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth(0.82f).height(14.dp))
            GiSkeleton(modifier = Modifier.fillMaxWidth().height(48.dp))
        }
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
    val brief = briefView.brief
    var selectedKeyNumber by remember { mutableStateOf<KeyNumber?>(null) }
    val navigationBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val heroLogoModifier = sharedLogoModifier(briefLogoKey(brief.ticker), sharedScope, briefScope)

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        // The sections sheet is full-bleed and docks to the bottom, so only the page items are inset.
        contentPadding = PaddingValues(top = GiSpace.x2),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        item(key = "title", contentType = "title") {
            GiText(
                text = stringResource(Res.string.onboarding_success_headline, state.selectedCompanyName.orEmpty()),
                modifier = Modifier
                    .padding(horizontal = GiSpace.gutter)
                    .semantics { heading() },
                style = GiTextStyle.PageTitle
            )
        }
        companyBriefItems(
            brief = brief,
            onKeyNumberClick = { selectedKeyNumber = it },
            logoModifier = heroLogoModifier,
            layout = CompanyBriefLayout.Onboarding,
            sheetBottomPadding = GiSpace.safeArea + navigationBarBottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = GiSpace.x2),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
            ) {
                GiText(
                    text = stringResource(Res.string.onboarding_success_body),
                    style = GiTextStyle.Body,
                    color = GiTheme.colors.muted,
                    textAlign = TextAlign.Center
                )
                GiButton(
                    text = stringResource(Res.string.onboarding_cta_start_exploring),
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                )
                GiText(
                    text = stringResource(Res.string.onboarding_brief_disclaimer),
                    style = GiTextStyle.Meta,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    selectedKeyNumber?.let { keyNumber ->
        CompanyKeyNumberSheet(keyNumber = keyNumber, onDismiss = { selectedKeyNumber = null })
    }
}

@Composable
private fun BriefError(onRetry: () -> Unit, onFinish: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.gutter),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x6, Alignment.CenterVertically)
    ) {
        GiText(
            text = stringResource(Res.string.onboarding_brief_error_message),
            style = GiTextStyle.Body,
            textAlign = TextAlign.Center
        )
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiButton(
                text = stringResource(Res.string.try_again),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
            GiButton(
                text = stringResource(Res.string.onboarding_cta_go_to_app_short),
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                size = GiButtonSize.Medium,
                variant = GiButtonVariant.TextMuted
            )
        }
    }
}

@Composable
private fun BriefLimitReached(onSignUp: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = GiSpace.gutter),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x6, Alignment.CenterVertically)
    ) {
        GiText(
            text = stringResource(Res.string.onboarding_brief_limit_message),
            style = GiTextStyle.Body,
            textAlign = TextAlign.Center
        )
        GiButton(
            text = stringResource(Res.string.sign_up),
            onClick = onSignUp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
@ReadOnlyComposable
private fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE

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
            onRetryBrief = {},
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
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun OnboardingPickStockPreview() {
    OnboardingScreenPreview(state = OnboardingUiState(currentScreen = 2))
}

@PreviewLightDark
@Composable
private fun OnboardingBriefLoadingPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(
            currentScreen = 3,
            selectedTicker = "TSLA",
            selectedCompanyName = "Tesla",
            briefView = BriefView.Loading
        )
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun OnboardingBriefPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(
            currentScreen = 3,
            selectedTicker = "TSLA",
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

@PreviewLightDark
@Composable
private fun OnboardingBriefLimitReachedPreview() {
    OnboardingScreenPreview(
        state = OnboardingUiState(currentScreen = 3, selectedTicker = "TSLA", briefView = BriefView.LimitReached)
    )
}
