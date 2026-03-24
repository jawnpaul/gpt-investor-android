package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SignUpViewModel(private val authRepository: AuthenticationRepository) : ViewModel() {
    private val _signUpUiState = MutableStateFlow(SignUpUiState())
    val signUpUiState = _signUpUiState.asStateFlow()

    private val _actions = MutableSharedFlow<SignUpUiAction>()
    val actions get() = _actions

    fun handleEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.EmailChanged -> {
                _signUpUiState.update {
                    it.copy(email = event.email)
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
                    it.copy(name = event.name)
                }
            }

            is SignUpUiEvent.PasswordChanged -> {
                _signUpUiState.update {
                    it.copy(password = event.password)
                }
            }

            SignUpUiEvent.SignUpClick -> {
                signUpWithEmailAndPassword()
            }

            is SignUpUiEvent.SignUpWithGoogle -> {
                signUpWithGoogle(event.platformContext)
            }
        }
    }

    private fun signUpWithEmailAndPassword() {
        val email = _signUpUiState.value.email
        val password = _signUpUiState.value.password
        val name = _signUpUiState.value.name
        _signUpUiState.update {
            it.copy(
                loading = true
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
                        loading = false
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
                        loading = false
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast(failure.message.toString()))
            }
        }
    }

    private fun signUpWithGoogle(platformContext: PlatformContext) {
        _signUpUiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.signUpWithGoogle(platformContext).onSuccess {
                _signUpUiState.update {
                    it.copy(
                        loading = false
                    )
                }
                handleAction(action = SignUpUiAction.OnShowToast("Sign up Success"))
                handleAction(action = SignUpUiAction.OnGoToHome)
            }.onFailure { failure ->
                _signUpUiState.update {
                    it.copy(
                        loading = false
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

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val loading: Boolean = false
) {
    val enableButton = email.trim().isNotEmpty() &&
        password.trim().isNotEmpty() &&
        name.trim()
            .isNotEmpty() &&
        !loading
}

sealed interface SignUpUiEvent {
    data class EmailChanged(val email: String) : SignUpUiEvent
    data class PasswordChanged(val password: String) : SignUpUiEvent
    data class NameChanged(val name: String) : SignUpUiEvent
    data object SignUpClick : SignUpUiEvent
    data object GoBack : SignUpUiEvent
    data object GoToLogin : SignUpUiEvent
    data class SignUpWithGoogle(val platformContext: PlatformContext) : SignUpUiEvent
}

sealed interface SignUpUiAction {
    data class OnShowToast(val message: String) : SignUpUiAction
    data object OnGoToLogin : SignUpUiAction
    data object OnGoBack : SignUpUiAction
    data object OnGoToHome : SignUpUiAction
}
