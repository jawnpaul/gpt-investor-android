package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
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
import com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel.ConversationAction.*
import com.thejawnpaul.gptinvestor.features.feedback.FeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.onSuccess
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val getDefaultPromptResponseUseCase: GetDefaultPromptResponseUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val fedBackRepository: FeedbackRepository,
    private val modelsRepository: ModelsRepository
) :
    ViewModel() {

    private val conversationViewMutableStateFlow = MutableStateFlow(ConversationView())
    val conversation get() = conversationViewMutableStateFlow

    private val _actions = MutableSharedFlow<ConversationAction>()
    val actions get() = _actions

    private val _conversationMessages = MutableStateFlow(mutableListOf<GenAiTextMessage>())
    val conversationMessages get() = _conversationMessages

    private val selectedConversationId = MutableStateFlow(-1L)

    private var upgradeModelId: String? = null

    init {
        getDefaultPrompts()
        getAvailableModels()
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
        getDefaultPromptResponseUseCase(prompt) {
            it.onFailure {
                handleConversationFailure(it)
            }
            it.onSuccess { result ->
                result as StructuredConversation
                // update the id of the conversation
                selectedConversationId.update {
                    Timber.e(result.id.toString())
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
            val updatedConversation = (state.conversation as? StructuredConversation)?.let { conversation ->
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
                Timber.e("AI exception")
                processAction(ConversationAction.ShowToast( "An error occurred"))
            }

            is Failure.RateLimitExceeded -> {
                conversationViewMutableStateFlow.update { state ->
                    state.copy(
                        showRateLimitBottomSheet = true
                    )
                }
                Timber.e("Rate limit exceeded")
                processAction(ConversationAction.ShowToast("Rate limit exceeded. Please try again later."))
            }

            is Failure.ContextLimitReached -> {
                Timber.e("Context limit reached")
                processAction(ConversationAction.ShowToast("Context limit reached."))
            }

            is Failure.NetworkConnection -> {
                processAction(ConversationAction.ShowToast("No internet connection"))
            }

            is Failure.ServerError -> {
                processAction(ConversationAction.ShowToast("Server error. Please try again later."))
            }

            else -> {
                Timber.e(failure.toString())
                processAction(ConversationAction.ShowToast("Something went wrong"))
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

        selectedConversationId.update {
            conversation.id
        }

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
                conversationViewMutableStateFlow.update { it.copy(showWaitListBottomSheet = event.showBottomSheet) }
                event.modelId?.let {
                    upgradeModelId = it
                }
            }

            is ConversationEvent.ShowRateLimitBottomSheet -> {
                conversationViewMutableStateFlow.update {
                    it.copy(showRateLimitBottomSheet = event.showBottomSheet)
                }
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
                it.copy(conversation = conversation.copy(messageList = updatedMessages.toMutableList()))
            } ?: it
        }
        viewModelScope.launch {
            fedBackRepository.giveFeedback(messageId, status, reason)
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                conversationViewMutableStateFlow.update {
                    it.copy(availableModels = models)
                }
            }.onFailure {
                Timber.e(it.toString())
            }
        }
    }

    private fun selectWaitListOption(option: String) {
        if (conversationViewMutableStateFlow.value.selectedWaitlistOptions.contains(option)) {
            conversationViewMutableStateFlow.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions - option) }
        } else {
            conversationViewMutableStateFlow.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions + option) }
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
                Timber.e(failure.stackTraceToString())
            }
        }
    }
}

sealed interface ConversationEvent {

    data class CopyToClipboard(val text: String) : ConversationEvent

    data class SendFeedback(val messageId: Long, val status: Int, val reason: String?) :
        ConversationEvent

    data class UpdateInputQuery(val query: String) : ConversationEvent

    data class DefaultPromptClicked(val prompt: DefaultPrompt) : ConversationEvent

    data class SuggestedPromptClicked(val prompt: String) : ConversationEvent
    data class SendPrompt(val query: String? = null) : ConversationEvent
    data class ModelChanged(val model: AvailableModel) : ConversationEvent
    data class SelectWaitlistOption(val option: String) : ConversationEvent
    data object JoinWaitlist : ConversationEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) :
        ConversationEvent

    data class ShowRateLimitBottomSheet(val showBottomSheet: Boolean) : ConversationEvent
}

sealed interface ConversationAction {
    data object OnGoBack : ConversationAction
    data class OnGoToWebView(val url: String) : ConversationAction
    data class OnCopy(val text: String) : ConversationAction
    data class ShowToast(val message: String) : ConversationAction
}
