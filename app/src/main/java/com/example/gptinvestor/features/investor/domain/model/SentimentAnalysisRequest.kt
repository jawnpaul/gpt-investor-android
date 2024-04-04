package com.example.gptinvestor.features.investor.domain.model

import com.example.gptinvestor.features.company.presentation.model.NewsPresentation

data class SentimentAnalysisRequest(
    val ticker: String,
    val news: List<NewsPresentation>
)
