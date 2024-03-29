package com.example.gptinvestor.features.company.data.remote.model

import com.squareup.moshi.Json

data class CompanyFinancialsRemote(
    @field:Json(name = "balance_sheet") val balanceSheet: String,
    @field:Json(name = "close") val close: Float,
    @field:Json(name = "currency") val currency: String,
    @field:Json(name = "financials") val financials: String,
    @field:Json(name = "high") val high: String,
    @field:Json(name = "historical_data") val historicalData: String,
    @field:Json(name = "low") val low: Float,
    @field:Json(name = "market_cap") val marketCap: Long,
    @field:Json(name = "news") val news: List<CompanyNews>
)

data class CompanyNews(
    @field:Json(name = "link") val link: String,
    @field:Json(name = "providerPublishTime") val providerPublishTime: Long,
    @field:Json(name = "publisher") val publisher: String,
    @field:Json(name = "relatedTickers") val relatedTickers: List<String>,
    @field:Json(name = "thumbNail") val thumbNail: NewsThumbNail,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "uuid") val id: String
)

data class NewsThumbNail(
    @field:Json(name = "resolutions") val resolutions: List<NewsResolution>
)

data class NewsResolution(
    @field:Json(name = "height") val height: Int,
    @field:Json(name = "tag") val tag: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "width") val width: Int
)

data class CompanyFinancialsRequest(
    @field:Json(name = "ticker")val ticker: String,
    @field:Json(name = "years")val years: Int
)
