package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment
import com.thejawnpaul.gptinvestor.ic_lock
import com.thejawnpaul.gptinvestor.key_event
import com.thejawnpaul.gptinvestor.latest_digest
import com.thejawnpaul.gptinvestor.tap_to_unlock_full_summaries
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today_s_digest
import com.thejawnpaul.gptinvestor.unlock
import com.thejawnpaul.gptinvestor.updated_at
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DigestDetailScreen(
    digestView: DailyDigestView,
    onBack: () -> Unit,
    onUnlockPremium: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        DigestDetailTopBar(
            isStale = digestView.isStale,
            briefDateLabel = digestView.briefDateLabel,
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
private fun DigestDetailTopBar(isStale: Boolean, briefDateLabel: String?, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(if (isStale) Res.string.latest_digest else Res.string.today_s_digest),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (briefDateLabel != null) {
                Text(
                    text = briefDateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalGPTInvestorColors.current.textColors.secondary50
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    TickerBadge(ticker = stock.ticker)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = customColors.textColors.secondary50,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (stock.priceChange != null) {
                    Spacer(Modifier.width(8.dp))
                    PriceChangeChip(percent = stock.priceChange)
                }
            }

            if (stock.sentiment != null) {
                Spacer(Modifier.height(6.dp))
                SentimentLabel(sentiment = stock.sentiment)
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

            val updatedLabel = stock.updatedLabel ?: return@Column
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(Res.string.updated_at, updatedLabel),
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textColors.secondary50
            )
        }
    }
}

@Composable
private fun DigestLockedStocksCard(lockedStocks: List<LockedStockDigestPresentation>, modifier: Modifier = Modifier) {
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
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    TickerBadge(ticker = stock.ticker)
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stock.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = customColors.textColors.secondary50,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (stock.sentiment != null) {
                            Spacer(Modifier.height(2.dp))
                            SentimentLabel(sentiment = stock.sentiment)
                        }
                    }
                    if (stock.priceChange != null) {
                        Spacer(Modifier.width(8.dp))
                        PriceChangeChip(percent = stock.priceChange)
                    }
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

@PreviewLightDark
@Composable
private fun DigestDetailScreenPreview() {
    GPTInvestorTheme {
        Surface {
            DigestDetailScreen(
                digestView = DailyDigestView(
                    status = DailyDigestStatus.READY,
                    briefDateLabel = "Fri, Oct 9",
                    stocks = listOf(
                        StockDigestPresentation(
                            ticker = "AAPL",
                            name = "Apple Inc.",
                            summary = "Apple firmed up overnight after an upbeat services read and constructive " +
                                "supplier commentary out of Asia. Analyst notes turned more positive into next week's print.",
                            keyEvent = "iPhone 17 pre-orders beat Street estimates by roughly 12%.",
                            sentiment = DigestSentiment.POSITIVE,
                            priceChange = 1.5,
                            updatedLabel = "Sat 7:01 AM"
                        )
                    ),
                    lockedStocks = listOf(
                        LockedStockDigestPresentation("NVDA", "NVIDIA Corp.", DigestSentiment.NEGATIVE, -2.3),
                        LockedStockDigestPresentation("TSLA", "Tesla, Inc.", DigestSentiment.NEUTRAL, 0.4)
                    ),
                    lockedStocksCount = 2
                ),
                onBack = {},
                onUnlockPremium = {}
            )
        }
    }
}
