package com.thejawnpaul.gptinvestor.features.authentication.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences
) :
    ViewModel() {

    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn = _isUserSignedIn.asStateFlow()

    private val _newAuthState = MutableStateFlow(NewAuthenticationUIState())
    val newAuthState get() = _newAuthState

    private val _actions = MutableSharedFlow<AuthenticationAction>()
    val actions get() = _actions

    fun handleEvent(event: AuthenticationEvent) {
        when (event) {
            AuthenticationEvent.GoToLoginScreen -> {
                _newAuthState.update {
                    it.copy(
                        authenticationScreen = AuthenticationScreen.Login,
                        email = "",
                        password = ""
                    )
                }
            }

            AuthenticationEvent.GoToSignUpScreen -> {
                _newAuthState.update {
                    it.copy(
                        authenticationScreen = AuthenticationScreen.SignUp,
                        email = "",
                        password = ""
                    )
                }
            }

            is AuthenticationEvent.Login -> {
                // perform login
                loginWithEmailAndPassword()
            }

            is AuthenticationEvent.SignUp -> {
                // perform sign up
                signUpWithEmailAndPassword()
            }

            is AuthenticationEvent.EmailChanged -> {
                _newAuthState.update { it.copy(email = event.email) }
            }

            is AuthenticationEvent.PasswordChanged -> {
                _newAuthState.update { it.copy(password = event.password) }
            }

            is AuthenticationEvent.SignUpWithGoogle -> {
                loginWithGoogle(event.context)
            }

            is AuthenticationEvent.NameChanged -> {
                _newAuthState.update { it.copy(name = event.name) }
            }
        }
    }

    fun loginWithEmailAndPassword() {
        val email = _newAuthState.value.email
        val password = _newAuthState.value.password
        _newAuthState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            val loginSuccess = authRepository.loginWithEmailAndPassword(email, password)
            if (loginSuccess.isSuccess) {
                _newAuthState.update {
                    it.copy(
                        loading = false,
                        email = "",
                        password = ""
                    )
                }
                _actions.emit(AuthenticationAction.OnLogin("Login Success"))
            } else {
                _actions.emit(AuthenticationAction.OnLogin(loginSuccess.exceptionOrNull()?.message.toString()))
                _newAuthState.update { it.copy(loading = false, errorMessage = "Login failed") }
            }
        }
    }

    private fun signUpWithEmailAndPassword() {
        val email = _newAuthState.value.email
        val password = _newAuthState.value.password
        val name = _newAuthState.value.name
        _newAuthState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            val signUpResponse = authRepository.signUpWithEmailAndPassword(
                email = email,
                password = password,
                name = name
            )
            if (signUpResponse.isSuccess) {
                _newAuthState.update {
                    it.copy(
                        loading = false,
                        email = "",
                        password = ""
                    )
                }
                _actions.emit(AuthenticationAction.OnSignUp("Sign up Success, Please check your email to verify your account"))
            } else {
                _newAuthState.update {
                    it.copy(
                        loading = false,
                        errorMessage = "Sign up failed"
                    )
                }
                _actions.emit(AuthenticationAction.OnSignUp(signUpResponse.exceptionOrNull()?.message.toString()))
            }
        }
    }

    private fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(context)
            if (result.isSuccess) {
                _actions.emit(AuthenticationAction.OnLogin("Login Success"))
            } else {
                _actions.emit(AuthenticationAction.OnLogin(result.exceptionOrNull()?.message ?: "Login failed"))
            }
        }
    }

    fun changeTheme(theme: String) {
        viewModelScope.launch {
            gptInvestorPreferences.setThemePreference(theme)
        }
    }
}

sealed class AuthResult<T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error<T>(val message: String) : AuthResult<T>()
    class Loading<T> : AuthResult<T>()
}

data class DrawerState(
    val user: String? = null,
    val theme: String? = "Dark"
)

data class NewAuthenticationUIState(
    val authenticationScreen: AuthenticationScreen = AuthenticationScreen.Login,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null
) {
    val enableButton = if (authenticationScreen == AuthenticationScreen.Login) {
        email.trim().isNotEmpty() && password.trim().isNotEmpty() && !loading
    } else {
        email.trim().isNotEmpty() && password.trim().isNotEmpty() && name.trim()
            .isNotEmpty() && !loading
    }
}

sealed interface AuthenticationScreen {
    object Login : AuthenticationScreen
    object SignUp : AuthenticationScreen
}

sealed interface AuthenticationEvent {
    data object GoToLoginScreen : AuthenticationEvent
    data object GoToSignUpScreen : AuthenticationEvent
    data object Login : AuthenticationEvent
    data object SignUp : AuthenticationEvent
    data class EmailChanged(val email: String) : AuthenticationEvent
    data class PasswordChanged(val password: String) : AuthenticationEvent
    data class NameChanged(val name: String) : AuthenticationEvent
    data class SignUpWithGoogle(val context: Context) : AuthenticationEvent
}

sealed interface AuthenticationAction {
    data class OnLogin(val message: String) : AuthenticationAction
    data class OnSignUp(val message: String) : AuthenticationAction
}
