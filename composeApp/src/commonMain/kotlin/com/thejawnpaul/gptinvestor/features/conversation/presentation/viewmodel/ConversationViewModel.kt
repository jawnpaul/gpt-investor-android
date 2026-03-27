package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.features.billing.domain.BillingConstants
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptResponseUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction.OnCopy
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import io.ktor.http.decodeURLQueryComponent
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ConversationViewModel(
    savedStateHandle: SavedStateHandle,
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val getDefaultPromptResponseUseCase: GetDefaultPromptResponseUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val feedBackRepository: FeedbackRepository,
    private val modelsRepository: ModelsRepository,
    private val billingRepository: IBillingRepository,
    private val appPreferences: AppPreferences,
    private val analyticsLogger: AnalyticsLogger
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
        conversationViewMutableStateFlow.update {
            it.copy(loading = true)
        }
        requestStartTime = Clock.System.now().toEpochMilliseconds()

        logMessageSent(source = "default_prompt", title = prompt.title)
        getDefaultPromptResponseUseCase(prompt) {
            it.onFailure {
                handleConversationFailure(it)
            }
            it.onSuccess { result ->
                result as StructuredConversation
                // update the id of the conversation
                selectedConversationId.update {
                    Logger.e(result.id.toString())
                    result.id
                }

                conversationViewMutableStateFlow.update { state ->
                    state.copy(
                        loading = result.messageList.last().loading,
                        conversation = result,
                        genText = result.messageList.first().response.toString()
                    )
                }
            }
        }
    }

    private fun getInputResponse(query: String? = null) {
        if (query != null && query.trim().isNotBlank()) {
            conversationViewMutableStateFlow.update {
                it.copy(loading = true)
            }
            requestStartTime = Clock.System.now().toEpochMilliseconds()

            logMessageSent(source = "user_input")
            getInputPromptUseCase(
                ConversationPrompt(
                    conversationId = selectedConversationId.value,
                    query = query
                )
            ) {
                it.fold(
                    ::handleInputResponseFailure,
                    ::handleInputResponseSuccess
                )
            }
        } else {
            if (conversation.value.query.trim().isNotEmpty()) {
                conversationViewMutableStateFlow.update {
                    it.copy(loading = true)
                }
                requestStartTime = Clock.System.now().toEpochMilliseconds()
                logMessageSent(source = "user_input")
                getInputPromptUseCase(
                    ConversationPrompt(
                        conversationId = selectedConversationId.value,
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
    }

    private fun getSuggestedPromptResponse(query: String) {
        conversationViewMutableStateFlow.update {
            it.copy(loading = true)
        }
        requestStartTime = Clock.System.now().toEpochMilliseconds()

        logMessageSent(source = "suggested_prompt")
        getInputPromptUseCase(
            ConversationPrompt(
                conversationId = selectedConversationId.value,
                query = query
            )
        ) {
            it.fold(
                ::handleInputResponseFailure,
                ::handleInputResponseSuccess
            )
        }
    }

    private fun handleInputResponseFailure(failure: Failure) {
        handleConversationFailure(failure)
    }

    private fun handleConversationFailure(failure: Failure) {
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
            state.copy(loading = false, conversation = updatedConversation)
        }
        when (failure) {
            is GenAIException -> {
                Logger.e("AI exception")
                processAction(ConversationAction.ShowToast("An error occurred"))
            }

            is Failure.RateLimitExceeded -> {
                analyticsLogger.logEvent(
                    eventName = "rate-limit-hit",
                    params = mapOf(
                        "user_type" to if (conversationViewMutableStateFlow.value.isGuest) "guest" else "logged_in"
                    )
                )
                conversationViewMutableStateFlow.update { state ->
                    state.copy(
                        showRateLimitBottomSheet = state.isGuest
                    )
                }
                Logger.e("Rate limit exceeded")
                if (!conversationViewMutableStateFlow.value.isGuest) {
                    processAction(
                        ConversationAction.ShowToast("Rate limit exceeded. Please try again later.")
                    )
                }
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
                processAction(ConversationAction.ShowToast("No internet connection"))
            }

            is Failure.ServerError -> {
                processAction(ConversationAction.ShowToast("Server error. Please try again later."))
            }

            else -> {
                Logger.e(failure.toString())
                processAction(ConversationAction.ShowToast("Something went wrong"))
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

        selectedConversationId.update {
            conversation.id
        }

        val duration = Clock.System.now().toEpochMilliseconds() - requestStartTime
        analyticsLogger.logEvent(
            eventName = "response-received",
            params = mapOf(
                "duration_ms" to duration,
                "status" to "success"
            )
        )

        conversationViewMutableStateFlow.update { state ->
            state.copy(
                query = "",
                loading = conversation.messageList.last().loading,
                conversation = conversation,
                genText = conversation.messageList.last().response.toString()
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

    fun launchPurchaseFlow(platformContext: PlatformContext) {
        viewModelScope.launch {
            if (conversationViewMutableStateFlow.value.isGuest) {
                appPreferences.setIsGuestLoggedIn(false)
                processAction(ConversationAction.OnSignOutGuest)
            } else {
                val result = billingRepository.launchPurchaseFlow(
                    platformContext = platformContext,
                    productId = BillingConstants.PRO_SUBSCRIPTION_PRODUCT_ID
                )
                handleEvent(ConversationEvent.ShowRateLimitBottomSheet(showBottomSheet = false))
                if (result is BillingResult.Error) {
                    processAction(ConversationAction.ShowToast("Billing Error: ${result.message}"))
                }
            }
        }
    }

    private fun startConversation(title: String?, chatInput: String?) {
        if (chatInput != null) {
            val decodedChatInput =
                chatInput.decodeURLQueryComponent()
            if (title != null) {
                val decodedTitle =
                    title.decodeURLQueryComponent()
                handleEvent(
                    ConversationEvent.DefaultPromptClicked(
                        prompt = DefaultPrompt(
                            title = decodedTitle,
                            query = decodedChatInput
                        )
                    )
                )
            } else {
                handleEvent(ConversationEvent.SendPrompt(query = decodedChatInput))
            }
        }
    }

    private fun logMessageSent(source: String, title: String? = null) {
        val params = buildMap {
            put("source", source)
            title?.let { put("title", it) }
            if (conversation.value.isGuest) {
                put("user_type", "guest")
            } else {
                put("user_type", "logged_in")
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
}

sealed interface ConversationAction {
    data object OnGoBack : ConversationAction
    data class OnGoToWebView(val url: String) : ConversationAction
    data class OnCopy(val text: String) : ConversationAction
    data class ShowToast(val message: String) : ConversationAction
    data object OnSignOutGuest : ConversationAction
    data object OnGoToSignUp : ConversationAction
}

data class HomeChatInput(val title: String? = null, val input: String? = null)
