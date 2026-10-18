package com.thejawnpaul.gptinvestor.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
)

@Serializable
data class GuestTokenRequest(@SerialName("unique_id") val uniqueId: String)

