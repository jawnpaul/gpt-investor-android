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
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.live_prices_unavailable
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(3) {
                    ShimmerBox(width = 160.dp, height = 68.dp, shape = RoundedCornerShape(16.dp))
                }
            }
        }
        view.showError -> {
            HomeErrorCard(
                message = stringResource(Res.string.live_prices_unavailable),
                onRetry = onRetry,
                modifier = modifier
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
