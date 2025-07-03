package com.thejawnpaul.gptinvestor.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
internal actual val client: HttpClient = HttpClient(Android) {
    engine {
        connectTimeout = 60_000 // 60 seconds
        socketTimeout = 60_000 // 60 seconds
    }
    defaultRequest {
        url(BuildConfig.BASE_URL)
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.e("KtorHttp Call", message)
            }
        }
        level = LogLevel.ALL
    }

    install(Auth) {
        bearer {
            loadTokens {
                val token = BuildConfig.ACCESS_TOKEN.takeIf { it.isNotEmpty() }
                if (token != null) {
                    BearerTokens(token, "")
                } else null
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = true
        })
    }
}
