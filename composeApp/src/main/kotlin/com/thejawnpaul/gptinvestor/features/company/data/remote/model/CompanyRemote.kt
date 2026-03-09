package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyRemote(
    @SerialName("ticker") val ticker: String,
    @SerialName("summary") val summary: String,
    @SerialName("industry") val industry: String,
    @SerialName("industry_key") val industryKey: String,
    @SerialName("sector") val sector: String,
    @SerialName("sector_key") val sectorKey: String,
    @SerialName("country") val country: String,
    @SerialName("name") val name: String,
    @SerialName("logo_url") val logoUrl: String,
    @SerialName("website") val website: String,
    @SerialName("date") val date: String
) {
    fun toEntity() = CompanyEntity(
        ticker = ticker,
        summary = summary,
        industry = industry,
        industryKey = industryKey,
        sector = sector,
        sectorKey = sectorKey,
        country = country,
        name = name,
        logoUrl = logoUrl,
        website = website,
        date = date
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
    @SerialName("market_cap") val marketCap: Long? = null,
    @SerialName("news") val news: List<CompanyNews>? = null,
    @SerialName("pe_ratio") val peRatio: Float? = null,
    @SerialName("percentage_change") val change: Float? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("current_price") val price: Float? = null,
    @SerialName("historical_data") val historicalData: List<HistoricalData>? = null,
    @SerialName("company_name") val name: String? = null,
    @SerialName("logo_url") val imageUrl: String? = null
) {
    val newsSourcesString: String
        get() = buildString {
            appendLine("- [Yahoo finance](https://finance.yahoo.com/quote/$ticker)")
            news?.forEach { appendLine("- [${it.publisher}](${it.link})") }
        }
}

@Serializable
data class CompanyPriceRequest(@SerialName("tickers") val tickers: List<String>)

@Serializable
data class CompanyPriceResponse(
    @SerialName("change") val change: Float,
    @SerialName("price") val price: Float,
    @SerialName("ticker") val ticker: String
)
