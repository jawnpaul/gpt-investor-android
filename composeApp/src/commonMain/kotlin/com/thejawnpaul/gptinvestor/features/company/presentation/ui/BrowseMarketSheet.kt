package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.browse_company_count
import com.thejawnpaul.gptinvestor.browse_market
import com.thejawnpaul.gptinvestor.browse_market_all
import com.thejawnpaul.gptinvestor.browse_market_subtitle
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.company.domain.model.Market
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiRadioRow
import com.thejawnpaul.gptinvestor.theme.components.GiSheetSubtitle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseMarketSheet(
    markets: List<Market>,
    selectedMarketKey: String?,
    allMarketsCount: Int,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val select: (String?) -> Unit = { key ->
        scope.launch { sheetState.hide() }.invokeOnCompletion { onSelect(key) }
    }
    val title = stringResource(Res.string.browse_market)
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = title,
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        subtitle = stringResource(Res.string.browse_market_subtitle),
        subtitleStyle = GiSheetSubtitle.Below,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .semantics { contentDescription = title }
                .selectableGroup()
        ) {
            GiRadioRow(
                title = stringResource(Res.string.browse_market_all),
                detail = pluralStringResource(Res.plurals.browse_company_count, allMarketsCount, allMarketsCount),
                selected = selectedMarketKey == null,
                onClick = { select(null) },
                leading = { MarketTile { MarketGlobe() } }
            )
            markets.forEach { market ->
                GiDivider()
                GiRadioRow(
                    title = market.name,
                    detail = pluralStringResource(Res.plurals.browse_company_count, market.count, market.count),
                    selected = market.key == selectedMarketKey,
                    onClick = { select(market.key) },
                    leading = { MarketTile { MarketCode(code = market.key) } }
                )
            }
        }
    }
}

@Composable
private fun MarketTile(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .size(MarketTileSize)
            .clip(GiRadius.sm)
            .background(GiTheme.colors.tile)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MarketCode(code: String, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val base = GiTheme.type.tileInitials
    val style = remember(base, density) { base.copy(fontSize = with(density) { base.fontSize.value.dp.toSp() }) }
    val color = GiTheme.colors.muted
    BasicText(text = code, modifier = modifier, style = style, color = { color }, maxLines = 1)
}

@Composable
private fun MarketGlobe(modifier: Modifier = Modifier) {
    Icon(
        imageVector = GiIcons.globe,
        contentDescription = null,
        modifier = modifier.size(GiIconSize.sm),
        tint = GiTheme.colors.muted
    )
}

private val MarketTileSize = 32.dp

@PreviewLightDark
@Composable
private fun BrowseMarketSheetPreview() {
    GPTInvestorTheme {
        BrowseMarketSheet(
            markets = listOf(
                Market(key = "NG", name = "Nigeria", count = 47),
                Market(key = "US", name = "United States", count = 503)
            ),
            selectedMarketKey = "NG",
            allMarketsCount = 550,
            onSelect = {},
            onDismiss = {}
        )
    }
}
