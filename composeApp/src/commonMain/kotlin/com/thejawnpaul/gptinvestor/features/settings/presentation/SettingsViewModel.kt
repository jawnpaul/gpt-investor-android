package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.TIER_PREMIUM
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.UpdateUserSettingsRequest
import com.thejawnpaul.gptinvestor.features.settings.domain.SettingsRepository
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsView())
    val uiState get() = _uiState

    private val _actions = MutableSharedFlow<SettingsAction>()
    val actions get() = _actions

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getUserSettings().onSuccess { settings ->
                _uiState.update {
                    it.copy(
                        isPremium = settings.tier == TIER_PREMIUM,
                        digestDeliveryHour = settings.digestDeliveryHour,
                        digestDeliveryHourLabel = settings.digestDeliveryHour?.let(::formatHour)
                    )
                }
            }
        }
    }

    private fun updateDigestTime(hour: Int) {
        _uiState.update { it.copy(showTimePicker = false) }
        viewModelScope.launch {
            settingsRepository.updateUserSettings(
                UpdateUserSettingsRequest(
                    digestDeliveryHour = hour,
                    timezoneIana = TimeZone.currentSystemDefault().id
                )
            ).onSuccess { settings ->
                _uiState.update {
                    it.copy(
                        digestDeliveryHour = settings.digestDeliveryHour,
                        digestDeliveryHourLabel = settings.digestDeliveryHour?.let(::formatHour)
                    )
                }
            }
        }
    }

    private fun formatHour(hour: Int): String {
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "$displayHour:00 $period"
    }

    private fun deleteAccount() {
        _uiState.update { it.copy(showDeleteDialog = false) }
        viewModelScope.launch {
            authenticationRepository.deleteAccount()
        }
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.DeleteAccount -> deleteAccount()
            is SettingsEvent.UpdateDigestTime -> updateDigestTime(event.hour)
            SettingsEvent.ShowDeleteDialog -> _uiState.update { it.copy(showDeleteDialog = true) }
            SettingsEvent.DismissDeleteDialog -> _uiState.update { it.copy(showDeleteDialog = false) }
            SettingsEvent.ShowTimePicker -> _uiState.update { it.copy(showTimePicker = true) }
            SettingsEvent.DismissTimePicker -> _uiState.update { it.copy(showTimePicker = false) }
        }
    }

    fun processAction(action: SettingsAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

sealed interface SettingsEvent {
    data object DeleteAccount : SettingsEvent
    data class UpdateDigestTime(val hour: Int) : SettingsEvent
    data object ShowDeleteDialog : SettingsEvent
    data object DismissDeleteDialog : SettingsEvent
    data object ShowTimePicker : SettingsEvent
    data object DismissTimePicker : SettingsEvent
}

sealed interface SettingsAction {
    data object OnGoBack : SettingsAction
}
