package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity

@JsonClass(generateAdapter = true)
data class CompanyRemote(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "summary") val summary: String,
    @field:Json(name = "industry") val industry: String,
    @field:Json(name = "industry_key") val industryKey: String,
    @field:Json(name = "sector") val sector: String,
    @field:Json(name = "sector_key") val sectorKey: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "logo_url") val logoUrl: String,
    @field:Json(name = "website") val website: String,
    @field:Json(name = "date") val date: String
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

@JsonClass(generateAdapter = true)
data class CompanyDetailRemoteRequest(
    @field:Json(name = "ticker") val ticker: String
)

@JsonClass(generateAdapter = true)
data class CompanyDetailRemoteResponse(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "summary") val about: String,
    @field:Json(name = "market_cap") val marketCap: Long,
    @field:Json(name = "news") val news: List<CompanyNews>,
    @field:Json(name = "pe_ratio") val peRatio: Float,
    @field:Json(name = "percentage_change") val change: Float,
    @field:Json(name = "revenue") val revenue: Long,
    @field:Json(name = "current_price") val price: Float,
    @field:Json(name = "historical_data") val historicalData: List<HistoricalData>,
    @field:Json(name = "company_name") val name: String,
    @field:Json(name = "logo_url") val imageUrl: String,
){
    val newsSourcesString = buildString {
        appendLine("- [Yahoo finance](https://finance.yahoo.com/quote/$ticker)")
        news.forEach { appendLine("- [${it.publisher}](${it.link})") }
    }
}

@JsonClass(generateAdapter = true)
data class HistoricalData(
    @field:Json(name = "Close") val close: Float,
    @field:Json(name = "Date") val date: String,
    @field:Json(name = "High") val high: Float,
    @field:Json(name = "Low") val low: Float,
    @field:Json(name = "Open") val open: Float,
    @field:Json(name = "Volume") val volume: Long
)