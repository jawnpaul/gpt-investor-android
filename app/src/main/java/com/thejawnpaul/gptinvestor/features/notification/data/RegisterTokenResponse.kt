package com.thejawnpaul.gptinvestor.features.notification.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterTokenResponse(
    @field:Json(name = "message")val message: String? = null,
    @field:Json(name = "error")val error: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterTokenRequest(
    @field:Json(name = "token")val token: String? = null
)
