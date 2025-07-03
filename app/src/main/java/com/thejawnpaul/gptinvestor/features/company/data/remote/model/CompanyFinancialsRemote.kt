package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import com.github.marlonlom.utilities.timeago.TimeAgo
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyFinancialsRemote(
    @SerialName("balance_sheet") val balanceSheet: String,
    val close: Float,
    val open: Float,
    val volume: Long,
    val currency: String,
    val financials: String,
    val high: Float,
    @SerialName("historical_data") val historicalData: String,
    val low: Float,
    @SerialName("market_cap") val marketCap: Long,
    val news: List<CompanyNews>
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
    val link: String,
    val providerPublishTime: Long,
    val publisher: String,
    val relatedTickers: List<String>,
    val thumbNail: NewsThumbNail?,
    val title: String,
    val type: String,
    @SerialName("uuid") val id: String
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

@Serializable
data class NewsThumbNail(
    val resolutions: List<NewsResolution> = emptyList()
)

@Serializable
data class NewsResolution(
    val height: Int,
    val tag: String,
    val url: String,
    val width: Int
)

@Serializable
data class CompanyFinancialsRequest(
    val ticker: String,
    val years: Int
)
