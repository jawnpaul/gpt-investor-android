package com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.platform.AppInfo
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.dark
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IConversationRepository
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.settings.domain.SettingsRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import com.thejawnpaul.gptinvestor.light
import com.thejawnpaul.gptinvestor.system
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class ProfileViewModel(
    private val appPreferences: AppPreferences,
    private val authenticationRepository: AuthenticationRepository,
    private val conversationRepository: IConversationRepository,
    private val topPickRepository: ITopPickRepository,
    private val tidbitRepository: TidbitRepository,
    private val settingsRepository: SettingsRepository,
    private val premiumEventBus: PremiumEventBus,
    private val watchlistRepository: WatchlistRepository,
    private val notificationRepository: NotificationRepository,
    private val notificationPermissionController: NotificationPermissionController,
    appInfo: AppInfo,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    val themeMap =
        mapOf(Res.string.light to "Light", Res.string.dark to "Dark", Res.string.system to "System")

    private val _state = MutableStateFlow(
        ProfileUiState(themeList = themeMap.keys.toList(), appVersion = appInfo.versionName)
    )

    private val _actions = MutableSharedFlow<ProfileAction>()
    val actions = _actions.asSharedFlow()

    val state = combine(
        appPreferences.userName,
        conversationRepository.getQueryCount(),
        appPreferences.notificationPermission,
        settingsRepository.isPremium,
        topPickRepository.getSavedTopPicksCount(),
        tidbitRepository.getSavedTidbitsCount().onStart { emit(0) },
        appPreferences.isGuestLoggedIn,
        _state
    ) { args ->
        val userName = args[0] as String?
        val queryCount = args[1] as Int
        val notificationPermission = args[2] as Boolean?
        val isPremium = args[3] as Boolean
        val savedPicksCount = args[4] as Int
        val savedTidbitsCount = args[5] as Int
        val isGuestLoggedIn = args[6] as Boolean?
        val currentState = args[7] as ProfileUiState

        currentState.copy(
            userName = userName.orEmpty(),
            queryCount = queryCount,
            isNotificationsEnabled = notificationPermission ?: false,
            isPremiumUser = isPremium,
            savedPicksCount = savedPicksCount,
            savedTidbitsCount = savedTidbitsCount,
            isGuestSession = isGuestLoggedIn == true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value
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

            ProfileEvent.AppearanceClicked -> {
                analyticsLogger.logEvent(
                    eventName = "appearance-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToAppearance)
            }

            ProfileEvent.HistoryClicked -> {
                analyticsLogger.logEvent(
                    eventName = "history-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToHistory)
            }

            ProfileEvent.NotificationsClicked -> {
                if (!state.value.canTurnOnNotifications) return
                analyticsLogger.logEvent(
                    eventName = "notifications-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                _state.update { it.copy(requestNotificationPermission = true) }
            }

            ProfileEvent.NotificationPermissionGranted -> {
                _state.update { it.copy(requestNotificationPermission = false) }
                viewModelScope.launch {
                    appPreferences.setNotificationPermission(true)
                    analyticsLogger.logEvent(
                        eventName = "notification-permission-granted",
                        params = mapOf("permission" to true, "source" to "profile_screen")
                    )
                    notificationRepository.syncTokenIfNeeded()
                }
            }

            ProfileEvent.NotificationPermissionDenied -> {
                _state.update { it.copy(requestNotificationPermission = false) }
                viewModelScope.launch { appPreferences.setNotificationPermission(false) }
                processAction(ProfileAction.ShowNotificationSettingsHint)
            }

            ProfileEvent.ScreenShown -> viewModelScope.launch {
                if (appPreferences.isGuestLoggedIn.first() == true) return@launch
                launch { syncNotificationPermission() }
                watchlistRepository.getWatchlist().onSuccess { watchlist ->
                    _state.update { it.copy(watchingCount = watchlist.items.size) }
                }
            }

            ProfileEvent.WatchingClicked -> processAction(ProfileAction.NavigateToWatchlist)

            ProfileEvent.PrivacyClicked -> {
                analyticsLogger.logEvent(
                    eventName = "privacy-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToPrivacy)
            }

            ProfileEvent.SavedPicksClicked -> {
                analyticsLogger.logEvent(
                    eventName = "saved-top-picks-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToSavedPicks)
            }

            ProfileEvent.SavedTidbitsClicked -> {
                analyticsLogger.logEvent(
                    eventName = "saved-tidbits-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToSavedTidbits)
            }

            ProfileEvent.UpgradeToPremiumClicked -> {
                analyticsLogger.logEvent(
                    eventName = "unlock-premium-tapped",
                    params = mapOf("source" to "profile")
                )
                premiumEventBus.requestPremium(source = "profile")
            }

            is ProfileEvent.ChangeTheme -> viewModelScope.launch {
                val theme: String = themeMap.get(event.theme) ?: "System"
                appPreferences.setThemePreference(theme)
                analyticsLogger.logEvent(
                    eventName = "theme-changed",
                    params = mapOf("theme" to event.theme)
                )
            }

            ProfileEvent.SettingsClicked -> {
                analyticsLogger.logEvent(
                    eventName = "settings-clicked",
                    params = mapOf("source" to "profile_screen")
                )
                processAction(ProfileAction.NavigateToSettings)
            }

            ProfileEvent.SignInClicked -> processAction(ProfileAction.NavigateToLogin)

            ProfileEvent.CreateAccountClicked -> processAction(ProfileAction.NavigateToSignUp)
        }
    }

    private suspend fun syncNotificationPermission() {
        val enabled = notificationPermissionController.areNotificationsEnabled()
        if (appPreferences.notificationPermission.first() == enabled) return
        appPreferences.setNotificationPermission(enabled)
        if (enabled) notificationRepository.syncTokenIfNeeded()
    }

    private fun processAction(action: ProfileAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    fun getTheme() {
        viewModelScope.launch {
            appPreferences.themePreference.collect { theme ->
                _state.update { state ->
                    val theme = themeMap.filterValues { it == theme }.keys.firstOrNull()
                    state.copy(currentTheme = theme ?: Res.string.system)
                }
            }
        }
    }
}
