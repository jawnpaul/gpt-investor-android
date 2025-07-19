package com.thejawnpaul.gptinvestor.features.company.presentation.model

import com.thejawnpaul.gptinvestor.core.utility.getCurrencySymbol
import com.thejawnpaul.gptinvestor.core.utility.toCurrency
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import nl.jacobras.humanreadable.HumanReadable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class CompanyFinancialsPresentation(
    val open: String,
    val low: String,
    val close: String,
    val volume: String,
    val high: String,
    val marketCap: String,
    val news: List<NewsPresentation>,
    val historicalData: String,
    val balanceSheet: String,
    val financials: String
)

data class NewsPresentation(
    val title: String,
    val id: String,
    val type: String,
    val relativeDate: String,
    val publisher: String,
    val imageUrl: String,
    val link: String
)


fun CompanyFinancials.toPresentation(): CompanyFinancialsPresentation {
    return CompanyFinancialsPresentation(
        open = open.toCurrency(getCurrencySymbol(currency)),
        low = low.toCurrency(getCurrencySymbol(currency)),
        close = close.toCurrency(getCurrencySymbol(currency)),
        volume = volume.toString(),
        high = high.toCurrency(getCurrencySymbol(currency)),
        marketCap = marketCap.toString(),
        news = news.map { it.toPresentation() },
        balanceSheet = balanceSheet,
        historicalData = historicalData,
        financials = financials
    )
}

@OptIn(ExperimentalTime::class)
fun CompanyNews.toPresentation() = NewsPresentation(
    title = title,
    id = id,
    type = type,
    relativeDate = HumanReadable.timeAgo(
        instant = Instant
            .fromEpochMilliseconds(providerPublishTime.times(1000))
    ),
    publisher = publisher,
    imageUrl = thumbNail?.resolutions?.first()?.url ?: "",
    link = link
)