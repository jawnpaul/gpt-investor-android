package com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.live_prices_unavailable
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeTrendingSection(
    view: TrendingCompaniesView,
    onRetry: () -> Unit,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        view.loading -> {
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(3) {
                    HomeTrendingShimmer()
                }
            }
        }
        view.showError -> {
            HomeErrorCard(
                message = stringResource(Res.string.live_prices_unavailable),
                onRetry = onRetry,
                modifier = modifier.padding(horizontal = 16.dp)
            )
        }
        else -> {
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(view.companies) { stock ->
                    HomeTrendingMoverItem(stock = stock, onClick = onClick)
                }
            }
        }
    }
}

@Composable
fun HomeTrendingMoverItem(stock: TrendingStockPresentation, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    val avatarColor = remember(stock.tickerSymbol) { tickerAvatarColor(stock.tickerSymbol) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = { onClick(stock.tickerSymbol) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = avatarColor
            ) {
                if (stock.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = stock.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stock.tickerSymbol.firstOrNull()?.uppercase() ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stock.tickerSymbol,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = stock.companyName,
                    style = MaterialTheme.typography.labelSmall,
                    color = gptInvestorColors.textColors.secondary50,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val isPositive = stock.percentageChange >= 0
            val changeColor = if (isPositive) {
                gptInvestorColors.greenColors.defaultGreen
            } else {
                gptInvestorColors.redColors.allRed
            }
            val changeText = if (isPositive) "+${stock.percentageChange}%" else "${stock.percentageChange}%"

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = changeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    text = changeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = changeColor
                )
            }
        }
    }
}

private fun tickerAvatarColor(ticker: String): Color {
    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFF9C27B0),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF00BCD4),
        Color(0xFF607D8B)
    )
    val index = (ticker.firstOrNull()?.code ?: 0) % colors.size
    return colors[index]
}

@Composable
private fun HomeTrendingShimmer(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ShimmerBox(
                modifier = Modifier.size(36.dp),
                shape = CircleShape
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ShimmerBox(
                    width = 40.dp,
                    height = 14.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerBox(
                    width = 60.dp,
                    height = 10.dp,
                    shape = RoundedCornerShape(4.dp)
                )
            }

            ShimmerBox(
                width = 44.dp,
                height = 18.dp,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionPreview() {
    val companies = listOf(
        TrendingStockPresentation(
            companyName = "Apple Inc.",
            tickerSymbol = "AAPL",
            imageUrl = "",
            percentageChange = 1.2f
        ),
        TrendingStockPresentation(
            companyName = "Tesla, Inc.",
            tickerSymbol = "TSLA",
            imageUrl = "",
            percentageChange = -2.5f
        ),
        TrendingStockPresentation(
            companyName = "NVIDIA Corporation",
            tickerSymbol = "NVDA",
            imageUrl = "",
            percentageChange = 0.5f
        )
    )
    GPTInvestorTheme {
        Surface {
            HomeTrendingSection(
                view = TrendingCompaniesView(
                    loading = false,
                    companies = companies,
                    error = null
                ),
                onRetry = {},
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionLoadingPreview() {
    GPTInvestorTheme {
        Surface {
            HomeTrendingSection(
                view = TrendingCompaniesView(
                    loading = true,
                    companies = emptyList(),
                    error = null
                ),
                onRetry = {},
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun HomeTrendingSectionErrorPreview() {
    GPTInvestorTheme {
        Surface {
            HomeTrendingSection(
                view = TrendingCompaniesView(
                    loading = false,
                    companies = emptyList(),
                    error = "Something went wrong"
                ),
                onRetry = {},
                onClick = {}
            )
        }
    }
}
