package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextOverflow
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
fun SentimentBadge(sentiment: BriefSentiment, summary: String?, modifier: Modifier = Modifier) {
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
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            stringResource(Res.string.sentiment_neutral)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = bg, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = fg.copy(alpha = 0.1f),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = fg, shape = CircleShape)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = fg
                )
            }
        }
        if (!summary.isNullOrBlank()) {
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SentimentBadgePreview() {
    GPTInvestorTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SentimentBadge(
                    sentiment = BriefSentiment.Bullish,
                    summary = "Strong earnings and growth outlook."
                )
                SentimentBadge(
                    sentiment = BriefSentiment.Neutral,
                    summary = "Steady performance with minor headwinds."
                )
                SentimentBadge(
                    sentiment = BriefSentiment.Bearish,
                    summary = "Increased competition and slowing sales."
                )
            }
        }
    }
}
