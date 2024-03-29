package com.example.gptinvestor.features.company.presentation.model

data class CompanyFinancialsPresentation(
    val open: String,
    val low: String,
    val close: String,
    val volume: String,
    val high: String,
    val marketCap: String,
    val news: List<NewsPresentation>
)

data class NewsPresentation(
    val title: String,
    val id: String,
    val type: String,
    val relativeDate: String,
    val publisher: String,
    val imageUrl: String
)
