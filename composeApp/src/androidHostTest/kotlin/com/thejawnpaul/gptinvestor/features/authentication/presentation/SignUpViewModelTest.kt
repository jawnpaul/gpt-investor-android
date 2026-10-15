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
    fun `fields show no errors before anything is typed`() = runTest {
        val state = buildViewModel().signUpUiState.value

        assertThat(state.nameError).isFalse()
        assertThat(state.emailError).isFalse()
        assertThat(state.passwordError).isFalse()
    }

    @Test
    fun `typing validates the field on every keystroke`() = runTest {
        val viewModel = buildViewModel()

        viewModel.handleEvent(SignUpUiEvent.EmailChanged("a"))
        assertThat(viewModel.signUpUiState.value.emailError).isTrue()

        viewModel.handleEvent(SignUpUiEvent.EmailChanged("a@b.co"))
        assertThat(viewModel.signUpUiState.value.emailError).isFalse()

        viewModel.handleEvent(SignUpUiEvent.PasswordChanged("investor"))
        assertThat(viewModel.signUpUiState.value.passwordError).isFalse()

        viewModel.handleEvent(SignUpUiEvent.PasswordChanged("investo"))
        assertThat(viewModel.signUpUiState.value.passwordError).isTrue()
    }

    @Test
    fun `typing in one field leaves the others untouched`() = runTest {
        val viewModel = buildViewModel()

        viewModel.handleEvent(SignUpUiEvent.NameChanged("Ada"))

        val state = viewModel.signUpUiState.value
        assertThat(state.nameError).isFalse()
        assertThat(state.emailError).isFalse()
        assertThat(state.passwordError).isFalse()
    }

    @Test
    fun `clearing a field shows its error`() = runTest {
        val viewModel = buildViewModel()
        viewModel.handleEvent(SignUpUiEvent.NameChanged("Ada"))

        viewModel.handleEvent(SignUpUiEvent.NameChanged(""))

        assertThat(viewModel.signUpUiState.value.nameError).isTrue()
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

    @Test
    fun `sign up is enabled only when every field is valid`() = runTest {
        val viewModel = buildViewModel()
        assertThat(viewModel.signUpUiState.value.isSignUpEnabled).isFalse()

        viewModel.fill(name = "Ada", email = "ada@example", password = "investor2026")
        assertThat(viewModel.signUpUiState.value.isSignUpEnabled).isFalse()

        viewModel.handleEvent(SignUpUiEvent.EmailChanged("ada@example.com"))
        viewModel.handleEvent(SignUpUiEvent.PasswordChanged("abc"))
        assertThat(viewModel.signUpUiState.value.isSignUpEnabled).isFalse()

        viewModel.handleEvent(SignUpUiEvent.PasswordChanged("investor2026"))
        assertThat(viewModel.signUpUiState.value.isSignUpEnabled).isTrue()
    }

    @Test
    fun `while google loads only the google button stays enabled`() = runTest {
        val state =
            SignUpUiState(
                name = "Ada",
                email = "ada@example.com",
                password = "investor2026",
                loadingMethod = AuthMethod.Google
            )

        assertThat(state.isGoogleEnabled).isTrue()
        assertThat(state.isSignUpEnabled).isFalse()
        assertThat(state.isAppleEnabled).isFalse()
    }
}
