package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.core.functional.Failure
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApiError(
    @SerialName("error") val code: String? = null,
    val message: String? = null,
    val upgrade: Boolean? = null,
    @SerialName("retry_after") val retryAfterSeconds: Long? = null
)

private val lenientApiJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

fun parseApiError(body: String?): ApiError {
    if (body.isNullOrBlank()) return ApiError()
    val json = body.lineSequence().firstOrNull { it.startsWith("data:") }?.removePrefix("data:")?.trim() ?: body
    return try {
        lenientApiJson.decodeFromString<ApiError>(json)
    } catch (_: Exception) {
        ApiError()
    }
}

fun ApiError.toRateLimitExceeded(): Failure.RateLimitExceeded =
    Failure.RateLimitExceeded(upgrade = upgrade ?: true, retryAfterSeconds = retryAfterSeconds)
