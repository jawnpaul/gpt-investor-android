package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiBriefCard(
    stock: GiStock,
    summary: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sentiment: GiSentiment? = null,
    sentimentLabel: String? = null,
    sentimentCaption: String? = null,
    keyEventLabel: String? = null,
    keyEvent: String? = null,
    updatedText: String? = null
) {
    GiCard(modifier = modifier, onClick = onClick) {
        GiStockSummary(stock = stock)
        if (sentiment != null && sentimentLabel != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiSentimentPill(sentiment = sentiment, label = sentimentLabel)
                if (sentimentCaption != null) {
                    Text(text = sentimentCaption, style = GiTheme.type.meta, color = GiTheme.colors.muted)
                }
            }
        }
        Text(
            text = summary,
            style = GiTheme.type.body,
            color = GiTheme.colors.body,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        if (keyEventLabel != null && keyEvent != null) {
            GiInfoTile(label = keyEventLabel, text = keyEvent)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = updatedText.orEmpty(),
                modifier = Modifier.weight(1f),
                style = GiTheme.type.meta,
                color = GiTheme.colors.muted
            )
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = GiTheme.colors.muted
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiBriefCardPreview() {
    GiPreviewSurface {
        GiBriefCard(
            stock = previewStock.copy(initials = "AA", title = "Apple Inc.", subtitle = "AAPL · NASDAQ", percent = 0.1),
            sentiment = GiSentiment.Positive,
            sentimentLabel = "Bullish",
            sentimentCaption = "Sentiment",
            summary = "Apple rose 0.1% to $341.27 on Oct 13, while revenue grew 6.4% year over year.",
            onClick = {},
            updatedText = "Updated Wed 07:01",
            keyEventLabel = "Key event",
            keyEvent = "New HomeView tablet announced on 14 Oct."
        )
    }
}
