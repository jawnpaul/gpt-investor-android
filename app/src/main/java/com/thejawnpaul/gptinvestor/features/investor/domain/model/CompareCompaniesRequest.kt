package com.thejawnpaul.gptinvestor.features.investor.domain.model

import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyFinancialsPresentation

data class CompareCompaniesRequest(
    val currentCompany: CompanyFinancialsPresentation,
    val otherCompanyTicker: String,
    val currentCompanyTicker: String
)
