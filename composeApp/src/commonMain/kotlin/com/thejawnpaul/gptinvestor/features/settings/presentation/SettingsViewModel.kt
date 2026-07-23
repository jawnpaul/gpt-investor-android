package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.TIER_PREMIUM
import com.thejawnpaul.gptinvestor.features.settings.domain.SettingsRepository
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
                _uiState.update { it.copy(isPremium = settings.tier == TIER_PREMIUM) }
            }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            authenticationRepository.deleteAccount()
        }
    }

    fun handleEvent(event: SettingsEvent) {
        when (event) {
            SettingsEvent.DeleteAccount -> {
                deleteAccount()
            }
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
}

sealed interface SettingsAction {
    data object OnGoBack : SettingsAction
}
