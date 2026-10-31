package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.browse_companies_title
import com.thejawnpaul.gptinvestor.browse_company_count
import com.thejawnpaul.gptinvestor.browse_empty_all
import com.thejawnpaul.gptinvestor.browse_empty_in_sector
import com.thejawnpaul.gptinvestor.browse_empty_market_sector
import com.thejawnpaul.gptinvestor.browse_empty_query
import com.thejawnpaul.gptinvestor.browse_empty_query_market
import com.thejawnpaul.gptinvestor.browse_empty_sector
import com.thejawnpaul.gptinvestor.browse_error_body
import com.thejawnpaul.gptinvestor.browse_error_title
import com.thejawnpaul.gptinvestor.browse_loading
import com.thejawnpaul.gptinvestor.browse_market_all
import com.thejawnpaul.gptinvestor.browse_market_chip_description
import com.thejawnpaul.gptinvestor.browse_more_error
import com.thejawnpaul.gptinvestor.browse_search_all_sectors
import com.thejawnpaul.gptinvestor.browse_search_placeholder
import com.thejawnpaul.gptinvestor.browse_sector
import com.thejawnpaul.gptinvestor.browse_sector_all
import com.thejawnpaul.gptinvestor.browse_show_all_markets
import com.thejawnpaul.gptinvestor.browse_stocks_empty
import com.thejawnpaul.gptinvestor.clear_search
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.domain.model.Market
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.BrowseStocksEvent
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.BrowseStocksState
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.features.search.presentation.ui.SearchArtTile
import com.thejawnpaul.gptinvestor.features.search.presentation.ui.SearchCopyMaxWidth
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSearchField
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiSpinner
import com.thejawnpaul.gptinvestor.theme.components.GiStockRow
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.components.giLazyListGroup
import com.thejawnpaul.gptinvestor.theme.giAnimatePlacement
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private const val LARGE_TEXT_FONT_SCALE = 1.5f
private const val SKELETON_ROWS = 8

private val SkeletonNameWidths = listOf(120.dp, 157.dp, 194.dp, 140.dp)
private val SkeletonNameHeight = 14.dp
private val SkeletonTickerWidth = 48.dp
private val SkeletonTickerHeight = 10.dp
private val SkeletonLogoSize = 40.dp

@Composable
fun BrowseStocksScreen(
    state: BrowseStocksState,
    companiesPaging: Flow<PagingData<CompanyPresentation>>,
    onEvent: (BrowseStocksEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val companies = companiesPaging.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column(modifier = Modifier.background(GiTheme.colors.bg)) {
                GiTopBar(
                    title = stringResource(Res.string.browse_companies_title),
                    onBack = { onEvent(BrowseStocksEvent.GoBack) },
                    backContentDescription = stringResource(Res.string.back)
                )
                GiSearchField(
                    value = state.query,
                    onValueChange = { onEvent(BrowseStocksEvent.UpdateQuery(it)) },
                    placeholder = stringResource(Res.string.browse_search_placeholder),
                    clearContentDescription = stringResource(Res.string.clear_search),
                    modifier = Modifier.padding(horizontal = GiSpace.gutter)
                )
            }
        }
    ) { innerPadding ->
        BrowseStocksContent(
            state = state,
            companies = companies,
            onEvent = onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        )
    }
    if (state.isMarketSheetOpen) {
        BrowseMarketSheet(
            markets = state.markets,
            selectedMarketKey = state.selectedMarketKey,
            allMarketsCount = state.allMarketsCount,
            onSelect = { onEvent(BrowseStocksEvent.SelectMarket(it)) },
            onDismiss = { onEvent(BrowseStocksEvent.DismissMarketSheet) }
        )
    }
}

@Composable
private fun BrowseStocksContent(
    state: BrowseStocksState,
    companies: LazyPagingItems<CompanyPresentation>,
    onEvent: (BrowseStocksEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val refresh = companies.loadState.refresh
    val isEmpty = companies.itemCount == 0
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.giLazyListGroup(
            state = listState,
            contentType = COMPANY_CONTENT_TYPE,
            inset = GiSpace.gutter,
            colors = GiTheme.colors
        ),
        state = listState,
        contentPadding = PaddingValues(top = GiSpace.x3)
    ) {
        item(key = "sectors", contentType = "sectors") {
            if (state.sectors.isNotEmpty() || state.showMarketFilter) {
                SectorChips(
                    sectors = state.sectors,
                    selected = state.selectedSector,
                    onSelect = { onEvent(BrowseStocksEvent.SelectSector(it)) },
                    modifier = Modifier.padding(bottom = GiSpace.x4),
                    marketChip = if (state.showMarketFilter) {
                        {
                            MarketChip(market = state.selectedMarket, onClick = {
                                onEvent(BrowseStocksEvent.OpenMarketSheet)
                            })
                        }
                    } else {
                        null
                    }
                )
            }
        }

        val countLine = state.countLine
        if (countLine != null && !isEmpty) {
            item(key = "countLine", contentType = "countLine") {
                CountLine(
                    count = countLine,
                    market = state.selectedMarket,
                    sector = state.selectedSector as? SectorInput.CustomSector,
                    modifier = Modifier
                        .then(countLineMotion())
                        .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x3)
                )
            }
        }

        when {
            isEmpty && refresh is LoadState.Loading -> item(key = "loading", contentType = "loading") {
                BrowseSkeleton()
            }

            isEmpty && refresh is LoadState.Error -> item(key = "error", contentType = "state") {
                BrowseErrorState(
                    onRetry = { companies.retry() },
                    modifier = Modifier.padding(start = GiSpace.gutter, end = GiSpace.gutter, top = BrowseErrorTop)
                )
            }

            isEmpty && refresh is LoadState.NotLoading -> item(key = "empty", contentType = "state") {
                BrowseEmptyState(
                    state = state,
                    onEvent = onEvent,
                    onRetry = { companies.refresh() },
                    modifier = Modifier
                        .padding(horizontal = GiSpace.gutter)
                        .padding(top = BrowseEmptyTop)
                )
            }

            else -> items(
                count = companies.itemCount,
                key = companies.itemKey { it.ticker },
                contentType = { COMPANY_CONTENT_TYPE }
            ) { index ->
                val company = companies[index]
                if (company != null) {
                    GiStockRow(
                        stock = tickerStock(
                            ticker = company.ticker,
                            title = company.name,
                            subtitle = company.ticker,
                            percent = null,
                            imageUrl = company.logo.ifBlank { null }
                        ),
                        onClick = { onEvent(BrowseStocksEvent.GoToCompanyDetail(company.ticker)) },
                        modifier = Modifier.padding(horizontal = GiSpace.gutter),
                        showCaret = true
                    )
                } else {
                    BrowseSkeletonRow(modifier = Modifier.padding(horizontal = GiSpace.gutter))
                }
            }
        }

        when (companies.loadState.append) {
            is LoadState.Loading -> if (!isEmpty) {
                item(key = "appendLoading", contentType = "append") {
                    val description = stringResource(Res.string.browse_loading)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = GiSpace.x4)
                            .semantics {
                                liveRegion = LiveRegionMode.Polite
                                contentDescription = description
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        GiSpinner()
                    }
                }
            }

            is LoadState.Error -> item(key = "appendError", contentType = "append") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = GiSpace.gutter, vertical = GiSpace.x2),
                    horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GiText(
                        text = stringResource(Res.string.browse_more_error),
                        style = GiTextStyle.BodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    GiButton(
                        text = stringResource(Res.string.try_again),
                        onClick = { companies.retry() },
                        size = GiButtonSize.Medium,
                        variant = GiButtonVariant.Text
                    )
                }
            }

            is LoadState.NotLoading -> Unit
        }

        item(key = "bottomSpace", contentType = "spacer") {
            Column {
                Box(modifier = Modifier.height(GiSpace.x6))
                Box(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectorChips(
    sectors: List<SectorInput>,
    selected: SectorInput,
    onSelect: (SectorInput) -> Unit,
    marketChip: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    val groupLabel = stringResource(Res.string.browse_sector)
    val chip: @Composable (SectorInput) -> Unit = { sector ->
        GiChip(
            text = sector.label(),
            onClick = { onSelect(sector) },
            selected = sector == selected,
            leadingIcon = if (sector == selected) GiIcons.check else null,
            singleChoice = true
        )
    }
    if (LocalDensity.current.fontScale >= LARGE_TEXT_FONT_SCALE) {
        FlowRow(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = groupLabel }
                .selectableGroup()
                .padding(horizontal = GiSpace.gutter),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            if (marketChip != null) {
                Box(modifier = Modifier.giAnimatePlacement()) { marketChip() }
            }
            sectors.forEach { Box(modifier = Modifier.giAnimatePlacement()) { chip(it) } }
        }
    } else {
        val rowState = rememberLazyListState()
        val hasMarketChip = marketChip != null
        LaunchedEffect(hasMarketChip) {
            if (hasMarketChip && rowState.firstVisibleItemIndex <= MARKET_ROW_ITEMS) rowState.scrollToItem(0)
        }
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = groupLabel }
                .selectableGroup(),
            state = rowState,
            contentPadding = PaddingValues(horizontal = GiSpace.gutter),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (marketChip != null) {
                item(key = "market", contentType = "market") { Box(modifier = chipRowMotion()) { marketChip() } }
                item(key = "marketDivider", contentType = "marketDivider") {
                    MarketDivider(modifier = chipRowMotion())
                }
            }
            items(items = sectors, key = { it.key() }, contentType = { "sector" }) {
                Box(modifier = chipRowMotion()) { chip(it) }
            }
        }
    }
}

@Composable
private fun LazyItemScope.chipRowMotion(): Modifier = if (isReducedMotionEnabled()) {
    Modifier.animateItem(fadeInSpec = null, placementSpec = null, fadeOutSpec = null)
} else {
    Modifier.animateItem(
        fadeInSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised),
        placementSpec = GiMotion.smooth(),
        fadeOutSpec = tween(GiMotion.INSTANT, easing = GiEasing.linear)
    )
}

@Composable
private fun LazyItemScope.countLineMotion(): Modifier = if (isReducedMotionEnabled()) {
    Modifier.animateItem(fadeInSpec = null, placementSpec = null, fadeOutSpec = null)
} else {
    Modifier.animateItem(
        fadeInSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised),
        placementSpec = null,
        fadeOutSpec = tween(GiMotion.INSTANT, easing = GiEasing.linear)
    )
}

@Composable
private fun MarketChip(market: Market?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val label = market?.name ?: stringResource(Res.string.browse_market_all)
    val description = stringResource(Res.string.browse_market_chip_description, label)
    GiChip(
        text = label,
        onClick = onClick,
        modifier = modifier.semantics { contentDescription = description },
        selected = market != null,
        leadingIcon = GiIcons.globe,
        trailingIcon = GiIcons.caretDown
    )
}

@Composable
private fun MarketDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = GiEdge.width, height = MarketDividerHeight)
            .background(GiTheme.colors.line)
    )
}

@Composable
private fun CountLine(count: Int, market: Market?, sector: SectorInput.CustomSector?, modifier: Modifier = Modifier) {
    val text = listOfNotNull(
        pluralStringResource(Res.plurals.browse_company_count, count, count),
        market?.name,
        sector?.sectorName
    ).joinToString(separator = " · ")
    GiText(
        text = text,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        style = GiTextStyle.BodySmall,
        color = GiTheme.colors.muted
    )
}

@Composable
private fun BrowseEmptyState(
    state: BrowseStocksState,
    onEvent: (BrowseStocksEvent) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sector = state.selectedSector as? SectorInput.CustomSector
    val market = state.selectedMarket
    val query = state.query.trim()
    val body: String
    val actionText: String
    val onAction: () -> Unit
    when {
        market != null && sector != null && query.isEmpty() -> {
            body = stringResource(Res.string.browse_empty_market_sector, market.name, sector.sectorName)
            actionText = stringResource(Res.string.browse_show_all_markets)
            onAction = { onEvent(BrowseStocksEvent.SelectMarket(null)) }
        }

        market != null && query.isNotEmpty() -> {
            body = stringResource(Res.string.browse_empty_query_market, market.name, query)
            actionText = stringResource(Res.string.browse_show_all_markets)
            onAction = { onEvent(BrowseStocksEvent.SelectMarket(null)) }
        }

        sector != null && query.isNotEmpty() -> {
            body = stringResource(Res.string.browse_empty_in_sector, sector.sectorName, query)
            actionText = stringResource(Res.string.browse_search_all_sectors)
            onAction = { onEvent(BrowseStocksEvent.SelectSector(SectorInput.AllSector)) }
        }

        sector != null -> {
            body = stringResource(Res.string.browse_empty_sector, sector.sectorName)
            actionText = stringResource(Res.string.browse_search_all_sectors)
            onAction = { onEvent(BrowseStocksEvent.SelectSector(SectorInput.AllSector)) }
        }

        query.isNotEmpty() -> {
            body = stringResource(Res.string.browse_empty_query, query)
            actionText = stringResource(Res.string.clear_search)
            onAction = { onEvent(BrowseStocksEvent.UpdateQuery("")) }
        }

        else -> {
            body = stringResource(Res.string.browse_empty_all)
            actionText = stringResource(Res.string.try_again)
            onAction = onRetry
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        SearchArtTile(
            icon = GiIcons.magnifyingGlass,
            container = GiTheme.colors.violetSoft,
            content = GiTheme.colors.violet
        )
        GiText(
            text = stringResource(Res.string.browse_stocks_empty),
            modifier = Modifier
                .padding(top = GiSpace.x2)
                .semantics { heading() },
            style = GiTextStyle.PageTitle,
            textAlign = TextAlign.Center
        )
        GiText(
            text = body,
            modifier = Modifier.widthIn(max = SearchCopyMaxWidth),
            style = GiTextStyle.Body,
            textAlign = TextAlign.Center
        )
        GiButton(
            text = actionText,
            onClick = onAction,
            modifier = Modifier.padding(top = GiSpace.x2),
            size = GiButtonSize.Large
        )
    }
}

@Composable
private fun BrowseErrorState(onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(GiSpace.x5)
    ) {
        SearchArtTile(icon = GiIcons.wifiSlash, container = GiTheme.colors.tile, content = GiTheme.colors.muted)
        Column(
            modifier = Modifier.widthIn(max = SearchCopyMaxWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiText(
                text = stringResource(Res.string.browse_error_title),
                modifier = Modifier.semantics { heading() },
                style = GiTextStyle.StatNumber,
                textAlign = TextAlign.Center
            )
            GiText(
                text = stringResource(Res.string.browse_error_body),
                style = GiTextStyle.Body,
                textAlign = TextAlign.Center
            )
        }
        GiButton(
            text = stringResource(Res.string.try_again),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            size = GiButtonSize.Large,
            leadingIcon = GiIcons.arrowClockwise
        )
    }
}

private const val COMPANY_CONTENT_TYPE = "company"
private const val MARKET_ROW_ITEMS = 2

private val BrowseEmptyTop = 56.dp
private val MarketDividerHeight = 20.dp
private val BrowseErrorTop = 72.dp

@Composable
private fun BrowseSkeleton(modifier: Modifier = Modifier) {
    val description = stringResource(Res.string.browse_loading)
    Column(
        modifier = modifier
            .clearAndSetSemantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = description
            }
    ) {
        GiListGroup(modifier = Modifier.padding(horizontal = GiSpace.gutter)) {
            repeat(SKELETON_ROWS) { index ->
                if (index > 0) GiDivider()
                BrowseSkeletonRow(nameWidthIndex = index)
            }
        }
    }
}

@Composable
private fun BrowseSkeletonRow(modifier: Modifier = Modifier, nameWidthIndex: Int = 0) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GiSpace.x4, vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiSkeleton(modifier = Modifier.size(SkeletonLogoSize))
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSkeleton(
                modifier = Modifier
                    .width(SkeletonNameWidths[nameWidthIndex % SkeletonNameWidths.size])
                    .height(SkeletonNameHeight)
            )
            GiSkeleton(modifier = Modifier.size(width = SkeletonTickerWidth, height = SkeletonTickerHeight))
        }
    }
}

private fun SectorInput.key(): String = when (this) {
    SectorInput.AllSector -> "all"
    is SectorInput.CustomSector -> sectorKey
}

@Composable
private fun SectorInput.label(): String = when (this) {
    SectorInput.AllSector -> stringResource(Res.string.browse_sector_all)
    is SectorInput.CustomSector -> sectorName
}

private val previewCompanies = listOf(
    "MMM" to "3M Company",
    "AOS" to "A. O. Smith Corporation",
    "ABT" to "Abbott Laboratories",
    "ABBV" to "AbbVie Inc.",
    "ACN" to "Accenture plc"
).map { (ticker, name) ->
    CompanyPresentation(
        ticker = ticker,
        name = name,
        logo = "",
        summary = "",
        price = 0f,
        priceChange = PriceChange(change = 0f, date = 0L)
    )
}

private val previewSectors = listOf(
    SectorInput.AllSector,
    SectorInput.CustomSector(sectorName = "Technology", sectorKey = "technology"),
    SectorInput.CustomSector(sectorName = "Healthcare", sectorKey = "healthcare"),
    SectorInput.CustomSector(sectorName = "Energy", sectorKey = "energy")
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun BrowseStocksScreenPreview() {
    GPTInvestorTheme {
        BrowseStocksScreen(
            state = BrowseStocksState(sectors = previewSectors),
            companiesPaging = flowOf(PagingData.from(previewCompanies)),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun BrowseStocksScreenEmptyPreview() {
    GPTInvestorTheme {
        BrowseStocksScreen(
            state = BrowseStocksState(sectors = previewSectors, selectedSector = previewSectors[1], query = "appzzq"),
            companiesPaging = flowOf(
                PagingData.empty(
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(endOfPaginationReached = true),
                        prepend = LoadState.NotLoading(endOfPaginationReached = true),
                        append = LoadState.NotLoading(endOfPaginationReached = true)
                    )
                )
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun BrowseStocksScreenLoadingPreview() {
    GPTInvestorTheme {
        Box(modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)) {
            BrowseSkeleton()
        }
    }
}
