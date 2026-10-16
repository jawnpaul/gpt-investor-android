package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

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
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.answerFailed
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptResponseUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction.OnCopy
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
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
class ConversationViewModel(
    savedStateHandle: SavedStateHandle,
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val getDefaultPromptResponseUseCase: GetDefaultPromptResponseUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val feedBackRepository: FeedbackRepository,
    private val modelsRepository: ModelsRepository,
    private val premiumEventBus: PremiumEventBus,
    private val appPreferences: AppPreferences,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val guestSessionNotifier: GuestSessionNotifier
) : ViewModel() {

    private val conversationViewMutableStateFlow = MutableStateFlow(ConversationView())
    val conversation get() = conversationViewMutableStateFlow

    private val _actions = MutableSharedFlow<ConversationAction>()
    val actions get() = _actions

    private val _conversationMessages = MutableStateFlow(mutableListOf<GenAiTextMessage>())
    val conversationMessages get() = _conversationMessages

    private val selectedConversationId = MutableStateFlow(-1L)

    private var upgradeModelId: String? = null

    val title: String? = savedStateHandle["title"]
    val chatInput: String? = savedStateHandle["chatInput"]

    private var requestStartTime: Long = 0L

    private var answerJob: Job? = null

    private var answerGeneration = 0

    init {
        getDefaultPrompts()
        getAvailableModels()
        startConversation(title = title, chatInput = chatInput)
        checkGuestStatus()
    }

    private fun checkGuestStatus() {
        viewModelScope.launch {
            appPreferences.isGuestLoggedIn.collect { isGuest ->
                conversationViewMutableStateFlow.update { it.copy(isGuest = isGuest ?: false) }
            }
        }
    }

    fun updateInput(input: String) {
        conversationViewMutableStateFlow.update { it.copy(query = input) }
    }

    private fun getDefaultPrompts() {
        getDefaultPromptsUseCase(GetDefaultPromptsUseCase.None()) {
            it.onFailure {
            }

            it.onSuccess { result ->
                conversationViewMutableStateFlow.update { state ->
                    if (state.conversation is DefaultConversation) {
                        state.copy(conversation = DefaultConversation(prompts = result))
                    } else {
                        state
                    }
                }
            }
        }
    }

    private fun getDefaultPromptResponse(prompt: DefaultPrompt) {
        startAnswer(prompt.query)
        logMessageSent(source = "default_prompt", title = prompt.title)
        val generation = answerGeneration
        answerJob = getDefaultPromptResponseUseCase(prompt) {
            if (generation == answerGeneration) {
                it.fold(::handleConversationFailure, ::handleDefaultPromptResponseSuccess)
            }
        }
    }

    private fun handleDefaultPromptResponseSuccess(result: Conversation) {
        result as StructuredConversation
        selectedConversationId.update { result.id }
        conversationViewMutableStateFlow.update { state ->
            state.copy(
                loading = !result.streamComplete &&
                    result.messageList.lastOrNull { it is GenAiTextMessage }?.loading == true,
                conversation = result,
                pendingQuery = null
            )
        }
    }

    private fun getInputResponse(query: String? = null) {
        val prompt = query?.takeIf { it.isNotBlank() } ?: conversation.value.query
        if (prompt.trim().isNotEmpty()) ask(query = prompt, source = "user_input")
    }

    private fun getSuggestedPromptResponse(query: String) {
        ask(query = query, source = "suggested_prompt")
    }

    private fun retryAnswer(query: String) {
        if (query.isBlank() || conversation.value.loading) return
        ask(query = query, source = null)
    }

    private fun ask(query: String, source: String?) {
        startAnswer(query, keepDraft = source == null)
        source?.let { logMessageSent(source = it) }
        val generation = answerGeneration
        answerJob = getInputPromptUseCase(
            ConversationPrompt(
                conversationId = selectedConversationId.value,
                query = query
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

    private fun startAnswer(query: String, keepDraft: Boolean = false) {
        answerGeneration++
        conversationViewMutableStateFlow.update {
            it.copy(loading = true, pendingQuery = query, query = if (keepDraft) it.query else "")
        }
        requestStartTime = Clock.System.now().toEpochMilliseconds()
    }

    private fun stopAnswering() {
        answerGeneration++
        answerJob?.cancel()
        answerJob = null
        conversationViewMutableStateFlow.update { state ->
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
        handleConversationFailure(failure)
    }

    private fun handleConversationFailure(failure: Failure) {
        var shownInline = false
        conversationViewMutableStateFlow.update { state ->
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
                query = state.pendingQuery ?: state.query
            )
        }
        when (failure) {
            is GenAIException -> {
                Logger.e("AI exception")
                if (!shownInline) processAction(ConversationAction.ShowToast("An error occurred"))
            }

            is Failure.GuestSessionExpired -> {
                guestSessionNotifier.notify(
                    GuestSessionEvent(reason = GuestSessionReason.Expired, source = GuestSessionSource.Stream)
                )
            }

            is Failure.GuestLimitReached -> {
                val isGuest = conversationViewMutableStateFlow.value.isGuest
                if (isGuest) {
                    guestSessionNotifier.notify(
                        GuestSessionEvent(reason = GuestSessionReason.Limit, source = GuestSessionSource.Stream)
                    )
                }
            }

            is Failure.RateLimitExceeded -> {
                val isGuest = conversationViewMutableStateFlow.value.isGuest
                analyticsLogger.logEvent(
                    eventName = "rate-limit-hit",
                    params = mapOf("user_type" to if (isGuest) "guest" else "authenticated")
                )
                Logger.e("Rate limit exceeded")
                if (isGuest) {
                    guestSessionNotifier.notify(
                        GuestSessionEvent(reason = GuestSessionReason.Limit, source = GuestSessionSource.Stream)
                    )
                } else {
                    processAction(
                        ConversationAction.ShowToast("Rate limit exceeded. Please try again later.")
                    )
                }
                conversationViewMutableStateFlow.update { it.copy(showRateLimitBottomSheet = false) }
            }

            is Failure.ContextLimitReached -> {
                Logger.e("Context limit reached")
                processAction(ConversationAction.ShowToast("Context limit reached."))
                conversationViewMutableStateFlow.update { state ->
                    state.copy(
                        showRateLimitBottomSheet = false
                    )
                }
            }

            is Failure.NetworkConnection -> {
                if (!shownInline) processAction(ConversationAction.ShowToast("No internet connection"))
            }

            is Failure.ServerError -> {
                if (!shownInline) processAction(ConversationAction.ShowToast("Server error. Please try again later."))
            }

            else -> {
                Logger.e(failure.toString())
                if (!shownInline) processAction(ConversationAction.ShowToast("Something went wrong"))
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

        selectedConversationId.update {
            conversation.id
        }

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

        conversationViewMutableStateFlow.update { state ->
            state.copy(
                loading = !conversation.streamComplete &&
                    conversation.messageList.lastOrNull { it is GenAiTextMessage }?.loading == true,
                conversation = conversation,
                pendingQuery = null
            )
        }
    }

    fun handleEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.CopyToClipboard -> {
                analyticsLogger.logEvent(
                    eventName = "message-copied",
                    params = mapOf("text_length" to event.text.length)
                )
                processAction(OnCopy(event.text))
            }

            is ConversationEvent.SendFeedback -> {
                sendFeedback(event.messageId, event.status, event.reason)
            }

            is ConversationEvent.DefaultPromptClicked -> {
                getDefaultPromptResponse(event.prompt)
            }

            is ConversationEvent.SendPrompt -> {
                getInputResponse(event.query)
            }

            is ConversationEvent.SuggestedPromptClicked -> {
                getSuggestedPromptResponse(event.prompt)
            }

            is ConversationEvent.RetryAnswer -> {
                retryAnswer(event.query)
            }

            is ConversationEvent.UpdateInputQuery -> {
                updateInput(event.query)
            }

            is ConversationEvent.ModelChanged -> {
                analyticsLogger.logEvent(
                    eventName = "model-changed-in-chat",
                    params = mapOf("model_id" to event.model.modelId)
                )
                conversationViewMutableStateFlow.update {
                    it.copy(selectedModel = event.model)
                }
            }

            ConversationEvent.JoinWaitlist -> {
                upgradeModelId?.let {
                    joinModelWaitlist(it)
                }
            }

            is ConversationEvent.SelectWaitlistOption -> {
                selectWaitListOption(event.option)
            }

            is ConversationEvent.UpgradeModel -> {
                conversationViewMutableStateFlow.update {
                    it.copy(showWaitListBottomSheet = event.showBottomSheet)
                }
                event.modelId?.let {
                    upgradeModelId = it
                }
            }

            is ConversationEvent.ShowRateLimitBottomSheet -> {
                conversationViewMutableStateFlow.update {
                    it.copy(showRateLimitBottomSheet = event.showBottomSheet)
                }
            }

            ConversationEvent.GoToSignUp -> {
                processAction(ConversationAction.OnGoToSignUp)
            }

            ConversationEvent.StopAnswering -> {
                stopAnswering()
            }

            is ConversationEvent.ShareAnswer -> {
                processAction(ConversationAction.OnShare(event.text))
            }

            ConversationEvent.GoToHistory -> {
                processAction(ConversationAction.OnGoToHistory)
            }

            is ConversationEvent.OpenCompany -> {
                processAction(ConversationAction.OnOpenCompany(event.ticker))
            }
        }
    }

    fun processAction(action: ConversationAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }

    private fun sendFeedback(messageId: Long, status: Int, reason: String?) {
        conversationViewMutableStateFlow.update {
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

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                conversationViewMutableStateFlow.update {
                    it.copy(availableModels = models)
                }
            }.onFailure {
                Logger.e(it.toString())
            }
        }
    }

    private fun selectWaitListOption(option: String) {
        if (conversationViewMutableStateFlow.value.selectedWaitlistOptions.contains(option)) {
            conversationViewMutableStateFlow.update {
                it.copy(
                    selectedWaitlistOptions =
                    it.selectedWaitlistOptions - option
                )
            }
        } else {
            conversationViewMutableStateFlow.update {
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
                reasons = conversationViewMutableStateFlow.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
            }
        }
    }

    fun handleUpgradeClick() {
        viewModelScope.launch {
            if (conversationViewMutableStateFlow.value.isGuest) {
                appPreferences.setIsGuestLoggedIn(false)
                processAction(ConversationAction.OnSignOutGuest)
            } else {
                handleEvent(ConversationEvent.ShowRateLimitBottomSheet(showBottomSheet = false))
                premiumEventBus.requestPremium(source = "conversation_rate_limit")
            }
        }
    }

    private fun startConversation(title: String?, chatInput: String?) {
        val resolved = resolveConversationStart(title, chatInput) ?: return
        if (resolved.first != null) {
            handleEvent(
                ConversationEvent.DefaultPromptClicked(
                    prompt = DefaultPrompt(
                        title = resolved.first!!,
                        query = resolved.second
                    )
                )
            )
        } else {
            handleEvent(ConversationEvent.SendPrompt(query = resolved.second))
        }
    }

    private fun logMessageSent(source: String, title: String? = null) {
        val params = buildMap {
            put("source", source)
            title?.let { put("title", it) }
            if (conversation.value.isGuest) {
                put("user_type", "guest")
            } else {
                put("user_type", "authenticated")
            }
        }
        analyticsLogger.logEvent(eventName = "message-sent", params = params)
    }
}

sealed interface ConversationEvent {

    data class CopyToClipboard(val text: String) : ConversationEvent

    data class SendFeedback(val messageId: Long, val status: Int, val reason: String?) : ConversationEvent

    data class UpdateInputQuery(val query: String) : ConversationEvent

    data class DefaultPromptClicked(val prompt: DefaultPrompt) : ConversationEvent

    data class SuggestedPromptClicked(val prompt: String) : ConversationEvent
    data class SendPrompt(val query: String? = null) : ConversationEvent
    data class ModelChanged(val model: AvailableModel) : ConversationEvent
    data class SelectWaitlistOption(val option: String) : ConversationEvent
    data object JoinWaitlist : ConversationEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : ConversationEvent

    data class ShowRateLimitBottomSheet(val showBottomSheet: Boolean) : ConversationEvent
    data object GoToSignUp : ConversationEvent
    data object StopAnswering : ConversationEvent
    data class RetryAnswer(val query: String) : ConversationEvent
    data class ShareAnswer(val text: String) : ConversationEvent
    data object GoToHistory : ConversationEvent
    data class OpenCompany(val ticker: String) : ConversationEvent
}

sealed interface ConversationAction {
    data object OnGoBack : ConversationAction
    data class OnGoToWebView(val url: String) : ConversationAction
    data class OnCopy(val text: String) : ConversationAction
    data class ShowToast(val message: String) : ConversationAction
    data object OnSignOutGuest : ConversationAction
    data object OnGoToSignUp : ConversationAction
    data class OnShare(val text: String) : ConversationAction
    data object OnGoToHistory : ConversationAction
    data class OnOpenCompany(val ticker: String) : ConversationAction
}

data class HomeChatInput(val title: String? = null, val input: String? = null)

internal fun resolveConversationStart(title: String?, chatInput: String?): Pair<String?, String>? {
    if (chatInput == null) return null
    return Pair(title, chatInput)
}
