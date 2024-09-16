package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptResponseUseCase
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetDefaultPromptsUseCase
import com.thejawnpaul.gptinvestor.features.conversation.presentation.state.ConversationView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val getDefaultPromptsUseCase: GetDefaultPromptsUseCase,
    private val getDefaultPromptResponseUseCase: GetDefaultPromptResponseUseCase
) :
    ViewModel() {

    private val conversationViewMutableStateFlow = MutableStateFlow(ConversationView())
    val conversation get() = conversationViewMutableStateFlow

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
                val a = result as StructuredConversation
                conversationViewMutableStateFlow.update { state ->
                    state.copy(loading = a.messageList.last().loading, conversation = result)
                }
            }
        }
    }
}
