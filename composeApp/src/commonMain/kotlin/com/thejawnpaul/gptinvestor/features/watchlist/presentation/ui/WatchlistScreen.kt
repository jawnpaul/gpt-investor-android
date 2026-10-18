package com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.browse_top_picks
import com.thejawnpaul.gptinvestor.build_your_watchlist
import com.thejawnpaul.gptinvestor.couldn_t_load_your_watchlist
import com.thejawnpaul.gptinvestor.create_an_account
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistBriefStatus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.formatUsdPrice
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.PopularStockUiModel
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistLoadError
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistUiState
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistEvent
import com.thejawnpaul.gptinvestor.in_your_brief
import com.thejawnpaul.gptinvestor.joins_your_next_brief
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.search_stocks
import com.thejawnpaul.gptinvestor.sign_in_action
import com.thejawnpaul.gptinvestor.summary_with_premium
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiEmptyState
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIconButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.today
import com.thejawnpaul.gptinvestor.try_again
import com.thejawnpaul.gptinvestor.upgrade
import com.thejawnpaul.gptinvestor.watch
import com.thejawnpaul.gptinvestor.watchlist_daily_summary_body
import com.thejawnpaul.gptinvestor.watchlist_daily_summary_title
import com.thejawnpaul.gptinvestor.watchlist_empty_description
import com.thejawnpaul.gptinvestor.watchlist_error_network
import com.thejawnpaul.gptinvestor.watchlist_error_server
import com.thejawnpaul.gptinvestor.watchlist_guest_body
import com.thejawnpaul.gptinvestor.watchlist_loading
import com.thejawnpaul.gptinvestor.watchlist_offline_body
import com.thejawnpaul.gptinvestor.watchlist_offline_title
import com.thejawnpaul.gptinvestor.watchlist_popular
import com.thejawnpaul.gptinvestor.watchlist_prices_as_of
import com.thejawnpaul.gptinvestor.watchlist_remove_cd
import com.thejawnpaul.gptinvestor.watchlist_stocks_count
import com.thejawnpaul.gptinvestor.watchlist_title
import com.thejawnpaul.gptinvestor.watchlist_up_count
import com.thejawnpaul.gptinvestor.watchlist_watch_cd
import com.thejawnpaul.gptinvestor.you_re_browsing_as_a_guest
import kotlin.math.abs
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private enum class WatchlistScreenState { Loading, SignUpRequired, Error, Empty, Content }

private const val LARGE_TEXT_FONT_SCALE = 1.5f
private const val SKELETON_ROWS = 6

private val ListSheetShape = GiRadius.md.copy(bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize)

@Composable
fun WatchlistScreen(state: WatchlistUiState, onEvent: (WatchlistEvent) -> Unit, modifier: Modifier = Modifier) {
    val screenState = when {
        state.requiresSignUp -> WatchlistScreenState.SignUpRequired
        state.isLoading &&
            state.watchlistItems.isEmpty() &&
            state.popularStocks.isEmpty() -> WatchlistScreenState.Loading
        state.error != null && state.watchlistItems.isEmpty() -> WatchlistScreenState.Error
        state.watchlistItems.isEmpty() -> WatchlistScreenState.Empty
        else -> WatchlistScreenState.Content
    }
    var pendingRemoval by remember { mutableStateOf<WatchlistItem?>(null) }

    AnimatedContent(
        targetState = screenState,
        modifier = modifier
            .fillMaxSize()
            .background(GiTheme.colors.bg),
        transitionSpec = {
            fadeIn(tween(GiMotion.BASE, easing = GiEasing.emphasised)) togetherWith
                fadeOut(tween(GiMotion.FAST, easing = GiEasing.emphasised))
        },
        label = "watchlist_state"
    ) { target ->
        val onSearch = { onEvent(WatchlistEvent.OnSearchClick) }
        when (target) {
            WatchlistScreenState.Loading -> WatchlistLoading(onSearch = onSearch)
            WatchlistScreenState.SignUpRequired -> WatchlistGuest(
                onSearch = onSearch,
                onSignIn = { onEvent(WatchlistEvent.OnSignInClick) }
            )
            WatchlistScreenState.Error -> Column(modifier = Modifier.fillMaxSize()) {
                WatchlistHeader(onSearch = onSearch)
                GiErrorState(
                    title = stringResource(Res.string.couldn_t_load_your_watchlist),
                    body = stringResource(
                        if (state.error == WatchlistLoadError.NETWORK) {
                            Res.string.watchlist_error_network
                        } else {
                            Res.string.watchlist_error_server
                        }
                    ),
                    actionText = stringResource(Res.string.try_again),
                    onAction = { onEvent(WatchlistEvent.RetryWatchlist) },
                    modifier = Modifier.weight(1f)
                )
            }
            WatchlistScreenState.Empty -> WatchlistEmpty(state = state, onEvent = onEvent)
            WatchlistScreenState.Content -> WatchlistContent(
                state = state,
                onEvent = onEvent,
                onRemove = { pendingRemoval = it }
            )
        }
    }

    pendingRemoval?.let { item ->
        RemoveFromWatchlistDialog(
            ticker = item.ticker,
            companyName = item.companyName,
            onConfirm = {
                pendingRemoval = null
                onEvent(WatchlistEvent.OnRemoveClick(item.ticker))
            },
            onDismiss = { pendingRemoval = null }
        )
    }
}

@Composable
private fun WatchlistHeader(
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x2,
                bottom = if (subtitle != null) GiSpace.x5 else GiSpace.x3
            ),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = if (subtitle != null) Alignment.Top else Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiText(
                text = stringResource(Res.string.watchlist_title),
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.ScreenTitle
            )
            subtitle?.invoke()
        }
        GiIconButton(
            icon = GiIcons.magnifyingGlass,
            contentDescription = stringResource(Res.string.search_stocks),
            onClick = onSearch
        )
    }
}

@Composable
private fun WatchlistSubtitle(items: List<WatchlistItem>, offline: Boolean, pricesUpdatedAt: String?) {
    val colors = GiTheme.colors
    val count = pluralStringResource(Res.plurals.watchlist_stocks_count, items.size, items.size)
    val upCount = items.count { (it.priceChange ?: 0.0) > 0.0 }
    val hasPrices = items.any { it.priceChange != null }
    val upText = stringResource(Res.string.watchlist_up_count, upCount)
    val today = stringResource(Res.string.today)
    val asOf = pricesUpdatedAt?.let { stringResource(Res.string.watchlist_prices_as_of, it) }
    val text = buildAnnotatedString {
        append(count)
        when {
            offline && asOf != null -> append(" · $asOf")
            hasPrices -> {
                append(" · ")
                withStyle(SpanStyle(color = colors.gain, fontWeight = FontWeight.Medium)) { append(upText) }
                append(" $today")
            }
        }
    }
    Text(text = text, style = GiTheme.type.metaRelaxed, color = colors.muted)
}

@Composable
private fun ListSheet(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(ListSheetShape)
            .background(GiTheme.colors.raised)
            .border(GiEdge.width, GiTheme.colors.line, ListSheetShape),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistContent(
    state: WatchlistUiState,
    onEvent: (WatchlistEvent) -> Unit,
    onRemove: (WatchlistItem) -> Unit
) {
    val colors = GiTheme.colors
    val offline = state.error == WatchlistLoadError.NETWORK
    val lockedCount = state.watchlistItems.count { it.briefStatus == WatchlistBriefStatus.LOCKED }
    val header = @Composable {
        WatchlistHeader(onSearch = { onEvent(WatchlistEvent.OnSearchClick) }) {
            WatchlistSubtitle(items = state.watchlistItems, offline = offline, pricesUpdatedAt = state.pricesUpdatedAt)
        }
        if (offline) {
            OfflineBanner(
                onRetry = { onEvent(WatchlistEvent.RetryWatchlist) },
                modifier = Modifier.padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x5)
            )
        }
    }
    val row = @Composable { item: WatchlistItem ->
        WatchlistStockRow(
            item = item,
            offline = offline,
            onClick = { onEvent(WatchlistEvent.OnItemClick(item.ticker)) },
            onRemove = { onRemove(item) }
        )
    }
    val banner = @Composable {
        if (lockedCount > 0) {
            val total = state.watchlistItems.size
            val free = total - lockedCount
            GiBanner(
                title = stringResource(Res.string.watchlist_daily_summary_title),
                subtitle = pluralStringResource(Res.plurals.watchlist_daily_summary_body, free, free, total),
                modifier = Modifier.padding(GiSpace.x4),
                action = {
                    GiButton(
                        text = stringResource(Res.string.upgrade),
                        onClick = { onEvent(WatchlistEvent.OnUpgradeClick) },
                        size = GiButtonSize.Small
                    )
                }
            )
        }
    }
    val pullState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(state.isLoading) { if (!state.isLoading) isRefreshing = false }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onEvent(WatchlistEvent.RetryWatchlist)
        },
        modifier = Modifier.fillMaxSize(),
        state = pullState,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = colors.raised,
                color = colors.violet
            )
        }
    ) {
        if (isLargeText()) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                header()
                ListSheet {
                    state.watchlistItems.forEachIndexed { index, item ->
                        if (index > 0) GiDivider()
                        row(item)
                    }
                    banner()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                header()
                ListSheet(modifier = Modifier.weight(1f)) {
                    val reducedMotion = isReducedMotionEnabled()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = state.watchlistItems,
                            key = { item -> item.ticker },
                            contentType = { "stock" }
                        ) { item ->
                            Column(modifier = watchlistItemMotion(reducedMotion)) {
                                row(item)
                                GiDivider()
                            }
                        }
                        item(key = "premium_banner", contentType = "banner") {
                            Box(modifier = watchlistItemMotion(reducedMotion)) { banner() }
                        }
                    }
                }
            }
        }
    }
}

private fun LazyItemScope.watchlistItemMotion(reducedMotion: Boolean): Modifier = if (reducedMotion) {
    Modifier.animateItem(fadeInSpec = null, placementSpec = null, fadeOutSpec = null)
} else {
    Modifier.animateItem(
        fadeInSpec = tween(GiMotion.BASE, easing = GiEasing.emphasised),
        placementSpec = GiMotion.smooth(),
        fadeOutSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised)
    )
}

@Composable
private fun OfflineBanner(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    GiBanner(
        title = stringResource(Res.string.watchlist_offline_title),
        subtitle = stringResource(Res.string.watchlist_offline_body),
        icon = GiIcons.wifiSlash,
        tone = GiBannerTone.Caution,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        action = {
            GiButton(
                text = stringResource(Res.string.retry),
                onClick = onRetry,
                size = GiButtonSize.Small,
                variant = GiButtonVariant.Caution
            )
        }
    )
}

@Composable
private fun WatchlistStockRow(item: WatchlistItem, offline: Boolean, onClick: () -> Unit, onRemove: () -> Unit) {
    val stock = tickerStock(
        ticker = item.ticker,
        title = item.ticker,
        subtitle = item.companyName,
        percent = if (offline) null else item.priceChange,
        imageUrl = item.logoUrl
    )
    GiStockRow(
        stock = stock,
        onClick = onClick,
        price = if (offline) null else item.currentPrice?.let(::formatUsdPrice),
        status = item.briefStatus?.let { status -> { BriefStatusLine(status = status) } },
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (offline && (item.currentPrice != null || item.priceChange != null)) {
                    OfflinePrice(price = item.currentPrice, change = item.priceChange)
                }
                GiIconButton(
                    icon = GiIcons.trash,
                    contentDescription = stringResource(Res.string.watchlist_remove_cd, item.ticker),
                    onClick = onRemove,
                    variant = GiIconButtonVariant.Ghost
                )
            }
        }
    )
}

@Composable
private fun OfflinePrice(price: Double?, change: Double?) {
    val colors = GiTheme.colors
    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(GiSpace.x0_5)) {
        price?.let {
            Text(
                text = formatUsdPrice(it),
                style = GiTheme.type.labelRegular.merge(TextStyle(fontFeatureSettings = "tnum")),
                color = colors.muted,
                maxLines = 1
            )
        }
        change?.let {
            Text(
                text = formatSignedChange(it),
                style = GiTheme.type.eyebrow.merge(TextStyle(fontFeatureSettings = "tnum")),
                color = colors.muted,
                maxLines = 1
            )
        }
    }
}

private fun formatSignedChange(percent: Double): String {
    val tenths = (percent * 10).roundToInt()
    val (arrow, sign) = when {
        tenths > 0 -> "▲ " to "+"
        tenths < 0 -> "▼ " to "−"
        else -> "" to ""
    }
    return "$arrow$sign${abs(tenths) / 10}.${abs(tenths) % 10}%"
}

private val STATUS_DOT = GiSpace.x1_5

@Composable
private fun BriefStatusLine(status: WatchlistBriefStatus) {
    val colors = GiTheme.colors
    val color = when (status) {
        WatchlistBriefStatus.IN_BRIEF -> colors.gain
        WatchlistBriefStatus.LOCKED -> colors.violet
        WatchlistBriefStatus.PENDING_NEXT_BRIEF -> colors.muted
    }
    Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x1), verticalAlignment = Alignment.CenterVertically) {
        when (status) {
            WatchlistBriefStatus.IN_BRIEF -> Box(
                modifier = Modifier.size(STATUS_DOT).clip(GiRadius.full).background(color)
            )
            WatchlistBriefStatus.LOCKED -> Icon(
                imageVector = GiIcons.lock,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(GiIconSize.xs)
            )
            WatchlistBriefStatus.PENDING_NEXT_BRIEF -> Box(
                modifier = Modifier.size(STATUS_DOT).border(GiEdge.emphasis, color, GiRadius.full)
            )
        }
        GiText(
            text = stringResource(
                when (status) {
                    WatchlistBriefStatus.IN_BRIEF -> Res.string.in_your_brief
                    WatchlistBriefStatus.LOCKED -> Res.string.summary_with_premium
                    WatchlistBriefStatus.PENDING_NEXT_BRIEF -> Res.string.joins_your_next_brief
                }
            ),
            style = GiTextStyle.NavLabel,
            color = color
        )
    }
}

private val SKEL_TITLE = 64.dp
private val SKEL_SUBTITLE = 132.dp
private val SKEL_HEADER = 120.dp
private val SKEL_PILL = 48.dp
private val SKEL_LINE = 12.dp
private val SKEL_LINE_SMALL = 10.dp
private val SKEL_TILE = 40.dp

@Composable
private fun WatchlistLoading(onSearch: () -> Unit) {
    val loadingLabel = stringResource(Res.string.watchlist_loading)
    Column(modifier = Modifier.fillMaxSize()) {
        WatchlistHeader(onSearch = onSearch) {
            Box(modifier = Modifier.padding(vertical = GiSpace.x1)) {
                GiSkeleton(modifier = Modifier.size(width = SKEL_HEADER, height = SKEL_LINE))
            }
        }
        ListSheet(
            modifier = Modifier
                .weight(1f)
                .clearAndSetSemantics { contentDescription = loadingLabel }
        ) {
            repeat(SKELETON_ROWS) { index ->
                if (index > 0) GiDivider()
                Row(
                    modifier = Modifier.fillMaxWidth().padding(GiSpace.x4),
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiSkeleton(modifier = Modifier.size(SKEL_TILE))
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                        GiSkeleton(modifier = Modifier.size(width = SKEL_TITLE, height = SKEL_LINE))
                        GiSkeleton(modifier = Modifier.size(width = SKEL_SUBTITLE, height = SKEL_LINE_SMALL))
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
                    ) {
                        GiSkeleton(modifier = Modifier.size(width = SKEL_TITLE, height = SKEL_LINE))
                        GiSkeleton(
                            modifier = Modifier.size(width = SKEL_PILL, height = GiSpace.x5),
                            shape = GiRadius.xs
                        )
                    }
                }
            }
        }
    }
}

private val GUEST_ART_WIDTH = 160.dp
private val GUEST_ART_HEIGHT = 140.dp
private val GUEST_BODY_MAX = 300.dp

@Composable
private fun WatchlistGuest(onSearch: () -> Unit, onSignIn: () -> Unit) {
    val colors = GiTheme.colors
    Column(modifier = Modifier.fillMaxSize()) {
        WatchlistHeader(onSearch = onSearch)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x6),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(width = GUEST_ART_WIDTH, height = GUEST_ART_HEIGHT)
                    .clip(GiRadius.md)
                    .background(colors.violetSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = GiIcons.star,
                    contentDescription = null,
                    modifier = Modifier.size(GiIconSize.xl),
                    tint = colors.violet
                )
            }
            GiText(
                text = stringResource(Res.string.you_re_browsing_as_a_guest),
                modifier = Modifier.padding(top = GiSpace.x2).semantics { heading() },
                style = GiTextStyle.PageTitle,
                textAlign = TextAlign.Center
            )
            GiText(
                text = stringResource(Res.string.watchlist_guest_body),
                modifier = Modifier.widthIn(max = GUEST_BODY_MAX),
                style = GiTextStyle.Body,
                textAlign = TextAlign.Center
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = GiSpace.x2),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
            ) {
                GiButton(
                    text = stringResource(Res.string.sign_in_action),
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth()
                )
                GiButton(
                    text = stringResource(Res.string.create_an_account),
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth(),
                    variant = GiButtonVariant.Text
                )
            }
        }
    }
}

@Composable
private fun WatchlistEmpty(state: WatchlistUiState, onEvent: (WatchlistEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        WatchlistHeader(onSearch = { onEvent(WatchlistEvent.OnSearchClick) })
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x6),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
        ) {
            GiEmptyState(
                icon = GiIcons.star,
                title = stringResource(Res.string.build_your_watchlist),
                body = stringResource(Res.string.watchlist_empty_description),
                actionText = stringResource(Res.string.browse_top_picks),
                onAction = { onEvent(WatchlistEvent.OnBrowseTopPicksClick) }
            )
            GiButton(
                text = stringResource(Res.string.search_stocks),
                onClick = { onEvent(WatchlistEvent.OnSearchClick) },
                variant = GiButtonVariant.Text
            )
        }
        if (state.popularStocks.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.sectionGap),
                verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)
            ) {
                GiSectionHeader(title = stringResource(Res.string.watchlist_popular))
                GiListGroup {
                    state.popularStocks.forEachIndexed { index, stock ->
                        if (index > 0) GiDivider()
                        PopularStockRow(stock = stock, onWatch = { onEvent(WatchlistEvent.OnWatchClick(it)) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularStockRow(stock: PopularStockUiModel, onWatch: (String) -> Unit) {
    val ticker = stock.company.ticker
    val watchDescription = stringResource(Res.string.watchlist_watch_cd, ticker)
    GiStockRow(
        stock = tickerStock(
            ticker = ticker,
            title = ticker,
            subtitle = stock.company.companyName,
            percent = null,
            imageUrl = stock.logoUrl
        ),
        trailing = {
            GiButton(
                text = stringResource(Res.string.watch),
                onClick = { onWatch(ticker) },
                modifier = Modifier
                    .padding(end = GiSpace.x3)
                    .semantics { contentDescription = watchDescription },
                size = GiButtonSize.Small,
                variant = GiButtonVariant.Secondary
            )
        }
    )
}

@Composable
private fun isLargeText(): Boolean = LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE

private val previewItems = listOf(
    WatchlistItem("NVDA", "NVIDIA Corporation", "", true, "", 224.30, -1.9, WatchlistBriefStatus.LOCKED),
    WatchlistItem("AAPL", "Apple Inc.", "", false, "", 345.56, 1.3, WatchlistBriefStatus.IN_BRIEF),
    WatchlistItem("MSFT", "Microsoft Corporation", "", true, "", 540.31, -0.6, WatchlistBriefStatus.LOCKED),
    WatchlistItem("GOOGL", "Alphabet Inc.", "", true, "", 353.97, 0.1, WatchlistBriefStatus.LOCKED),
    WatchlistItem("AMZN", "Amazon.com, Inc.", "", true, "", 256.27, -0.9, WatchlistBriefStatus.LOCKED)
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun WatchlistListPreview() {
    GPTInvestorTheme {
        WatchlistScreen(state = WatchlistUiState(watchlistItems = previewItems), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun WatchlistOfflinePreview() {
    GPTInvestorTheme {
        WatchlistScreen(
            state = WatchlistUiState(
                watchlistItems = previewItems,
                error = WatchlistLoadError.NETWORK,
                pricesUpdatedAt = "13:05"
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun WatchlistLoadingPreview() {
    GPTInvestorTheme {
        WatchlistScreen(state = WatchlistUiState(isLoading = true), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun WatchlistGuestPreview() {
    GPTInvestorTheme {
        WatchlistScreen(state = WatchlistUiState(requiresSignUp = true), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun WatchlistEmptyPreview() {
    GPTInvestorTheme {
        WatchlistScreen(
            state = WatchlistUiState(
                popularStocks = listOf(
                    PopularStockUiModel(RecommendedDigestCompany("AAPL", "Apple")),
                    PopularStockUiModel(RecommendedDigestCompany("NVDA", "NVIDIA"))
                )
            ),
            onEvent = {}
        )
    }
}
