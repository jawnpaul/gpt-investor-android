package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.InvalidResetEmailException
import com.thejawnpaul.gptinvestor.features.authentication.domain.TooManyResetRequestsException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthenticationRepository = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(email: String = "ada@example.com") = ForgotPasswordViewModel(
        authRepository = authRepository,
        analyticsLogger = analyticsLogger,
        savedStateHandle = SavedStateHandle(mapOf(FORGOT_PASSWORD_EMAIL_ARG to email))
    )

    @Test
    fun `email is prefilled from the login screen`() = runTest {
        val state = buildViewModel().uiState.value

        assertThat(state.email).isEqualTo("ada@example.com")
        assertThat(state.isSendEnabled).isTrue()
    }

    @Test
    fun `invalid email does not call the repository`() = runTest {
        val viewModel = buildViewModel(email = "ada@")

        viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.emailError).isTrue()
        coVerify(exactly = 0) { authRepository.requestPasswordReset(any()) }
    }

    @Test
    fun `successful request shows the sent state and starts the cooldown`() = runTest {
        coEvery { authRepository.requestPasswordReset(any()) } returns Result.success(Unit)
        val viewModel = buildViewModel()

        viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
        runCurrent()

        val state = viewModel.uiState.value
        assertThat(state.sentTo).isEqualTo("ada@example.com")
        assertThat(state.resendSecondsLeft).isEqualTo(RESEND_COOLDOWN_SECONDS)
        assertThat(state.isResendEnabled).isFalse()
    }

    @Test
    fun `resend is enabled again after the cooldown`() = runTest {
        coEvery { authRepository.requestPasswordReset(any()) } returns Result.success(Unit)
        val viewModel = buildViewModel()
        viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
        runCurrent()

        viewModel.handleEvent(ForgotPasswordUiEvent.ResendClick)
        runCurrent()
        coVerify(exactly = 1) { authRepository.requestPasswordReset(any()) }

        advanceTimeBy(RESEND_COOLDOWN_SECONDS * 1_000L + 1)
        assertThat(viewModel.uiState.value.isResendEnabled).isTrue()

        viewModel.handleEvent(ForgotPasswordUiEvent.ResendClick)
        runCurrent()
        coVerify(exactly = 2) { authRepository.requestPasswordReset("ada@example.com") }
    }

    @Test
    fun `server rejecting the email shows the email error`() = runTest {
        coEvery { authRepository.requestPasswordReset(any()) } returns Result.failure(InvalidResetEmailException())
        val viewModel = buildViewModel()

        viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.emailError).isTrue()
        assertThat(state.sentTo).isNull()
        assertThat(state.sending).isFalse()
    }

    @Test
    fun `rate limit shows the rate limited error until the email changes`() = runTest {
        coEvery {
            authRepository.requestPasswordReset(any())
        } returns Result.failure(TooManyResetRequestsException())
        val viewModel = buildViewModel()

        viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.rateLimited).isTrue()
        assertThat(viewModel.uiState.value.sentTo).isNull()

        viewModel.handleEvent(ForgotPasswordUiEvent.EmailChanged("ada@example.co"))
        assertThat(viewModel.uiState.value.rateLimited).isFalse()
    }

    @Test
    fun `network failure shows a toast`() = runTest {
        coEvery { authRepository.requestPasswordReset(any()) } returns Result.failure(Exception("Offline"))
        val viewModel = buildViewModel()

        viewModel.actions.test {
            viewModel.handleEvent(ForgotPasswordUiEvent.SendClick)
            assertThat(awaitItem()).isEqualTo(ForgotPasswordUiAction.OnShowToast("Offline"))
        }
    }
}
