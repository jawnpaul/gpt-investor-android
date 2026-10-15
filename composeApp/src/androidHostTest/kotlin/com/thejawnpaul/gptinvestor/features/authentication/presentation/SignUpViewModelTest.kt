package com.thejawnpaul.gptinvestor.features.authentication.presentation

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.EmailAlreadyExistsException
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
class SignUpViewModelTest {

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

    private fun buildViewModel() = SignUpViewModel(authRepository, analyticsLogger)

    private fun SignUpViewModel.fill(name: String, email: String, password: String) {
        handleEvent(SignUpUiEvent.NameChanged(name))
        handleEvent(SignUpUiEvent.EmailChanged(email))
        handleEvent(SignUpUiEvent.PasswordChanged(password))
    }

    @Test
    fun `invalid submit sets errors and does not call the repository`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fill(name = " ", email = "notanemail", password = "abc")

        viewModel.actions.test {
            viewModel.handleEvent(SignUpUiEvent.SignUpClick)
            assertThat(awaitItem()).isEqualTo(SignUpUiAction.InvalidSubmit(firstInvalid = SignUpField.Name))
        }

        val state = viewModel.signUpUiState.value
        assertThat(state.nameError).isTrue()
        assertThat(state.emailError).isTrue()
        assertThat(state.passwordError).isTrue()
        verify { analyticsLogger.logEvent(eventName = "sign-up-button-clicked", params = mapOf()) }
        coVerify(exactly = 0) { authRepository.signUpWithEmailAndPassword(any(), any(), any()) }
    }

    @Test
    fun `invalid submit focuses the first invalid field`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fill(name = "Ada", email = "ada@example.com", password = "abc")

        viewModel.actions.test {
            viewModel.handleEvent(SignUpUiEvent.SignUpClick)
            assertThat(awaitItem()).isEqualTo(SignUpUiAction.InvalidSubmit(firstInvalid = SignUpField.Password))
        }
    }

    @Test
    fun `valid submit calls the repository with the trimmed email`() = runTest {
        coEvery { authRepository.signUpWithEmailAndPassword(any(), any(), any()) } returns Result.success("ok")
        val viewModel = buildViewModel()
        viewModel.fill(name = "Ada", email = " ada@example.com ", password = "investor2026")

        viewModel.handleEvent(SignUpUiEvent.SignUpClick)
        advanceUntilIdle()

        coVerify {
            authRepository.signUpWithEmailAndPassword(
                email = "ada@example.com",
                password = "investor2026",
                name = "Ada"
            )
        }
        assertThat(viewModel.signUpUiState.value.loadingMethod).isNull()
    }

    @Test
    fun `editing a field clears its error`() = runTest {
        val viewModel = buildViewModel()
        viewModel.handleEvent(SignUpUiEvent.SignUpClick)
        assertThat(viewModel.signUpUiState.value.emailError).isTrue()

        viewModel.handleEvent(SignUpUiEvent.EmailChanged("a"))

        val state = viewModel.signUpUiState.value
        assertThat(state.emailError).isFalse()
        assertThat(state.nameError).isTrue()
        assertThat(state.passwordError).isTrue()
    }

    @Test
    fun `leaving a non-empty invalid field shows its error and a blank one stays clear`() = runTest {
        val viewModel = buildViewModel()
        viewModel.handleEvent(SignUpUiEvent.EmailChanged("notanemail"))

        viewModel.handleEvent(SignUpUiEvent.FieldBlurred(SignUpField.Email))
        viewModel.handleEvent(SignUpUiEvent.FieldBlurred(SignUpField.Password))

        val state = viewModel.signUpUiState.value
        assertThat(state.emailError).isTrue()
        assertThat(state.passwordError).isFalse()
    }

    @Test
    fun `email conflict shows the conflict prompt`() = runTest {
        coEvery {
            authRepository.signUpWithEmailAndPassword(any(), any(), any())
        } returns Result.failure(EmailAlreadyExistsException())
        val viewModel = buildViewModel()
        viewModel.fill(name = "Ada", email = "ada@example.com", password = "investor2026")

        viewModel.handleEvent(SignUpUiEvent.SignUpClick)
        advanceUntilIdle()

        val state = viewModel.signUpUiState.value
        assertThat(state.showEmailConflictPrompt).isTrue()
        assertThat(state.loadingMethod).isNull()
    }

    @Test
    fun `google sign up sets the loading method then clears it`() = runTest {
        val result = CompletableDeferred<Result<Unit>>()
        coEvery { authRepository.signUpWithGoogle(platformContext) } coAnswers { result.await() }
        val viewModel = buildViewModel()

        viewModel.handleEvent(SignUpUiEvent.SignUpWithGoogle(platformContext))
        advanceUntilIdle()
        assertThat(viewModel.signUpUiState.value.loadingMethod).isEqualTo(AuthMethod.Google)

        result.complete(Result.success(Unit))
        advanceUntilIdle()
        assertThat(viewModel.signUpUiState.value.loadingMethod).isNull()
    }
}
