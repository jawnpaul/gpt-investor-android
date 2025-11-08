package com.thejawnpaul.gptinvestor.remotetest

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class TestRemoteModule {
    @Single
    fun provideKtorfit(httpClient: HttpClient): Ktorfit =
        Ktorfit.Builder()
            .baseUrl("url")
            .httpClient(httpClient)
            .build()

    @Single
    fun provideHttpClient(json: Json): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("KtorHttp Call: $message")
                }
            }
            level = LogLevel.ALL
        }
    }

    @Single
    fun provideJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
        prettyPrint = true
    }
}