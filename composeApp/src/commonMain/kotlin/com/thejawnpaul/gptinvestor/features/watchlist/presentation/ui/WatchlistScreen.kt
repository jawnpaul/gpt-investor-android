package com.thejawnpaul.gptinvestor.features.watchlist.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.browse_top_picks
import com.thejawnpaul.gptinvestor.build_your_watchlist
import com.thejawnpaul.gptinvestor.cancel
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeErrorCard
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistItem
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.PopularStockUiModel
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.state.WatchlistUiState
import com.thejawnpaul.gptinvestor.features.watchlist.presentation.viewmodel.WatchlistEvent
import com.thejawnpaul.gptinvestor.ic_lock
import com.thejawnpaul.gptinvestor.or_search_for_any_stock
import com.thejawnpaul.gptinvestor.outline_visibility_24
import com.thejawnpaul.gptinvestor.popular_this_week
import com.thejawnpaul.gptinvestor.remove
import com.thejawnpaul.gptinvestor.remove_from_watchlist
import com.thejawnpaul.gptinvestor.remove_from_watchlist_confirmation
import com.thejawnpaul.gptinvestor.search
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.watch
import com.thejawnpaul.gptinvestor.watchlist_empty_description
import com.thejawnpaul.gptinvestor.watchlist_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private enum class WatchlistScreenState { Loading, Error, Empty, Content }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(state: WatchlistUiState, onEvent: (WatchlistEvent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.watchlist_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(WatchlistEvent.OnSearchClick) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(Res.string.search)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        val screenState = when {
            state.isLoading && state.watchlistItems.isEmpty() -> WatchlistScreenState.Loading
            state.error != null && state.watchlistItems.isEmpty() -> WatchlistScreenState.Error
            state.watchlistItems.isEmpty() -> WatchlistScreenState.Empty
            else -> WatchlistScreenState.Content
        }

        AnimatedContent(
            targetState = screenState,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(150)) },
            label = "watchlist_state"
        ) { target ->
            when (target) {
                WatchlistScreenState.Loading -> WatchlistLoadingState(innerPadding = innerPadding)
                WatchlistScreenState.Error -> Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HomeErrorCard(
                        message = state.error ?: "",
                        onRetry = { onEvent(WatchlistEvent.RetryWatchlist) }
                    )
                }
                WatchlistScreenState.Empty -> WatchlistEmptyState(
                    state = state,
                    onEvent = onEvent,
                    innerPadding = innerPadding
                )
                WatchlistScreenState.Content -> WatchlistContent(
                    state = state,
                    onEvent = onEvent,
                    innerPadding = innerPadding
                )
            }
        }
    }
}

@Composable
private fun WatchlistLoadingState(innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(6) {
            WatchlistItemShimmer()
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun WatchlistItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBox(size = 40.dp, shape = CircleShape)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerBox(width = 64.dp, height = 16.dp)
            ShimmerBox(width = 120.dp, height = 12.dp)
        }
    }
}

@Composable
private fun WatchlistEmptyState(
    state: WatchlistUiState,
    onEvent: (WatchlistEvent) -> Unit,
    innerPadding: PaddingValues
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(gptInvestorColors.utilColors.allDark2.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.outline_visibility_24),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = gptInvestorColors.accentColors.allAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.build_your_watchlist),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(Res.string.watchlist_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = gptInvestorColors.textColors.secondary50,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onEvent(WatchlistEvent.OnBrowseTopPicksClick) },
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(Res.string.browse_top_picks))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.or_search_for_any_stock),
            style = MaterialTheme.typography.bodyMedium,
            color = gptInvestorColors.textColors.secondary50
        )

        Spacer(modifier = Modifier.height(64.dp))

        if (state.popularStocks.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.popular_this_week),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = gptInvestorColors.textColors.secondary50,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(180.dp)
                ) {
                    items(state.popularStocks, key = { it.company.ticker }) { stock ->
                        PopularStockItem(
                            stock = stock,
                            onWatchClick = { onEvent(WatchlistEvent.OnWatchClick(it)) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WatchlistContent(
    state: WatchlistUiState,
    onEvent: (WatchlistEvent) -> Unit,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(state.watchlistItems, key = { it.ticker }) { item ->
            WatchlistItemRow(
                item = item,
                onClick = { onEvent(WatchlistEvent.OnItemClick(item.ticker)) },
                onRemove = { onEvent(WatchlistEvent.OnRemoveClick(item.ticker)) },
                modifier = Modifier.animateItem()
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun WatchlistItemRow(
    item: WatchlistItem,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = stringResource(Res.string.remove_from_watchlist)) },
            text = { Text(text = stringResource(Res.string.remove_from_watchlist_confirmation, item.ticker)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onRemove()
                    }
                ) {
                    Text(text = stringResource(Res.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(text = stringResource(Res.string.cancel))
                }
            }
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            AsyncImage(
                model = item.logoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.ticker,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = item.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = gptInvestorColors.textColors.secondary50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (item.locked) {
            Icon(
                painter = painterResource(Res.drawable.ic_lock),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = gptInvestorColors.textColors.secondary50
            )
        } else {
            IconButton(onClick = { showConfirmDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(Res.string.remove_from_watchlist),
                    modifier = Modifier.size(18.dp),
                    tint = gptInvestorColors.textColors.secondary50
                )
            }
        }
    }
}

@Composable
private fun PopularStockItem(
    stock: PopularStockUiModel,
    onWatchClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    OutlinedCard(
        modifier = modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = stock.color.copy(alpha = 0.8f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = stock.company.ticker.take(1),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stock.company.ticker,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = stock.company.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = gptInvestorColors.textColors.secondary50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onWatchClick(stock.company.ticker) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = gptInvestorColors.accentColors.allAccent20,
                    contentColor = gptInvestorColors.accentColors.allAccent
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = stringResource(Res.string.watch),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun WatchlistScreenPreview() {
    GPTInvestorTheme {
        Surface {
            WatchlistScreen(
                state = WatchlistUiState(
                    popularStocks = listOf(
                        PopularStockUiModel(RecommendedDigestCompany("AAPL", "Apple"), Color.Black),
                        PopularStockUiModel(RecommendedDigestCompany("NVDA", "NVIDIA"), Color(0xFF76B900))
                    )
                ),
                onEvent = {}
            )
        }
    }
}
