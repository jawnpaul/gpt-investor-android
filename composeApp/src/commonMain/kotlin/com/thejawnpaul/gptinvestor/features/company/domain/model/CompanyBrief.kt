package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.thejawnpaul.gptinvestor.core.utility.relativeTime
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.core.utility.toReadable
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.BriefNewsItem
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.BriefOpportunityRisk
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyBriefRemote
import kotlin.math.roundToInt
import kotlin.time.Clock

data class CompanyBrief(
    val ticker: String,
    val name: String,
    val logoUrl: String,
    val price: Float,
    val change: Float,
    val sentiment: BriefSentiment?,
    val sentimentSummary: String?,
    val summary: String?,
    val keyNumbers: List<KeyNumber>,
    val news: List<NewsBrief>,
    val risk: BriefSection?,
    val opportunity: BriefSection?
)

enum class BriefSentiment { Bullish, Bearish, Neutral }

enum class BriefTone { Positive, Neutral, Negative }

data class KeyNumber(val key: KeyNumberType, val value: String, val insight: String?, val tone: BriefTone)

enum class KeyNumberType { MarketCap, PeRatio, RevenueGrowth, DividendYield }

data class NewsBrief(
    val id: String,
    val publisher: String,
    val publishedRelative: String,
    val title: String,
    val whatItMeans: String?,
    val tone: BriefTone,
    val link: String
)

data class BriefSection(val title: String, val body: String)

private fun String.toSentiment(): BriefSentiment? = when (uppercase()) {
    "BULLISH", "POSITIVE" -> BriefSentiment.Bullish
    "BEARISH", "NEGATIVE" -> BriefSentiment.Bearish
    "NEUTRAL" -> BriefSentiment.Neutral
    else -> null
}

private fun String?.toToneOrNeutral(): BriefTone = when (this?.uppercase()) {
    "POSITIVE" -> BriefTone.Positive
    "NEGATIVE" -> BriefTone.Negative
    else -> BriefTone.Neutral
}

private fun Float.signedTone(): BriefTone = when {
    this > 0f -> BriefTone.Positive
    this < 0f -> BriefTone.Negative
    else -> BriefTone.Neutral
}

private fun Float.formatRatio(): String {
    val rounded = (this * 10).roundToInt() / 10f
    val whole = rounded.toInt()
    return if (whole.toFloat() == rounded) whole.toString() else rounded.toString()
}

private fun Float.formatSignedPercent(): String {
    val rounded = (this * 10).roundToInt() / 10f
    val sign = if (rounded > 0f) "+" else ""
    return "$sign$rounded%"
}

private fun Float.formatPercent(): String {
    val rounded = (this * 100).roundToInt() / 100f
    return "$rounded%"
}

fun CompanyBriefRemote.toBrief(now: Long = Clock.System.now().epochSeconds): CompanyBrief = CompanyBrief(
    ticker = ticker.orEmpty(),
    name = companyName.orEmpty(),
    logoUrl = logoUrl?.toHttpsUrl().orEmpty(),
    price = currentPrice?.toFloat() ?: 0f,
    change = percentageChange?.toFloat() ?: 0f,
    sentiment = sentiment?.toSentiment(),
    sentimentSummary = sentimentSummary,
    summary = summary?.takeIf { it.isNotBlank() },
    keyNumbers = buildBriefKeyNumbers(),
    news = news.orEmpty().map { it.toBrief(now) },
    risk = risk?.toSection(),
    opportunity = opportunity?.toSection()
)

private fun CompanyBriefRemote.buildBriefKeyNumbers(): List<KeyNumber> = buildList {
    marketCap?.takeIf { it > 0 }?.let {
        add(
            KeyNumber(
                key = KeyNumberType.MarketCap,
                value = "$" + it.toReadable(),
                insight = marketCapInsight,
                tone = marketCapInsightTone.toToneOrNeutral()
            )
        )
    }
    peRatio?.toFloat()?.let {
        add(
            KeyNumber(
                key = KeyNumberType.PeRatio,
                value = it.formatRatio(),
                insight = peRatioInsight,
                tone = peRatioInsightTone.toToneOrNeutral()
            )
        )
    }
    revenueGrowth?.toFloat()?.let {
        add(
            KeyNumber(
                key = KeyNumberType.RevenueGrowth,
                value = it.formatSignedPercent(),
                insight = revenueGrowthInsight,
                tone = revenueGrowthInsightTone?.toToneOrNeutral() ?: it.signedTone()
            )
        )
    }
    dividendYield?.toFloat()?.let {
        add(
            KeyNumber(
                key = KeyNumberType.DividendYield,
                value = it.formatPercent(),
                insight = dividendYieldInsight,
                tone = dividendYieldInsightTone.toToneOrNeutral()
            )
        )
    }
}

private fun BriefNewsItem.toBrief(now: Long): NewsBrief = NewsBrief(
    id = url.orEmpty(),
    publisher = publisher.orEmpty(),
    publishedRelative = relativeTime(epochSec = publishedAt ?: 0L, now = now),
    title = headline.orEmpty(),
    whatItMeans = impact?.takeIf { it.isNotBlank() },
    tone = BriefTone.Neutral,
    link = url.orEmpty()
)

private fun BriefOpportunityRisk.toSection(): BriefSection? {
    val safeBody = body?.takeIf { it.isNotBlank() } ?: return null
    return BriefSection(title = title?.takeIf { it.isNotBlank() }.orEmpty(), body = safeBody)
}
