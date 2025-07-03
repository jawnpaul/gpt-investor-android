package com.thejawnpaul.gptinvestor.features.notification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterTokenResponse(
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class RegisterTokenRequest(
    val token: String? = null,
    @SerialName("user_id") val userId: String? = null
)
