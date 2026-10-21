package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.couldn_t_load
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.live_prices_unavailable
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiMoverCard
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import org.jetbrains.compose.resources.stringResource

private enum class TrendingDisplay { Loading, Error, Loaded }

@Composable
fun HomeTrendingSection(
    view: TrendingCompaniesView,
    onRetry: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val display = when {
        view.loading -> TrendingDisplay.Loading
        view.showError -> TrendingDisplay.Error
        else -> TrendingDisplay.Loaded
    }
    val rowPadding = PaddingValues(horizontal = GiSpace.gutter)
    AnimatedContent(
        targetState = display,
        transitionSpec = { fadeIn(tween(GiMotion.BASE)) togetherWith fadeOut(tween(GiMotion.FAST)) },
        label = "TrendingSection",
        modifier = modifier
    ) { state ->
        when (state) {
            TrendingDisplay.Loading -> LazyRow(
                contentPadding = rowPadding,
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                userScrollEnabled = false
            ) {
                items(3) { MoverSkeleton() }
            }
            TrendingDisplay.Error -> GiBanner(
                title = stringResource(Res.string.couldn_t_load),
                modifier = Modifier.padding(rowPadding).semantics { liveRegion = LiveRegionMode.Polite },
                subtitle = stringResource(Res.string.live_prices_unavailable),
                tone = GiBannerTone.Error
            ) {
                GiButton(
                    text = stringResource(Res.string.retry),
                    onClick = onRetry,
                    size = GiButtonSize.Small,
                    variant = GiButtonVariant.Secondary
                )
            }
            TrendingDisplay.Loaded -> LazyRow(
                contentPadding = rowPadding,
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
            ) {
                items(view.companies, key = { it.tickerSymbol }) { stock ->
                    GiMoverCard(
                        stock = tickerStock(
                            ticker = stock.tickerSymbol,
                            title = stock.tickerSymbol,
                            subtitle = stock.companyName,
                            percent = stock.percentageChange.toDouble(),
                            imageUrl = stock.imageUrl
                        ),
                        onClick = { onClick(stock.tickerSymbol) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(GiMotion.BASE),
                            placementSpec = GiMotion.smooth(),
                            fadeOutSpec = tween(GiMotion.FAST)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MoverSkeleton() {
    GiCard(
        modifier = Modifier.width(248.dp),
        contentPadding = PaddingValues(horizontal = GiSpace.x4, vertical = GiSpace.x3)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3), verticalAlignment = Alignment.CenterVertically) {
            GiSkeleton(modifier = Modifier.size(40.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.5f).height(14.dp))
                GiSkeleton(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionPreview() {
    GPTInvestorTheme {
        HomeTrendingSection(
            view = TrendingCompaniesView(
                companies = listOf(
                    TrendingStockPresentation("NVIDIA Corporation", "NVDA", "", 2.84f),
                    TrendingStockPresentation("Skydance Corporation", "SKYD", "", -23.84f)
                )
            ),
            onRetry = {},
            onClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionLoadingPreview() {
    GPTInvestorTheme {
        HomeTrendingSection(view = TrendingCompaniesView(loading = true), onRetry = {}, onClick = {})
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionErrorPreview() {
    GPTInvestorTheme {
        HomeTrendingSection(view = TrendingCompaniesView(error = "Network"), onRetry = {}, onClick = {})
    }
}
