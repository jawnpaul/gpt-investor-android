package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.utility.toReadable
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.model.toPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.TimePeriod
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.theme.linkMedium

@Composable
fun CompanyDetailDataSource(modifier: Modifier, list: List<NewsPresentation> = emptyList(), onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(width = 2.dp, color = DividerDefaults.color),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.sources),
                style = MaterialTheme.typography.bodySmall
            )
            OverlappingIcons(
                modifier = Modifier,
                images = list.map { it.imageUrl }.take(4)
            )
        }
    }
}

@Composable
fun OverlappingIcons(modifier: Modifier, images: List<String>) {
    Box(modifier) {
        images.reversed().forEachIndexed { index, it ->
            AsyncImage(
                model = it,
                modifier = Modifier
                    .padding(start = (index * 12).dp)
                    .size(20.dp)
                    .clip(CircleShape),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun CompanyDetailPriceCard(modifier: Modifier = Modifier, ticker: String, price: Float, imageUrl: String, change: Float) {
    OutlinedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // Text
                Text(text = ticker, style = MaterialTheme.typography.titleLarge)

                // price
                StockPriceText(currencySymbol = "$", amount = price)

                // change pill
                PercentageChangePill(change = change, date = "today")
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                contentScale = ContentScale.Inside
            )
        }
    }
}

@Composable
fun StockPriceText(modifier: Modifier = Modifier, currencySymbol: String, amount: Float) {
    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(vertical = 8.dp)) {
        Text(currencySymbol, modifier = Modifier.align(Alignment.Bottom))
        Text(
            amount.toTwoDecimalPlaces().toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
fun PercentageChangePill(modifier: Modifier = Modifier, change: Float, date: String) {
    Surface(
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 1.dp, color = DividerDefaults.color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (change < 0) {
                Image(
                    modifier = Modifier.padding(end = 2.dp),
                    painter = painterResource(R.drawable.trending_down),
                    contentDescription = null
                )
                Text(
                    "${change.toTwoDecimalPlaces()}%",
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
                    "+${change.toTwoDecimalPlaces()}%",
                    color = Color(15, 151, 61),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Image(
                modifier = Modifier.padding(8.dp),
                painter = painterResource(R.drawable.trending_ellipse),
                contentDescription = null
            )

            Text(date, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AboutStockCard(modifier: Modifier, companySummary: String, companyName: String) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.about_company_name, companyName),
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        ExpandableText(
            text = companySummary,
            collapsedMaxLine = 4,
            style = MaterialTheme.typography.bodyMedium,
            showMoreText = stringResource(R.string.read_more),
            showMoreStyle = SpanStyle(
                textDecoration = TextDecoration.Underline,
                fontStyle = MaterialTheme.typography.linkMedium.fontStyle,
                fontWeight = FontWeight.W500
            ),
            showLessText = stringResource(R.string.read_less),
            showLessStyle = SpanStyle(
                textDecoration = TextDecoration.Underline,
                fontStyle = MaterialTheme.typography.linkMedium.fontStyle,
                fontWeight = FontWeight.W500
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailTab(modifier: Modifier, company: CompanyDetailRemoteResponse, onClickNews: (url: String) -> Unit, onClickSources: () -> Unit) {
    val titles = listOf("Overview", "Key ratios", "News")
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val gptInvestorColors = LocalGPTInvestorColors.current
    Column(modifier = modifier) {
        PrimaryTabRow(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(50)),
            selectedTabIndex = selectedTabIndex.intValue,
            indicator = {},
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            titles.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex.intValue == index

                val backgroundColor =
                    if (isSelected) gptInvestorColors.utilColors.borderBright10 else Color.Transparent

                Tab(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .clip(RoundedCornerShape(50))
                        .background(backgroundColor),
                    selected = isSelected,
                    unselectedContentColor = gptInvestorColors.textColors.secondary50,
                    onClick = {
                        selectedTabIndex.intValue = index
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = if (isSelected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }

        AnimatedContent(
            targetState = selectedTabIndex.intValue,
            label = "AnimatedContent"
        ) { targetState ->

            when (targetState) {
                0 -> {
                    CompanyHistoryGraph(
                        modifier = Modifier,
                        historicalData = company.historicalData,
                        companySummary = company.about,
                        companyName = company.name,
                        sources = company.news.map { it.toPresentation() },
                        onClickSources = onClickSources
                    )
                }

                1 -> {
                    CompanyKeyRatios(
                        modifier = Modifier,
                        marketCap = company.marketCap,
                        peRatio = company.peRatio,
                        revenue = company.revenue
                    )
                }

                2 -> {
                    CompanyDetailsNews(
                        modifier = Modifier,
                        news = company.news.map { it.toPresentation() },
                        onClick = onClickNews
                    )
                }
            }
        }
    }
}

@Composable
fun CompanyHistoryGraph(
    modifier: Modifier,
    historicalData: List<HistoricalData>,
    companyName: String,
    companySummary: String,
    sources: List<NewsPresentation> = emptyList(),
    onClickSources: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 2.dp, color = DividerDefaults.color)
    ) {
        var selected by remember { mutableStateOf<TimePeriod>(TimePeriod.OneWeek()) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            ) {
                val default = listOf(GraphPoint(date = "", 2.0F))
                val graphPoints = historicalData.map {
                    with(it) {
                        GraphPoint(
                            date = date,
                            amount = close.toTwoDecimalPlaces()
                        )
                    }
                }
                val points = graphPoints.ifEmpty { default }
                when (selected) {
                    is TimePeriod.OneYear -> {
                        SmoothLineGraph(points)
                    }

                    is TimePeriod.OneMonth -> {
                        SmoothLineGraph(points.takeLast(30))
                    }

                    is TimePeriod.OneWeek -> {
                        SmoothLineGraph(points.takeLast(7))
                    }

                    is TimePeriod.ThreeMonths -> {
                        SmoothLineGraph(points.takeLast(90))
                    }
                }
            }

            val options = listOf(
                TimePeriod.OneWeek(),
                TimePeriod.OneMonth(),
                TimePeriod.ThreeMonths(),
                TimePeriod.OneYear()
            )

            TimePeriodRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                list = options,
                selected = selected,
                onClick = {
                    selected = it
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                thickness = 2.dp
            )

            // About company card
            AboutStockCard(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                companySummary = companySummary,
                companyName = companyName
            )

            CompanyDetailDataSource(
                modifier = Modifier.padding(16.dp),
                list = sources,
                onClick = onClickSources
            )
        }
    }
}

@Composable
fun TimePeriodRow(modifier: Modifier, list: List<TimePeriod>, selected: TimePeriod, onClick: (period: TimePeriod) -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        list.forEach {
            SingleTimePeriod(
                modifier = Modifier.padding(end = 8.dp),
                period = it,
                isSelected = it == selected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun SingleTimePeriod(modifier: Modifier, period: TimePeriod, isSelected: Boolean, onClick: (period: TimePeriod) -> Unit) {
    if (isSelected) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            onClick = { onClick(period) },
            color = LocalGPTInvestorColors.current.utilColors.borderBright10
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                text = period.title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else {
        Text(
            modifier = Modifier.clickable { onClick(period) },
            text = period.title,
            style = MaterialTheme.typography.bodySmall,
            color = LocalGPTInvestorColors.current.textColors.secondary50
        )
    }
}

@Composable
fun CompanyKeyRatios(modifier: Modifier, marketCap: Long, peRatio: Float, revenue: Long) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 2.dp, color = DividerDefaults.color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.padding()) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.market_cap).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${marketCap.toReadable()}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

            Column(modifier = Modifier.padding()) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.pe_ratio).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${peRatio.toTwoDecimalPlaces()}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

            Column(modifier = Modifier.padding()) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = stringResource(R.string.revenue).uppercase(),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${revenue.toReadable()}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun CompanyDetailsNews(modifier: Modifier, news: List<NewsPresentation>, onClick: (url: String) -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            news.take(3).forEachIndexed { index, item ->
                CompanyDetailNewsItem(
                    modifier = Modifier,
                    news = item,
                    onClick = onClick
                )

                if (index < 2) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun CompanyDetailNewsItem(modifier: Modifier, news: NewsPresentation, onClick: (url: String) -> Unit) {
    Column(modifier = modifier) {
        Text(
            text = news.publisher,
            modifier = Modifier.padding(bottom = 8.dp),
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = news.title,
            style = MaterialTheme.typography.bodyMedium
        )

        Column(
            modifier = Modifier
                .padding(bottom = 4.dp)
                .clickable { onClick(Uri.encode(news.link)) }
        ) {
            Text(
                text = stringResource(R.string.read_more),
                style = MaterialTheme.typography.linkMedium,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Preview
@Composable
fun PreviewComposable(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                val tabs = listOf("Overview", "Key Ratio", "News", "Another Tab")
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                CompanyDetailDataSource(modifier = Modifier, list = listOf(), onClick = {})

                CompanyDetailPriceCard(
                    ticker = "AAPL",
                    price = 120.0F,
                    change = 2.0F,
                    imageUrl = ""
                )

                AboutStockCard(
                    modifier = Modifier,
                    companySummary = "I am\nthe best \n of",
                    companyName = "Microsoft corporation"
                )

                CompanyKeyRatios(
                    modifier = Modifier,
                    marketCap = 120000000L,
                    peRatio = 45.3F,
                    revenue = 1010000L
                )

                CompanyDetailNewsItem(
                    modifier = Modifier,
                    news = NewsPresentation(
                        title = "The strength of the black panther has been stripped away",
                        id = "",
                        type = "",
                        relativeDate = "",
                        publisher = "Yahoo Finance",
                        imageUrl = "",
                        link = ""
                    )
                ) { }
            }
        }
    }
}
