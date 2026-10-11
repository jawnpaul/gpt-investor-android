package com.thejawnpaul.gptinvestor.features.digest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigestResponse(
    @SerialName("state") val status: String? = null,
    @SerialName("digest_date") val digestDate: String?,
    @SerialName("is_stale") val isStale: Boolean? = null,
    @SerialName("items") val items: List<DigestItemRemote>? = null,
    @SerialName("recommendations") val recommendations: List<RecommendedDigestCompany>? = null
)

@Serializable
data class RecommendedDigestCompany(
    @SerialName("ticker")val ticker: String = "",
    @SerialName("company_name")val companyName: String = ""
)
