package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.declined
import com.thejawnpaul.gptinvestor.improved
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.tap_to_unlock_full_summaries
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today_s_digest
import com.thejawnpaul.gptinvestor.unchanged
import com.thejawnpaul.gptinvestor.unlock
import com.thejawnpaul.gptinvestor.x_companies
import com.thejawnpaul.gptinvestor.x_of_y
import org.jetbrains.compose.resources.stringResource

@Composable
fun DigestDetailScreen(
    digestView: DailyDigestView,
    onBack: () -> Unit,
    onUnlockPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = digestView.stocks.size + digestView.lockedStocks.size
    val unlockedCount = digestView.stocks.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        DigestDetailTopBar(
            date = digestView.lastUpdated,
            totalCount = totalCount,
            unlockedCount = unlockedCount,
            onBack = onBack
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(digestView.stocks) { stock ->
                DigestFullStockCard(stock = stock)
            }

            if (digestView.lockedStocks.isNotEmpty()) {
                item {
                    DigestLockedStocksCard(lockedStocks = digestView.lockedStocks)
                }
                item {
                    DigestUnlockBanner(onUnlockPremium = onUnlockPremium)
                }
            }
        }
    }
}

@Composable
private fun DigestDetailTopBar(
    date: String?,
    totalCount: Int,
    unlockedCount: Int,
    onBack: () -> Unit
) {
    val customColors = LocalGPTInvestorColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, customColors.utilColors.borderBright10, CircleShape)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(Res.string.today_s_digest),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (totalCount > 0) {
                val subtitle = buildString {
                    if (date != null) {
                        append(date)
                        append(" · ")
                    }
                    append(stringResource(Res.string.x_companies, totalCount))
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textColors.secondary50
                )
            }
        }

        if (totalCount > 0) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = stringResource(Res.string.x_of_y, unlockedCount, totalCount),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun DigestFullStockCard(stock: StockDigestPresentation, modifier: Modifier = Modifier) {
    val customColors = LocalGPTInvestorColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TickerBadge(ticker = stock.ticker)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = customColors.textColors.secondary50
                    )
                }
                SentimentBadge(sentimentChange = stock.sentimentChange)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = stock.summary,
                style = MaterialTheme.typography.bodyMedium
            )

            if (stock.keyEvent != null) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    color = customColors.utilColors.allDark2.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = stringResource(Res.string.key_event),
                            style = MaterialTheme.typography.labelSmall,
                            color = customColors.textColors.secondary50,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stock.keyEvent,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            val timeLabel = stock.generatedAt ?: return@Column
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Generated $timeLabel",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textColors.secondary50
            )
        }
    }
}

@Composable
private fun DigestLockedStocksCard(
    lockedStocks: List<LockedStockDigestPresentation>,
    modifier: Modifier = Modifier
) {
    val customColors = LocalGPTInvestorColors.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            lockedStocks.forEachIndexed { index, stock ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = customColors.textColors.secondary50,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    TickerBadge(ticker = stock.ticker)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textColors.secondary50,
                        modifier = Modifier.weight(1f)
                    )
                    SentimentBadge(sentimentChange = stock.sentimentChange)
                }
                if (index < lockedStocks.size - 1) {
                    HorizontalDivider(color = customColors.utilColors.borderBright10)
                }
            }
        }
    }
}

@Composable
private fun DigestUnlockBanner(onUnlockPremium: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.tap_to_unlock_full_summaries),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = onUnlockPremium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.unlock),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TickerBadge(ticker: String) {
    val customColors = LocalGPTInvestorColors.current

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(customColors.utilColors.allDark2.copy(alpha = 0.08f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = ticker,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SentimentBadge(sentimentChange: String) {
    val customColors = LocalGPTInvestorColors.current
    val sentiment = sentimentChange.lowercase()

    when (sentiment) {
        "improved" -> {
            Surface(
                color = customColors.greenColors.allGreen10,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = customColors.greenColors.allGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.improved),
                        style = MaterialTheme.typography.labelSmall,
                        color = customColors.greenColors.allGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        "declined" -> {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.declined),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        else -> {
            Text(
                text = stringResource(Res.string.unchanged),
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textColors.secondary50
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DigestDetailScreenPreview() {
    GPTInvestorTheme {
        Surface {
            DigestDetailScreen(
                digestView = DailyDigestView(
                    status = DailyDigestStatus.READY,
                    lastUpdated = "Tue, Jul 14",
                    stocks = listOf(
                        StockDigestPresentation(
                            ticker = "AAPL",
                            name = "Apple Inc.",
                            summary = "Apple firmed up overnight after an upbeat services read and constructive " +
                                "supplier commentary out of Asia. Analyst notes turned more positive into next week's print.",
                            isImproved = true,
                            sentimentChange = "improved",
                            keyEvent = "iPhone 17 pre-orders beat Street estimates by roughly 12%.",
                            generatedAt = "6:42 AM ET"
                        )
                    ),
                    lockedStocks = listOf(
                        LockedStockDigestPresentation("NVDA", "NVIDIA Corp.", "declined"),
                        LockedStockDigestPresentation("TSLA", "Tesla, Inc.", "unchanged")
                    ),
                    lockedStocksCount = 2
                ),
                onBack = {},
                onUnlockPremium = {}
            )
        }
    }
}
