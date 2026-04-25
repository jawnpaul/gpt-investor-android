package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
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
                _loginUiState.update { it.copy(email = event.email) }
            }

            is LoginUiEvent.PasswordChanged -> {
                _loginUiState.update { it.copy(password = event.password) }
            }

            LoginUiEvent.LoginClick -> {
                analyticsLogger.logEvent(eventName = "login-button-clicked", params = mapOf())
                loginWithEmailAndPassword()
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
        val email = _loginUiState.value.email
        val password = _loginUiState.value.password
        _loginUiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email = email, password = password).onSuccess {
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun loginWithGoogle(platformContext: PlatformContext) {
        _loginUiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.loginWithGoogle(platformContext).onSuccess {
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun loginWithApple() {
        _loginUiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.loginWithApple().onSuccess {
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast("Login Success"))
                handleAction(action = LoginUiAction.OnGoToHome)
            }.onFailure { failure ->
                _loginUiState.update { it.copy(loading = false) }
                handleAction(action = LoginUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }
}

data class LoginUiState(val email: String = "", val password: String = "", val loading: Boolean = false) {
    val enableButton = email.trim().isNotEmpty() && password.trim().isNotEmpty() && !loading
}

sealed interface LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent
    data class PasswordChanged(val password: String) : LoginUiEvent
    data object LoginClick : LoginUiEvent
    data class LoginWithGoogle(val platformContext: PlatformContext) : LoginUiEvent
    data object LoginWithApple : LoginUiEvent
    data object GoBack : LoginUiEvent
    data object GoToSignUp : LoginUiEvent
}

sealed interface LoginUiAction {
    data class OnShowToast(val message: String) : LoginUiAction
    data object OnGoToSignUp : LoginUiAction
    data object OnGoBack : LoginUiAction
    data object OnGoToHome : LoginUiAction
}
