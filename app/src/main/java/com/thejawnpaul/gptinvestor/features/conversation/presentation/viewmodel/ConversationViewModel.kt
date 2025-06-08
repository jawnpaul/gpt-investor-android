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
                    state.copy(conversation = DefaultConversation(prompts = result))
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
        when (failure) {
            is GenAIException -> {
                Timber.e("AI exception")
            }

            else -> {
                Timber.e(failure.toString())
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

        selectedConversationId.update {
            Timber.e(conversation.id.toString())
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
}

sealed interface ConversationAction {
    data object OnGoBack : ConversationAction
    data class OnGoToWebView(val url: String) : ConversationAction
    data class OnCopy(val text: String) : ConversationAction
}
