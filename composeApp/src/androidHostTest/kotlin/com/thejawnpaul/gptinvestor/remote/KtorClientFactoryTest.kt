package com.thejawnpaul.gptinvestor.remote

import com.google.common.truth.Truth.assertThat
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class KtorClientFactoryTest {

    private class InMemoryTokenStorage(var storedAccessToken: String?, var storedRefreshToken: String? = null) :
        TokenStorage {
        override fun getAccessToken(): String? = storedAccessToken
        override fun getRefreshToken(): String? = storedRefreshToken
        override fun saveAccessToken(token: String) {
            storedAccessToken = token
        }
        override fun saveRefreshToken(token: String) {
            storedRefreshToken = token
        }
        override fun replaceAccessToken(expected: String?, token: String): Boolean {
            if (storedAccessToken != expected) return false
            storedAccessToken = token
            return true
        }
    }

    private class FakeUnauthorizedCallback : UnauthorizedCallback {
        var unauthorizedCalls = 0
        override fun onUnauthorized() {
            unauthorizedCalls++
        }
        override suspend fun guestUniqueId(): String = "installation-id"
    }

    private val tokenStorage = InMemoryTokenStorage(storedAccessToken = "expired-guest-token")
    private val unauthorizedCallback = FakeUnauthorizedCallback()
    private val onboardingAuthHeaders = mutableListOf<String?>()

    private fun mockEngineFactory(onGuestLogin: () -> Unit) = object : HttpClientEngineFactory<HttpClientEngineConfig> {
        override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine = MockEngine { request ->
            handle(request, onGuestLogin)
        }
    }

    private fun MockRequestHandleScope.handle(request: HttpRequestData, onGuestLogin: () -> Unit): HttpResponseData {
        val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
        return when {
            request.url.encodedPath.endsWith("v1/guest-login") -> {
                onGuestLogin()
                respond("""{"access_token":"new-guest-token"}""", HttpStatusCode.OK, jsonHeaders)
            }

            else -> {
                val authorization = request.headers[HttpHeaders.Authorization]
                onboardingAuthHeaders.add(authorization)
                if (authorization == "Bearer expired-guest-token") {
                    respond(
                        """{"error":"guest_session_expired"}""",
                        HttpStatusCode.Unauthorized,
                        headersOf(
                            HttpHeaders.ContentType to listOf("application/json"),
                            HttpHeaders.WWWAuthenticate to listOf("Bearer")
                        )
                    )
                } else {
                    respond("""{"message":"ok"}""", HttpStatusCode.OK, jsonHeaders)
                }
            }
        }
    }

    @Test
    fun `should retry with user token when guest refresh finishes after login`() = runTest {
        val client = KtorClientFactory.create(
            tokenStorage = tokenStorage,
            unauthorizedCallback = unauthorizedCallback,
            engineFactory = mockEngineFactory(
                onGuestLogin = {
                    tokenStorage.saveAccessToken("user-access-token")
                    tokenStorage.saveRefreshToken("user-refresh-token")
                }
            )
        )

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-guest-token", "Bearer user-access-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("user-access-token")
        assertThat(tokenStorage.storedRefreshToken).isEqualTo("user-refresh-token")
        assertThat(unauthorizedCallback.unauthorizedCalls).isEqualTo(0)
    }

    @Test
    fun `should save and use new guest token when guest session expires`() = runTest {
        val client = KtorClientFactory.create(
            tokenStorage = tokenStorage,
            unauthorizedCallback = unauthorizedCallback,
            engineFactory = mockEngineFactory(onGuestLogin = {})
        )

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-guest-token", "Bearer new-guest-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("new-guest-token")
    }
}
