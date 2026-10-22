package com.thejawnpaul.gptinvestor.remote

import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
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

    private class Backend(
        val onExpiredTokenSent: () -> Unit = {},
        val onGuestLogin: () -> Unit = {},
        val guestLoginStatus: HttpStatusCode = HttpStatusCode.OK,
        val onRefresh: () -> Unit = {}
    )

    private val tokenStorage = InMemoryTokenStorage(storedAccessToken = "expired-guest-token")
    private val unauthorizedCallback = FakeUnauthorizedCallback()
    private val onboardingAuthHeaders = mutableListOf<String?>()
    private lateinit var client: HttpClient

    private fun createClient(backend: Backend) {
        client = KtorClientFactory.create(
            tokenStorage = tokenStorage,
            unauthorizedCallback = unauthorizedCallback,
            engineFactory = mockEngineFactory(backend)
        )
    }

    private fun mockEngineFactory(backend: Backend) = object : HttpClientEngineFactory<HttpClientEngineConfig> {
        override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine = MockEngine { request ->
            handle(request, backend)
        }
    }

    private fun logIn() {
        tokenStorage.saveAccessToken("user-access-token")
        tokenStorage.saveRefreshToken("user-refresh-token")
        BearerTokenManager(client).clearCache()
    }

    private fun signOut() {
        tokenStorage.storedAccessToken = null
        tokenStorage.storedRefreshToken = null
        BearerTokenManager(client).clearCache()
    }

    private fun MockRequestHandleScope.handle(request: HttpRequestData, backend: Backend): HttpResponseData {
        val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
        return when {
            request.url.encodedPath.endsWith("v1/guest-login") -> {
                backend.onGuestLogin()
                respond("""{"access_token":"new-guest-token"}""", backend.guestLoginStatus, jsonHeaders)
            }

            request.url.encodedPath.endsWith("v1.1/refresh") -> {
                backend.onRefresh()
                respond("""{"access_token":"new-user-token"}""", HttpStatusCode.OK, jsonHeaders)
            }

            else -> {
                val authorization = request.headers[HttpHeaders.Authorization]
                onboardingAuthHeaders.add(authorization)
                val error = when (authorization) {
                    "Bearer expired-guest-token" -> "guest_session_expired"
                    "Bearer expired-user-token" -> "token_expired"
                    else -> null
                }
                if (error != null) {
                    backend.onExpiredTokenSent()
                    respond(
                        """{"error":"$error"}""",
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
        createClient(Backend(onGuestLogin = ::logIn))

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
    fun `should keep user token when login finishes before guest expiry is handled`() = runTest {
        createClient(Backend(onExpiredTokenSent = ::logIn))

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-guest-token", "Bearer user-access-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("user-access-token")
        assertThat(unauthorizedCallback.unauthorizedCalls).isEqualTo(0)
    }

    @Test
    fun `should not sign out user when guest refresh fails after login`() = runTest {
        createClient(Backend(onGuestLogin = ::logIn, guestLoginStatus = HttpStatusCode.InternalServerError))

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-guest-token", "Bearer user-access-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("user-access-token")
        assertThat(unauthorizedCallback.unauthorizedCalls).isEqualTo(0)
    }

    @Test
    fun `should report unauthorized when guest refresh fails`() = runTest {
        createClient(Backend(guestLoginStatus = HttpStatusCode.InternalServerError))

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(unauthorizedCallback.unauthorizedCalls).isEqualTo(1)
    }

    @Test
    fun `should save and use new guest token when guest session expires`() = runTest {
        createClient(Backend())

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-guest-token", "Bearer new-guest-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("new-guest-token")
    }

    @Test
    fun `should not restore user token when refresh finishes after sign out`() = runTest {
        tokenStorage.storedAccessToken = "expired-user-token"
        tokenStorage.storedRefreshToken = "user-refresh-token"
        createClient(Backend(onRefresh = ::signOut))

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.Unauthorized)
        assertThat(onboardingAuthHeaders).containsExactly("Bearer expired-user-token")
        assertThat(tokenStorage.storedAccessToken).isNull()
        assertThat(unauthorizedCallback.unauthorizedCalls).isEqualTo(0)
    }

    @Test
    fun `should save and use new user token when access token expires`() = runTest {
        tokenStorage.storedAccessToken = "expired-user-token"
        tokenStorage.storedRefreshToken = "user-refresh-token"
        createClient(Backend())

        val response = client.post("v1/user/onboarding")

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        assertThat(onboardingAuthHeaders)
            .containsExactly("Bearer expired-user-token", "Bearer new-user-token")
            .inOrder()
        assertThat(tokenStorage.storedAccessToken).isEqualTo("new-user-token")
    }
}
