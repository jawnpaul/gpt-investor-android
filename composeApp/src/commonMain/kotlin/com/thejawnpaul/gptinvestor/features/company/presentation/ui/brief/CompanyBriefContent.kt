package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.digest_sentiment_negative
import com.thejawnpaul.gptinvestor.digest_sentiment_neutral
import com.thejawnpaul.gptinvestor.digest_sentiment_positive
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSection
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefSentiment
import com.thejawnpaul.gptinvestor.features.company.domain.model.BriefTone
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumber
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.domain.model.NewsBrief
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.tickerStock
import com.thejawnpaul.gptinvestor.opportunity_label
import com.thejawnpaul.gptinvestor.risk_label
import com.thejawnpaul.gptinvestor.section_key_numbers
import com.thejawnpaul.gptinvestor.section_risk_opportunity
import com.thejawnpaul.gptinvestor.section_summary
import com.thejawnpaul.gptinvestor.section_whats_happening
import com.thejawnpaul.gptinvestor.tap_to_explain
import com.thejawnpaul.gptinvestor.the_opportunity
import com.thejawnpaul.gptinvestor.the_risk
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiDeltaPill
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiExplainerTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileTone
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiKeyNumberRow
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiPriceDisplay
import com.thejawnpaul.gptinvestor.theme.components.GiSentiment
import com.thejawnpaul.gptinvestor.theme.components.GiSentimentPill
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.today
import com.thejawnpaul.gptinvestor.what_it_means
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun LazyListScope.companyBriefItems(
    brief: CompanyBrief,
    onVerdictClick: () -> Unit,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)? = null,
    logoModifier: Modifier = Modifier
) {
    item(key = "hero", contentType = "hero") { BriefHero(brief = brief, logoModifier = logoModifier) }
    brief.sentiment?.let { sentiment ->
        item(key = "verdict", contentType = "verdict") {
            BriefVerdictCard(sentiment = sentiment, summary = brief.sentimentSummary, onClick = onVerdictClick)
        }
    }
    item(key = "sections", contentType = "sections") {
        BriefSections(brief = brief, onKeyNumberClick = onKeyNumberClick, onNewsClick = onNewsClick)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BriefHero(brief: CompanyBrief, modifier: Modifier = Modifier, logoModifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        GiStockSummary(
            stock = tickerStock(
                ticker = brief.ticker,
                title = brief.name,
                subtitle = brief.ticker,
                percent = null,
                imageUrl = brief.logoUrl.ifBlank { null }
            ),
            size = GiStockSummarySize.Header,
            logoModifier = logoModifier
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            GiPriceDisplay(price = brief.price.toDouble())
            GiDeltaPill(percent = brief.change.toDouble(), suffix = stringResource(Res.string.today))
        }
    }
}

@Composable
private fun BriefVerdictCard(
    sentiment: BriefSentiment,
    summary: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    GiCard(modifier = modifier.fillMaxWidth(), onClick = onClick, spacing = GiSpace.x2) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GiSentimentPill(sentiment = sentiment.toGi(), label = stringResource(sentiment.labelRes()))
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = colors.muted
            )
        }
        if (!summary.isNullOrBlank()) {
            Text(text = summary, color = colors.ink, style = GiTheme.type.statNumber)
        }
    }
}

@Composable
private fun BriefSections(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val sections = buildList<@Composable () -> Unit> {
        brief.summary?.let { add { BriefSummarySection(summary = it) } }
        if (brief.keyNumbers.isNotEmpty()) {
            add { BriefKeyNumbersSection(keyNumbers = brief.keyNumbers, onKeyNumberClick = onKeyNumberClick) }
        }
        if (brief.news.isNotEmpty()) {
            add { BriefNewsSection(news = brief.news, onNewsClick = onNewsClick) }
        }
        if (brief.risk != null || brief.opportunity != null) {
            add { BriefRiskOpportunitySection(risk = brief.risk, opportunity = brief.opportunity) }
        }
    }
    if (sections.isEmpty()) return
    GiListGroup(modifier = modifier) {
        sections.forEachIndexed { index, section ->
            if (index > 0) GiDivider()
            Column(modifier = Modifier.padding(GiSpace.cardPad)) { section() }
        }
    }
}

@Composable
private fun BriefCardSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier, color = GiTheme.colors.ink, style = GiTheme.type.sectionTitle)
}

@Composable
private fun BriefSummarySection(summary: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
        BriefCardSectionTitle(text = stringResource(Res.string.section_summary))
        Text(text = summary, color = GiTheme.colors.body, style = GiTheme.type.body)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BriefKeyNumbersSection(
    keyNumbers: List<KeyNumber>,
    onKeyNumberClick: (KeyNumber) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = GiSpace.x2),
            horizontalArrangement = Arrangement.SpaceBetween,
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            BriefCardSectionTitle(text = stringResource(Res.string.section_key_numbers))
            if (keyNumbers.any { it.insight != null }) {
                Text(
                    text = stringResource(Res.string.tap_to_explain),
                    color = GiTheme.colors.muted,
                    style = GiTheme.type.meta
                )
            }
        }
        keyNumbers.forEachIndexed { index, keyNumber ->
            if (index > 0) GiDivider()
            GiKeyNumberRow(
                label = stringResource(keyNumber.key.labelRes()),
                value = keyNumber.value,
                insight = keyNumber.insight,
                tone = keyNumber.tone.toGi(),
                valueTone = if (keyNumber.key == KeyNumberType.RevenueGrowth) signOf(keyNumber.value) else null,
                onClick = if (keyNumber.insight != null) ({ onKeyNumberClick(keyNumber) }) else null
            )
        }
    }
}

@Composable
private fun BriefNewsSection(news: List<NewsBrief>, onNewsClick: ((String) -> Unit)?, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        BriefCardSectionTitle(
            text = stringResource(Res.string.section_whats_happening),
            modifier = Modifier.padding(bottom = GiSpace.x1)
        )
        news.forEachIndexed { index, item ->
            if (index > 0) GiDivider()
            Column(
                modifier = Modifier.padding(vertical = GiSpace.x3),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                Text(text = "${item.publisher} · ${item.publishedRelative}", color = colors.muted, style = type.meta)
                Text(
                    text = item.title,
                    modifier = if (onNewsClick != null) {
                        Modifier.clickable(role = Role.Button) { onNewsClick(item.link) }
                    } else {
                        Modifier
                    },
                    color = colors.ink,
                    style = type.label
                )
                item.whatItMeans?.let {
                    GiExplainerTile(label = stringResource(Res.string.what_it_means), text = it)
                }
            }
        }
    }
}

@Composable
private fun BriefRiskOpportunitySection(
    risk: BriefSection?,
    opportunity: BriefSection?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        BriefCardSectionTitle(text = stringResource(Res.string.section_risk_opportunity))
        risk?.let {
            BriefRiskOpportunityRow(section = it, isRisk = true)
        }
        if (risk != null && opportunity != null) GiDivider()
        opportunity?.let {
            BriefRiskOpportunityRow(section = it, isRisk = false)
        }
    }
}

@Composable
private fun BriefRiskOpportunityRow(section: BriefSection, isRisk: Boolean, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    Row(
        modifier = modifier.padding(vertical = GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        GiIconTile(
            icon = if (isRisk) GiIcons.warning else GiIcons.check,
            size = GiIconTileSize.Small,
            tone = if (isRisk) GiIconTileTone.Loss else GiIconTileTone.Gain
        )
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            Text(
                text = stringResource(if (isRisk) Res.string.risk_label else Res.string.opportunity_label),
                color = if (isRisk) colors.loss else colors.gain,
                style = type.tag
            )
            Text(
                text = section.title.ifBlank {
                    stringResource(if (isRisk) Res.string.the_risk else Res.string.the_opportunity)
                },
                color = colors.ink,
                style = type.label
            )
            Text(text = section.body, color = colors.body, style = type.body)
        }
    }
}

internal fun BriefSentiment.toGi(): GiSentiment = when (this) {
    BriefSentiment.Bullish -> GiSentiment.Positive
    BriefSentiment.Neutral -> GiSentiment.Neutral
    BriefSentiment.Bearish -> GiSentiment.Negative
}

internal fun BriefSentiment.labelRes(): StringResource = when (this) {
    BriefSentiment.Bullish -> Res.string.digest_sentiment_positive
    BriefSentiment.Neutral -> Res.string.digest_sentiment_neutral
    BriefSentiment.Bearish -> Res.string.digest_sentiment_negative
}

internal fun BriefTone.toGi(): GiSentiment = when (this) {
    BriefTone.Positive -> GiSentiment.Positive
    BriefTone.Neutral -> GiSentiment.Neutral
    BriefTone.Negative -> GiSentiment.Negative
}

internal fun signOf(value: String): GiSentiment = when {
    value.startsWith("-") || value.startsWith("−") -> GiSentiment.Negative
    value.startsWith("+") -> GiSentiment.Positive
    else -> GiSentiment.Neutral
}

internal val previewCompanyBrief = CompanyBrief(
    id = "tsla",
    ticker = "TSLA",
    name = "Tesla, Inc.",
    logoUrl = "",
    price = 379.07f,
    change = -0.95f,
    sentiment = BriefSentiment.Neutral,
    sentimentSummary = "Tesla faces both exciting opportunities in clean energy and real risks from competition.",
    summary = "Tesla makes electric cars and energy storage systems, which are important for the world's shift " +
        "towards cleaner energy.",
    keyNumbers = listOf(
        KeyNumber(
            key = KeyNumberType.MarketCap,
            value = "$1.5T",
            insight = "Tesla's very large value shows that investors expect a lot of future growth.",
            tone = BriefTone.Positive
        ),
        KeyNumber(
            key = KeyNumberType.PeRatio,
            value = "351",
            insight = "Tesla's price is very high compared with the profit it makes today.",
            tone = BriefTone.Negative
        ),
        KeyNumber(
            key = KeyNumberType.RevenueGrowth,
            value = "−2.9%",
            insight = "Tesla's sales have recently decreased compared with last year.",
            tone = BriefTone.Negative
        )
    ),
    news = listOf(
        NewsBrief(
            id = "1",
            publisher = "MT Newswires",
            publishedRelative = "Yesterday",
            title = "Tesla, General Motors, other US automakers expected to benefit from resilient SAAR, RBC says",
            whatItMeans = "Tesla might see more sales because of steady car buying trends in the US.",
            tone = BriefTone.Neutral,
            link = ""
        )
    ),
    risk = BriefSection(
        title = "Competition and regulation",
        body = "Other car companies are making their own electric vehicles, and regulators are looking closely at " +
            "some of Tesla's new technology."
    ),
    opportunity = BriefSection(
        title = "Clean energy leader",
        body = "Tesla is a leader in electric cars and battery technology, putting it in a strong position as the " +
            "world moves towards greener energy."
    )
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun CompanyBriefContentPreview() {
    GPTInvestorTheme {
        LazyColumn(
            modifier = Modifier.background(GiTheme.colors.bg),
            contentPadding = PaddingValues(GiSpace.gutter),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            companyBriefItems(brief = previewCompanyBrief, onVerdictClick = {}, onKeyNumberClick = {})
        }
    }
}
