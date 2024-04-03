package com.example.gptinvestor.features.investor.presentation.state

import com.example.gptinvestor.features.investor.domain.model.SimilarCompanies

data class SimilarCompaniesView(
    val loading: Boolean = false,
    val result: SimilarCompanies? = null,
    val error: String? = null
)
