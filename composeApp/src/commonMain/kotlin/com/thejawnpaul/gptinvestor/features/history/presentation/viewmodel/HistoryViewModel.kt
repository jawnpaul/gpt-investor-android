package com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.domain.BillingConstants
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetAllHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetSingleHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryConversationView
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryDetailAction.OnCopy
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenAction.OnGoToHistoryDetail
import kotlin.time.Clock
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
    private val billingRepository: IBillingRepository,
    @Provided private val analyticsLogger: AnalyticsLogger
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
        conversationView.update { it.copy(loading = true) }
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
        conversationView.update { it.copy(loading = false) }
        Logger.e(failure.toString())
        processHistoryDetailAction(HistoryDetailAction.ShowToast("Failed to load conversation"))
    }

    private fun handleGetSingleHistorySuccess(conversation: StructuredConversation) {
        conversationView.update { it.copy(loading = false, conversation = conversation) }
    }

    fun updateInput(input: String) {
        conversationView.update { it.copy(query = input) }
    }

    fun getInputResponse() {
        if (conversation.value.query.trim().isNotEmpty()) {
            conversationView.update {
                it.copy(loading = true)
            }

            requestStartTime = Clock.System.now().toEpochMilliseconds()
            logMessageSent(source = "user_input")
            getInputPromptUseCase(
                ConversationPrompt(
                    conversationId = conversationId ?: -1L,
                    query = conversation.value.query
                )
            ) {
                it.fold(
                    ::handleInputResponseFailure,
                    ::handleInputResponseSuccess
                )
            }
        }
    }

    private fun handleInputResponseFailure(failure: Failure) {
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
            state.copy(loading = false, conversation = updatedConversation)
        }
        when (failure) {
            is GenAIException -> {
                Logger.e("AI exception")
                processHistoryDetailAction(HistoryDetailAction.ShowToast("An error occurred"))
            }

            is Failure.RateLimitExceeded -> {
                analyticsLogger.logEvent(
                    eventName = "rate-limit-hit",
                    params = mapOf(
                        "user_type" to if (conversation.value.isGuest) "guest" else "logged_in"
                    )
                )
                Logger.e("Rate limit exceeded")
                conversationView.update { it.copy(showRateLimitBottomSheet = false) }
                processHistoryDetailAction(
                    HistoryDetailAction.ShowToast("Rate limit exceeded. Please try again later.")
                )
            }

            is Failure.ContextLimitReached -> {
                Logger.e("Context limit reached")
                conversationView.update { it.copy(showRateLimitBottomSheet = false) }
                processHistoryDetailAction(HistoryDetailAction.ShowToast("Context limit reached."))
            }

            is Failure.NetworkConnection -> {
                processHistoryDetailAction(HistoryDetailAction.ShowToast("No internet connection"))
            }

            is Failure.ServerError -> {
                processHistoryDetailAction(
                    HistoryDetailAction.ShowToast("Server error. Please try again later.")
                )
            }

            else -> {
                Logger.e(failure.toString())
                processHistoryDetailAction(HistoryDetailAction.ShowToast("Something went wrong"))
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

        val duration = Clock.System.now().toEpochMilliseconds() - requestStartTime
        analyticsLogger.logEvent(
            eventName = "response-received",
            params = mapOf(
                "duration_ms" to duration,
                "status" to "success"
            )
        )

        conversationView.update { state ->
            state.copy(
                query = "",
                loading = conversation.messageList.last().loading,
                conversation = conversation,
                genText = conversation.messageList.last().response.toString()
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

            HistoryDetailEvent.GoToSignUp -> {
                processHistoryDetailAction(HistoryDetailAction.OnGoToSignUp)
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

    fun launchPurchaseFlow(platformContext: PlatformContext) {
        viewModelScope.launch {
            val result = billingRepository.launchPurchaseFlow(
                platformContext = platformContext,
                productId = BillingConstants.PRO_SUBSCRIPTION_PRODUCT_ID
            )
            handleHistoryDetailEvent(
                HistoryDetailEvent.ShowRateLimitBottomSheet(showBottomSheet = false)
            )
            if (result is BillingResult.Error) {
                processHistoryDetailAction(
                    HistoryDetailAction.ShowToast("Billing Error: ${result.message}")
                )
            }
        }
    }

    private fun getSuggestedPromptResponse(query: String) {
        conversationView.update {
            it.copy(loading = true)
        }
        logMessageSent(source = "suggested_prompt")
        getInputPromptUseCase(
            ConversationPrompt(
                conversationId = conversationId ?: -1L,
                query = query
            )
        ) {
            it.fold(
                ::handleInputResponseFailure,
                ::handleInputResponseSuccess
            )
        }
    }

    private fun logMessageSent(source: String) {
        val params = buildMap {
            put("source", source)
            if (conversation.value.isGuest) {
                put("user_type", "guest")
            } else {
                put("user_type", "logged_in")
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
    data object GoToSignUp : HistoryDetailEvent
}

sealed interface HistoryDetailAction {
    data object OnGoBack : HistoryDetailAction
    data class OnGoToWebView(val url: String) : HistoryDetailAction
    data class OnCopy(val text: String) : HistoryDetailAction
    data class ShowToast(val message: String) : HistoryDetailAction
    data object OnGoToSignUp : HistoryDetailAction
}
