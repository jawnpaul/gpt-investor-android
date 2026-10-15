package com.thejawnpaul.gptinvestor.features.authentication.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.domain.InvalidResetEmailException
import com.thejawnpaul.gptinvestor.features.authentication.domain.TooManyResetRequestsException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ForgotPasswordViewModel(
    private val authRepository: AuthenticationRepository,
    @Provided private val analyticsLogger: AnalyticsLogger,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ForgotPasswordUiState(email = savedStateHandle.get<String>(FORGOT_PASSWORD_EMAIL_ARG).orEmpty())
    )
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<ForgotPasswordUiAction>()
    val actions get() = _actions

    private var cooldownJob: Job? = null

    fun handleEvent(event: ForgotPasswordUiEvent) {
        when (event) {
            is ForgotPasswordUiEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(email = event.email, emailError = !isValidEmail(event.email), rateLimited = false)
                }
            }

            ForgotPasswordUiEvent.SendClick -> {
                if (_uiState.value.sending) return
                analyticsLogger.logEvent(eventName = "forgot-password-send-clicked", params = mapOf())
                if (isValidEmail(_uiState.value.email)) {
                    requestReset()
                } else {
                    _uiState.update { it.copy(emailError = true) }
                }
            }

            ForgotPasswordUiEvent.ResendClick -> {
                if (!_uiState.value.isResendEnabled) return
                analyticsLogger.logEvent(eventName = "forgot-password-resend-clicked", params = mapOf())
                requestReset()
            }

            ForgotPasswordUiEvent.GoBack -> handleAction(ForgotPasswordUiAction.OnGoBack)
        }
    }

    private fun requestReset() {
        val email = _uiState.value.email.trim()
        _uiState.update { it.copy(sending = true, rateLimited = false) }
        viewModelScope.launch {
            authRepository.requestPasswordReset(email).onSuccess {
                _uiState.update { it.copy(sending = false, sentTo = email) }
                startCooldown()
            }.onFailure { failure ->
                _uiState.update { it.copy(sending = false) }
                when (failure) {
                    is InvalidResetEmailException -> _uiState.update { it.copy(emailError = true, sentTo = null) }
                    is TooManyResetRequestsException -> _uiState.update { it.copy(rateLimited = true) }
                    else -> handleAction(ForgotPasswordUiAction.OnShowToast(failure.message.toString()))
                }
            }
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            for (secondsLeft in RESEND_COOLDOWN_SECONDS downTo 1) {
                _uiState.update { it.copy(resendSecondsLeft = secondsLeft) }
                delay(ONE_SECOND_MILLIS)
            }
            _uiState.update { it.copy(resendSecondsLeft = 0) }
        }
    }

    private fun handleAction(action: ForgotPasswordUiAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

const val FORGOT_PASSWORD_EMAIL_ARG = "email"
internal const val RESEND_COOLDOWN_SECONDS = 60
private const val ONE_SECOND_MILLIS = 1_000L

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: Boolean = false,
    val sending: Boolean = false,
    val rateLimited: Boolean = false,
    val sentTo: String? = null,
    val resendSecondsLeft: Int = 0
) {
    val isSendEnabled: Boolean
        get() = isValidEmail(email)

    val isResendEnabled: Boolean
        get() = !sending && resendSecondsLeft == 0
}

sealed interface ForgotPasswordUiEvent {
    data class EmailChanged(val email: String) : ForgotPasswordUiEvent
    data object SendClick : ForgotPasswordUiEvent
    data object ResendClick : ForgotPasswordUiEvent
    data object GoBack : ForgotPasswordUiEvent
}

sealed interface ForgotPasswordUiAction {
    data class OnShowToast(val message: String) : ForgotPasswordUiAction
    data object OnGoBack : ForgotPasswordUiAction
}
