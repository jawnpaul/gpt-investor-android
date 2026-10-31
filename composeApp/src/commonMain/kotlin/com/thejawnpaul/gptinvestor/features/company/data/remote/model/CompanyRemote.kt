package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("summary") val summary: String? = null,
    @SerialName("industry") val industry: String? = null,
    @SerialName("industry_key") val industryKey: String? = null,
    @SerialName("sector") val sector: String? = null,
    @SerialName("sector_key") val sectorKey: String? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("name") val name: String,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("date") val date: String? = null,
    @SerialName("is_watched") val isWatched: Boolean? = null
) {
    fun toEntity() = CompanyEntity(
        ticker = ticker,
        summary = summary.orEmpty(),
        industry = industry.orEmpty(),
        industryKey = industryKey.orEmpty(),
        sector = sector.orEmpty(),
        sectorKey = sectorKey.orEmpty(),
        country = country.orEmpty(),
        name = name,
        logoUrl = logoUrl.orEmpty().toHttpsUrl(),
        website = website.orEmpty(),
        date = date.orEmpty()
    )
}

@Serializable
data class CompanyDetailRemoteRequest(@SerialName("ticker") val ticker: String)

@Serializable
data class HistoricalData(
    @SerialName("Close") val close: Float,
    @SerialName("Date") val date: String,
    @SerialName("High") val high: Float,
    @SerialName("Low") val low: Float,
    @SerialName("Open") val open: Float,
    @SerialName("Volume") val volume: Long
)

@Serializable
data class CompanyDetailRemoteResponse(
    @SerialName("ticker") val ticker: String,
    @SerialName("summary") val about: String? = null,
    @Serializable(with = WholeNumberSerializer::class)
    @SerialName("market_cap")
    val marketCap: Long? = null,
    @SerialName("news") val news: List<CompanyNews>? = null,
    @SerialName("pe_ratio") val peRatio: Float? = null,
    @SerialName("percentage_change") val change: Float? = null,
    @Serializable(with = WholeNumberSerializer::class)
    @SerialName("revenue")
    val revenue: Long? = null,
    @SerialName("current_price") val price: Float? = null,
    @SerialName("market") val market: String? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("currency_symbol") val currencySymbol: String? = null,
    @SerialName("historical_data") val historicalData: List<HistoricalData>? = null,
    @SerialName("company_name") val name: String? = null,
    @SerialName("logo_url") val imageUrl: String? = null,
    @SerialName("sentiment") val sentiment: String? = null,
    @SerialName("sentiment_summary") val sentimentSummary: String? = null,
    @SerialName("dividend_yield") val dividendYield: Float? = null,
    @SerialName("revenue_growth") val revenueGrowth: Float? = null,
    @SerialName("market_cap_insight") val marketCapInsight: String? = null,
    @SerialName("market_cap_insight_tone") val marketCapInsightTone: String? = null,
    @SerialName("pe_ratio_insight") val peRatioInsight: String? = null,
    @SerialName("pe_ratio_insight_tone") val peRatioInsightTone: String? = null,
    @SerialName("revenue_growth_insight") val revenueGrowthInsight: String? = null,
    @SerialName("revenue_growth_insight_tone") val revenueGrowthInsightTone: String? = null,
    @SerialName("dividend_yield_insight") val dividendYieldInsight: String? = null,
    @SerialName("dividend_yield_insight_tone") val dividendYieldInsightTone: String? = null,
    @SerialName("risk") val risk: BriefSectionRemote? = null,
    @SerialName("opportunity") val opportunity: BriefSectionRemote? = null
) {
    val newsSourcesString: String
        get() = buildString {
            appendLine("- [Yahoo finance](https://finance.yahoo.com/quote/$ticker)")
            news?.forEach { appendLine("- [${it.publisher}](${it.link})") }
        }
    val formattedImageUrl: String
        get() = imageUrl?.toHttpsUrl() ?: ""
}

@Serializable
data class BriefSectionRemote(
    @SerialName("title") val title: String? = null,
    @SerialName("body") val body: String? = null
)

@Serializable
data class CompanyPriceRequest(@SerialName("tickers") val tickers: List<String>)

@Serializable
data class CompanyLogosRequest(@SerialName("tickers") val tickers: List<String>)

@Serializable
data class CompanyLogoRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("logo_url") val logoUrl: String? = null
)

@Serializable
data class CompanyPriceResponse(
    @SerialName("change") val change: Float,
    @SerialName("price") val price: Float,
    @SerialName("ticker") val ticker: String
)
