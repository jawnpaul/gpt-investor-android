package com.thejawnpaul.gptinvestor.features.authentication.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(@SerialName("email") val email: String, @SerialName("password") val password: String)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("user") val user: User? = null
)

@Serializable
data class User(
    @SerialName("email") val email: String? = null,
    @SerialName("uid") val uid: String? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class FirebaseLoginRequest(@SerialName("id_token") val idToken: String)

@Serializable
data class SignUpRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("name") val name: String
)

@Serializable
data class SignUpResponse(
    @SerialName("message") val message: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("user_id") val userId: String? = null
)
