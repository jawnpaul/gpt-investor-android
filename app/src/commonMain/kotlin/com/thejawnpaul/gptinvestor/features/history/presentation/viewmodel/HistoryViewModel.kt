package com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.functional.Failure
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class HistoryViewModel(
    private val getAllHistoryUseCase: GetAllHistoryUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getSingleHistoryUseCase: GetSingleHistoryUseCase,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val fedBackRepository: FeedbackRepository,
    private val modelsRepository: ModelsRepository
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

    private val conversationId: Long?
        get() = savedStateHandle.get<Long>("conversationId")

    init {
        getAllHistory()
        getAvailableModels()
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
                Logger.e("AI exception")
            }

            is Failure.RateLimitExceeded -> {
                Logger.e("Rate limit exceeded")
            }

            else -> {
                Logger.e(failure.toString())
            }
        }
    }

    private fun handleInputResponseSuccess(conversation: Conversation) {
        conversation as StructuredConversation

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
                it.copy(conversation = conversation.copy(messageList = updatedMessages.toMutableList()))
            } ?: it
        }
        viewModelScope.launch {
            fedBackRepository.giveFeedback(messageId, status, reason)
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

    fun processAction(action: HistoryScreenAction) {
        when (action) {
            is OnGoToHistoryDetail -> {
            }

            HistoryScreenAction.OnGoBack -> {
            }
        }
    }

    fun handleHistoryDetailEvent(event: HistoryDetailEvent) {
        when (event) {
            is HistoryDetailEvent.ClickSuggestedPrompt -> {
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
                processHistoryDetailAction(OnCopy(event.text))
            }

            is HistoryDetailEvent.ModelChange -> {
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
            conversationView.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions - option) }
        } else {
            conversationView.update { it.copy(selectedWaitlistOptions = it.selectedWaitlistOptions + option) }
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
}

sealed interface HistoryScreenEvent {
    data class HistoryItemClicked(val conversationId: Long) : HistoryScreenEvent
    data object GoBack : HistoryScreenEvent
}

sealed interface HistoryScreenAction {
    data class OnGoToHistoryDetail(val conversationId: Long) : HistoryScreenAction
    data object OnGoBack : HistoryScreenAction
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
}

sealed interface HistoryDetailAction {
    data object OnGoBack : HistoryDetailAction
    data class OnGoToWebView(val url: String) : HistoryDetailAction
    data class OnCopy(val text: String) : HistoryDetailAction
}
