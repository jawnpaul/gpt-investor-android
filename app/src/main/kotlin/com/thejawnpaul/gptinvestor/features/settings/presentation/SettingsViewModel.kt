package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.settings.presentation.state.SettingsView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(SettingsView())
    val uiState get() = _uiState

    private val _actions = MutableSharedFlow<SettingsAction>()
    val actions get() = _actions

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
