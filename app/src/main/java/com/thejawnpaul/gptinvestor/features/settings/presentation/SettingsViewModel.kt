package com.thejawnpaul.gptinvestor.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {

    fun deleteAccount() {
        viewModelScope.launch {
            authenticationRepository.deleteAccount()
        }
    }
}
