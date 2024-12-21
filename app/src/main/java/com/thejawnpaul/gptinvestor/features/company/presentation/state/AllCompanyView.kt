package com.thejawnpaul.gptinvestor.features.company.presentation.state

import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyPresentation

data class AllCompanyView(
    val loading: Boolean = false,
    val companies: List<CompanyPresentation> = emptyList(),
    val error: String? = null,
    val query: String = ""
) {
    val showError = error != null && companies.isEmpty()
    val showSearchError = query.isNotBlank() && companies.isEmpty()
}
