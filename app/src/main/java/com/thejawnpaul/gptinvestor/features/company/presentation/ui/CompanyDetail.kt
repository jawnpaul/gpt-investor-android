package com.thejawnpaul.gptinvestor.features.company.presentation.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.utility.toReadable
import com.thejawnpaul.gptinvestor.core.utility.toTwoDecimalPlaces
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.TimePeriod
import com.thejawnpaul.gptinvestor.ui.theme.GPTInvestorTheme

@Composable
fun CompanyDetailDataSource(
    modifier: Modifier = Modifier,
    list: List<NewsPresentation> = emptyList(),
    source: String
) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        if (expanded) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                RichText(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)) {
                    Markdown(content = source)
                }

                IconButton(modifier = Modifier.align(Alignment.TopEnd), onClick = {
                    expanded = !expanded
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) {
                            stringResource(R.string.show_less)
                        } else {
                            stringResource(R.string.show_more)
                        }
                    )
                }


            }
        } else {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.padding(end = 4.dp),
                        painter = painterResource(R.drawable.data_from_icon),
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(R.string.data_from),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        list.take(4).forEachIndexed { i, it ->
                            Surface(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(),
                                shape = CircleShape,
                                border = BorderStroke(width = 2.dp, color = Color(227, 239, 252))
                            ) {
                                AsyncImage(
                                    model = it.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.CenterVertically),
                                    contentScale = ContentScale.Crop
                                )

                            }
                        }
                    }


                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) {
                                stringResource(R.string.show_less)
                            } else {
                                stringResource(R.string.show_more)
                            }
                        )
                    }

                }
            }
        }
    }

}

@Composable
fun CompanyDetailPriceCard(
    modifier: Modifier = Modifier,
    ticker: String,
    price: Float,
    imageUrl: String,
    change: Float
) {
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
                //Text
                Text(text = ticker, style = MaterialTheme.typography.titleLarge)

                //price
                StockPriceText(currencySymbol = "$", amount = price)

                //change pill
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
fun AboutStockCard(modifier: Modifier = Modifier, companySummary: String, companyName: String) {
    OutlinedCard(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                stringResource(R.string.about_company_name, companyName),
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            ExpandableText(text = companySummary, collapsedMaxLine = 4)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailTab(
    modifier: Modifier = Modifier,
    company: CompanyDetailRemoteResponse,
    onClickNews: (url: String) -> Unit
) {
    val titles = listOf("Overview", "Key ratios", "News")
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = selectedTabIndex.intValue) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.intValue == index,
                    onClick = {
                        selectedTabIndex.intValue = index
                    },
                    text = {
                        Text(
                            text = title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                        historicalData = company.historicalData
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
                        news = company.news.map { it.toPresentation() }, onClick = onClickNews
                    )
                }
            }
        }

    }
}


@Composable
fun CompanyHistoryGraph(
    modifier: Modifier = Modifier,
    historicalData: List<HistoricalData>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var selected by remember { mutableStateOf<TimePeriod>(TimePeriod.OneYear()) }

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
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            ) {
                options.forEachIndexed { index, timePeriod ->
                    SegmentedButton(
                        icon = {},
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = {
                            selected = options[index]
                        },
                        selected = selected == timePeriod
                    ) {
                        Text(timePeriod.title)
                    }
                }
            }
        }
    }
}


@Composable
fun CompanyKeyRatios(
    modifier: Modifier = Modifier,
    marketCap: Long,
    peRatio: Float,
    revenue: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.market_cap).uppercase(),
                style = MaterialTheme.typography.titleMedium
            )
            Text("$${marketCap.toReadable()}")
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.pe_ratio).uppercase(),
                style = MaterialTheme.typography.titleMedium
            )
            Text("${peRatio.toTwoDecimalPlaces()}")
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.revenue).uppercase(),
                style = MaterialTheme.typography.titleMedium
            )
            Text("$${revenue.toReadable()}")
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun CompanyDetailsNews(
    modifier: Modifier = Modifier,
    news: List<NewsPresentation>,
    onClick: (url: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        news.take(3).forEach { item ->
            CompanyDetailNewsItem(news = item, onClick = onClick)
        }
    }
}

@Composable
fun CompanyDetailNewsItem(
    modifier: Modifier = Modifier,
    news: NewsPresentation,
    onClick: (url: String) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Column(modifier = Modifier
                .padding(bottom = 4.dp)
                .clickable { onClick(Uri.encode(news.link)) }) {
                Row {
                    Text(stringResource(R.string.learn_more).uppercase())
                    Image(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
                HorizontalDivider(modifier = Modifier.width(128.dp))
            }
        }
    }
}

@Preview
@Composable
fun PreviewComposable(modifier: Modifier = Modifier) {
    GPTInvestorTheme {
        Surface {
            Column(modifier = Modifier.fillMaxSize()) {
                CompanyDetailDataSource(list = listOf(), source = "")

                CompanyDetailPriceCard(
                    ticker = "AAPL",
                    price = 120.0F,
                    change = 2.0F,
                    imageUrl = ""
                )

                AboutStockCard(
                    companySummary = "I am\nthe best \n of",
                    companyName = "Microsoft corporation"
                )

                CompanyKeyRatios(marketCap = 120000000L, peRatio = 45.3F, revenue = 1010000L)

                CompanyDetailNewsItem(
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