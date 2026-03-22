package com.thejawnpaul.gptinvestor.features.notification.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterTokenResponse(
    @SerialName("message")val message: String? = null,
    @SerialName("error")val error: String? = null
)

@Serializable
data class RegisterTokenRequest(@SerialName("token")val token: String? = null)
