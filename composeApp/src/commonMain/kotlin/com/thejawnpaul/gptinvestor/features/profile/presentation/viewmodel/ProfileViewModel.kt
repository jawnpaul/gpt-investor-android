package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class ProfileViewModel(
    private val appPreferences: AppPreferences,
    private val authenticationRepository: AuthenticationRepository,
    private val conversationRepository: IConversationRepository,
    private val topPickRepository: ITopPickRepository,
    private val tidbitRepository: TidbitRepository,
    private val billingRepository: IBillingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())

    private val _actions = MutableSharedFlow<ProfileAction>()
    val actions = _actions.asSharedFlow()

    val state = combine(
        appPreferences.userName,
        conversationRepository.getQueryCount(),
        appPreferences.notificationPermission,
        billingRepository.currentPurchases(),
        topPickRepository.getSavedTopPicksCount(),
        tidbitRepository.getSavedTidbitsCount().onStart { emit(0) },
        _state
    ) { args ->
        val userName = args[0] as String?
        val queryCount = args[1] as Int
        val notificationPermission = args[2] as Boolean?
        val purchases = args[3] as List<*>
        val savedPicksCount = args[4] as Int
        val savedTidbitsCount = args[5] as Int
        val currentState = args[6] as ProfileUiState

        currentState.copy(
            userName = userName.orEmpty(),
            queryCount = queryCount,
            isNotificationsEnabled = notificationPermission ?: false,
            isPremiumUser = purchases.isNotEmpty(),
            savedPicksCount = savedPicksCount,
            savedTidbitsCount = savedTidbitsCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    init {
        getTheme()
    }

    fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.SignOutClicked -> _state.update { it.copy(showSignOutConfirmation = true) }
            ProfileEvent.DismissSignOutDialog -> _state.update { it.copy(showSignOutConfirmation = false) }
            ProfileEvent.ConfirmSignOut -> {
                _state.update { it.copy(showSignOutConfirmation = false) }
                viewModelScope.launch { authenticationRepository.signOut() }
            }

            ProfileEvent.AppearanceClicked -> viewModelScope.launch {
                _actions.emit(ProfileAction.NavigateToAppearance)
            }
            ProfileEvent.HistoryClicked -> viewModelScope.launch { _actions.emit(ProfileAction.NavigateToHistory) }
            ProfileEvent.NotificationsClicked -> viewModelScope.launch {
                _actions.emit(ProfileAction.NavigateToNotifications)
            }
            ProfileEvent.PrivacyClicked -> viewModelScope.launch { _actions.emit(ProfileAction.NavigateToPrivacy) }
            ProfileEvent.SavedPicksClicked -> viewModelScope.launch {
                _actions.emit(ProfileAction.NavigateToSavedPicks)
            }
            ProfileEvent.SavedTidbitsClicked -> viewModelScope.launch {
                _actions.emit(ProfileAction.NavigateToSavedTidbits)
            }
            ProfileEvent.UpgradeToPremiumClicked -> viewModelScope.launch {
                _actions.emit(ProfileAction.NavigateToUpgrade)
            }
        }
    }

    fun getTheme() {
        viewModelScope.launch {
            appPreferences.themePreference.collect { theme ->
                _state.update {
                    it.copy(currentTheme = theme ?: "System")
                }
            }
        }
    }
}
