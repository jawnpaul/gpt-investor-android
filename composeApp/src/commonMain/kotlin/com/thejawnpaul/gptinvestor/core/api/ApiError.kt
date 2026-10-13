package com.thejawnpaul.gptinvestor.core.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApiError(
    val code: String? = null,
    val message: String? = null,
    val limit: Int? = null,
    @SerialName("retry_after_seconds") val retryAfterSeconds: Int? = null
)

private val lenientApiJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun parseApiError(body: String?): ApiError {
    if (body.isNullOrBlank()) return ApiError()
    return try {
        lenientApiJson.decodeFromString<ApiError>(body)
    } catch (_: Exception) {
        ApiError()
    }
}
