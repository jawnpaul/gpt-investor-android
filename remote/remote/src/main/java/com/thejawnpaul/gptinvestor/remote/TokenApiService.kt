package com.thejawnpaul.gptinvestor.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Header

interface TokenApiService {
    @POST("v1.1/refresh")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<TokenResponse>
}

@JsonClass(generateAdapter = true)
data class TokenResponse(
    @field:Json(name = "access_token")val accessToken: String,
)
