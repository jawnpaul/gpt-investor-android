package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptResponseUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val getDefaultPromptResponseUseCase: GetDefaultPromptResponseUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase
) :
    ViewModel() {

    private val conversationViewMutableStateFlow = MutableStateFlow(ConversationView())
    val conversation get() = conversationViewMutableStateFlow

    private val _genText = MutableStateFlow("I am default")
    val genText = _genText

    private val _conversationMessages = MutableStateFlow(mutableListOf<GenAiMessage>())
    val conversationMessages get() = _conversationMessages

    private val _selectedConversationId = MutableStateFlow(0L)

    init {
        getDefaultPrompts()
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

    fun getDefaultPromptResponse(prompt: DefaultPrompt) {
        conversationViewMutableStateFlow.update {
            it.copy(loading = true)
        }
        getDefaultPromptResponseUseCase(prompt) {
            it.onFailure {
            }
            it.onSuccess { result ->
                result as StructuredConversation
                // update the id of the conversation
                _selectedConversationId.update {
                    Timber.e(result.id.toString())
                    result.id
                }
                _genText.update {
                    result.messageList.first().response.toString()
                }

                conversationViewMutableStateFlow.update { state ->
                    state.copy(loading = result.messageList.last().loading, conversation = result)
                }
            }
        }
    }

    fun getInputResponse() {
        conversationViewMutableStateFlow.update {
            it.copy(loading = true)
        }
        getInputPromptUseCase(
            ConversationPrompt(
                conversationId = _selectedConversationId.value,
                query = conversation.value.query
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

        _selectedConversationId.update {
            Timber.e(conversation.id.toString())
            conversation.id
        }

        _genText.update {
            conversation.messageList.last().response.toString()
        }
        conversationViewMutableStateFlow.update { state ->
            state.copy(
                query = "",
                loading = conversation.messageList.last().loading,
                conversation = conversation
            )
        }
    }
}
