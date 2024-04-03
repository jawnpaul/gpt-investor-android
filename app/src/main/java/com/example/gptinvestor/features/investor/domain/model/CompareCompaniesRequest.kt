package com.example.gptinvestor.features.investor.domain.model

import com.example.gptinvestor.features.company.presentation.model.CompanyFinancialsPresentation

data class CompareCompaniesRequest(
    val currentCompany: CompanyFinancialsPresentation,
    val otherCompanyTicker: String,
    val currentCompanyTicker: String
)
