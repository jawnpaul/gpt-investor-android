package com.thejawnpaul.gptinvestor.features.authentication.presentation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val analyticsLogger: AnalyticsLogger
) :
    ViewModel() {

    private val _authState = MutableStateFlow(AuthenticationUIState())
    val authState = _authState.asStateFlow()

    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn = _isUserSignedIn.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAuthState().collect { isSignedIn ->
                _authState.update { it.copy(isUserSignedIn = isSignedIn) }
            }
        }
    }

    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch {
            _authState.update { it.copy(loading = true) }
            authRepository.signUp(launcher)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun handleSignInResult(result: ActivityResult) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                _authState.update { it.copy(loading = false, user = authRepository.currentUser) }
                analyticsLogger.identifyUser(
                    eventName = "Sign Up",
                    params = mapOf(
                        "user_id" to authRepository.currentUser?.uid.toString(),
                        "email" to authRepository.currentUser?.email.toString(),
                        "name" to authRepository.currentUser?.displayName.toString(),
                        "sign_up_method" to authRepository.currentUser?.providerId.toString()
                    )
                )
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
            authRepository.login(email, password).collect { isSuccessful ->
                if (isSuccessful) {
                    _authState.update {
                        it.copy(
                            loading = false,
                            user = authRepository.currentUser
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
    val password: String = ""
) {
    val enableLoginButton = email.trim().isNotEmpty() && password.trim().isNotEmpty()
}
