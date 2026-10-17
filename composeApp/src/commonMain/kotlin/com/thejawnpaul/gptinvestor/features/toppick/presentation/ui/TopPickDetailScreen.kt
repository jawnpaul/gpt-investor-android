package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.about_company_name
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.ContentBookmarkFill
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickAction
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickEvent
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPickDetailView
import com.thejawnpaul.gptinvestor.helpful
import com.thejawnpaul.gptinvestor.sources
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiDeltaPill
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIconToggleButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiPriceDisplay
import com.thejawnpaul.gptinvestor.theme.components.GiPriceSize
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiTabs
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastResult
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBarTitleStyle
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.today
import com.thejawnpaul.gptinvestor.top_pick_disclaimer
import com.thejawnpaul.gptinvestor.top_pick_error_title
import com.thejawnpaul.gptinvestor.top_pick_key_metrics
import com.thejawnpaul.gptinvestor.top_pick_key_risks
import com.thejawnpaul.gptinvestor.top_pick_label
import com.thejawnpaul.gptinvestor.top_pick_loading
import com.thejawnpaul.gptinvestor.top_pick_rationale
import com.thejawnpaul.gptinvestor.top_pick_save
import com.thejawnpaul.gptinvestor.top_pick_saved_to_picks
import com.thejawnpaul.gptinvestor.top_pick_share
import com.thejawnpaul.gptinvestor.top_pick_sources_subtitle
import com.thejawnpaul.gptinvestor.top_pick_tab_key_ratios
import com.thejawnpaul.gptinvestor.top_pick_tab_news
import com.thejawnpaul.gptinvestor.top_pick_tab_overview
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
    modifier: Modifier = Modifier,
    onOpenSavedList: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val scrollState = rememberScrollState()
    val toastState = rememberGiToastState()
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var headerHeight by remember { mutableIntStateOf(0) }
    val collapsed by remember(scrollState) { derivedStateOf { headerHeight > 0 && scrollState.value > headerHeight } }
    val pick = state.topPick
    val company = state.companyPresentation
    val savedMessage = stringResource(Res.string.top_pick_saved_to_picks)
    val viewLabel = stringResource(Res.string.view)
    var isLiked by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column {
                AnimatedContent(
                    targetState = if (collapsed) pick else null,
                    transitionSpec = {
                        fadeIn(tween(GiMotion.FAST, easing = GiEasing.emphasised)) togetherWith
                            fadeOut(tween(GiMotion.INSTANT, easing = GiEasing.emphasised))
                    },
                    contentKey = { it != null },
                    label = "pickTopBar"
                ) { collapsedPick ->
                    if (collapsedPick != null) {
                        GiTopBar(
                            title = null,
                            onBack = { onAction(TopPickAction.OnGoBack) },
                            backContentDescription = stringResource(Res.string.back),
                            scrolled = true,
                            center = { CompactPickHeader(pick = collapsedPick, company = company) }
                        )
                    } else {
                        GiTopBar(
                            title = stringResource(Res.string.top_pick_label),
                            onBack = { onAction(TopPickAction.OnGoBack) },
                            backContentDescription = stringResource(Res.string.back),
                            titleStyle = GiTopBarTitleStyle.Centered
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
            PickActionBar(
                isLiked = isLiked,
                isSaved = pick?.isSaved == true,
                enabled = pick != null,
                onLike = {
                    onEvent(if (it) TopPickEvent.LikeTopPick else TopPickEvent.RemoveLikeTopPick)
                    isLiked = it
                },
                onSave = { save ->
                    onEvent(if (save) TopPickEvent.BookmarkTopPick else TopPickEvent.RemoveBookmarkTopPick)
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
                onShare = { onEvent(TopPickEvent.ShareTopPick) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                pick != null -> Column(modifier = Modifier.verticalScroll(scrollState)) {
                    PickHeader(
                        pick = pick,
                        company = company,
                        modifier = Modifier.onSizeChanged { headerHeight = it.height }
                    )
                    PickSheet(
                        state = state,
                        pick = pick,
                        onEvent = onEvent,
                        onOpenUrl = { url -> runCatching { uriHandler.openUri(url) } }
                    )
                }

                state.loading -> PickSkeleton()

                else -> GiErrorState(
                    title = stringResource(Res.string.top_pick_error_title),
                    body = stringResource(Res.string.check_connection_try_again),
                    actionText = stringResource(Res.string.try_again),
                    onAction = { onEvent(TopPickEvent.Retry) }
                )
            }

            GiToastHost(
                state = toastState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x4)
            )
        }

        if (state.showNewsSourcesBottomSheet && company != null) {
            GiBottomSheet(
                onDismissRequest = { onEvent(TopPickEvent.ClickNewsSources(show = false)) },
                title = stringResource(Res.string.sources),
                subtitle = stringResource(Res.string.top_pick_sources_subtitle),
                closeContentDescription = stringResource(Res.string.close)
            ) {
                val yahooName = stringResource(Res.string.yahoo_finance)
                SourceRows(
                    sources = remember(company, yahooName) { pickSources(company, yahooName) },
                    onOpen = { url -> runCatching { uriHandler.openUri(url) } }
                )
            }
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
    val change = (company?.change ?: pick.percentageChange).toDouble()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x1, bottom = GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
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
            GiPriceDisplay(price = price, size = GiPriceSize.Medium)
            GiDeltaPill(percent = change, suffix = stringResource(Res.string.today))
        }
    }
}

@Composable
private fun CompactPickHeader(pick: TopPickPresentation, company: CompanyDetailRemoteResponse?) {
    val colors = GiTheme.colors
    val change = (company?.change ?: pick.percentageChange).toDouble()
    Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
        GiLogoTile(
            initials = pickInitials(pick.ticker),
            imageUrl = company?.formattedImageUrl?.takeIf { it.isNotBlank() } ?: pick.imageUrl,
            size = GiSpace.x8
        )
        Column(modifier = Modifier.weight(1f)) {
            GiText(
                text = company?.name ?: pick.companyName,
                style = GiTextStyle.Label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            GiText(text = pick.ticker, style = GiTextStyle.Meta, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            GiText(
                text = formatPrice((company?.price ?: pick.currentPrice).toDouble()),
                style = GiTextStyle.LabelRegular
            )
            GiText(
                text = formatPercent(change),
                style = GiTextStyle.Caption,
                fontWeight = FontWeight.Medium,
                color = when {
                    change > 0 -> colors.gain
                    change < 0 -> colors.loss
                    else -> colors.muted
                }
            )
        }
    }
}

@Composable
private fun PickSheet(
    state: TopPickDetailView,
    pick: TopPickPresentation,
    onEvent: (TopPickEvent) -> Unit,
    onOpenUrl: (String) -> Unit
) {
    val colors = GiTheme.colors
    val company = state.companyPresentation
    val shape = GiRadius.md.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    var tab by remember { mutableIntStateOf(0) }
    val lineColor = colors.line
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.raised)
            .drawBehind {
                val stroke = GiEdge.width.toPx()
                clipRect(bottom = GiSpace.x3.toPx() + stroke) {
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this@drawBehind),
                        color = lineColor,
                        style = Stroke(width = stroke * 2)
                    )
                }
            }
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x6, bottom = GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x8)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PickSectionTitle(text = stringResource(Res.string.top_pick_rationale), modifier = Modifier.weight(1f))
                ScoreTag(score = pick.confidenceScore)
            }
            ClampedText(
                text = pick.rationale,
                maxLines = RATIONALE_LINES,
                onReadMore = if (state.isLoggedIn) null else ({ onEvent(TopPickEvent.Authenticate(showDialog = true)) })
            )
        }

        if (state.isLoggedIn) {
            if (pick.metrics.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                    PickSectionTitle(text = stringResource(Res.string.top_pick_key_metrics))
                    MetricTiles(metrics = pick.metrics)
                }
            }
            if (pick.risks.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                    PickSectionTitle(text = stringResource(Res.string.top_pick_key_risks))
                    RiskList(risks = pick.risks)
                }
            }

            if (company != null) {
                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x6)) {
                    GiTabs(
                        tabs = listOf(
                            stringResource(Res.string.top_pick_tab_overview),
                            stringResource(Res.string.top_pick_tab_key_ratios),
                            stringResource(Res.string.top_pick_tab_news)
                        ),
                        selectedIndex = tab,
                        onSelect = { tab = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    when (tab) {
                        0 -> PriceChartSection(
                            companyName = company.name ?: pick.companyName,
                            history = company.historicalData.orEmpty()
                        )
                        1 -> KeyRatios(company = company)
                        else -> PickNews(company = company, onOpen = onOpenUrl)
                    }
                }

                if (tab == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                        PickSectionTitle(
                            text = stringResource(Res.string.about_company_name, company.name ?: pick.companyName)
                        )
                        company.about?.takeIf {
                            it.isNotBlank()
                        }?.let { ClampedText(text = it, maxLines = ABOUT_LINES) }
                        val yahooName = stringResource(Res.string.yahoo_finance)
                        val sourceCount = remember(company, yahooName) { pickSources(company, yahooName).size }
                        SourcesRow(
                            count = sourceCount,
                            onClick = { onEvent(TopPickEvent.ClickNewsSources(show = true)) },
                            modifier = Modifier.padding(top = GiSpace.x2)
                        )
                    }
                }
            }
        }

        GiText(text = stringResource(Res.string.top_pick_disclaimer), style = GiTextStyle.Meta)
    }
}

private const val RATIONALE_LINES = 4
private const val ABOUT_LINES = 3

internal fun pickSources(company: CompanyDetailRemoteResponse, yahooName: String): List<PickSource> {
    val news = company.news.orEmpty().distinctBy { it.publisher }.map { PickSource(name = it.publisher, url = it.link) }
    return news + PickSource(name = yahooName, url = "https://finance.yahoo.com/quote/${company.ticker}")
}

@Composable
private fun PickActionBar(
    isLiked: Boolean,
    isSaved: Boolean,
    enabled: Boolean,
    onLike: (Boolean) -> Unit,
    onSave: (Boolean) -> Unit,
    onShare: () -> Unit
) {
    Column(modifier = Modifier.background(GiTheme.colors.raised)) {
        GiDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = GiSpace.x3, vertical = GiSpace.x2),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiIconToggleButton(
                checked = isLiked,
                onCheckedChange = onLike,
                icon = GiIcons.thumbsUp,
                checkedIcon = GiIcons.thumbsUpFill,
                contentDescription = stringResource(Res.string.helpful),
                variant = GiIconButtonVariant.Ghost,
                enabled = enabled
            )
            GiIconToggleButton(
                checked = isSaved,
                onCheckedChange = onSave,
                icon = GiIcons.bookmarkSimple,
                checkedIcon = ContentBookmarkFill,
                contentDescription = stringResource(Res.string.top_pick_save),
                variant = GiIconButtonVariant.Ghost,
                enabled = enabled
            )
            Spacer(modifier = Modifier.weight(1f))
            GiIconButton(
                icon = GiIcons.shareNetwork,
                contentDescription = stringResource(Res.string.top_pick_share),
                onClick = onShare,
                variant = GiIconButtonVariant.Ghost,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun PickSkeleton() {
    val colors = GiTheme.colors
    val loading = stringResource(Res.string.top_pick_loading)
    val shape = GiRadius.md.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = loading
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        Column(
            modifier = Modifier.padding(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x1,
                bottom = GiSpace.x6
            ),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
        ) {
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
                .fillMaxSize()
                .clip(shape)
                .background(colors.raised)
                .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x6),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                GiSkeleton(modifier = Modifier.width(SKEL_SECTION).height(SKEL_TITLE))
                GiSkeleton(modifier = Modifier.width(SKEL_SCORE).height(GiSpace.x6), shape = GiRadius.xs)
            }
            listOf(1f, 1f, 0.92f, 0.55f).forEach {
                GiSkeleton(modifier = Modifier.fillMaxWidth(it).height(SKEL_LINE))
            }
            Spacer(modifier = Modifier.height(GiSpace.x2))
            GiSkeleton(modifier = Modifier.width(SKEL_NAME).height(SKEL_TITLE))
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                repeat(3) { GiSkeleton(modifier = Modifier.weight(1f).height(SKEL_TILE)) }
            }
            Spacer(modifier = Modifier.height(GiSpace.x2))
            GiSkeleton(modifier = Modifier.width(SKEL_SECTION_SHORT).height(SKEL_TITLE))
            GiSkeleton(modifier = Modifier.fillMaxWidth(0.8f).height(SKEL_LINE))
            GiSkeleton(modifier = Modifier.fillMaxWidth(0.7f).height(SKEL_LINE))
        }
    }
}

private val SKEL_NAME = 120.dp
private val SKEL_TICKER = 150.dp
private val SKEL_PRICE = 128.dp
private val SKEL_PILL = 104.dp
private val SKEL_SECTION = 180.dp
private val SKEL_SECTION_SHORT = 100.dp
private val SKEL_SCORE = 80.dp
private val SKEL_TITLE = 22.dp
private val SKEL_LINE = 14.dp
private val SKEL_TILE = 66.dp

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
            onAction = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun TopPickDetailLoadingPreview() {
    GPTInvestorTheme {
        TopPickDetailScreen(state = TopPickDetailView(loading = true), onEvent = {}, onAction = {})
    }
}
