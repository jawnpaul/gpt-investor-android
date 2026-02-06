package com.thejawnpaul.gptinvestor.features.authentication.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @field:Json(name = "access_token") val accessToken: String?,
    @field:Json(name = "refresh_token") val refreshToken: String?,
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "status") val status: String?,
    @field:Json(name = "user") val user: User?
)

@JsonClass(generateAdapter = true)
data class User(
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "uid") val uid: String?
)
