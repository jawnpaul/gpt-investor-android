package com.thejawnpaul.gptinvestor.features.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.curated_for_you
import com.thejawnpaul.gptinvestor.daily_learn_tidbit
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.discover_lesson_error_body
import com.thejawnpaul.gptinvestor.discover_lesson_error_title
import com.thejawnpaul.gptinvestor.discover_picks_error_title
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.news_new_stories
import com.thejawnpaul.gptinvestor.news_section_news
import com.thejawnpaul.gptinvestor.news_section_picks
import com.thejawnpaul.gptinvestor.search_a_stock_to_analyse
import com.thejawnpaul.gptinvestor.see_all
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLargeTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiSegmentedControl
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.top_picks_today
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun DiscoverScreen(
    pagerState: PagerState,
    isGuestSession: Boolean,
    hasNewStories: Boolean,
    onGoToLogin: () -> Unit,
    picks: @Composable () -> Unit,
    news: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val reducedMotion = isReducedMotionEnabled()
    var selectedPage by rememberSaveable { mutableIntStateOf(pagerState.settledPage) }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { selectedPage = it }
    }
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = { GiLargeTopBar(title = stringResource(Res.string.discover)) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                modifier = Modifier.padding(horizontal = GiSpace.gutter).padding(top = GiSpace.x2, bottom = GiSpace.x4),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
            ) {
                if (isGuestSession) TopGuestLabel(onClick = onGoToLogin)
                GiSegmentedControl(
                    options = listOf(
                        stringResource(Res.string.news_section_picks),
                        stringResource(Res.string.news_section_news)
                    ),
                    selectedIndex = selectedPage,
                    onSelect = { page ->
                        selectedPage = page
                        scope.launch {
                            if (reducedMotion) {
                                pagerState.scrollToPage(page)
                            } else {
                                pagerState.animateScrollToPage(page, animationSpec = GiMotion.snappy())
                            }
                        }
                    },
                    dotIndices = if (hasNewStories && selectedPage != NEWS_PAGE) {
                        setOf(NEWS_PAGE)
                    } else {
                        emptySet()
                    },
                    dotDescription = stringResource(Res.string.news_new_stories),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                beyondViewportPageCount = 1,
                key = { it }
            ) { page ->
                if (page == NEWS_PAGE) news() else picks()
            }
        }
    }
}

const val PICKS_PAGE = 0
const val NEWS_PAGE = 1
const val DISCOVER_PAGE_COUNT = 2

@Composable
fun DiscoverPicksTab(state: DiscoveryScreenState, onEvent: (DiscoveryEvent) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.sectionGap),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        item(key = "search") {
            DiscoverSearchEntry(onClick = { onEvent(DiscoveryEvent.GoToSearch) })
        }
        item(key = "browse") {
            BrowseCompaniesCard(onClick = { onEvent(DiscoveryEvent.GoToBrowse) })
        }
        item(key = "picks") {
            TopPicksSection(
                view = state.topPicksView,
                picks = state.previewTopPicks,
                onSeeAll = { onEvent(DiscoveryEvent.GoToAllTopPicks) },
                onRetry = { onEvent(DiscoveryEvent.RetryTopPicks) },
                onClickPick = { onEvent(DiscoveryEvent.ClickTopPick(id = it)) },
                modifier = Modifier.padding(top = GiSpace.x2)
            )
        }
        item(key = "tidbit") {
            val placement = if (isReducedMotionEnabled()) {
                Modifier
            } else {
                Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null, placementSpec = GiMotion.smooth())
            }
            TidbitSection(
                tidbit = state.homeTidbitView,
                onRetry = { onEvent(DiscoveryEvent.RetryTidbit) },
                onClick = { onEvent(DiscoveryEvent.ClickTidbit(id = it)) },
                modifier = placement.padding(top = GiSpace.x2)
            )
        }
    }
}

@Composable
private fun TopPicksSection(
    view: TopPicksView,
    picks: List<TopPickPresentation>,
    onSeeAll: () -> Unit,
    onRetry: () -> Unit,
    onClickPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
        GiSectionHeader(
            title = stringResource(Res.string.curated_for_you),
            modifier = Modifier.semantics { heading() },
            eyebrow = stringResource(Res.string.top_picks_today),
            trailingText = stringResource(Res.string.see_all),
            onTrailingClick = onSeeAll
        )
        when {
            view.loading && view.topPicks.isEmpty() -> TopPicksSkeleton()
            view.showError -> DiscoverSectionError(
                title = stringResource(Res.string.discover_picks_error_title),
                body = stringResource(Res.string.check_connection_try_again),
                onRetry = onRetry
            )
            else -> picks.forEach { pick ->
                TopPickCard(pick = pick, onClick = { onClickPick(pick.id) })
            }
        }
    }
}

@Composable
private fun TidbitSection(
    tidbit: HomeTidbitView,
    onRetry: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasTidbit = tidbit.id.isNotBlank() || tidbit.title.isNotBlank()
    if (!tidbit.loading && tidbit.error == null && !hasTidbit) return
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
        GiEyebrow(
            text = stringResource(Res.string.daily_learn_tidbit),
            modifier = Modifier.semantics { heading() }
        )
        when {
            tidbit.loading -> TidbitSkeleton()
            tidbit.error != null -> DiscoverSectionError(
                title = stringResource(Res.string.discover_lesson_error_title),
                body = stringResource(Res.string.discover_lesson_error_body),
                onRetry = onRetry
            )
            else -> TidbitCard(tidbit = tidbit, onClick = { onClick(tidbit.id) })
        }
    }
}

@Composable
private fun DiscoverSearchEntry(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = GiControlHeight.search)
            .clip(GiRadius.sm)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.sm)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x2),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = GiIcons.magnifyingGlass,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.md),
            tint = colors.muted
        )
        GiText(
            text = stringResource(Res.string.search_a_stock_to_analyse),
            modifier = Modifier.weight(1f),
            style = GiTextStyle.Input,
            color = colors.muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private val previewPicks = listOf(
    TopPickPresentation(
        id = "1",
        ticker = "GOOGL",
        companyName = "Alphabet Inc.",
        rationale = "Alphabet remains a cornerstone of the digital economy. Its dominance in search and " +
            "advertising provides a massive cash engine to fund its aggressive AI infrastructure build-out.",
        metrics = emptyList(),
        risks = emptyList(),
        confidenceScore = 80,
        isSaved = false,
        percentageChange = 1.2f,
        currentPrice = 185.0f
    ),
    TopPickPresentation(
        id = "2",
        ticker = "AVGO",
        companyName = "Broadcom Inc.",
        rationale = "Broadcom is the primary beneficiary of the AI infrastructure boom.",
        metrics = emptyList(),
        risks = emptyList(),
        confidenceScore = 75,
        isSaved = false,
        percentageChange = 0.4f,
        currentPrice = 320.0f
    )
)

private val previewTidbit = HomeTidbitView(
    id = "t1",
    title = "Rethink early retirement: lessons from the FIRE movement"
)

@Composable
private fun DiscoverPreview(state: DiscoveryScreenState) {
    GPTInvestorTheme {
        DiscoverScreen(
            pagerState = rememberPagerState { DISCOVER_PAGE_COUNT },
            isGuestSession = state.isGuestSession,
            hasNewStories = true,
            onGoToLogin = {},
            picks = { DiscoverPicksTab(state = state, onEvent = {}) },
            news = {}
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun DiscoverScreenLoadedPreview() {
    DiscoverPreview(
        DiscoveryScreenState(topPicksView = TopPicksView(topPicks = previewPicks), homeTidbitView = previewTidbit)
    )
}

@PreviewLightDark
@Composable
private fun DiscoverScreenLoadingPreview() {
    DiscoverPreview(
        DiscoveryScreenState(
            topPicksView = TopPicksView(loading = true),
            homeTidbitView = HomeTidbitView(loading = true)
        )
    )
}

@PreviewLightDark
@Composable
private fun DiscoverScreenGuestErrorPreview() {
    DiscoverPreview(
        DiscoveryScreenState(
            isGuestSession = true,
            topPicksView = TopPicksView(error = "Something went wrong"),
            homeTidbitView = HomeTidbitView(error = "Something went wrong")
        )
    )
}
