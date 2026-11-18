package com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui

import androidx.compose.runtime.Composable
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.AlertsQuestionEvent
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.AlertsQuestionState
import org.koin.compose.koinInject

@Composable
fun AlertsQuestion(state: AlertsQuestionState, onEvent: (AlertsQuestionEvent) -> Unit) {
    val permissionController: NotificationPermissionController = koinInject()
    state.ticker?.let { ticker ->
        AlertsQuestionSheet(
            ticker = ticker,
            onAccept = { onEvent(AlertsQuestionEvent.Accept) },
            onDismiss = { onEvent(AlertsQuestionEvent.Dismiss) }
        )
    }
    permissionController.RequestPermissionIfNeeded(
        shouldRequest = state.requestPermission,
        onGrant = { onEvent(AlertsQuestionEvent.PermissionResult(true)) },
        onDeny = { onEvent(AlertsQuestionEvent.PermissionResult(false)) }
    )
}
