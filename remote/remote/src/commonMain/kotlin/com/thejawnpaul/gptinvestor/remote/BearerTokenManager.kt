package com.thejawnpaul.gptinvestor.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

class BearerTokenManager(private val client: HttpClient) {
    fun clearCache() {
        client.authProvider<BearerAuthProvider>()?.clearToken()
    }
}
