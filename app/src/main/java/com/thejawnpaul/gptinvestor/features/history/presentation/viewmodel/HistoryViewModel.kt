package com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.conversation.data.error.GenAIException
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetAllHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.GetSingleHistoryUseCase
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryConversationView
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getSingleHistoryUseCase: GetSingleHistoryUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase
) :
    ViewModel() {

    private val _historyScreenView = MutableStateFlow(HistoryScreenView())
    val historyScreenView get() = _historyScreenView

    private val _conversationView = MutableStateFlow(HistoryConversationView())
    val conversation get() = _conversationView

    private val _genText = MutableStateFlow("")
    val genText = _genText

    private val conversationId: Long?
        get() = savedStateHandle.get<Long>("conversationId")

    init {
        getAllHistory()
    }

    private fun getAllHistory() {
        _historyScreenView.update { it.copy(loading = true) }
        getAllHistoryUseCase(GetAllHistoryUseCase.None()) {
            it.fold(
                ::handleGetAllHistoryFailure,
                ::handleGetAllHistorySuccess
            )
        }
    }

    private fun handleGetAllHistoryFailure(failure: Failure) {
        _historyScreenView.update { it.copy(loading = false) }
        Timber.e(failure.toString())
    }

    private fun handleGetAllHistorySuccess(response: List<StructuredConversation>) {
        _historyScreenView.update { it.copy(loading = false, list = response) }

    }

    fun updateConversationId(conversationId: String) {
        savedStateHandle["conversationId"] = conversationId.toLong()
        getConversation()
    }

    private fun getConversation() {
        _conversationView.update { it.copy(loading = true) }
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
        _conversationView.update { it.copy(loading = false) }
        Timber.e(failure.toString())
    }

    private fun handleGetSingleHistorySuccess(conversation: StructuredConversation) {
        _conversationView.update { it.copy(loading = false, conversation = conversation) }
    }

    fun updateInput(input: String) {
        _conversationView.update { it.copy(query = input) }
    }

    fun getInputResponse() {
        if (conversation.value.query.trim().isNotEmpty()) {
            _conversationView.update {
                it.copy(loading = true)
            }
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

        _genText.update {
            conversation.messageList.last().response.toString()
        }

        _conversationView.update { state ->
            state.copy(
                query = "",
                loading = conversation.messageList.last().loading,
                conversation = conversation
            )
        }
    }

    fun getSuggestedPromptResponse(query: String) {
        _conversationView.update {
            it.copy(loading = true)
        }
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
}