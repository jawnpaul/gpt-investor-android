package com.thejawnpaul.gptinvestor.features.company.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllCompanyResponse(
    @SerialName("page") val page: Int,
    @SerialName("page_size") val pageSize: Int,
    @SerialName("total_companies") val totalCompanies: Int? = null,
    @SerialName("companies") val data: List<CompanyRemote>
)
