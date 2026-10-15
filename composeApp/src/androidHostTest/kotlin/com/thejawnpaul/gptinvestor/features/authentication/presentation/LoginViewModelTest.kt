package com.thejawnpaul.gptinvestor.features.authentication.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.EmailNotVerifiedException
import com.thejawnpaul.gptinvestor.features.authentication.domain.InvalidCredentialsException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository: AuthenticationRepository = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val platformContext: PlatformContext = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = LoginViewModel(authRepository, analyticsLogger)

    private fun LoginViewModel.fill(email: String, password: String) {
        handleEvent(LoginUiEvent.EmailChanged(email))
        handleEvent(LoginUiEvent.PasswordChanged(password))
    }

    @Test
    fun `invalid submit sets errors and does not call the repository`() = runTest {
        val viewModel = buildViewModel()

        viewModel.actions.test {
            viewModel.handleEvent(LoginUiEvent.LoginClick)
            assertThat(awaitItem()).isEqualTo(LoginUiAction.InvalidSubmit(firstInvalid = LoginField.Email))
        }

        val state = viewModel.loginUiState.value
        assertThat(state.emailError).isTrue()
        assertThat(state.passwordError).isTrue()
        verify { analyticsLogger.logEvent(eventName = "login-button-clicked", params = mapOf()) }
        coVerify(exactly = 0) { authRepository.loginWithEmailAndPassword(any(), any()) }
    }

    @Test
    fun `invalid submit focuses the password when only it is empty`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fill(email = "ada@example.com", password = "")

        viewModel.actions.test {
            viewModel.handleEvent(LoginUiEvent.LoginClick)
            assertThat(awaitItem()).isEqualTo(LoginUiAction.InvalidSubmit(firstInvalid = LoginField.Password))
        }
    }

    @Test
    fun `typing validates the field on every keystroke`() = runTest {
        val viewModel = buildViewModel()

        viewModel.handleEvent(LoginUiEvent.EmailChanged("a"))
        assertThat(viewModel.loginUiState.value.emailError).isTrue()

        viewModel.handleEvent(LoginUiEvent.EmailChanged("a@b.co"))
        assertThat(viewModel.loginUiState.value.emailError).isFalse()
        assertThat(viewModel.loginUiState.value.passwordError).isFalse()
    }

    @Test
    fun `login is enabled only when the email is valid and the password is not blank`() = runTest {
        val viewModel = buildViewModel()
        assertThat(viewModel.loginUiState.value.isLoginEnabled).isFalse()

        viewModel.fill(email = "ada@example", password = "secret")
        assertThat(viewModel.loginUiState.value.isLoginEnabled).isFalse()

        viewModel.handleEvent(LoginUiEvent.EmailChanged("ada@example.com"))
        assertThat(viewModel.loginUiState.value.isLoginEnabled).isTrue()

        viewModel.handleEvent(LoginUiEvent.PasswordChanged(" "))
        assertThat(viewModel.loginUiState.value.isLoginEnabled).isFalse()
    }

    @Test
    fun `valid submit calls the repository with the trimmed email`() = runTest {
        coEvery { authRepository.loginWithEmailAndPassword(any(), any()) } returns Result.success("ok")
        val viewModel = buildViewModel()
        viewModel.fill(email = " ada@example.com ", password = "secret")

        viewModel.handleEvent(LoginUiEvent.LoginClick)
        advanceUntilIdle()

        coVerify { authRepository.loginWithEmailAndPassword(email = "ada@example.com", password = "secret") }
        assertThat(viewModel.loginUiState.value.loadingMethod).isNull()
    }

    @Test
    fun `invalid credentials show the inline error without a toast`() = runTest {
        coEvery {
            authRepository.loginWithEmailAndPassword(any(), any())
        } returns Result.failure(InvalidCredentialsException())
        val viewModel = buildViewModel()
        viewModel.fill(email = "ada@example.com", password = "wrong")

        viewModel.actions.test {
            viewModel.handleEvent(LoginUiEvent.LoginClick)
            advanceUntilIdle()
            expectNoEvents()
        }

        val state = viewModel.loginUiState.value
        assertThat(state.showInvalidCredentials).isTrue()
        assertThat(state.loadingMethod).isNull()
    }

    @Test
    fun `editing either field clears the invalid credentials error`() = runTest {
        coEvery {
            authRepository.loginWithEmailAndPassword(any(), any())
        } returns Result.failure(InvalidCredentialsException())
        val viewModel = buildViewModel()
        viewModel.fill(email = "ada@example.com", password = "wrong")
        viewModel.handleEvent(LoginUiEvent.LoginClick)
        advanceUntilIdle()

        viewModel.handleEvent(LoginUiEvent.PasswordChanged("wrong2"))
        assertThat(viewModel.loginUiState.value.showInvalidCredentials).isFalse()

        viewModel.handleEvent(LoginUiEvent.LoginClick)
        advanceUntilIdle()
        viewModel.handleEvent(LoginUiEvent.EmailChanged("ada@example.co"))
        assertThat(viewModel.loginUiState.value.showInvalidCredentials).isFalse()
    }

    @Test
    fun `unverified email shows the verify banner`() = runTest {
        coEvery {
            authRepository.loginWithEmailAndPassword(any(), any())
        } returns Result.failure(EmailNotVerifiedException())
        val viewModel = buildViewModel()
        viewModel.fill(email = "ada@example.com", password = "secret")

        viewModel.handleEvent(LoginUiEvent.LoginClick)
        advanceUntilIdle()

        assertThat(viewModel.loginUiState.value.showVerifyEmailPrompt).isTrue()
        assertThat(viewModel.loginUiState.value.showInvalidCredentials).isFalse()
    }

    @Test
    fun `other failures show a toast`() = runTest {
        coEvery {
            authRepository.loginWithEmailAndPassword(any(), any())
        } returns Result.failure(Exception("Server down"))
        val viewModel = buildViewModel()
        viewModel.fill(email = "ada@example.com", password = "secret")

        viewModel.actions.test {
            viewModel.handleEvent(LoginUiEvent.LoginClick)
            assertThat(awaitItem()).isEqualTo(LoginUiAction.OnShowToast("Server down"))
        }
    }

    @Test
    fun `google login sets the loading method then clears it`() = runTest {
        val result = CompletableDeferred<Result<Unit>>()
        coEvery { authRepository.loginWithGoogle(platformContext) } coAnswers { result.await() }
        val viewModel = buildViewModel()

        viewModel.handleEvent(LoginUiEvent.LoginWithGoogle(platformContext))
        advanceUntilIdle()
        assertThat(viewModel.loginUiState.value.loadingMethod).isEqualTo(AuthMethod.Google)
        assertThat(viewModel.loginUiState.value.isLoginEnabled).isFalse()

        result.complete(Result.success(Unit))
        advanceUntilIdle()
        assertThat(viewModel.loginUiState.value.loadingMethod).isNull()
    }

    @Test
    fun `forgot password opens reset with the typed email`() = runTest {
        val viewModel = buildViewModel()
        viewModel.handleEvent(LoginUiEvent.EmailChanged(" ada@example.com "))

        viewModel.actions.test {
            viewModel.handleEvent(LoginUiEvent.ForgotPasswordClick)
            assertThat(awaitItem()).isEqualTo(LoginUiAction.OnGoToForgotPassword(email = "ada@example.com"))
        }
    }
}
