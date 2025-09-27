package com.thejawnpaul.gptinvestor.features.investor.domain.model

import com.thejawnpaul.gptinvestor.features.company.presentation.model.NewsPresentation

data class SentimentAnalysisRequest(val ticker: String, val news: List<NewsPresentation>)
