package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.EmailNotVerifiedException
import com.thejawnpaul.gptinvestor.features.authentication.domain.InvalidCredentialsException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class LoginViewModel(
    private val authRepository: AuthenticationRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    private val _actions = MutableSharedFlow<LoginUiAction>()
    val actions get() = _actions

    fun handleEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _loginUiState.update {
                    it.copy(email = event.email, showInvalidCredentials = false).validated(LoginField.Email)
                }
            }

            is LoginUiEvent.PasswordChanged -> {
                _loginUiState.update {
                    it.copy(password = event.password, showInvalidCredentials = false).validated(LoginField.Password)
                }
            }

            LoginUiEvent.LoginClick -> {
                if (_loginUiState.value.loadingMethod != null) return
                analyticsLogger.logEvent(eventName = "login-button-clicked", params = mapOf())
                val invalidFields = LoginField.entries.filter { _loginUiState.value.isInvalid(it) }
                if (invalidFields.isEmpty()) {
                    loginWithEmailAndPassword()
                } else {
                    _loginUiState.update { state ->
                        LoginField.entries.fold(state) { acc, field -> acc.withError(field, field in invalidFields) }
                    }
                    handleAction(action = LoginUiAction.InvalidSubmit(firstInvalid = invalidFields.first()))
                }
            }

            LoginUiEvent.ForgotPasswordClick -> {
                analyticsLogger.logEvent(eventName = "forgot-password-clicked", params = mapOf())
                handleAction(action = LoginUiAction.OnGoToForgotPassword(email = _loginUiState.value.email.trim()))
            }

            is LoginUiEvent.LoginWithGoogle -> {
                analyticsLogger.logEvent(eventName = "google-login-button-clicked", params = mapOf())
                loginWithGoogle(event.platformContext)
            }

            LoginUiEvent.LoginWithApple -> {
                analyticsLogger.logEvent(eventName = "apple-login-button-clicked", params = mapOf())
                loginWithApple()
            }

            LoginUiEvent.GoBack -> {
                handleAction(action = LoginUiAction.OnGoBack)
            }

            LoginUiEvent.GoToSignUp -> {
                handleAction(action = LoginUiAction.OnGoToSignUp)
            }
        }
    }

    private fun handleAction(action: LoginUiAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    private fun loginWithEmailAndPassword() {
        val email = _loginUiState.value.email.trim()
        val password = _loginUiState.value.password
        _loginUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Email,
                showVerifyEmailPrompt = false,
                showInvalidCredentials = false
            )
        }
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email = email, password = password).onSuccess {
                _loginUiState.update { it.copy(loadingMethod = null) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loadingMethod = null) }
                when (failure) {
                    is EmailNotVerifiedException -> _loginUiState.update { it.copy(showVerifyEmailPrompt = true) }
                    is InvalidCredentialsException -> _loginUiState.update { it.copy(showInvalidCredentials = true) }
                    else -> handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
                }
            }
        }
    }

    private fun loginWithGoogle(platformContext: PlatformContext) {
        _loginUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Google
            )
        }
        viewModelScope.launch {
            authRepository.loginWithGoogle(platformContext).onSuccess {
                _loginUiState.update { it.copy(loadingMethod = null) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loadingMethod = null) }
                handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun loginWithApple() {
        _loginUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Apple
            )
        }
        viewModelScope.launch {
            authRepository.loginWithApple().onSuccess {
                _loginUiState.update { it.copy(loadingMethod = null) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loadingMethod = null) }
                handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loadingMethod: AuthMethod? = null,
    val showVerifyEmailPrompt: Boolean = false,
    val showInvalidCredentials: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false
) {
    val isLoginEnabled: Boolean
        get() = LoginField.entries.none { isInvalid(it) } && isAvailable(AuthMethod.Email)

    val isGoogleEnabled: Boolean
        get() = isAvailable(AuthMethod.Google)

    val isAppleEnabled: Boolean
        get() = isAvailable(AuthMethod.Apple)

    private fun isAvailable(method: AuthMethod): Boolean = loadingMethod == null || loadingMethod == method
}

enum class LoginField { Email, Password }

private fun LoginUiState.isInvalid(field: LoginField): Boolean = when (field) {
    LoginField.Email -> !isValidEmail(email)
    LoginField.Password -> password.isBlank()
}

private fun LoginUiState.validated(field: LoginField): LoginUiState = withError(field, isInvalid(field))

private fun LoginUiState.withError(field: LoginField, error: Boolean): LoginUiState = when (field) {
    LoginField.Email -> copy(emailError = error)
    LoginField.Password -> copy(passwordError = error)
}

sealed interface LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent
    data class PasswordChanged(val password: String) : LoginUiEvent
    data object LoginClick : LoginUiEvent
    data class LoginWithGoogle(val platformContext: PlatformContext) : LoginUiEvent
    data object LoginWithApple : LoginUiEvent
    data object GoBack : LoginUiEvent
    data object GoToSignUp : LoginUiEvent
    data object ForgotPasswordClick : LoginUiEvent
}

sealed interface LoginUiAction {
    data class OnShowToast(val message: String) : LoginUiAction
    data object OnGoToSignUp : LoginUiAction
    data object OnGoBack : LoginUiAction
    data object OnGoToHome : LoginUiAction
    data class OnGoToForgotPassword(val email: String) : LoginUiAction
    data class InvalidSubmit(val firstInvalid: LoginField) : LoginUiAction
}
