package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.platform.AppConfig
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.LoginResponse
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.BearerTokenManager
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthenticationRepositoryImplTest {

    @MockK
    lateinit var analyticsLogger: AnalyticsLogger

    @MockK
    lateinit var gptInvestorPreferences: AppPreferences

    @MockK
    lateinit var tokenSyncManager: TokenSyncManager

    @MockK
    lateinit var apiService: KtorApiService

    @MockK
    lateinit var tokenStorage: TokenStorage

    @MockK
    lateinit var bearerTokenManager: BearerTokenManager

    @MockK
    lateinit var appConfig: AppConfig

    private lateinit var repository: AuthenticationRepositoryImpl

    private val loginResponse = LoginResponse(
        accessToken = "new-access-token",
        refreshToken = "new-refresh-token",
        message = "Login successful",
        user = User(uid = "uid-123", email = "user@test.com", name = "Test User")
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AuthenticationRepositoryImpl(
            analyticsLogger = analyticsLogger,
            gptInvestorPreferences = gptInvestorPreferences,
            tokenSyncManager = tokenSyncManager,
            apiService = apiService,
            tokenStorage = tokenStorage,
            bearerTokenManager = bearerTokenManager,
            appConfig = appConfig
        )
    }

    private fun stubSuccessfulLogin() {
        coEvery { apiService.loginWithEmailAndPassword(any()) } returns KtorResponse(
            isSuccessful = true,
            body = loginResponse,
            errorBody = null,
            code = 200
        )
        coJustRun { gptInvestorPreferences.setUserId(any()) }
        coJustRun { gptInvestorPreferences.setIsUserLoggedIn(any()) }
        coJustRun { gptInvestorPreferences.setUserName(any()) }
        justRun { tokenSyncManager.syncToken() }
        justRun { tokenStorage.saveAccessToken(any()) }
        justRun { tokenStorage.saveRefreshToken(any()) }
        justRun { bearerTokenManager.clearCache() }
        coJustRun { gptInvestorPreferences.clearIsGuestLoggedIn() }
        justRun { analyticsLogger.identifyUser(any(), any()) }
    }

    @Test
    fun `should clear bearer token cache after successful email login`() = runTest {
        stubSuccessfulLogin()

        val result = repository.loginWithEmailAndPassword("user@test.com", "password")

        assertThat(result.isSuccess).isTrue()
        verify(exactly = 1) { bearerTokenManager.clearCache() }
    }

    @Test
    fun `should not clear bearer token cache when email login fails`() = runTest {
        coEvery { apiService.loginWithEmailAndPassword(any()) } returns KtorResponse(
            isSuccessful = false,
            body = null,
            errorBody = null,
            code = 401
        )

        val result = repository.loginWithEmailAndPassword("user@test.com", "wrongpassword")

        assertThat(result.isFailure).isTrue()
        verify(exactly = 0) { bearerTokenManager.clearCache() }
    }

    @Test
    fun `should save new access and refresh tokens after successful email login`() = runTest {
        stubSuccessfulLogin()

        repository.loginWithEmailAndPassword("user@test.com", "password")

        verify(exactly = 1) { tokenStorage.saveAccessToken("new-access-token") }
        verify(exactly = 1) { tokenStorage.saveRefreshToken("new-refresh-token") }
    }

    @Test
    fun `should clear bearer token cache before clearing guest session after successful email login`() = runTest {
        val callOrder = mutableListOf<String>()
        coEvery { apiService.loginWithEmailAndPassword(any()) } returns KtorResponse(
            isSuccessful = true,
            body = loginResponse,
            errorBody = null,
            code = 200
        )
        coJustRun { gptInvestorPreferences.setUserId(any()) }
        coJustRun { gptInvestorPreferences.setIsUserLoggedIn(any()) }
        coJustRun { gptInvestorPreferences.setUserName(any()) }
        justRun { tokenSyncManager.syncToken() }
        justRun { tokenStorage.saveAccessToken(any()) }
        justRun { tokenStorage.saveRefreshToken(any()) }
        every { bearerTokenManager.clearCache() } answers { callOrder.add("clearCache") }
        coEvery { gptInvestorPreferences.clearIsGuestLoggedIn() } coAnswers { callOrder.add("clearGuest") }
        justRun { analyticsLogger.identifyUser(any(), any()) }

        repository.loginWithEmailAndPassword("user@test.com", "password")

        assertThat(callOrder).containsExactly("clearCache", "clearGuest").inOrder()
    }

    @Test
    fun `should return failure when email login throws exception`() = runTest {
        coEvery { apiService.loginWithEmailAndPassword(any()) } throws RuntimeException("Network error")

        val result = repository.loginWithEmailAndPassword("user@test.com", "password")

        assertThat(result.isFailure).isTrue()
        verify(exactly = 0) { bearerTokenManager.clearCache() }
    }

    @Test
    fun `should return EmailNotVerifiedException when server returns 403`() = runTest {
        coEvery { apiService.loginWithEmailAndPassword(any()) } returns KtorResponse(
            isSuccessful = false,
            body = null,
            errorBody = null,
            code = 403
        )

        val result = repository.loginWithEmailAndPassword("user@test.com", "password")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isInstanceOf(EmailNotVerifiedException::class.java)
        verify(exactly = 0) { bearerTokenManager.clearCache() }
    }
}
