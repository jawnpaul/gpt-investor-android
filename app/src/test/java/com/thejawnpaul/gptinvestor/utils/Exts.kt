package com.thejawnpaul.gptinvestor.utils

import com.google.common.io.Resources
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import java.net.URL
import kotlinx.serialization.json.Json

internal fun getJson(path: String): String {
    val uri: URL = Resources.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
}

internal fun makeTestKtorApiService(
    handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
): KtorApiService {
    val mockEngine = MockEngine(handler)
    val httpClient = HttpClient(mockEngine) {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                isLenient = true
            })
        }
        defaultRequest {
            url("https://api.test.com/")
        }
    }
    return KtorApiService(httpClient)
}

internal fun mockResponse(
    content: String,
    status: HttpStatusCode = HttpStatusCode.OK,
    headers: io.ktor.http.Headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
): HttpResponseData = respond(content, status, headers)

