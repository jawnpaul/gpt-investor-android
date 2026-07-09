package com.thejawnpaul.gptinvestor.features.digest.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DigestResponse(
    @SerialName("digest_date") val digestDate: String?,
    @SerialName("is_stale") val isStale: Boolean?= null,
    @SerialName("items") val items: List<DigestItemRemote>? = null
)
