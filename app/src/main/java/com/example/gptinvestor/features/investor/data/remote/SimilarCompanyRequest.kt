package com.example.gptinvestor.features.investor.data.remote

import com.example.gptinvestor.features.company.presentation.model.NewsPresentation

data class SimilarCompanyRequest(
    val ticker: String,
    val historicalData: String,
    val balanceSheet: String,
    val financials: String,
    val news: List<NewsPresentation>
)
