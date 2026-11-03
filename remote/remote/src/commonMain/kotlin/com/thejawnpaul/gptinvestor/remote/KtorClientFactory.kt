package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineFactory
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
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import co.touchlab.kermit.Logger as Log

private suspend fun HttpResponse.errorCode(): String? = try {
    Json.parseToJsonElement(bodyAsText()).jsonObject["error"]?.jsonPrimitive?.contentOrNull
} catch (_: Exception) {
    null
}

private val GUEST_TOKEN_ERRORS = setOf("guest_session_expired", "invalid_guest_token")

private fun TokenStorage.loadBearerTokens(): BearerTokens? =
    getAccessToken()?.let { BearerTokens(accessToken = it, refreshToken = getRefreshToken()) }

private fun HttpResponse.sentAccessToken(): String? =
    request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")

object KtorClientFactory {
    fun create(
        tokenStorage: TokenStorage,
        unauthorizedCallback: UnauthorizedCallback,
        clientInfo: ClientInfo,
        engineFactory: HttpClientEngineFactory<*> = getHttpClientEngine()
    ): HttpClient {
        return HttpClient(engineFactory) {
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
                        tokenStorage.loadBearerTokens()
                    }

                    refreshTokens {
                        val sentToken = response.sentAccessToken()

                        fun unauthorizedUnlessTokenChanged(): BearerTokens? {
                            if (tokenStorage.getAccessToken() != sentToken) return tokenStorage.loadBearerTokens()
                            unauthorizedCallback.onUnauthorized()
                            return null
                        }

                        if (response.errorCode() in GUEST_TOKEN_ERRORS) {
                            val uniqueId = unauthorizedCallback.guestUniqueId()
                                ?: return@refreshTokens unauthorizedUnlessTokenChanged()
                            return@refreshTokens try {
                                val guestResponse = client.post("v1/guest-login") {
                                    markAsRefreshTokenRequest()
                                    setBody(GuestTokenRequest(uniqueId))
                                }
                                if (guestResponse.status == HttpStatusCode.OK) {
                                    val accessToken = guestResponse.body<TokenResponse>().accessToken
                                    if (tokenStorage.replaceAccessToken(sentToken, accessToken)) {
                                        BearerTokens(accessToken, null)
                                    } else {
                                        tokenStorage.loadBearerTokens()
                                    }
                                } else {
                                    unauthorizedUnlessTokenChanged()
                                }
                            } catch (e: Exception) {
                                Log.e(e) { "Guest token refresh failed" }
                                null
                            }
                        }

                        val refreshToken = tokenStorage.getRefreshToken()
                            ?: return@refreshTokens unauthorizedUnlessTokenChanged()

                        try {
                            val response = client.post("v1.1/refresh") {
                                markAsRefreshTokenRequest()
                                header("Authorization", "Bearer $refreshToken")
                            }

                            if (response.status == HttpStatusCode.OK) {
                                val accessToken = response.body<TokenResponse>().accessToken
                                if (tokenStorage.replaceAccessToken(sentToken, accessToken)) {
                                    BearerTokens(accessToken, refreshToken)
                                } else {
                                    tokenStorage.loadBearerTokens()
                                }
                            } else {
                                unauthorizedUnlessTokenChanged()
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
                header("Accept-Language", getCurrentLocale())
                header("X-Client", "${clientInfo.platform}/${clientInfo.versionName}")
            }
        }
    }
}

