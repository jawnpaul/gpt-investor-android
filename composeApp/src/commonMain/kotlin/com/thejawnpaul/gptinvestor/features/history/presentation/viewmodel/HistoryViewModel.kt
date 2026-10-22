package com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.session.GuestRateLimitNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionEvent
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.core.session.GuestSessionSource
import com.thejawnpaul.gptinvestor.core.session.rateLimitRetryMessage
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.answerFailed
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.stopAnswer
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.withFailedAnswer
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.withdrawFailedAnswer
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetAllHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetSingleHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryConversationView
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction.OnCopy
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction.OnGoToHistoryDetail
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import kotlin.time.Clock
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class HistoryViewModel(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getSingleHistoryUseCase: GetSingleHistoryUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val feedBackRepository: FeedbackRepository,
    private val modelsRepository: ModelsRepository,
    private val premiumEventBus: PremiumEventBus,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val guestRateLimitNotifier: GuestRateLimitNotifier,
    private val guestSessionNotifier: GuestSessionNotifier
) : ViewModel() {

    private val _historyScreenViewState = MutableStateFlow(HistoryScreenView())
    val historyScreenViewState get() = _historyScreenViewState

    private val _actions = MutableSharedFlow<HistoryScreenAction>()
    val actions get() = _actions

    private val conversationView = MutableStateFlow(HistoryConversationView())
    val conversation get() = conversationView

    private val _historyDetailAction = MutableSharedFlow<HistoryDetailAction>()
    val historyDetailAction get() = _historyDetailAction

    private var upgradeModelId: String? = null

    private var requestStartTime: Long = 0L

    private var answerJob: Job? = null

    private var answerGeneration = 0

    private val conversationId: Long?
        get() {
            val id = savedStateHandle.get<Any>("conversationId")
            return when (id) {
                is Long -> id
                is String -> id.toLongOrNull()
                else -> null
            }
        }

    init {
        getAllHistory()
        getAvailableModels()
        getConversation()
    }

    private fun getAllHistory() {
        _historyScreenViewState.update { it.copy(loading = true) }
        getAllHistoryUseCase(GetAllHistoryUseCase.None()) {
            it.fold(
                ::handleGetAllHistoryFailure,
                ::handleGetAllHistorySuccess
            )
        }
    }

    private fun handleGetAllHistoryFailure(failure: Failure) {
        _historyScreenViewState.update { it.copy(loading = false) }
        Logger.e(failure.toString())
        viewModelScope.launch {
            _actions.emit(HistoryScreenAction.ShowToast("Failed to load history"))
        }
    }

    private fun handleGetAllHistorySuccess(response: Map<String, List<StructuredConversation>>) {
        _historyScreenViewState.update {
            it.copy(loading = false, list = response)
        }
    }

    fun updateConversationId(conversationId: String) {
        savedStateHandle["conversationId"] = conversationId.toLong()
        getConversation()
    }

    private fun getConversation() {
        conversationId?.let { id ->

            getSingleHistoryUseCase(id) {
                it.fold(
                    ::handleGetSingleHistoryFailure,
                    ::handleGetSingleHistorySuccess
                )
            }
        }
    }

    private fun handleGetSingleHistoryFailure(failure: Failure) {
        Logger.e(failure.toString())
        processHistoryDetailAction(HistoryDetailAction.ShowToast("Failed to load conversation"))
    }

    private fun handleGetSingleHistorySuccess(conversation: StructuredConversation) {
        conversationView.update { it.copy(conversation = conversation) }
    }

    fun updateInput(input: String) {
        conversationView.update { it.copy(query = input) }
    }

    fun getInputResponse() {
        val query = conversation.value.query
        if (query.trim().isNotEmpty()) ask(query = query, source = "user_input")
    }

    private fun retryAnswer(query: String) {
        if (query.isBlank() || conversation.value.loading) return
        val current = conversation.value.conversation as? StructuredConversation
        val failed = current?.let { withdrawFailedAnswer(it.messageList, query) }
        if (current != null && failed != null) {
            conversationView.update {
                it.copy(conversation = current.copy(messageList = failed.messages.toMutableList()))
            }
        }
        ask(query = query, source = null, replacesMessageId = failed?.messageId)
    }

    private fun ask(query: String, source: String?, replacesMessageId: Long? = null) {
        answerGeneration++
        val generation = answerGeneration
        conversationView.update {
            it.copy(
                loading = true,
                pendingQuery = query,
                query = if (source == null) it.query else ""
            )
        }
        requestStartTime = Clock.System.now().toEpochMilliseconds()
        source?.let { logMessageSent(source = it) }
        answerJob = getInputPromptUseCase(
            ConversationPrompt(
                conversationId = conversationId ?: -1L,
                query = query,
                replacesMessageId = replacesMessageId
            )
        ) {
            if (generation == answerGeneration) {
                it.fold(
                    ::handleInputResponseFailure,
                    ::handleInputResponseSuccess
                )
            }
        }
    }

    private fun stopAnswering() {
        answerGeneration++
        answerJob?.cancel()
        answerJob = null
        withdrawAnswer()
    }

    private fun withdrawAnswer() {
        conversationView.update { state ->
            val conversation = state.conversation as? StructuredConversation
            val stopped = conversation?.let { stopAnswer(it.messageList) }
            val next = when {
                state.pendingQuery != null || conversation == null || stopped == null ->
                    state.copy(query = state.pendingQuery ?: state.query)
                else -> state.copy(
                    conversation = conversation.copy(messageList = stopped.messages.toMutableList()),
                    query = stopped.restoredQuery ?: state.query
                )
            }
            next.copy(pendingQuery = null, loading = false)
        }
    }

    private fun handleInputResponseFailure(failure: Failure) {
        if (failure is Failure.GuestLimitReached) {
            withdrawAnswer()
            if (conversation.value.isGuest) {
                guestSessionNotifier.notify(
                    GuestSessionEvent(reason = GuestSessionReason.QuestionLimit, source = GuestSessionSource.Stream)
                )
            }
            return
        }
        var shownInline = false
        conversationView.update { state ->
            val updatedConversation =
                (state.conversation as? StructuredConversation)?.let { conversation ->
                    val updatedMessages = ArrayList(conversation.messageList)
                    if (updatedMessages.isNotEmpty()) {
                        val lastMessage = updatedMessages.last()
                        if (lastMessage is GenAiTextMessage && lastMessage.loading) {
                            updatedMessages[updatedMessages.size - 1] = lastMessage.copy(
                                loading = false,
                                response = lastMessage.response ?: "Couldn't generate a response"
                            )
                        }
                    }
                    conversation.copy(messageList = updatedMessages)
                } ?: state.conversation
            shownInline = state.pendingQuery == null &&
                ((updatedConversation as? StructuredConversation)?.messageList?.lastOrNull() as? GenAiTextMessage)
                    ?.answerFailed == true
            state.copy(
                loading = false,
                conversation = updatedConversation,
                pendingQuery = null,
                query = state.pendingQuery ?: state.query,
                offlineAnswerIds = state.offlineAnswerIds.withFailedAnswer(
                    updatedConversation,
                    offline = failure is Failure.NetworkConnection
                )
            )
        }
        when (failure) {
            is GenAIException -> {
                Logger.e("AI exception")
                if (!shownInline) processHistoryDetailAction(HistoryDetailAction.ShowToast("An error occurred"))
            }

            is Failure.RateLimitExceeded -> {
                val isGuest = conversation.value.isGuest
                analyticsLogger.logEvent(
                    eventName = "rate-limit-hit",
                    params = mapOf(
                        "user_type" to if (isGuest) "guest" else "authenticated",
                        "upgrade" to failure.upgrade
                    )
                )
                Logger.e("Rate limit exceeded")
                conversationView.update { it.copy(showRateLimitBottomSheet = false) }
                when {
                    isGuest -> guestRateLimitNotifier.notifyRateLimit()

                    failure.upgrade -> premiumEventBus.requestPremium(source = "history_rate_limit")

                    else -> viewModelScope.launch {
                        processHistoryDetailAction(
                            HistoryDetailAction.ShowToast(rateLimitRetryMessage(failure.retryAfterSeconds))
                        )
                    }
                }
            }

            is Failure.ContextLimitReached -> {
                Logger.e("Context limit reached")
                conversationView.update { it.copy(showRateLimitBottomSheet = false) }
                processHistoryDetailAction(HistoryDetailAction.ShowToast("Context limit reached."))
            }

            is Failure.NetworkConnection -> {
                if (!shownInline) processHistoryDetailAction(HistoryDetailAction.ShowToast("No internet connection"))
            }

            is Failure.ServerError -> {
                if (!shownInline) {
                    processHistoryDetailAction(
                        HistoryDetailAction.ShowToast("Server error. Please try again later.")
                    )
                }
            }

            else -> {
                Logger.e(failure.toString())
                if (!shownInline) processHistoryDetailAction(HistoryDetailAction.ShowToast("Something went wrong"))
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation
        if (conversationId != conversation.id) savedStateHandle["conversationId"] = conversation.id

        if (conversation.streamComplete) {
            val duration = Clock.System.now().toEpochMilliseconds() - requestStartTime
            analyticsLogger.logEvent(
                eventName = "response-received",
                params = mapOf(
                    "duration_ms" to duration,
                    "status" to "success"
                )
            )
        }

        conversationView.update { state ->
            state.copy(
                loading = !conversation.streamComplete &&
                    conversation.messageList.lastOrNull { it is GenAiTextMessage }?.loading == true,
                conversation = conversation,
                pendingQuery = null
            )
        }
    }

    fun sendFeedback(messageId: Long, status: Int, reason: String?) {
        conversationView.update {
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
            feedBackRepository.giveFeedback(messageId, status, reason)
        }
    }

    fun handleEvent(event: HistoryScreenEvent) {
        when (event) {
            is HistoryScreenEvent.HistoryItemClicked -> {
                viewModelScope.launch {
                    _actions.emit(OnGoToHistoryDetail(event.conversationId))
                }
            }

            HistoryScreenEvent.GoBack -> {
                viewModelScope.launch {
                    _actions.emit(HistoryScreenAction.OnGoBack)
                }
            }
        }
    }

    fun handleHistoryDetailEvent(event: HistoryDetailEvent) {
        when (event) {
            is HistoryDetailEvent.ClickSuggestedPrompt -> {
                getSuggestedPromptResponse(query = event.prompt)
            }

            is HistoryDetailEvent.RetryAnswer -> {
                retryAnswer(event.query)
            }

            is HistoryDetailEvent.GetHistory -> {
                updateConversationId(event.conversationId.toString())
            }

            HistoryDetailEvent.GetInputResponse -> {
                getInputResponse()
            }

            is HistoryDetailEvent.SendFeedback -> {
                sendFeedback(event.messageId, event.status, event.reason)
            }

            is HistoryDetailEvent.UpdateInputQuery -> {
                updateInput(event.input)
            }

            is HistoryDetailEvent.CopyToClipboard -> {
                analyticsLogger.logEvent(
                    eventName = "message-copied",
                    params = mapOf("text_length" to event.text.length)
                )
                processHistoryDetailAction(OnCopy(event.text))
            }

            is HistoryDetailEvent.ModelChange -> {
                analyticsLogger.logEvent(
                    eventName = "model-changed-in-chat",
                    params = mapOf("model_id" to event.model.modelId)
                )
                conversationView.update { it.copy(selectedModel = event.model) }
            }

            HistoryDetailEvent.JoinWaitlist -> {
                upgradeModelId?.let {
                    joinModelWaitlist(it)
                }
            }

            is HistoryDetailEvent.SelectWaitlistOption -> {
                selectWaitListOption(event.option)
            }

            is HistoryDetailEvent.UpgradeModel -> {
                conversationView.update { it.copy(showWaitListBottomSheet = event.showBottomSheet) }
                event.modelId?.let {
                    upgradeModelId = it
                }
            }

            is HistoryDetailEvent.ShowRateLimitBottomSheet -> {
                conversationView.update {
                    it.copy(showRateLimitBottomSheet = event.showBottomSheet)
                }
            }

            HistoryDetailEvent.GoToLogin -> {
                processHistoryDetailAction(HistoryDetailAction.OnGoToLogin)
            }

            HistoryDetailEvent.StopAnswering -> {
                stopAnswering()
            }

            is HistoryDetailEvent.ShareAnswer -> {
                processHistoryDetailAction(HistoryDetailAction.OnShare(event.text))
            }

            HistoryDetailEvent.GoToHistory -> {
                processHistoryDetailAction(HistoryDetailAction.OnGoToHistory)
            }

            is HistoryDetailEvent.OpenCompany -> {
                processHistoryDetailAction(HistoryDetailAction.OnOpenCompany(event.ticker))
            }
        }
    }

    fun processHistoryDetailAction(action: HistoryDetailAction) {
        viewModelScope.launch {
            _historyDetailAction.emit(action)
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                conversationView.update { it.copy(availableModels = models) }
            }.onFailure {
                Logger.e(it.toString())
            }
        }
    }

    private fun selectWaitListOption(option: String) {
        if (conversationView.value.selectedWaitlistOptions.contains(option)) {
            conversationView.update {
                it.copy(
                    selectedWaitlistOptions =
                    it.selectedWaitlistOptions - option
                )
            }
        } else {
            conversationView.update {
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
                reasons = conversationView.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
            }
        }
    }

    fun handleUpgradeClick() {
        viewModelScope.launch {
            handleHistoryDetailEvent(HistoryDetailEvent.ShowRateLimitBottomSheet(showBottomSheet = false))
            premiumEventBus.requestPremium(source = "history_rate_limit")
        }
    }

    private fun getSuggestedPromptResponse(query: String) {
        ask(query = query, source = "suggested_prompt")
    }

    private fun logMessageSent(source: String) {
        val params = buildMap {
            put("source", source)
            if (conversation.value.isGuest) {
                put("user_type", "guest")
            } else {
                put("user_type", "authenticated")
            }
        }
        analyticsLogger.logEvent(eventName = "message-sent", params = params)
    }
}

sealed interface HistoryScreenEvent {
    data class HistoryItemClicked(val conversationId: Long) : HistoryScreenEvent
    data object GoBack : HistoryScreenEvent
}

sealed interface HistoryScreenAction {
    data class OnGoToHistoryDetail(val conversationId: Long) : HistoryScreenAction
    data object OnGoBack : HistoryScreenAction
    data class ShowToast(val message: String) : HistoryScreenAction
}

sealed interface HistoryDetailEvent {
    data class GetHistory(val conversationId: Long) : HistoryDetailEvent
    data class SendFeedback(val messageId: Long, val status: Int, val reason: String?) : HistoryDetailEvent

    data class UpdateInputQuery(val input: String) : HistoryDetailEvent
    data object GetInputResponse : HistoryDetailEvent
    data class ClickSuggestedPrompt(val prompt: String) : HistoryDetailEvent
    data class CopyToClipboard(val text: String) : HistoryDetailEvent
    data class ModelChange(val model: AvailableModel) : HistoryDetailEvent
    data object JoinWaitlist : HistoryDetailEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : HistoryDetailEvent

    data class SelectWaitlistOption(val option: String) : HistoryDetailEvent

    data class ShowRateLimitBottomSheet(val showBottomSheet: Boolean) : HistoryDetailEvent
    data object GoToLogin : HistoryDetailEvent
    data object StopAnswering : HistoryDetailEvent
    data class RetryAnswer(val query: String) : HistoryDetailEvent
    data class ShareAnswer(val text: String) : HistoryDetailEvent
    data object GoToHistory : HistoryDetailEvent
    data class OpenCompany(val ticker: String) : HistoryDetailEvent
}

sealed interface HistoryDetailAction {
    data object OnGoBack : HistoryDetailAction
    data class OnGoToWebView(val url: String) : HistoryDetailAction
    data class OnCopy(val text: String) : HistoryDetailAction
    data class ShowToast(val message: String) : HistoryDetailAction
    data object OnGoToLogin : HistoryDetailAction
    data class OnShare(val text: String) : HistoryDetailAction
    data object OnGoToHistory : HistoryDetailAction
    data class OnOpenCompany(val ticker: String) : HistoryDetailAction
}
