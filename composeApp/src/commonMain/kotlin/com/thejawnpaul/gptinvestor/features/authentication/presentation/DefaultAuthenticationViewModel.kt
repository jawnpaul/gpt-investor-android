package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class DefaultAuthenticationViewModel(
    private val authRepository: AuthenticationRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _actions = MutableSharedFlow<DefaultAuthenticationAction>()
    val actions get() = _actions

    fun handleEvent(event: DefaultAuthenticationEvent) {
        when (event) {
            DefaultAuthenticationEvent.GuestLogin -> {
                analyticsLogger.logEvent(eventName = "guest-login-clicked", params = mapOf())
                guestLogin()
            }

            DefaultAuthenticationEvent.SignUp -> {
                analyticsLogger.logEvent(eventName = "sign-up-clicked", params = mapOf())
                handleAction(action = DefaultAuthenticationAction.OnGoToSignUp)
            }
        }
    }

    private fun guestLogin() {
        _loading.update { true }
        viewModelScope.launch {
            authRepository.guestLogin().onSuccess {
                _loading.update { false }
                handleAction(action = DefaultAuthenticationAction.ShowToast("Guest session started"))
                handleAction(action = DefaultAuthenticationAction.OnGoToHome)
            }.onFailure { failure ->
                _loading.update { false }
                _actions.emit(DefaultAuthenticationAction.ShowToast(failure.message.toString()))
            }
        }
    }

    private fun handleAction(action: DefaultAuthenticationAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

sealed interface DefaultAuthenticationEvent {
    data object GuestLogin : DefaultAuthenticationEvent
    data object SignUp : DefaultAuthenticationEvent
}

sealed interface DefaultAuthenticationAction {
    data class ShowToast(val message: String) : DefaultAuthenticationAction
    data object OnGoToHome : DefaultAuthenticationAction
    data object OnGoToSignUp : DefaultAuthenticationAction
}

data class DrawerState(val user: String? = null, val theme: String? = "Dark")
