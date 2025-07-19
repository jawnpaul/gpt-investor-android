package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyRemote(
    val ticker: String,
    val summary: String,
    val industry: String,
    @SerialName("industry_key") val industryKey: String,
    val sector: String,
    @SerialName("sector_key") val sectorKey: String,
    val country: String,
    val name: String,
    @SerialName("logo_url") val logoUrl: String,
    val website: String,
    val date: String
) {
    fun toEntity() = CompanyEntity(
        ticker = ticker,
        summary = summary,
        industry = industry,
        industryKey = industryKey,
        sector = sector,
        sectorKey = sectorKey,
        country = country,
        name,
        logoUrl = logoUrl,
        website = website,
        date = date
    )
}

@Serializable
data class CompanyDetailRemoteRequest(
    val ticker: String
)

@Serializable
data class CompanyDetailRemoteResponse(
    val ticker: String,
    @SerialName("summary") val about: String,
    @SerialName("market_cap") val marketCap: Long,
    val news: List<CompanyNews>,
    @SerialName("pe_ratio") val peRatio: Float,
    @SerialName("percentage_change") val change: Float,
    val revenue: Long,
    @SerialName("current_price") val price: Float,
    @SerialName("historical_data") val historicalData: List<HistoricalData>,
    @SerialName("company_name") val name: String,
    @SerialName("logo_url") val imageUrl: String
) {
    val newsSourcesString = buildString {
        appendLine("- [Yahoo finance](https://finance.yahoo.com/quote/$ticker)")
        news.forEach { appendLine("- [${it.publisher}](${it.link})") }
    }
}

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
data class CompanyPriceRequest(val tickers: List<String>)

@Serializable
data class CompanyPriceResponse(
    val change: Float,
    val price: Float,
    val ticker: String
)
