package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction.*
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.repository.ITopPickRepository
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val authenticationRepository: AuthenticationRepository,
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val remoteConfig: RemoteConfig,
    private val preferences: GPTInvestorPreferences,
    private val analyticsLogger: AnalyticsLogger,
    private val topPickRepository: ITopPickRepository,
    private val notificationRepository: NotificationRepository,
    private val modelsRepository: ModelsRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _actions = MutableSharedFlow<HomeAction>()
    val actions get() = _actions

    val theme = preferences.themePreference
    val notificationPermission = preferences.notificationPermission

    private var upgradeModelId: String? = null

    init {
        remoteConfig.init()
        // getTopPicks()
        getCurrentUser()
        getAvailableModels()
        getDefaultPrompts()

        viewModelScope.launch {
            preferences.themePreference.collect { theme ->
                _uiState.update { it.copy(theme = theme) }
            }
            preferences.notificationPermission.collect { permission ->
                _uiState.update { it.copy(requestForNotificationPermission = permission) }
            }
        }
    }

    fun getTopPicks() {
        getTopPicksUseCase(GetTopPicksUseCase.None()) {
            it.onFailure {
            }
            it.onSuccess {
            }
        }

        viewModelScope.launch {
            topPickRepository.getTopPicksByDate().collect { topPicks ->
                val topPicksPresentation = topPicks.map { topPick ->
                    with(topPick) {
                        TopPickPresentation(
                            id = id,
                            companyName = companyName,
                            ticker = ticker,
                            rationale = rationale,
                            metrics = metrics,
                            risks = risks,
                            confidenceScore = confidenceScore,
                            isSaved = isSaved,
                            imageUrl = imageUrl,
                            percentageChange = percentageChange,
                            currentPrice = currentPrice
                        )
                    }
                }
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            topPicks = topPicksPresentation
                        )
                    )
                }
            }
        }

        /*_uiState.update { currentState ->
            currentState.copy(
                topPicksView = currentState.topPicksView.copy(loading = true, error = null)
            )
        }

        getTopPicksUseCase(GetTopPicksUseCase.None()) { result ->
            result.onFailure {
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            error = "Something went wrong."
                        )
                    )
                }
                getLocalPicks()
            }

            result.onSuccess { topPicksResult ->
                _uiState.update { currentState ->
                    currentState.copy(
                        topPicksView = currentState.topPicksView.copy(
                            loading = false,
                            topPicks = topPicksResult.map { topPick ->
                                with(topPick) {
                                    TopPickPresentation(
                                        id = id,
                                        companyName = companyName,
                                        ticker = ticker,
                                        rationale = rationale,
                                        metrics = metrics,
                                        risks = risks,
                                        confidenceScore = confidenceScore,
                                        isSaved = isSaved,
                                        imageUrl = imageUrl,
                                        percentageChange = percentageChange,
                                        currentPrice = currentPrice
                                    )
                                }
                            }
                        )
                    )
                }
            }
        }*/
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

                HomeEvent.RetryTopPicks -> {
                    getTopPicks()
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
                    notificationRepository.generateToken()
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
                            isDefaultPrompt = true
                        )
                    )
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
        "More analytical",
        "Risk warnings",
        "More personalised",
        "Human Tone",
        "More realistic",
        "Clearer answers",
        "More informative"
    ),
    val selectedWaitlistOptions: List<String> = emptyList(),
    val defaultPrompts: List<DefaultPrompt> = emptyList()
)

sealed interface HomeEvent {
    data class ChatInputChanged(val input: String) : HomeEvent
    data object SendClick : HomeEvent
    data class ChangeTheme(val theme: String) : HomeEvent
    data object RetryTopPicks : HomeEvent
    data object NotificationPermissionGranted : HomeEvent
    data object NotificationPermissionDenied : HomeEvent
    data class ModelChanged(val model: AvailableModel) : HomeEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : HomeEvent
    data class SelectWaitListOption(val option: String) : HomeEvent
    data object JoinWaitlist : HomeEvent
    data class DefaultPromptClicked(val prompt: DefaultPrompt) : HomeEvent
}

sealed interface HomeAction {
    data class OnStartConversation(
        val input: String? = null,
        val isDefaultPrompt: Boolean = false
    ) : HomeAction

    data object OnMenuClick : HomeAction
    data object OnGoToAllTopPicks : HomeAction
    data class OnGoToTopPickDetail(val id: String) : HomeAction
    data class OnGoToCompanyDetail(val ticker: String) : HomeAction
    data object OnGoToDiscover : HomeAction
}
