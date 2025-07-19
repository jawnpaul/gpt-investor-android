package com.thejawnpaul.gptinvestor.features.investor.data.remote


data class SimilarCompanyRequest(
    val ticker: String,
    val historicalData: String,
    val balanceSheet: String,
    val financials: String,
    val news: List<NewsRequest>
)

data class NewsRequest(
    val title: String,
    val id: String,
    val type: String,
    val relativeDate: String,
    val publisher: String,
    val imageUrl: String,
    val link: String
)
