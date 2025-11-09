package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.authentication.presentation.DrawerState
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.OnGoToAllTidbits
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.OnGoToTidbitDetail
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.OnStartConversation
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
class HomeViewModel(
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val remoteConfig: RemoteConfig,
    private val preferences: GPTInvestorPreferences,
    private val analyticsLogger: AnalyticsLogger,
    private val topPickRepository: ITopPickRepository,
    private val notificationRepository: NotificationRepository,
    private val modelsRepository: ModelsRepository,
    private val tidbitRepository: TidbitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<HomeAction>()
    val actions get() = _actions

    val theme = preferences.themePreference
    val notificationPermission = preferences.notificationPermission

    private var upgradeModelId: String? = null

    init {
        remoteConfig.init()
        getTopPicks()
        getCurrentUser()
        getAvailableModels()
        getDefaultPrompts()
        getTodayTidbit()

        viewModelScope.launch {
            preferences.themePreference.collect { theme ->
                _uiState.update {
                    it.copy(
                        theme = theme,
                        drawerState = it.drawerState.copy(theme = theme)
                    )
                }
            }
            preferences.notificationPermission.collect { permission ->
                _uiState.update { it.copy(requestForNotificationPermission = permission) }
            }

            authenticationRepository.getAuthState().collect { isSignedIn ->
                _uiState.update {
                    it.copy(
                        drawerState = it.drawerState.copy(
                            user = authenticationRepository.currentUser
                        )
                    )
                }
            }
        }
    }

    private fun getTopPicks() {
        getTopPicksUseCase(GetTopPicksUseCase.None()) {
            it.onFailure {
            }
            it.onSuccess {
            }
        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            authenticationRepository.getAuthState().collect { isSignedIn ->
                _uiState.update {
                    it.copy(currentUser = if (isSignedIn) authenticationRepository.currentUser else null)
                }
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.ChatInputChanged -> {
                    _uiState.update { it.copy(chatInput = event.input) }
                }

                is HomeEvent.SendClick -> {
                    _actions.emit(OnStartConversation(_uiState.value.chatInput))
                    _uiState.update { it.copy(chatInput = null) }
                }

                is HomeEvent.ChangeTheme -> {
                    preferences.setThemePreference(event.theme)
                    analyticsLogger.logEvent(
                        eventName = "Theme Changed",
                        params = mapOf("theme" to event.theme)
                    )
                }

                HomeEvent.NotificationPermissionDenied -> {
                    preferences.setNotificationPermission(false)
                }

                HomeEvent.NotificationPermissionGranted -> {
                    if (notificationPermission.first() == true) {
                        return@launch
                    }
                    preferences.setNotificationPermission(true)
                    analyticsLogger.logEvent(
                        eventName = "Notification Permission Granted",
                        params = mapOf("permission" to true)
                    )
                    // firebase token generation
                    notificationRepository.syncTokenIfNeeded()
                }

                is HomeEvent.ModelChanged -> {
                    if (event.model.isUserOnWaitlist == true) {
                        return@launch
                    }

                    _uiState.update { it.copy(selectedModel = event.model) }
                    analyticsLogger.logEvent(
                        eventName = "Model Changed",
                        params = mapOf("model" to event.model.modelId)
                    )
                }

                is HomeEvent.UpgradeModel -> {
                    _uiState.update { it.copy(showWaitlistBottomSheet = event.showBottomSheet) }
                    event.modelId?.let {
                        upgradeModelId = it
                    }
                }

                is HomeEvent.SelectWaitListOption -> {
                    selectWaitListOption(event.option)
                }

                HomeEvent.JoinWaitlist -> {
                    upgradeModelId?.let { modelId ->
                        joinModelWaitlist(modelId = modelId)
                    }
                }

                is HomeEvent.DefaultPromptClicked -> {
                    _actions.emit(
                        OnStartConversation(
                            input = event.prompt.query,
                            title = event.prompt.title
                        )
                    )
                    _uiState.update { it.copy(chatInput = null) }
                }

                is HomeEvent.SignOut -> {
                    viewModelScope.launch {
                        authenticationRepository.signOut(event.context)
                    }
                }

                is HomeEvent.ClickTidbit -> {
                    _actions.emit(OnGoToTidbitDetail(event.id))
                }

                HomeEvent.GoToAllTidbits -> {
                    _actions.emit(OnGoToAllTidbits)
                }
            }
        }
    }

    fun processAction(action: HomeAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    private fun selectWaitListOption(option: String) {
        if (_uiState.value.selectedWaitlistOptions.contains(option)) {
            _uiState.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions - option) }
        } else {
            _uiState.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions + option) }
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                _uiState.update { it.copy(availableModels = models) }
            }
        }
    }

    private fun joinModelWaitlist(modelId: String) {
        viewModelScope.launch {
            modelsRepository.putUserOnModelWaitlist(
                modelId = modelId,
                reasons = _uiState.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Timber.e(failure.stackTraceToString())
            }
        }
    }

    private fun getDefaultPrompts() {
        getDefaultPromptsUseCase(GetDefaultPromptsUseCase.None()) {
            it.onFailure {
            }

            it.onSuccess { result ->
                _uiState.update { state -> state.copy(defaultPrompts = result) }
            }
        }
    }

    private fun getTodayTidbit() {
        viewModelScope.launch {
            tidbitRepository.getTodayTidbit().onSuccess { tidbit ->
                _uiState.update {
                    it.copy(
                        homeTidbitView = with(tidbit) {
                            HomeTidbitView(
                                id = id,
                                previewUrl = previewUrl,
                                title = title,
                                description = summary
                            )
                        }
                    )
                }
            }
        }
    }
}

data class HomeUiState(
    val trendingCompaniesView: TrendingCompaniesView = TrendingCompaniesView(),
    val topPicksView: TopPicksView = TopPicksView(),
    val currentUser: FirebaseUser? = null,
    val chatInput: String? = null,
    val theme: String? = "Dark",
    val requestForNotificationPermission: Boolean? = null,
    val availableModels: List<AvailableModel> = emptyList(),
    val selectedModel: AvailableModel = DefaultModel(),
    val showWaitlistBottomSheet: Boolean = false,
    val waitlistAvailableOptions: List<String> = listOf(
        "Advanced analysis",
        "Personalized insights",
        "Risk awareness",
        "Actionable guidance",
        "Transparent data sources",
        "Unlimited queries"
    ),
    val selectedWaitlistOptions: List<String> = emptyList(),
    val defaultPrompts: List<DefaultPrompt> = emptyList(),
    val drawerState: DrawerState = DrawerState(),
    val homeTidbitView: HomeTidbitView? = null
)

sealed interface HomeEvent {
    data class ChatInputChanged(val input: String) : HomeEvent
    data object SendClick : HomeEvent
    data class ChangeTheme(val theme: String) : HomeEvent
    data object NotificationPermissionGranted : HomeEvent
    data object NotificationPermissionDenied : HomeEvent
    data class ModelChanged(val model: AvailableModel) : HomeEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : HomeEvent
    data class SelectWaitListOption(val option: String) : HomeEvent
    data object JoinWaitlist : HomeEvent
    data class DefaultPromptClicked(val prompt: DefaultPrompt) : HomeEvent
    data class SignOut(val context: Context) : HomeEvent
    data class ClickTidbit(val id: String) : HomeEvent
    data object GoToAllTidbits : HomeEvent
}

sealed interface HomeAction {
    data class OnStartConversation(val input: String? = null, val title: String? = null) : HomeAction

    data object OnGoToAllTopPicks : HomeAction
    data class OnGoToTopPickDetail(val id: String) : HomeAction
    data class OnGoToCompanyDetail(val ticker: String) : HomeAction
    data object OnGoToDiscover : HomeAction
    data object OnGoToSettings : HomeAction
    data object OnGoToHistory : HomeAction
    data object OnGoToSavedPicks : HomeAction
    data class OnGoToTidbitDetail(val id: String) : HomeAction
    data object OnGoToAllTidbits : HomeAction
    data object OnGoToSavedTidbits : HomeAction
}
