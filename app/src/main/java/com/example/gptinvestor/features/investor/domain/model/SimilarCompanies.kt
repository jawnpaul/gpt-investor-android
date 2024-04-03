package com.example.gptinvestor.features.investor.domain.model

data class SimilarCompanies(
    val codeText: String?,
    val companies: List<String>
) {
    val cleaned = companies.map { it.removeSurrounding("\"") }
}
