package com.thejawnpaul.gptinvestor.features.authentication.presentation

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val authRepository: AuthenticationRepository) :
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
            authRepository.signIn(launcher)
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
            }

            else -> {
                _authState.update { it.copy(loading = false, errorMessage = "Sign in failed") }
            }
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
    val errorMessage: String? = null
)
