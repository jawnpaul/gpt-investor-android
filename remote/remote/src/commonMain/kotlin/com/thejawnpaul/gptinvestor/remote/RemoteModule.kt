package com.thejawnpaul.gptinvestor.remote

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
object RemoteModule {
    @Single
    fun providesKtorfit(client: HttpClient): Ktorfit {
        return Ktorfit.Builder()
            .baseUrl(BuildKonfig.BASE_URL)
            .httpClient(client)
            .build()
    }

    @Single
    fun provideHttpClient(json: Json): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = if (BuildKonfig.DEBUG) LogLevel.ALL else LogLevel.NONE
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        val token = BuildKonfig.ACCESS_TOKEN.takeIf { it.isNotEmpty() }
                        if (token != null) {
                            BearerTokens(token, "")
                        } else null
                    }
                }
            }
        }
    }

    @Single
    fun provideJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }
}