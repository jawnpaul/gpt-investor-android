package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.github.marlonlom.utilities.timeago.TimeAgo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation

@JsonClass(generateAdapter = true)
data class CompanyFinancialsRemote(
    @field:Json(name = "balance_sheet") val balanceSheet: String,
    @field:Json(name = "close") val close: Float,
    @field:Json(name = "open") val open: Float,
    @field:Json(name = "volume") val volume: Long,
    @field:Json(name = "currency") val currency: String,
    @field:Json(name = "financials") val financials: String,
    @field:Json(name = "high") val high: Float,
    @field:Json(name = "historical_data") val historicalData: String,
    @field:Json(name = "low") val low: Float,
    @field:Json(name = "market_cap") val marketCap: Long,
    @field:Json(name = "news") val news: List<CompanyNews>
) {
    fun toDomainObject() = CompanyFinancials(
        open = open,
        high = high,
        low = low,
        close = close,
        volume = volume,
        marketCap = marketCap,
        currency = currency,
        news = news,
        historicalData = historicalData,
        balanceSheet = balanceSheet,
        financials = financials
    )
}

@JsonClass(generateAdapter = true)
data class CompanyNews(
    @field:Json(name = "link") val link: String,
    @field:Json(name = "providerPublishTime") val providerPublishTime: Long,
    @field:Json(name = "publisher") val publisher: String,
    @field:Json(name = "relatedTickers") val relatedTickers: List<String>,
    @field:Json(name = "thumbnail") val thumbNail: NewsThumbNail?,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "uuid") val id: String
) {
    fun toPresentation() = NewsPresentation(
        title = title,
        id = id,
        type = type,
        relativeDate = TimeAgo.using(time = providerPublishTime.times(1000)),
        publisher = publisher,
        imageUrl = thumbNail?.resolutions?.first()?.url ?: "",
        link = link
    )
}

@JsonClass(generateAdapter = true)
data class NewsThumbNail(
    @field:Json(name = "resolutions") val resolutions: List<NewsResolution> = emptyList()
)

@JsonClass(generateAdapter = true)
data class NewsResolution(
    @field:Json(name = "height") val height: Int,
    @field:Json(name = "tag") val tag: String,
    @field:Json(name = "url") val url: String,
    @field:Json(name = "width") val width: Int
)

@JsonClass(generateAdapter = true)
data class CompanyFinancialsRequest(
    @field:Json(name = "ticker") val ticker: String,
    @field:Json(name = "years") val years: Int
)
