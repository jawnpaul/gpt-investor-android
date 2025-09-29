package com.thejawnpaul.gptinvestor.utils

import com.google.common.io.Resources
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.api.createApiService
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import java.io.File
import java.net.URL
import okhttp3.mockwebserver.MockWebServer

private val okHttpClient: HttpClient
    get() = HttpClient {

    }

internal fun getJson(path: String): String {
    val uri: URL = Resources.getResource(path)
    val file = File(uri.path)
    return String(file.readBytes())
}

internal fun makeTestApiService(mockWebServer: MockWebServer): ApiService = Ktorfit.Builder()
    .baseUrl(mockWebServer.url("/").toString())
    .httpClient(okHttpClient)
    .build()
    .createApiService()
