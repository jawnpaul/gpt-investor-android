package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyBriefRemote(
    @SerialName("company_name") val companyName: String? = null,
    @SerialName("current_price") val currentPrice: Double? = null,
    @SerialName("dividend_yield") val dividendYield: Double? = null,
    @SerialName("dividend_yield_insight") val dividendYieldInsight: String? = null,
    @SerialName("dividend_yield_insight_tone") val dividendYieldInsightTone: String? = null,
    @SerialName("historical_data") val historicalData: List<BriefHistoricalData>? = null,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("market_cap") val marketCap: Long? = null,
    @SerialName("market_cap_insight") val marketCapInsight: String? = null,
    @SerialName("market_cap_insight_tone") val marketCapInsightTone: String? = null,
    @SerialName("news") val news: List<BriefNewsItem>? = null,
    @SerialName("opportunity") val opportunity: BriefOpportunityRisk? = null,
    @SerialName("pe_ratio") val peRatio: Double? = null,
    @SerialName("pe_ratio_insight") val peRatioInsight: String? = null,
    @SerialName("pe_ratio_insight_tone") val peRatioInsightTone: String? = null,
    @SerialName("percentage_change") val percentageChange: Double? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("revenue_growth") val revenueGrowth: Double? = null,
    @SerialName("revenue_growth_insight") val revenueGrowthInsight: String? = null,
    @SerialName("revenue_growth_insight_tone") val revenueGrowthInsightTone: String? = null,
    @SerialName("risk") val risk: BriefOpportunityRisk? = null,
    @SerialName("sentiment") val sentiment: String? = null,
    @SerialName("sentiment_summary") val sentimentSummary: String? = null,
    @SerialName("summary") val summary: String? = null,
    @SerialName("ticker") val ticker: String? = null
)

@Serializable
data class BriefHistoricalData(
    @SerialName("Close") val close: Double? = null,
    @SerialName("Date") val date: String? = null,
    @SerialName("High") val high: Double? = null,
    @SerialName("Low") val low: Double? = null,
    @SerialName("Open") val open: Double? = null,
    @SerialName("Volume") val volume: Long? = null
)

@Serializable
data class BriefNewsItem(
    @SerialName("headline") val headline: String? = null,
    @SerialName("impact") val impact: String? = null,
    @SerialName("publishedAt") val publishedAt: Long? = null,
    @SerialName("publisher") val publisher: String? = null,
    @SerialName("url") val url: String? = null
)

@Serializable
data class BriefOpportunityRisk(
    @SerialName("body") val body: String? = null,
    @SerialName("title") val title: String? = null
)
