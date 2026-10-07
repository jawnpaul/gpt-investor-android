package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class ProfileViewModel(
    private val authenticationRepository: AuthenticationRepository,
    private val preferences: AppPreferences,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    val uiState = combine(
        preferences.userName,
        preferences.themePreference,
        preferences.isGuestLoggedIn
    ) { userName, theme, isGuestLoggedIn ->
        ProfileUiState(user = userName, theme = theme, isGuestSession = isGuestLoggedIn == true)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    private val _actions = MutableSharedFlow<ProfileAction>()
    val actions get() = _actions

    fun handleEvent(event: ProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileEvent.ChangeTheme -> {
                    preferences.setThemePreference(event.theme)
                    analyticsLogger.logEvent(
                        eventName = "theme-changed",
                        params = mapOf("theme" to event.theme)
                    )
                }

                ProfileEvent.SignOut -> authenticationRepository.signOut()

                ProfileEvent.GoToSignUp -> _actions.emit(ProfileAction.OnGoToSignUp)

                ProfileEvent.GoToHistory -> navigate("history", "history", ProfileAction.OnGoToHistory)

                ProfileEvent.GoToSavedPicks -> navigate("saved_picks", "saved picks", ProfileAction.OnGoToSavedPicks)

                ProfileEvent.GoToSavedTidbits ->
                    navigate("saved_tidbits", "saved tidbits", ProfileAction.OnGoToSavedTidbits)

                ProfileEvent.GoToSettings -> navigate("settings", "settings", ProfileAction.OnGoToSettings)
            }
        }
    }

    private suspend fun navigate(destination: String, label: String, action: ProfileAction) {
        if (uiState.value.isGuestSession) {
            _actions.emit(ProfileAction.ShowToast("You can only see $label after signing in"))
        } else {
            analyticsLogger.logEvent(
                eventName = "navigation-clicked",
                params = mapOf("destination" to destination)
            )
            _actions.emit(action)
        }
    }
}

data class ProfileUiState(val user: String? = null, val theme: String? = "Dark", val isGuestSession: Boolean = false)

sealed interface ProfileEvent {
    data class ChangeTheme(val theme: String) : ProfileEvent
    data object SignOut : ProfileEvent
    data object GoToSignUp : ProfileEvent
    data object GoToHistory : ProfileEvent
    data object GoToSavedPicks : ProfileEvent
    data object GoToSavedTidbits : ProfileEvent
    data object GoToSettings : ProfileEvent
}

sealed interface ProfileAction {
    data object OnGoToSignUp : ProfileAction
    data object OnGoToHistory : ProfileAction
    data object OnGoToSavedPicks : ProfileAction
    data object OnGoToSavedTidbits : ProfileAction
    data object OnGoToSettings : ProfileAction
    data class ShowToast(val message: String) : ProfileAction
}
