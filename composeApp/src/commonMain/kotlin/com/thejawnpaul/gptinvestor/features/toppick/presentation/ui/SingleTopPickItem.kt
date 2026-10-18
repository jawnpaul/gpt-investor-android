package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiLogoTile
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle

@Composable
fun SingleTopPickItem(pickPresentation: TopPickPresentation, onClick: (String) -> Unit, modifier: Modifier = Modifier) {
    GiCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { onClick(pickPresentation.id) },
        spacing = GiSpace.x3
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiLogoTile(initials = pickInitials(pickPresentation.ticker), imageUrl = pickPresentation.imageUrl)
            Column(modifier = Modifier.weight(1f)) {
                GiText(
                    text = pickPresentation.companyName,
                    style = GiTextStyle.Label,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                GiText(text = pickPresentation.ticker, style = GiTextStyle.Meta, maxLines = 1)
            }
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.md),
                tint = GiTheme.colors.muted
            )
        }
        if (pickPresentation.rationale.isNotBlank()) {
            GiText(
                text = pickPresentation.rationale,
                style = GiTextStyle.BodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun TopPickCardSkeleton(modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), spacing = GiSpace.x3) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiSkeleton(modifier = Modifier.size(GiIconSize.xl))
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiSkeleton(modifier = Modifier.width(SKELETON_NAME_WIDTH).height(GiSpace.x4))
                GiSkeleton(modifier = Modifier.width(SKELETON_TICKER_WIDTH).height(GiSpace.x3))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiSkeleton(modifier = Modifier.fillMaxWidth().height(GiSpace.x3))
            GiSkeleton(modifier = Modifier.fillMaxWidth(SKELETON_LINE_FRACTION).height(GiSpace.x3))
        }
    }
}

internal fun pickInitials(ticker: String): String = ticker.filter { it.isLetterOrDigit() }.take(2).uppercase()

private val SKELETON_NAME_WIDTH = 120.dp
private val SKELETON_TICKER_WIDTH = 64.dp
private const val SKELETON_LINE_FRACTION = 0.8f

internal val previewPick = TopPickPresentation(
    id = "1",
    ticker = "GOOGL",
    companyName = "Alphabet Inc.",
    rationale = "Alphabet remains a cornerstone of the digital economy. Its dominance in search and advertising " +
        "provides a massive cash engine to fund its aggressive AI infrastructure build-out.",
    confidenceScore = 9,
    metrics = listOf("Revenue: $445.87B", "Growth: +24.2%", "Gross margin: 60.9%"),
    risks = listOf("High historical price volatility", "Regulatory scrutiny in antitrust matters"),
    isSaved = false,
    percentageChange = -0.18f,
    currentPrice = 353.13f
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SingleTopPickItemPreview() {
    GPTInvestorTheme {
        Box(modifier = Modifier.background(GiTheme.colors.bg).padding(GiSpace.gutter)) {
            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                SingleTopPickItem(pickPresentation = previewPick, onClick = {})
                TopPickCardSkeleton()
            }
        }
    }
}
