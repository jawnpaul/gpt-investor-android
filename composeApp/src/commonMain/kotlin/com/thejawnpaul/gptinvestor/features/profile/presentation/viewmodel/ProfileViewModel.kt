package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ProfileViewModel(
    private val appPreferences: AppPreferences,
    private val authenticationRepository: AuthenticationRepository,
    private val conversationRepository: IConversationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = combine(
        appPreferences.userName,
        conversationRepository.getQueryCount(),
        _state
    ) { userName, queryCount, currentState ->
        currentState.copy(
            userName = userName.orEmpty(),
            queryCount = queryCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.SignOutClicked -> _state.update { it.copy(showSignOutConfirmation = true) }
            ProfileEvent.DismissSignOutDialog -> _state.update { it.copy(showSignOutConfirmation = false) }
            ProfileEvent.ConfirmSignOut -> {
                _state.update { it.copy(showSignOutConfirmation = false) }
                viewModelScope.launch { authenticationRepository.signOut() }
            }
            else -> Unit
        }
    }
}
