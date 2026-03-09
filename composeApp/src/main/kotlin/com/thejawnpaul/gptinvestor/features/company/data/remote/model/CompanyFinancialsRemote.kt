package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.github.marlonlom.utilities.timeago.TimeAgo
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyFinancialsRemote(
    @SerialName("balance_sheet") val balanceSheet: String,
    @SerialName("close") val close: Float,
    @SerialName("open") val open: Float,
    @SerialName("volume") val volume: Long,
    @SerialName("currency") val currency: String,
    @SerialName("financials") val financials: String,
    @SerialName("high") val high: Float,
    @SerialName("historical_data") val historicalData: String,
    @SerialName("low") val low: Float,
    @SerialName("market_cap") val marketCap: Long,
    @SerialName("news") val news: List<CompanyNews>
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

@Serializable
data class CompanyNews(
    @SerialName("link") val link: String,
    @SerialName("providerPublishTime") val providerPublishTime: Long,
    @SerialName("publisher") val publisher: String,
    @SerialName("relatedTickers") val relatedTickers: List<String>,
    @SerialName("thumbnail") val thumbNail: NewsThumbNail?,
    @SerialName("title") val title: String,
    @SerialName("type") val type: String,
    @SerialName("uuid") val id: String
) {
    fun toPresentation() = NewsPresentation(
        title = title,
        id = id,
        type = type,
        relativeDate = TimeAgo.using(time = providerPublishTime.times(1000)),
        publisher = publisher,
        imageUrl = thumbNail?.resolutions?.firstOrNull()?.url ?: "",
        link = link
    )
}

@Serializable
data class NewsThumbNail(@SerialName("resolutions") val resolutions: List<NewsResolution?>? = null)

@Serializable
data class NewsResolution(
    @SerialName("height") val height: Int? = null,
    @SerialName("tag") val tag: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("width") val width: Int? = null
)

@Serializable
data class CompanyFinancialsRequest(@SerialName("ticker") val ticker: String, @SerialName("years") val years: Int)
