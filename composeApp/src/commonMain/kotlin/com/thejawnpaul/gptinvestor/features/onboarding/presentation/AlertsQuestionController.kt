package com.thejawnpaul.gptinvestor.features.onboarding.presentation

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.AskTrigger
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.GetQuestionToAskUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.MarkQuestionAskedUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

data class AlertsQuestionState(val ticker: String? = null, val requestPermission: Boolean = false)

sealed interface AlertsQuestionEvent {
    data object Accept : AlertsQuestionEvent
    data object Dismiss : AlertsQuestionEvent
    data class PermissionResult(val granted: Boolean) : AlertsQuestionEvent
}

@Factory
class AlertsQuestionController(
    private val getQuestionToAsk: GetQuestionToAskUseCase,
    private val markQuestionAsked: MarkQuestionAskedUseCase,
    private val preferences: AppPreferences,
    private val notificationRepository: NotificationRepository,
    private val notificationPermissionController: NotificationPermissionController,
    @Provided private val analyticsLogger: AnalyticsLogger
) {
    private val _state = MutableStateFlow(AlertsQuestionState())
    val state: StateFlow<AlertsQuestionState> = _state.asStateFlow()

    private lateinit var scope: CoroutineScope

    fun bind(scope: CoroutineScope) {
        this.scope = scope
    }

    fun onStockAdded(ticker: String) {
        scope.launch {
            if (_state.value.ticker != null) return@launch
            if (preferences.notificationPermission.first() != null) return@launch
            if (notificationPermissionController.areNotificationsEnabled()) return@launch
            val isGuest = preferences.isGuestLoggedIn.first() == true
            getQuestionToAsk.run(GetQuestionToAskUseCase.Params(AskTrigger.WATCHLIST_ADD, isGuest)).first()
                .onSuccess { question ->
                    _state.update { it.copy(ticker = ticker) }
                    logQuestionEvent(EVENT_QUESTION_SHOWN, mapOf("ticker" to ticker))
                    markQuestionAsked(question)
                }
        }
    }

    fun handleEvent(event: AlertsQuestionEvent) {
        when (event) {
            AlertsQuestionEvent.Accept -> accept()
            AlertsQuestionEvent.Dismiss -> dismiss()
            is AlertsQuestionEvent.PermissionResult -> onPermissionResult(event.granted)
        }
    }

    private fun accept() {
        _state.update { it.copy(ticker = null, requestPermission = true) }
    }

    private fun dismiss() {
        if (_state.value.ticker == null) return
        _state.update { it.copy(ticker = null) }
        logQuestionEvent(EVENT_QUESTION_DISMISSED)
    }

    private fun onPermissionResult(granted: Boolean) {
        _state.update { it.copy(requestPermission = false) }
        logQuestionEvent(EVENT_QUESTION_ANSWERED, mapOf("answer" to if (granted) "granted" else "denied"))
        scope.launch {
            preferences.setNotificationPermission(granted)
            if (granted) {
                analyticsLogger.logEvent("notification-permission-granted", mapOf("permission" to true))
                notificationRepository.syncTokenIfNeeded()
            }
        }
    }

    private fun logQuestionEvent(event: String, extra: Map<String, Any> = emptyMap()) {
        analyticsLogger.logEvent(event, mapOf("question" to "alerts", "source" to SOURCE_WATCHLIST_ADD) + extra)
    }

    private companion object {
        const val SOURCE_WATCHLIST_ADD = "watchlist_add"
    }
}
