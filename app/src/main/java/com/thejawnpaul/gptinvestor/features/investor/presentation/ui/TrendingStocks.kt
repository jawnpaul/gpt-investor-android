package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView

@Composable
fun SingleTrendingStockItem(
    modifier: Modifier = Modifier,
    onClick: (tickerSymbol: String) -> Unit,
    trendingStock: TrendingStockPresentation
) {
    Surface(
        modifier = Modifier.height(52.dp),
        onClick = {
            onClick(trendingStock.tickerSymbol)
        },
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 2.dp, color = Color(227, 239, 252))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier.width(100.dp),
                text = trendingStock.companyName,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )

            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.trending_ellipse),
                contentDescription = null
            )

            AsyncImage(
                model = trendingStock.imageUrl,
                modifier = Modifier.padding(vertical = 10.dp),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )

            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.trending_ellipse),
                contentDescription = null
            )

            if (trendingStock.percentageChange < 0) {
                Image(
                    modifier = Modifier.padding(end = 2.dp),
                    painter = painterResource(R.drawable.trending_down),
                    contentDescription = null
                )
                Text(
                    "${trendingStock.percentageChange}%",
                    color = Color(212, 38, 32),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Image(
                    modifier = Modifier.padding(end = 2.dp),
                    painter = painterResource(R.drawable.trending_up),
                    contentDescription = null
                )
                Text(
                    "+${trendingStock.percentageChange}%",
                    color = Color(15, 151, 61),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun TrendingStockList(
    modifier: Modifier = Modifier,
    state: TrendingCompaniesView,
    onClick: (tickerSymbol: String) -> Unit,
    onClickRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
    ) {
        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (state.error != null && state.companies.isEmpty()) {
            Button(onClick = onClickRetry, modifier = Modifier.align(Alignment.Center), enabled = !state.loading) {
                Text(stringResource(R.string.retry))
            }
        }

        if (state.companies.isNotEmpty()) {
            LazyHorizontalStaggeredGrid(
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxSize(),
                rows = StaggeredGridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalItemSpacing = 8.dp
            ) {
                items(
                    items = state.companies,
                    key = { item -> item.tickerSymbol }
                ) { item ->
                    SingleTrendingStockItem(onClick = onClick, trendingStock = item)
                }
            }
        }
    }
}
