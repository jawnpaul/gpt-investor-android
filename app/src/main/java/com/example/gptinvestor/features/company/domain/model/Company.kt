package com.example.gptinvestor.features.company.domain.model

import com.example.gptinvestor.features.company.presentation.model.CompanyPresentation

data class Company(
    val ticker: String,
    val name: String,
    val summary: String,
    val logo: String
) {
    fun toPresentation() = CompanyPresentation(ticker = ticker, name = name, logo = logo, summary = summary)
}
