package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.EmailAlreadyExistsException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class SignUpViewModel(
    private val authRepository: AuthenticationRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {
    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    private val _actions = MutableSharedFlow<SignUpUiAction>()
    val actions get() = _actions

    fun handleEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.EmailChanged -> {
                _signUpUiState.update {
                    it.copy(email = event.email, emailError = false)
                }
            }

            SignUpUiEvent.GoBack -> {
                handleAction(action = SignUpUiAction.OnGoBack)
            }

            SignUpUiEvent.GoToLogin -> {
                handleAction(action = SignUpUiAction.OnGoToLogin)
            }

            is SignUpUiEvent.NameChanged -> {
                _signUpUiState.update {
                    it.copy(name = event.name, nameError = false)
                }
            }

            is SignUpUiEvent.PasswordChanged -> {
                _signUpUiState.update {
                    it.copy(password = event.password, passwordError = false)
                }
            }

            is SignUpUiEvent.FieldBlurred -> {
                _signUpUiState.update {
                    if (it.valueOf(event.field).isBlank()) it else it.withError(event.field, it.isInvalid(event.field))
                }
            }

            SignUpUiEvent.SignUpClick -> {
                if (_signUpUiState.value.loadingMethod != null) return
                analyticsLogger.logEvent(eventName = "sign-up-button-clicked", params = mapOf())
                val invalidFields = SignUpField.entries.filter { _signUpUiState.value.isInvalid(it) }
                if (invalidFields.isEmpty()) {
                    signUpWithEmailAndPassword()
                } else {
                    _signUpUiState.update { state ->
                        SignUpField.entries.fold(state) { acc, field -> acc.withError(field, field in invalidFields) }
                    }
                    handleAction(action = SignUpUiAction.InvalidSubmit(firstInvalid = invalidFields.first()))
                }
            }

            is SignUpUiEvent.SignUpWithGoogle -> {
                analyticsLogger.logEvent(eventName = "google-sign-up-button-clicked", params = mapOf())
                signUpWithGoogle(event.platformContext)
            }
            SignUpUiEvent.SingUpWithApple -> {
                analyticsLogger.logEvent(eventName = "apple-sign-up-button-clicked", params = mapOf())
                signUpWithApple()
            }
        }
    }

    private fun signUpWithEmailAndPassword() {
        val email = _signUpUiState.value.email.trim()
        val password = _signUpUiState.value.password
        val name = _signUpUiState.value.name
        _signUpUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Email,
                showEmailConflictPrompt = false
            )
        }

        viewModelScope.launch {
            authRepository.signUpWithEmailAndPassword(
                email = email,
                password = password,
                name = name
            ).onSuccess {
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                handleAction(
                    action = SignUpUiAction.OnShowToast(
                        "Sign up Success, Please check your email to verify your account"
                    )
                )
                handleAction(action = SignUpUiAction.OnGoToLogin)
            }.onFailure { failure ->
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                if (failure is EmailAlreadyExistsException) {
                    _signUpUiState.update { it.copy(showEmailConflictPrompt = true) }
                } else {
                    handleAction(action = SignUpUiAction.OnShowToast(failure.message.toString()))
                }
            }
        }
    }

    private fun signUpWithGoogle(platformContext: PlatformContext) {
        _signUpUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Google
            )
        }
        viewModelScope.launch {
            authRepository.signUpWithGoogle(platformContext).onSuccess {
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast("Sign up Success"))
                handleAction(action = SignUpUiAction.OnGoToHome)
            }.onFailure { failure ->
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun signUpWithApple() {
        _signUpUiState.update {
            it.copy(
                loadingMethod = AuthMethod.Apple
            )
        }
        viewModelScope.launch {
            authRepository.signUpWithApple().onSuccess {
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast("Sign up Success"))
                handleAction(action = SignUpUiAction.OnGoToHome)
            }.onFailure { failure ->
                _signUpUiState.update {
                    it.copy(
                        loadingMethod = null
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun handleAction(action: SignUpUiAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

private const val MIN_PASSWORD_LENGTH = 8

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val loadingMethod: AuthMethod? = null,
    val showEmailConflictPrompt: Boolean = false,
    val nameError: Boolean = false,
    val emailError: Boolean = false,
    val passwordError: Boolean = false
) {
    val isSignUpEnabled: Boolean
        get() = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && isAvailable(AuthMethod.Email)

    val isGoogleEnabled: Boolean
        get() = isAvailable(AuthMethod.Google)

    val isAppleEnabled: Boolean
        get() = isAvailable(AuthMethod.Apple)

    private fun isAvailable(method: AuthMethod): Boolean = loadingMethod == null || loadingMethod == method
}

enum class SignUpField { Name, Email, Password }

private fun SignUpUiState.valueOf(field: SignUpField): String = when (field) {
    SignUpField.Name -> name
    SignUpField.Email -> email
    SignUpField.Password -> password
}

private fun SignUpUiState.isInvalid(field: SignUpField): Boolean = when (field) {
    SignUpField.Name -> name.isBlank()
    SignUpField.Email -> !isValidEmail(email)
    SignUpField.Password -> password.length < MIN_PASSWORD_LENGTH
}

private fun SignUpUiState.withError(field: SignUpField, error: Boolean): SignUpUiState = when (field) {
    SignUpField.Name -> copy(nameError = error)
    SignUpField.Email -> copy(emailError = error)
    SignUpField.Password -> copy(passwordError = error)
}

sealed interface SignUpUiEvent {
    data class EmailChanged(val email: String) : SignUpUiEvent
    data class PasswordChanged(val password: String) : SignUpUiEvent
    data class NameChanged(val name: String) : SignUpUiEvent
    data class FieldBlurred(val field: SignUpField) : SignUpUiEvent
    data object SignUpClick : SignUpUiEvent
    data object GoBack : SignUpUiEvent
    data object GoToLogin : SignUpUiEvent
    data class SignUpWithGoogle(val platformContext: PlatformContext) : SignUpUiEvent
    data object SingUpWithApple : SignUpUiEvent
}

sealed interface SignUpUiAction {
    data class OnShowToast(val message: String) : SignUpUiAction
    data object OnGoToLogin : SignUpUiAction
    data object OnGoBack : SignUpUiAction
    data object OnGoToHome : SignUpUiAction
    data class InvalidSubmit(val firstInvalid: SignUpField) : SignUpUiAction
}
