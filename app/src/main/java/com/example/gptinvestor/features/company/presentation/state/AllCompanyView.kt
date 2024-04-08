package com.example.gptinvestor.features.company.presentation.state

import com.example.gptinvestor.features.company.presentation.model.CompanyPresentation

data class AllCompanyView(
    val loading: Boolean = false,
    val companies: List<CompanyPresentation> = emptyList(),
    val error: String? = null
) {
    val showError = error != null && companies.isEmpty()
}
