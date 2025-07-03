package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
internal actual val client: HttpClient = HttpClient(Darwin) {
    install(HttpTimeout) {
        socketTimeoutMillis = 60_000L
        requestTimeoutMillis = 60_000L
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