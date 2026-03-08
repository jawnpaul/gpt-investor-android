package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as Log

object KtorClientFactory {
    fun create(
        tokenStorage: TokenStorage,
        unauthorizedCallback: UnauthorizedCallback
    ): HttpClient {
        return HttpClient(getHttpClientEngine()) {
            expectSuccess = false

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    encodeDefaults = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d(message, tag = "Ktor")
                    }
                }
                level = if (BuildConfig.DEBUG) LogLevel.HEADERS else LogLevel.NONE
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val token = tokenStorage.getAccessToken()
                        val refreshToken = tokenStorage.getRefreshToken()
                        if (token != null && refreshToken != null) {
                            BearerTokens(token, refreshToken)
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        val refreshToken = tokenStorage.getRefreshToken() ?: return@refreshTokens null

                        try {
                            val response = client.post("v1.1/refresh") {
                                header("Authorization", "Bearer $refreshToken")
                            }

                            if (response.status == HttpStatusCode.OK) {
                                val tokenResponse = response.body<TokenResponse>()
                                tokenStorage.saveAccessToken(tokenResponse.accessToken)
                                BearerTokens(tokenResponse.accessToken, refreshToken)
                            } else {

                                unauthorizedCallback.onUnauthorized()
                                null
                            }
                        } catch (e: Exception) {
                            Log.e(e) { "Token refresh failed" }
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        request.url.encodedPath.contains("/v1") && 
                        !request.url.encodedPath.contains("/refresh") &&
                        !request.url.encodedPath.contains("/login") &&
                        !request.url.encodedPath.contains("/signup")
                    }
                }
            }

            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}

