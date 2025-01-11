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
    @field:Json(name = "news") val news: List<CompanyNewsTwo>
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
data class CompanyNewsTwo(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "content") val newsContent: NewsContent
) {
    fun toPresentation() = NewsPresentation(
        title = newsContent.title ?: "",
        id = id,
        type = newsContent.contentType ?: "",
        relativeDate = newsContent.pubDate ?: "",
        publisher = newsContent.provider?.displayName ?: "Default publisher",
        imageUrl = newsContent.thumbnail?.resolutions?.first()?.url ?: "",
        link = newsContent.clickThroughUrl?.url ?: "www.google.com"
    )
}

@JsonClass(generateAdapter = true)
data class NewsContent(
    @field:Json(name = "bypassModal") val bypassModal: Boolean?,
    @field:Json(name = "canonicalUrl") val canonicalUrl: NewsUrl?,
    @field:Json(name = "clickThroughUrl") val clickThroughUrl: NewsUrl?,
    @field:Json(name = "contentType") val contentType: String?,
    @field:Json(name = "finance") val finance: NewsFinance?,
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "isHosted") val isHosted: Boolean?,
    @field:Json(name = "metadata") val metadata: NewsMetaData?,
    @field:Json(name = "previewUrl") val previewUrl: String? = null,
    @field:Json(name = "provider") val provider: NewsProvider?,
    @field:Json(name = "pubDate") val pubDate: String?,
    @field:Json(name = "storyline") val storyLine: StoryLine? = null,
    @field:Json(name = "summary") val summary: String?,
    @field:Json(name = "thumbnail") val thumbnail: NewsThumbNail?,
    @field:Json(name = "title") val title: String?
)

@JsonClass(generateAdapter = true)
data class StoryLine(
    @field:Json(name = "storylineItems") val storyLineItems: List<StoryLineItem>
)

@JsonClass(generateAdapter = true)
data class StoryLineItem(
    @field:Json(name = "content") val content: StoryLineContent
)

@JsonClass(generateAdapter = true)
data class StoryLineContent(
    @field:Json(name = "canonicalUrl") val canonicalUrl: StoryLineUrl?,
    @field:Json(name = "clickThroughUrl") val clickThroughUrl: StoryLineUrl?,
    @field:Json(name = "contentType") val contentType: String?,
    @field:Json(name = "id") val id: String?,
    @field:Json(name = "isHosted") val isHosted: Boolean?,
    @field:Json(name = "previewUrl") val previewUrl: Any?,
    @field:Json(name = "provider") val provider: StoryLineProvider?,
    @field:Json(name = "providerContentUrl") val providerContentUrl: String?,
    @field:Json(name = "thumbnail") val thumbnail: NewsThumbNail?,
    @field:Json(name = "title") val title: String?
)

@JsonClass(generateAdapter = true)
data class StoryLineUrl(
    @field:Json(name = "url") val url: String? = null
)

@JsonClass(generateAdapter = true)
data class StoryLineProvider(
    @field:Json(name = "displayName") val displayName: String?,
    @field:Json(name = "sourceId") val sourceId: String?
)

@JsonClass(generateAdapter = true)
data class NewsUrl(
    @field:Json(name = "lang") val lang: String?,
    @field:Json(name = "region") val region: String?,
    @field:Json(name = "site") val site: String?,
    @field:Json(name = "url") val url: String?
)

@JsonClass(generateAdapter = true)
data class NewsFinance(
    @field:Json(name = "premiumFinance") val premiumFinance: NewsPremiumFinance?
)

@JsonClass(generateAdapter = true)
data class NewsPremiumFinance(
    @field:Json(name = "isPremiumFreeNews") val isPremiumFreeNews: Boolean?,
    @field:Json(name = "isPremiumNews") val isPremiumNews: Boolean?
)

@JsonClass(generateAdapter = true)
data class NewsMetaData(
    @field:Json(name = "editorsPicks") val editorsPick: Boolean?
)

@JsonClass(generateAdapter = true)
data class NewsProvider(
    @field:Json(name = "displayName") val displayName: String,
    @field:Json(name = "url") val url: String
)

@JsonClass(generateAdapter = true)
data class NewsThumbNail(
    @field:Json(name = "caption") val caption: String,
    @field:Json(name = "originalHeight") val originalHeight: Int,
    @field:Json(name = "originalUrl") val originalUrl: String,
    @field:Json(name = "originalWidth") val originalWidth: Int,
    @field:Json(name = "resolutions") val resolutions: List<NewsResolution>? = emptyList()
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
