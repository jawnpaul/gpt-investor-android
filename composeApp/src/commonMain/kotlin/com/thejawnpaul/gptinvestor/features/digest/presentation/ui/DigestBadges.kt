package com.thejawnpaul.gptinvestor.features.digest.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.digest_sentiment
import com.thejawnpaul.gptinvestor.digest_sentiment_negative
import com.thejawnpaul.gptinvestor.digest_sentiment_neutral
import com.thejawnpaul.gptinvestor.digest_sentiment_positive
import com.thejawnpaul.gptinvestor.features.digest.presentation.DigestSentiment
import com.thejawnpaul.gptinvestor.features.digest.presentation.formatPriceChange
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

/** The day's price move, kept separate from sentiment so the two never contradict each other. */
@Composable
internal fun PriceChangeChip(percent: Double, modifier: Modifier = Modifier) {
    val customColors = LocalGPTInvestorColors.current
    val (background, content) = when {
        percent >= 0.05 -> customColors.greenColors.allGreen10 to customColors.greenColors.allGreen
        percent <= -0.05 -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.error
        else -> customColors.utilColors.allDark2.copy(alpha = 0.08f) to customColors.textColors.secondary50
    }
    Surface(color = background, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Text(
            text = formatPriceChange(percent),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = content,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/** "● Sentiment: Neutral"; the dot colour repeats the word so colour is never the only signal. */
@Composable
internal fun SentimentLabel(sentiment: DigestSentiment, modifier: Modifier = Modifier) {
    val customColors = LocalGPTInvestorColors.current
    val (dotColor: Color, label) = when (sentiment) {
        DigestSentiment.POSITIVE ->
            customColors.greenColors.allGreen to
                stringResource(Res.string.digest_sentiment_positive)
        DigestSentiment.NEUTRAL ->
            customColors.textColors.secondary50 to
                stringResource(Res.string.digest_sentiment_neutral)
        DigestSentiment.NEGATIVE ->
            MaterialTheme.colorScheme.error to
                stringResource(Res.string.digest_sentiment_negative)
    }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(Res.string.digest_sentiment, label),
            style = MaterialTheme.typography.labelSmall,
            color = customColors.textColors.secondary50
        )
    }
}
