package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import com.thejawnpaul.gptinvestor.Res
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
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiContentSheet
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
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummary
import com.thejawnpaul.gptinvestor.theme.components.GiStockSummarySize
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.today
import com.thejawnpaul.gptinvestor.what_it_means
import org.jetbrains.compose.resources.stringResource

/** Which screen the brief is laid out for. */
internal enum class CompanyBriefLayout {
    /** Company screen: verdict card, sections in one divided list group. The caller insets the list. */
    Company,

    /**
     * Onboarding first brief: verdict as page text; sections and the optional
     * footer on a full-bleed raised sheet docked to the bottom of the list.
     */
    Onboarding
}

/**
 * The brief as list items.
 *
 * With [CompanyBriefLayout.Onboarding], hero and verdict sit on the page (inset by [horizontalPadding]); the
 * sections and the optional [footer] sit on a full-bleed raised sheet. The sheet docks to the bottom of the list,
 * so the caller should not add bottom content padding; pass the space it needs (nav bar etc.) as
 * [sheetBottomPadding].
 *
 * With [CompanyBriefLayout.Company] (default), the caller insets the list; [horizontalPadding] and
 * [sheetBottomPadding] are unused and the [footer], if any, is its own item after the sections.
 */
internal fun LazyListScope.companyBriefItems(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)? = null,
    logoModifier: Modifier = Modifier,
    layout: CompanyBriefLayout = CompanyBriefLayout.Company,
    horizontalPadding: Dp = GiSpace.gutter,
    sheetBottomPadding: Dp = GiSpace.safeArea,
    footer: (@Composable ColumnScope.() -> Unit)? = null
) {
    when (layout) {
        CompanyBriefLayout.Company -> companyLayoutItems(
            brief = brief,
            onKeyNumberClick = onKeyNumberClick,
            onNewsClick = onNewsClick,
            logoModifier = logoModifier,
            footer = footer
        )

        CompanyBriefLayout.Onboarding -> onboardingLayoutItems(
            brief = brief,
            onKeyNumberClick = onKeyNumberClick,
            onNewsClick = onNewsClick,
            logoModifier = logoModifier,
            horizontalPadding = horizontalPadding,
            sheetBottomPadding = sheetBottomPadding,
            footer = footer
        )
    }
}

private fun LazyListScope.companyLayoutItems(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)?,
    logoModifier: Modifier,
    footer: (@Composable ColumnScope.() -> Unit)?
) {
    item(key = "hero", contentType = "hero") { BriefHero(brief = brief, logoModifier = logoModifier) }
    brief.sentimentSummary?.takeIf { it.isNotBlank() }?.let { summary ->
        item(key = "verdict", contentType = "verdict") { BriefVerdictCard(summary = summary) }
    }
    item(key = "sections", contentType = "sections") {
        BriefSectionsGroup(brief = brief, onKeyNumberClick = onKeyNumberClick, onNewsClick = onNewsClick)
    }
    footer?.let { content ->
        item(key = "footer", contentType = "footer") { Column { content() } }
    }
}

private fun LazyListScope.onboardingLayoutItems(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)?,
    logoModifier: Modifier,
    horizontalPadding: Dp,
    sheetBottomPadding: Dp,
    footer: (@Composable ColumnScope.() -> Unit)?
) {
    item(key = "hero", contentType = "hero") {
        BriefHero(
            brief = brief,
            modifier = Modifier.padding(horizontal = horizontalPadding),
            logoModifier = logoModifier
        )
    }
    brief.sentimentSummary?.takeIf { it.isNotBlank() }?.let { summary ->
        item(key = "verdict", contentType = "verdict") {
            GiText(
                text = summary,
                style = GiTextStyle.PageTitle,
                modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding)
            )
        }
    }
    item(key = "sections", contentType = "sections") {
        BriefSectionsSheet(
            brief = brief,
            onKeyNumberClick = onKeyNumberClick,
            onNewsClick = onNewsClick,
            bottomPadding = sheetBottomPadding,
            footer = footer,
            // The list spaces items by 16; the page-to-sheet gap is 24.
            modifier = Modifier.padding(top = GiSpace.x2)
        )
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
private fun BriefVerdictCard(summary: String, modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth()) {
        GiText(text = summary, style = GiTextStyle.StatNumber)
    }
}

// Company layout: sections in one list group, separated by dividers.
@Composable
private fun BriefSectionsGroup(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val layout = CompanyBriefLayout.Company
    val sections = buildList<@Composable () -> Unit> {
        brief.summary?.let { add { BriefSummarySection(summary = it) } }
        if (brief.keyNumbers.isNotEmpty()) {
            add {
                BriefKeyNumbersSection(
                    keyNumbers = brief.keyNumbers,
                    onKeyNumberClick = onKeyNumberClick,
                    layout = layout
                )
            }
        }
        if (brief.news.isNotEmpty()) {
            add { BriefNewsSection(news = brief.news, onNewsClick = onNewsClick, layout = layout) }
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

// Onboarding layout: sections and footer on a full-bleed raised sheet, spaced without dividers.
@Composable
private fun BriefSectionsSheet(
    brief: CompanyBrief,
    onKeyNumberClick: (KeyNumber) -> Unit,
    onNewsClick: ((String) -> Unit)?,
    bottomPadding: Dp,
    footer: (@Composable ColumnScope.() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val layout = CompanyBriefLayout.Onboarding
    val hasSections = brief.summary != null ||
        brief.keyNumbers.isNotEmpty() ||
        brief.news.isNotEmpty() ||
        brief.risk != null ||
        brief.opportunity != null
    if (!hasSections && footer == null) return
    GiContentSheet(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = GiSpace.gutter,
            end = GiSpace.gutter,
            top = GiSpace.x6,
            bottom = bottomPadding
        ),
        verticalSpacing = GiSpace.x8
    ) {
        brief.summary?.let { BriefSummarySection(summary = it) }
        if (brief.keyNumbers.isNotEmpty()) {
            BriefKeyNumbersSection(keyNumbers = brief.keyNumbers, onKeyNumberClick = onKeyNumberClick, layout = layout)
        }
        if (brief.news.isNotEmpty()) {
            BriefNewsSection(news = brief.news, onNewsClick = onNewsClick, layout = layout)
        }
        if (brief.risk != null || brief.opportunity != null) {
            BriefRiskOpportunityTile(risk = brief.risk, opportunity = brief.opportunity)
        }
        footer?.invoke(this)
    }
}

@Composable
private fun BriefCardSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        color = GiTheme.colors.ink,
        style = GiTheme.type.sectionTitle
    )
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
    layout: CompanyBriefLayout,
    modifier: Modifier = Modifier
) {
    val rows: @Composable () -> Unit = {
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        when (layout) {
            CompanyBriefLayout.Company -> {
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
                rows()
            }

            CompanyBriefLayout.Onboarding -> {
                BriefCardSectionTitle(text = stringResource(Res.string.section_key_numbers))
                Column { rows() }
            }
        }
    }
}

@Composable
private fun BriefNewsSection(
    news: List<NewsBrief>,
    onNewsClick: ((String) -> Unit)?,
    layout: CompanyBriefLayout,
    modifier: Modifier = Modifier
) {
    val itemPadding = if (layout == CompanyBriefLayout.Company) GiSpace.x3 else GiSpace.x4
    val items: @Composable () -> Unit = {
        news.forEachIndexed { index, item ->
            if (index > 0) GiDivider()
            BriefNewsItem(item = item, onNewsClick = onNewsClick, verticalPadding = itemPadding)
        }
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
        when (layout) {
            CompanyBriefLayout.Company -> {
                BriefCardSectionTitle(
                    text = stringResource(Res.string.section_whats_happening),
                    modifier = Modifier.padding(bottom = GiSpace.x1)
                )
                items()
            }

            CompanyBriefLayout.Onboarding -> {
                BriefCardSectionTitle(text = stringResource(Res.string.section_whats_happening))
                Column { items() }
            }
        }
    }
}

@Composable
private fun BriefNewsItem(
    item: NewsBrief,
    onNewsClick: ((String) -> Unit)?,
    verticalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    Column(
        modifier = modifier.padding(vertical = verticalPadding),
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

// Company layout: risk and opportunity as divided rows with coloured tags.
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
    val kind = stringResource(if (isRisk) Res.string.risk_label else Res.string.opportunity_label)
    val title = section.title.ifBlank {
        stringResource(if (isRisk) Res.string.the_risk else Res.string.the_opportunity)
    }
    Row(
        modifier = modifier
            .padding(vertical = GiSpace.x3)
            .clearAndSetSemantics { contentDescription = spokenSentences(kind, title, section.body) },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        GiIconTile(
            icon = if (isRisk) GiIcons.warning else GiIcons.check,
            size = GiIconTileSize.Small,
            tone = if (isRisk) GiIconTileTone.Loss else GiIconTileTone.Gain
        )
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            Text(
                text = kind,
                color = if (isRisk) colors.loss else colors.gain,
                style = type.tag
            )
            Text(
                text = title,
                color = colors.ink,
                style = type.label
            )
            Text(text = section.body, color = colors.body, style = type.body)
        }
    }
}

// Onboarding layout: risk and opportunity together in one tile box, without coloured tags.
@Composable
private fun BriefRiskOpportunityTile(risk: BriefSection?, opportunity: BriefSection?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
        BriefCardSectionTitle(text = stringResource(Res.string.section_risk_opportunity))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(GiRadius.sm)
                .background(GiTheme.colors.tile)
                .padding(GiSpace.x4),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            risk?.let { BriefRiskOpportunityTileRow(section = it, isRisk = true) }
            if (risk != null && opportunity != null) GiDivider()
            opportunity?.let { BriefRiskOpportunityTileRow(section = it, isRisk = false) }
        }
    }
}

@Composable
private fun BriefRiskOpportunityTileRow(section: BriefSection, isRisk: Boolean, modifier: Modifier = Modifier) {
    val title = section.title.ifBlank {
        stringResource(if (isRisk) Res.string.the_risk else Res.string.the_opportunity)
    }
    // No visible coloured tag here; keep "Risk" / "Opportunity" for screen readers.
    val kind = stringResource(if (isRisk) Res.string.risk_label else Res.string.opportunity_label)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = "$kind. $title. ${section.body}" },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        GiIconTile(
            icon = if (isRisk) GiIcons.warning else GiIcons.check,
            size = GiIconTileSize.Small,
            tone = if (isRisk) GiIconTileTone.Loss else GiIconTileTone.Gain
        )
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x1)) {
            GiText(text = title, style = GiTextStyle.Label)
            GiText(text = section.body, style = GiTextStyle.Body)
        }
    }
}

private fun spokenSentences(vararg parts: String?): String = parts
    .mapNotNull { part -> part?.trimEnd()?.takeIf { it.isNotEmpty() } }
    .joinToString(" ") { part -> if (part.last() in SENTENCE_ENDS) part else "$part." }

private val SENTENCE_ENDS = setOf('.', '?', '!')

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
            companyBriefItems(brief = previewCompanyBrief, onKeyNumberClick = {})
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun CompanyBriefContentOnboardingPreview() {
    GPTInvestorTheme {
        LazyColumn(
            modifier = Modifier.background(GiTheme.colors.bg),
            contentPadding = PaddingValues(top = GiSpace.x3),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            companyBriefItems(
                brief = previewCompanyBrief,
                onKeyNumberClick = {},
                layout = CompanyBriefLayout.Onboarding
            )
        }
    }
}
