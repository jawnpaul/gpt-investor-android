package com.thejawnpaul.gptinvestor.features.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.AskTrigger
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.OnboardingQuestion
import com.thejawnpaul.gptinvestor.features.onboarding.domain.repository.IOnboardingQuestionRepository
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.GetQuestionToAskUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.MarkQuestionAskedUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.domain.usecases.SaveAnswerUseCase
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.EVENT_QUESTION_ANSWERED
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.EVENT_QUESTION_DISMISSED
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.EVENT_QUESTION_SHOWN
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.GetTopPicksUseCase
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.picksForGoal
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import kotlin.collections.map
import kotlin.onFailure
import kotlin.onSuccess
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class DiscoverViewModel(
    private val companyRepository: CompanyRepository,
    private val getTopPicksUseCase: GetTopPicksUseCase,
    private val appPreferences: AppPreferences,
    private val savedStateHandle: SavedStateHandle,
    private val tidbitRepository: TidbitRepository,
    private val onboardingQuestionRepository: IOnboardingQuestionRepository,
    private val getQuestionToAsk: GetQuestionToAskUseCase,
    private val markQuestionAsked: MarkQuestionAskedUseCase,
    private val saveAnswer: SaveAnswerUseCase,
    @Provided private val analyticsLogger: AnalyticsLogger

) : ViewModel() {

    private val _discoveryScreenState = MutableStateFlow(DiscoveryScreenState())
    val discoveryScreenState = combine(
        _discoveryScreenState,
        appPreferences.isGuestLoggedIn,
        onboardingQuestionRepository.getQuestionState(OnboardingQuestion.GOAL)
    ) { state, isGuestLoggedIn, goal ->
        state.copy(isGuestSession = isGuestLoggedIn == true, savedGoal = InvestingGoal.fromAnswer(goal?.answer))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DiscoveryScreenState()
    )

    private val _actions = MutableSharedFlow<DiscoveryAction>()
    val actions get() = _actions.asSharedFlow()

    init {
        getTopPicks()
        getTodayTidbit()
        askGoalQuestion()
    }

    private fun askGoalQuestion() {
        viewModelScope.launch {
            val isGuest = appPreferences.isGuestLoggedIn.first() == true
            getQuestionToAsk(GetQuestionToAskUseCase.Params(AskTrigger.DISCOVER, isGuest)) { result ->
                result.onSuccess { question ->
                    _discoveryScreenState.update { it.copy(showGoalBanner = true) }
                    markQuestionAsked(question)
                    logGoalQuestionEvent(EVENT_QUESTION_SHOWN)
                }
            }
        }
    }

    private fun logGoalQuestionEvent(event: String, extra: Map<String, Any> = emptyMap()) {
        analyticsLogger.logEvent(event, mapOf("question" to "goal", "source" to "discover_banner") + extra)
    }

    fun handleEvent(event: DiscoveryEvent) {
        when (event) {
            is DiscoveryEvent.AnswerGoal -> {
                _discoveryScreenState.update { it.copy(showGoalBanner = false) }
                saveAnswer(SaveAnswerUseCase.Params(OnboardingQuestion.GOAL, event.goal.name))
                logGoalQuestionEvent(EVENT_QUESTION_ANSWERED, mapOf("answer" to event.goal.name))
            }

            DiscoveryEvent.DismissGoalBanner -> {
                _discoveryScreenState.update { it.copy(showGoalBanner = false) }
                logGoalQuestionEvent(EVENT_QUESTION_DISMISSED)
            }

            is DiscoveryEvent.GoToCompanyDetail -> {
                processAction(DiscoveryAction.OnNavigateToCompanyDetail(ticker = event.ticker))
            }

            is DiscoveryEvent.ClickTopPick -> {
                analyticsLogger.logEvent(
                    eventName = "discover-top-pick-tapped",
                    params = mapOf(
                        "top_pick_id" to event.id,
                        "user_type" to if (discoveryScreenState.value.isGuestSession) "guest" else "authenticated"
                    )
                )
                processAction(DiscoveryAction.OnGoToPickDetail(id = event.id))
            }

            DiscoveryEvent.GoToLogin -> {
                processAction(DiscoveryAction.OnGoToLogin)
            }

            DiscoveryEvent.GoToSearch -> {
                analyticsLogger.logEvent(
                    eventName = "discover-search-tapped",
                    params = mapOf(
                        "user_type" to if (discoveryScreenState.value.isGuestSession) "guest" else "authenticated"
                    )
                )
                processAction(DiscoveryAction.OnGoToSearch)
            }

            DiscoveryEvent.GoToBrowse -> {
                analyticsLogger.logEvent("browse-stocks-opened", mapOf("source" to "discover_picks"))
                processAction(DiscoveryAction.OnGoToBrowse)
            }
            DiscoveryEvent.GoToAllTopPicks -> processAction(DiscoveryAction.OnGoToAllTopPicks)
            DiscoveryEvent.RetryTidbit -> getTodayTidbit()
            DiscoveryEvent.RetryTopPicks -> getTopPicks()
            is DiscoveryEvent.ClickTidbit -> {
                analyticsLogger.logEvent(
                    eventName = "discover-tidbit-tapped",
                    params = mapOf(
                        "tidbit_id" to event.id,
                        "user_type" to if (discoveryScreenState.value.isGuestSession) "guest" else "authenticated"
                    )
                )
                processAction(DiscoveryAction.OnGoToTidbitDetail(id = event.id))
            }
        }
    }

    private fun getTopPicks() {
        _discoveryScreenState.update {
            it.copy(
                topPicksView = it.topPicksView.copy(
                    loading = true,
                    error = null
                )
            )
        }
        getTopPicksUseCase(GetTopPicksUseCase.None()) { result ->
            result.onFailure {
                _discoveryScreenState.update { state ->
                    state.copy(
                        topPicksView = state.topPicksView.copy(
                            loading = false,
                            error = "Something went wrong"
                        )
                    )
                }
            }
            result.onSuccess { picks ->
                _discoveryScreenState.update { state ->
                    state.copy(
                        topPicksView = state.topPicksView.copy(
                            loading = false,
                            error = null,
                            topPicks = picks.map { pick ->
                                TopPickPresentation(
                                    id = pick.id,
                                    companyName = pick.companyName,
                                    ticker = pick.ticker,
                                    rationale = pick.rationale,
                                    metrics = pick.metrics,
                                    risks = pick.risks,
                                    confidenceScore = pick.confidenceScore,
                                    isSaved = pick.isSaved,
                                    percentageChange = pick.percentageChange,
                                    imageUrl = pick.imageUrl,
                                    currentPrice = pick.currentPrice,
                                    goalTags = pick.goalTags
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    private fun getTodayTidbit() {
        _discoveryScreenState.update { it.copy(homeTidbitView = HomeTidbitView(loading = true)) }
        viewModelScope.launch {
            tidbitRepository.getTodayTidbit()
                .onSuccess { tidbit ->
                    _discoveryScreenState.update {
                        it.copy(
                            homeTidbitView = HomeTidbitView(
                                loading = false,
                                id = tidbit.id,
                                previewUrl = tidbit.previewUrl,
                                title = tidbit.title,
                                description = tidbit.summary
                            )
                        )
                    }
                }
                .onFailure {
                    _discoveryScreenState.update { state ->
                        state.copy(
                            homeTidbitView = state.homeTidbitView.copy(
                                loading = false,
                                error = "Something went wrong"
                            )
                        )
                    }
                }
        }
    }

    private fun processAction(action: DiscoveryAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

data class DiscoveryScreenState(
    val topPicks: List<TopPickPresentation> = emptyList(),
    val sectors: List<SectorInput> = emptyList(),
    val query: String = "",
    val selected: SectorInput? = null,
    val isGuestSession: Boolean = false,
    val homeTidbitView: HomeTidbitView = HomeTidbitView(),
    val topPicksView: TopPicksView = TopPicksView(),
    val savedGoal: InvestingGoal? = null,
    val showGoalBanner: Boolean = false
) {
    private val goalPicks: List<TopPickPresentation> = picksForGoal(topPicksView.topPicks, savedGoal)

    val noPicksForGoal: Boolean
        get() = goalPicks.isEmpty() && topPicksView.topPicks.isNotEmpty()

    val previewTopPicks: List<TopPickPresentation>
        get() = (goalPicks.ifEmpty { topPicksView.topPicks }).take(PREVIEW_TOP_PICK_COUNT)

    val showTopPicksSection: Boolean
        get() = topPicksView.loading || topPicksView.showError || topPicksView.topPicks.isNotEmpty()
}

private const val PREVIEW_TOP_PICK_COUNT = 2

sealed interface DiscoveryEvent {
    data class AnswerGoal(val goal: InvestingGoal) : DiscoveryEvent
    data object DismissGoalBanner : DiscoveryEvent
    data class ClickTopPick(val id: String) : DiscoveryEvent
    data class ClickTidbit(val id: String) : DiscoveryEvent
    data class GoToCompanyDetail(val ticker: String) : DiscoveryEvent
    data object GoToLogin : DiscoveryEvent
    data object GoToSearch : DiscoveryEvent
    data object GoToBrowse : DiscoveryEvent
    data object GoToAllTopPicks : DiscoveryEvent
    data object RetryTopPicks : DiscoveryEvent
    data object RetryTidbit : DiscoveryEvent
}

sealed interface DiscoveryAction {
    data class OnNavigateToCompanyDetail(val ticker: String) : DiscoveryAction
    data object OnGoBack : DiscoveryAction
    data class OnGoToPickDetail(val id: String) : DiscoveryAction
    data object OnGoToLogin : DiscoveryAction
    data object OnGoToSearch : DiscoveryAction
    data object OnGoToBrowse : DiscoveryAction
    data object OnGoToAllTopPicks : DiscoveryAction
    data class OnGoToTidbitDetail(val id: String) : DiscoveryAction
}
