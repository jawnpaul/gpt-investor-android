package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.sentiment_bearish
import com.thejawnpaul.gptinvestor.sentiment_bullish
import com.thejawnpaul.gptinvestor.sentiment_neutral
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

@Composable
fun SentimentBottomSheet(sentiment: BriefSentiment, summary: String?, modifier: Modifier = Modifier) {
    val colors = LocalGPTInvestorColors.current
    val (bg, fg, label) = when (sentiment) {
        BriefSentiment.Bullish -> Triple(
            colors.greenColors.allGreen.copy(alpha = 0.12f),
            colors.greenColors.allGreen,
            stringResource(Res.string.sentiment_bullish)
        )
        BriefSentiment.Bearish -> Triple(
            colors.redColors.allRed.copy(alpha = 0.12f),
            colors.redColors.allRed,
            stringResource(Res.string.sentiment_bearish)
        )
        BriefSentiment.Neutral -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurface,
            stringResource(Res.string.sentiment_neutral)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = bg,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = fg.copy(alpha = 0.15f),
                shape = RoundedCornerShape(corner = CornerSize(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color = fg, shape = CircleShape)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = fg
                    )
                }
            }
        }

        if (!summary.isNullOrBlank()) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@PreviewLightDark
@Composable
private fun SentimentBottomSheetPreview() {
    GPTInvestorTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SentimentBottomSheet(
                    sentiment = BriefSentiment.Bullish,
                    summary = "Services growth and steady cash flow keep the long-term outlook strong " +
                        "despite near-term hardware headwinds."
                )
                SentimentBottomSheet(
                    sentiment = BriefSentiment.Bearish,
                    summary = "Increased competition and slowing consumer demand " +
                        "are weighing on near-term expectations."
                )
                SentimentBottomSheet(
                    sentiment = BriefSentiment.Neutral,
                    summary = "The stock is steady today as big hopes and recent slowdowns balance each other out."
                )
            }
        }
    }
}
