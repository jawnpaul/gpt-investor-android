package com.thejawnpaul.gptinvestor.features.authentication.presentation

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AuthenticationViewModel(
    private val authRepository: AuthenticationRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val gptInvestorPreferences: GPTInvestorPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthenticationUIState())
    val authState = _authState.asStateFlow()

    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn = _isUserSignedIn.asStateFlow()

    private val _newAuthState = MutableStateFlow(NewAuthenticationUIState())
    val newAuthState get() = _newAuthState

    private val _actions = MutableSharedFlow<AuthenticationAction>()
    val actions get() = _actions

    init {
        viewModelScope.launch {
            authRepository.getAuthState().collect { isSignedIn ->
                _authState.update {
                    it.copy(isUserSignedIn = isSignedIn)
//                        user = authRepository.currentUser
//                    )
                }
            }
        }

        viewModelScope.launch {
            gptInvestorPreferences.themePreference.collect { theme ->
                _authState.update { it.copy(theme = theme) }
            }
        }
    }

    fun signIn() {
        viewModelScope.launch {
            _authState.update { it.copy(loading = true) }
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
//            authRepository.signOut(context)
        }
    }

    fun handleSignInResult(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                _authState.update { it.copy(loading = false /*, user = authRepository.currentUser*/) }
                analyticsLogger.identifyUser(eventName = "Sign Up", params = mapOf())
//                        "user_id" to authRepository.currentUser?.uid.toString(),
//                        "email" to authRepository.currentUser?.email.toString(),
//                        "name" to authRepository.currentUser?.displayName.toString(),
//                        "sign_up_method" to authRepository.currentUser?.providerId.toString()
//                    )
//                )
            }

            else -> {
                _authState.update { it.copy(loading = false, errorMessage = "Sign in failed") }
            }
        }
    }

    fun login() {
        val email = _authState.value.email
        val password = _authState.value.password
        _authState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.loginWithEmailAndPassword(email, password).collect { isSuccessful ->
                if (isSuccessful) {
                    _authState.update {
                        it.copy(
                            loading = false,
                            /*user = authRepository.currentUser*/
                        )
                    }
                } else {
                    _authState.update { it.copy(loading = false, errorMessage = "Login failed") }
                }
            }
        }
    }

    fun showLoginInput() {
        _authState.update { it.copy(showLoginInput = true) }
    }

    fun updateEmail(email: String) {
        _authState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _authState.update { it.copy(password = password) }
    }

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

            is AuthenticationEvent.LoginWithGoogle -> {
                loginWithGoogle(event.context)
            }

            is AuthenticationEvent.SignUpWithGoogle -> {
                signUpWithGoogle(event.context)
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
            authRepository.loginWithEmailAndPassword(email, password).collect { isSuccessful ->
                if (isSuccessful) {
                    _newAuthState.update {
                        it.copy(
                            loading = false,
                            email = "",
                            password = ""
                        )
                    }
                    _actions.emit(AuthenticationAction.OnLogin("Login Success"))
                } else {
                    _actions.emit(AuthenticationAction.OnLogin("Login failed"))
                    _newAuthState.update { it.copy(loading = false, errorMessage = "Login failed") }
                }
            }
        }
    }

    fun signUpWithEmailAndPassword() {
        val email = _newAuthState.value.email
        val password = _newAuthState.value.password
        _newAuthState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch {
            authRepository.signUpWithEmailAndPassword(email, password).collect { isSuccessful ->
                if (isSuccessful) {
                    _newAuthState.update {
                        it.copy(
                            loading = false,
                            email = "",
                            password = ""
                        )
                    }
                    _actions.emit(AuthenticationAction.OnSignUp("Sign up Success"))
                } else {
                    _newAuthState.update {
                        it.copy(
                            loading = false,
                            errorMessage = "Sign up failed"
                        )
                    }
                    _actions.emit(AuthenticationAction.OnSignUp("Sign up failed"))
                }
            }
        }
    }

    private fun signUpWithGoogle(context: Context) {
        viewModelScope.launch {
//            authRepository.signUp(context).collect {
//                if (it) {
//                    _actions.emit(AuthenticationAction.OnSignUp("Sign up Success"))
//                } else {
//                    _actions.emit(AuthenticationAction.OnSignUp("Sign up failed"))
//                }
//            }
        }
    }

    private fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
//            authRepository.loginWithGoogle(context).collect {
//                if (it) {
//                    _actions.emit(AuthenticationAction.OnLogin("Login Success"))
//                } else {
//                    _actions.emit(AuthenticationAction.OnLogin("Login failed"))
//                }
//            }
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

data class AuthenticationUIState(
    val isUserSignedIn: Boolean = false,
    val user: FirebaseUser? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val showLoginInput: Boolean = false,
    val email: String = "",
    val password: String = "",
    val theme: String? = "Dark"
) {
    val enableLoginButton = email.trim().isNotEmpty() && password.trim().isNotEmpty()
}

data class DrawerState(
    val user: FirebaseUser? = null,
    val theme: String? = "Dark"
)

data class NewAuthenticationUIState(
    val authenticationScreen: AuthenticationScreen = AuthenticationScreen.Login,
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null
) {
    val enableButton = email.trim().isNotEmpty() && password.trim().isNotEmpty() && !loading
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
    data class SignUpWithGoogle(val context: Context) : AuthenticationEvent
    data class LoginWithGoogle(val context: Context) : AuthenticationEvent
}

sealed interface AuthenticationAction {
    data class OnLogin(val message: String) : AuthenticationAction
    data class OnSignUp(val message: String) : AuthenticationAction
}
