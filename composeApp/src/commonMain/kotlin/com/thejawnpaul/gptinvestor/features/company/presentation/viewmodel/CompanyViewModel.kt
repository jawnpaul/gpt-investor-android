package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.session.GuestSessionEvent
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.core.session.GuestSessionSource
import com.thejawnpaul.gptinvestor.core.session.rateLimitRetryMessage
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyBriefUseCase
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction.OnCopy
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction.OnGoBack
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.answerFailed
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.stopAnswer
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PREMIUM_SOURCE_WATCHLIST_FULL
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistFailure
import com.thejawnpaul.gptinvestor.features.watchlist.domain.WatchlistRepository
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class CompanyViewModel(
    private val getCompanyBriefUseCase: GetCompanyBriefUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val modelsRepository: ModelsRepository,
    private val feedbackRepository: FeedbackRepository,
    private val appPreferences: AppPreferences,
    private val watchlistRepository: WatchlistRepository,
    private val premiumEventBus: PremiumEventBus,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val guestSessionNotifier: GuestSessionNotifier
) : ViewModel() {

    private val _selectedCompany = MutableStateFlow(SingleCompanyView())
    private val isGuestSession = MutableStateFlow(false)
    val selectedCompany =
        combine(
            _selectedCompany,
            appPreferences.isGuestLoggedIn,
            appPreferences.hasBriefSpotlightSeen
        ) { company, isGuest, spotlightSeen ->
            company.copy(
                isGuestSession = isGuest == true,
                showSpotlight = spotlightSeen != true
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SingleCompanyView()
        )

    private val _companyDetailAction = MutableSharedFlow<CompanyDetailAction>()
    val companyDetailAction get() = _companyDetailAction

    private val _companyFinancials = MutableStateFlow(CompanyFinancialsView())
    val companyFinancials get() = _companyFinancials

    private var upgradeModelId: String? = null

    private var companyTicker = ""

    private val selectedConversationId = MutableStateFlow(-1L)

    private var requestStartTime: Long = 0L

    private var askJob: Job? = null

    @Volatile
    private var askGeneration = 0

    private val selectedCompanyTicker: String?
        get() = savedStateHandle.get<String>("ticker")

    init {
        viewModelScope.launch {
            appPreferences.isGuestLoggedIn.collect { isGuest ->
                isGuestSession.value = isGuest == true
            }
        }
        _selectedCompany.update { it.copy(ticker = selectedCompanyTicker.orEmpty()) }
        getAvailableModels()
        getCompany()
    }

    private fun getPriceHistory(ticker: String) {
        getCompanyUseCase(ticker) {
            it.onSuccess { company ->
                _selectedCompany.update { state -> state.copy(priceHistory = company.historicalData.orEmpty()) }
            }
        }
    }

    private fun getCompany() {
        selectedCompanyTicker?.let { ticker ->
            if (_selectedCompany.value.priceHistory.isEmpty()) getPriceHistory(ticker)
            _selectedCompany.update { it.copy(loading = true) }
            getCompanyBriefUseCase(ticker) {
                it.onFailure { failure ->
                    _selectedCompany.update { state ->
                        state.copy(loading = false, error = "Something went wrong.")
                    }
                    when (failure) {
                        is Failure.GuestSessionExpired -> {
                            // UnauthorizedCallbackImpl already emitted the notifier for 401 path;
                            // emit here too to cover cases where the auth plugin did not intercept
                            guestSessionNotifier.notify(
                                GuestSessionEvent(
                                    reason = GuestSessionReason.Expired,
                                    source = GuestSessionSource.Request,
                                    ticker = ticker
                                )
                            )
                        }
                        is Failure.GuestLimitReached -> {
                            guestSessionNotifier.notify(
                                GuestSessionEvent(
                                    reason = GuestSessionReason.Limit,
                                    source = GuestSessionSource.Request,
                                    ticker = ticker
                                )
                            )
                        }
                        is Failure.RateLimitExceeded -> handleRateLimit(failure, source = "brief_rate_limit", ticker)
                        else -> { }
                    }
                }
                it.onSuccess { company ->
                    _selectedCompany.update { view ->
                        view.copy(
                            conversation = CompanyDetailDefaultConversation(
                                id = 0,
                                response = null
                            ),
                            loading = false,
                            companyName = company.name,
                            header = CompanyHeaderPresentation(
                                companyTicker = company.ticker,
                                companyLogo = company.logoUrl,
                                price = company.price,
                                percentageChange = company.change,
                                companyName = company.name
                            ),
                            brief = company
                        )
                    }
                    askPendingQuestion()
                }
            }
        }
    }

    private fun askPendingQuestion() {
        val question = savedStateHandle.get<String>("ask")?.takeIf { it.isNotBlank() } ?: return
        savedStateHandle["ask"] = null
        getQuery(question)
        getInputResponse()
    }

    fun getQuery(query: String) {
        _selectedCompany.update { it.copy(inputQuery = query) }
    }

    fun getInputResponse() {
        val query = _selectedCompany.value.inputQuery
        if (query.trim().isNotEmpty()) {
            _selectedCompany.update {
                it.copy(pendingQuery = query, isAnswering = true, inputQuery = "", answerOffline = false)
            }
            requestStartTime = Clock.System.now().toEpochMilliseconds()
            analyticsLogger.logEvent(
                eventName = "message-sent",
                params = mapOf(
                    "source" to "user_input"
                )
            )
            askQuery(query)
        }
    }

    private fun retryAnswer(query: String) {
        if (query.isBlank() || _selectedCompany.value.isAnswering) return
        _selectedCompany.update { it.copy(pendingQuery = query, isAnswering = true, answerOffline = false) }
        requestStartTime = Clock.System.now().toEpochMilliseconds()
        askQuery(query)
    }

    private fun askQuery(query: String) {
        when (_selectedCompany.value.conversation) {
            is CompanyDetailDefaultConversation -> {
                ask(
                    ConversationPrompt(
                        query = query,
                        tickerSymbol = _selectedCompany.value.header.companyTicker,
                        conversationId = -1L
                    )
                )
            }

            is StructuredConversation -> {
                ask(ConversationPrompt(query = query, conversationId = selectedConversationId.value))
            }

            else -> {
            }
        }
    }

    fun getSuggestedPromptResponse(query: String) {
        _selectedCompany.update {
            it.copy(pendingQuery = query, isAnswering = true, inputQuery = "", answerOffline = false)
        }
        requestStartTime = Clock.System.now().toEpochMilliseconds()
        analyticsLogger.logEvent(
            eventName = "message-sent",
            params = mapOf(
                "source" to "suggested_prompt"
            )
        )
        ask(ConversationPrompt(query = query, conversationId = selectedConversationId.value))
    }

    private fun ask(prompt: ConversationPrompt) {
        val generation = ++askGeneration
        askJob = getInputPromptUseCase(params = prompt) {
            if (generation == askGeneration) {
                it.fold(
                    ::handleCompanyInputResponseFailure,
                    ::handleCompanyInputResponseSuccess
                )
            }
        }
    }

    private fun stopAnswering() {
        askGeneration++
        askJob?.cancel()
        askJob = null
        var conversationCleared = false
        _selectedCompany.update { state ->
            val conversation = state.conversation as? StructuredConversation
            val stopped = conversation?.let { stopAnswer(it.messageList) }
            val next = when {
                state.pendingQuery != null || conversation == null || stopped == null ->
                    state.copy(inputQuery = state.pendingQuery ?: state.inputQuery)
                stopped.messages.isEmpty() -> {
                    conversationCleared = true
                    state.copy(
                        conversation = CompanyDetailDefaultConversation(id = 0, response = null),
                        inputQuery = stopped.restoredQuery ?: state.inputQuery
                    )
                }
                else -> state.copy(
                    conversation = conversation.copy(messageList = stopped.messages.toMutableList()),
                    inputQuery = stopped.restoredQuery ?: state.inputQuery
                )
            }
            next.copy(pendingQuery = null, isAnswering = false)
        }
        if (conversationCleared) selectedConversationId.value = -1L
        analyticsLogger.logEvent(
            eventName = "answer-stopped",
            params = mapOf("ticker" to (_selectedCompany.value.brief?.ticker ?: ""))
        )
    }

    private fun handleCompanyInputResponseFailure(failure: Failure) {
        Logger.e(failure.toString())
        val shownInline = _selectedCompany.value.let { state ->
            val last = (state.conversation as? StructuredConversation)?.messageList?.lastOrNull()
            state.pendingQuery == null && (last as? GenAiTextMessage)?.answerFailed == true
        }
        when (failure) {
            is Failure.GuestSessionExpired -> {
                guestSessionNotifier.notify(
                    GuestSessionEvent(
                        reason = GuestSessionReason.Expired,
                        source = GuestSessionSource.Request
                    )
                )
            }

            is Failure.GuestLimitReached -> {
                guestSessionNotifier.notify(
                    GuestSessionEvent(
                        reason = GuestSessionReason.Limit,
                        source = GuestSessionSource.Request
                    )
                )
            }

            is Failure.RateLimitExceeded -> {
                val isGuest = isGuestSession.value
                analyticsLogger.logEvent(
                    eventName = "rate-limit-hit",
                    params = mapOf(
                        "user_type" to if (isGuest) "guest" else "authenticated",
                        "upgrade" to failure.upgrade
                    )
                )
                handleRateLimit(failure, source = "company_rate_limit")
            }

            is GenAIException -> {
                if (!shownInline) {
                    processCompanyDetailAction(
                        CompanyDetailAction.ShowToast("An error occurred")
                    )
                }
            }

            is Failure.ContextLimitReached -> {
                processCompanyDetailAction(
                    CompanyDetailAction.ShowToast("Context limit reached.")
                )
            }

            is Failure.NetworkConnection -> {
                if (!shownInline) {
                    processCompanyDetailAction(
                        CompanyDetailAction.ShowToast("No internet connection")
                    )
                }
            }

            is Failure.ServerError -> {
                if (!shownInline) {
                    processCompanyDetailAction(
                        CompanyDetailAction.ShowToast("Server error. Please try again later.")
                    )
                }
            }

            else -> {
                if (!shownInline) {
                    processCompanyDetailAction(
                        CompanyDetailAction.ShowToast("Something went wrong")
                    )
                }
            }
        }
        _selectedCompany.update {
            it.copy(
                error = "Something went wrong.",
                loading = false,
                inputQuery = "",
                pendingQuery = null,
                isAnswering = false,
                answerOffline = failure is Failure.NetworkConnection
            )
        }
    }

    private fun handleCompanyInputResponseSuccess(conversation: Conversation) {
        val s = conversation as StructuredConversation
        Logger.e(s.toString())
        if (s.streamComplete) {
            val duration = Clock.System.now().toEpochMilliseconds() - requestStartTime
            analyticsLogger.logEvent(
                eventName = "response-received",
                params = mapOf(
                    "duration_ms" to duration,
                    "status" to "success"
                )
            )
        }
        _selectedCompany.update {
            it.copy(
                conversation = conversation,
                pendingQuery = null,
                isAnswering = !s.streamComplete && (s.messageList.lastOrNull() as? GenAiTextMessage)?.loading == true
            )
        }
        selectedConversationId.update { s.id }
    }

    fun handleCompanyDetailEvent(event: CompanyDetailEvent) {
        when (event) {
            CompanyDetailEvent.GoBack -> {
                processCompanyDetailAction(OnGoBack)
            }

            is CompanyDetailEvent.QueryInputChanged -> {
                getQuery(event.query)
            }

            CompanyDetailEvent.SendClick -> {
                getInputResponse()
            }

            is CompanyDetailEvent.SuggestedPromptClicked -> {
                getSuggestedPromptResponse(event.query)
            }

            is CompanyDetailEvent.CopyToClipboard -> {
                analyticsLogger.logEvent(
                    eventName = "message-copied",
                    params = mapOf("text_length" to event.text.length)
                )
                processCompanyDetailAction(OnCopy(event.text))
            }

            is CompanyDetailEvent.ModelChange -> {
                analyticsLogger.logEvent(
                    eventName = "model-changed-in-chat",
                    params = mapOf("model_id" to event.model.modelId)
                )
                _selectedCompany.update {
                    it.copy(selectedModel = event.model)
                }
            }

            CompanyDetailEvent.JoinWaitList -> {
                upgradeModelId?.let { modelId ->
                    joinModelWaitlist(modelId = modelId)
                }
            }

            is CompanyDetailEvent.SelectWaitlistOption -> {
                selectWaitListOption(option = event.option)
            }

            is CompanyDetailEvent.UpgradeModel -> {
                _selectedCompany.update {
                    it.copy(showWaitListBottomSheet = event.showBottomSheet)
                }
                event.modelId?.let {
                    upgradeModelId = it
                }
            }

            CompanyDetailEvent.SignUpClicked -> {
                processCompanyDetailAction(CompanyDetailAction.OnGoToSignUp)
            }

            is CompanyDetailEvent.SendFeedback -> {
                sendFeedback(event.messageId, event.status, event.reason)
            }

            is CompanyDetailEvent.BriefKeyNumberExpanded -> {
                analyticsLogger.logEvent(
                    eventName = "brief-key-number-expanded",
                    params = mapOf(
                        "ticker" to (_selectedCompany.value.brief?.ticker ?: ""),
                        "key_number_type" to event.keyNumberType.name,
                        "user_type" to if (isGuestSession.value) "guest" else "authenticated"
                    )
                )
            }

            is CompanyDetailEvent.AskAboutKeyNumber -> {
                analyticsLogger.logEvent(
                    eventName = "brief-key-number-ask-tapped",
                    params = mapOf(
                        "ticker" to (_selectedCompany.value.brief?.ticker ?: ""),
                        "key_number_type" to event.keyNumberType.name,
                        "user_type" to if (isGuestSession.value) "guest" else "authenticated"
                    )
                )
                _selectedCompany.update { it.copy(inputQuery = event.question) }
                getInputResponse()
            }

            is CompanyDetailEvent.RetryAnswer -> {
                retryAnswer(event.query)
            }

            CompanyDetailEvent.RetryLoad -> {
                _selectedCompany.update { it.copy(error = null) }
                getCompany()
            }

            CompanyDetailEvent.ShareBrief -> {
                val brief = _selectedCompany.value.brief ?: return
                processCompanyDetailAction(CompanyDetailAction.OnShare(brief.name, brief.ticker, brief.id))
            }

            CompanyDetailEvent.AddToWatchlist -> {
                if (_selectedCompany.value.watchlistPending) return
                val ticker = _selectedCompany.value.header.companyTicker.ifEmpty { selectedCompanyTicker.orEmpty() }
                _selectedCompany.update { it.copy(watchlistPending = true) }
                viewModelScope.launch {
                    val result = watchlistRepository.addStockToWatchList(ticker)
                    _selectedCompany.update { it.copy(watchlistPending = false) }
                    result.fold(
                        { failure ->
                            if (failure is WatchlistFailure.WatchlistFull) {
                                premiumEventBus.requestPremium(PREMIUM_SOURCE_WATCHLIST_FULL, failure.cap, ticker)
                            } else {
                                processCompanyDetailAction(CompanyDetailAction.WatchlistAddFailed)
                            }
                        },
                        {
                            analyticsLogger.logEvent(
                                eventName = "stock-added-to-watchlist",
                                params = mapOf("ticker" to ticker, "source" to "company_brief")
                            )
                            _selectedCompany.update { it.copy(brief = it.brief?.copy(isWatched = true)) }
                            processCompanyDetailAction(CompanyDetailAction.WatchlistAdded)
                        }
                    )
                }
            }

            CompanyDetailEvent.RemoveFromWatchlist -> {
                if (_selectedCompany.value.watchlistPending) return
                val ticker = _selectedCompany.value.brief?.ticker
                    ?: _selectedCompany.value.header.companyTicker.ifEmpty { selectedCompanyTicker.orEmpty() }
                _selectedCompany.update { it.copy(watchlistPending = true) }
                viewModelScope.launch {
                    val result = watchlistRepository.removeFromWatchlist(ticker)
                    _selectedCompany.update { it.copy(watchlistPending = false) }
                    result.fold(
                        { processCompanyDetailAction(CompanyDetailAction.WatchlistRemoveFailed(ticker)) },
                        {
                            analyticsLogger.logEvent(
                                eventName = "stock-removed-from-watchlist",
                                params = mapOf("ticker" to ticker, "source" to "company_brief")
                            )
                            _selectedCompany.update { it.copy(brief = it.brief?.copy(isWatched = false)) }
                            processCompanyDetailAction(CompanyDetailAction.WatchlistRemoved(ticker))
                        }
                    )
                }
            }

            CompanyDetailEvent.DismissSpotlight -> {
                viewModelScope.launch {
                    appPreferences.setHasBriefSpotlightSeen(true)
                }
            }

            CompanyDetailEvent.StopAnswering -> stopAnswering()

            is CompanyDetailEvent.ShareAnswer -> {
                analyticsLogger.logEvent(
                    eventName = "answer-shared",
                    params = mapOf("ticker" to (_selectedCompany.value.brief?.ticker ?: ""))
                )
                processCompanyDetailAction(CompanyDetailAction.OnShareText(event.text))
            }
        }
    }

    fun processCompanyDetailAction(action: CompanyDetailAction) {
        viewModelScope.launch {
            _companyDetailAction.emit(action)
        }
    }

    private fun handleRateLimit(failure: Failure.RateLimitExceeded, source: String, ticker: String? = null) {
        when {
            isGuestSession.value -> guestSessionNotifier.notify(
                GuestSessionEvent(
                    reason = GuestSessionReason.Limit,
                    source = GuestSessionSource.Request,
                    ticker = ticker
                )
            )

            failure.upgrade -> premiumEventBus.requestPremium(source = source, ticker = ticker)

            else -> viewModelScope.launch {
                _companyDetailAction.emit(
                    CompanyDetailAction.ShowToast(rateLimitRetryMessage(failure.retryAfterSeconds))
                )
            }
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                _selectedCompany.update { it.copy(availableModels = models) }
            }
        }
    }

    private fun selectWaitListOption(option: String) {
        if (_selectedCompany.value.selectedWaitlistOptions.contains(option)) {
            _selectedCompany.update {
                it.copy(
                    selectedWaitlistOptions =
                    it.selectedWaitlistOptions - option
                )
            }
        } else {
            _selectedCompany.update {
                it.copy(
                    selectedWaitlistOptions =
                    it.selectedWaitlistOptions + option
                )
            }
        }
    }

    private fun joinModelWaitlist(modelId: String) {
        viewModelScope.launch {
            modelsRepository.putUserOnModelWaitlist(
                modelId = modelId,
                reasons = _selectedCompany.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
            }
        }
    }

    private fun sendFeedback(messageId: Long, status: Int, reason: String?) {
        _selectedCompany.update {
            (it.conversation as? StructuredConversation)?.let { conversation ->
                val updatedMessages = conversation.messageList.map { message ->
                    if (message.id == messageId) {
                        (message as? GenAiTextMessage)?.copy(feedbackStatus = status)
                            ?: message
                    } else {
                        message
                    }
                }
                it.copy(
                    conversation = conversation.copy(messageList = updatedMessages.toMutableList())
                )
            } ?: it
        }

        viewModelScope.launch {
            analyticsLogger.logEvent(
                eventName = "feedback-given",
                params = mapOf(
                    "message_id" to messageId,
                    "status" to status,
                    "reason" to (reason ?: "")
                )
            )
            feedbackRepository.giveFeedback(messageId = messageId, status = status, reason = reason)
        }
    }
}

sealed interface CompanyDetailEvent {
    data class QueryInputChanged(val query: String) : CompanyDetailEvent
    data object GoBack : CompanyDetailEvent
    data object SendClick : CompanyDetailEvent
    data class SuggestedPromptClicked(val query: String) : CompanyDetailEvent
    data class CopyToClipboard(val text: String) : CompanyDetailEvent
    data class ModelChange(val model: AvailableModel) : CompanyDetailEvent
    data class SelectWaitlistOption(val option: String) : CompanyDetailEvent
    data object JoinWaitList : CompanyDetailEvent
    data object SignUpClicked : CompanyDetailEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : CompanyDetailEvent

    data class SendFeedback(val messageId: Long, val status: Int, val reason: String?) : CompanyDetailEvent
    data class BriefKeyNumberExpanded(val keyNumberType: KeyNumberType) : CompanyDetailEvent
    data class AskAboutKeyNumber(val keyNumberType: KeyNumberType, val question: String) : CompanyDetailEvent
    data object RetryLoad : CompanyDetailEvent
    data class RetryAnswer(val query: String) : CompanyDetailEvent
    data object ShareBrief : CompanyDetailEvent
    data object AddToWatchlist : CompanyDetailEvent
    data object RemoveFromWatchlist : CompanyDetailEvent
    data object DismissSpotlight : CompanyDetailEvent
    data object StopAnswering : CompanyDetailEvent
    data class ShareAnswer(val text: String) : CompanyDetailEvent
}

sealed interface CompanyDetailAction {
    data object OnGoBack : CompanyDetailAction
    data class OnNavigateToWebView(val url: String) : CompanyDetailAction
    data class OnCopy(val text: String) : CompanyDetailAction
    data object OnGoToSignUp : CompanyDetailAction
    data class ShowToast(val message: String) : CompanyDetailAction
    data class OnShare(val name: String, val ticker: String, val id: String) : CompanyDetailAction
    data object WatchlistAdded : CompanyDetailAction
    data object WatchlistAddFailed : CompanyDetailAction
    data class WatchlistRemoved(val ticker: String) : CompanyDetailAction
    data class WatchlistRemoveFailed(val ticker: String) : CompanyDetailAction
    data class OnShareText(val text: String) : CompanyDetailAction
    data class OnOpenCompany(val ticker: String) : CompanyDetailAction
}
