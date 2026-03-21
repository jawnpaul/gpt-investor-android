package com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.functional.onFailure
import com.thejawnpaul.gptinvestor.core.functional.onSuccess
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.company.domain.usecases.GetCompanyUseCase
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyFinancialsView
import com.thejawnpaul.gptinvestor.features.company.presentation.state.CompanyHeaderPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.state.SingleCompanyView
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction.OnCopy
import com.thejawnpaul.gptinvestor.features.company.presentation.viewmodel.CompanyDetailAction.OnGoBack
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.CompanyDetailDefaultConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ConversationPrompt
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.ModelsRepository
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.GetInputPromptUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class CompanyViewModel(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val getInputPromptUseCase: GetInputPromptUseCase,
    private val modelsRepository: ModelsRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _selectedCompany = MutableStateFlow(SingleCompanyView())
    val selectedCompany =
        combine(_selectedCompany, appPreferences.isGuestLoggedIn) { company, isGuest ->
            company.copy(isGuestSession = isGuest == true)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SingleCompanyView()
        )

    private val _companyDetailAction = MutableSharedFlow<CompanyDetailAction>()
    val companyDetailAction get() = _companyDetailAction

    private val _companyFinancials = MutableStateFlow(CompanyFinancialsView())
    val companyFinancials get() = _companyFinancials

    private var upgradeModelId: String? = null

    private var companyTicker = ""

    private val selectedConversationId = MutableStateFlow(-1L)

    private val selectedCompanyTicker: String?
        get() = savedStateHandle.get<String>("ticker")

    init {
        getAvailableModels()
        getCompany()
    }

    fun updateTicker(ticker: String) {
        savedStateHandle["ticker"] = ticker
        getCompany()
    }

    private fun getCompany() {
        selectedCompanyTicker?.let { ticker ->
            _selectedCompany.update { it.copy(loading = true) }
            getCompanyUseCase(ticker) {
                it.onFailure {
                    _selectedCompany.update { state ->
                        state.copy(
                            loading = false,
                            error = "Something went wrong."
                        )
                    }
                }
                it.onSuccess { company ->
                    _selectedCompany.update { view ->
                        view.copy(
                            conversation = CompanyDetailDefaultConversation(
                                id = 0,
                                response = company
                            ),
                            loading = false,
                            companyName = company.name ?: "",
                            header = CompanyHeaderPresentation(
                                companyTicker = company.ticker,
                                companyLogo = company.imageUrl?.toHttpsUrl() ?: "",
                                price = company.price ?: 0.0f,
                                percentageChange = company.change ?: 0.0f,
                                companyName = company.name ?: ""
                            )
                        )
                    }
                }
            }
        }
    }

    fun getQuery(query: String) {
        _selectedCompany.update { it.copy(inputQuery = query) }
    }

    fun getInputResponse() {
        if (_selectedCompany.value.inputQuery.trim().isNotEmpty()) {
            _selectedCompany.update { it.copy(loading = true) }
            when (_selectedCompany.value.conversation) {
                is CompanyDetailDefaultConversation -> {
                    val prompt = ConversationPrompt(
                        query = _selectedCompany.value.inputQuery,
                        tickerSymbol = _selectedCompany.value.header.companyTicker,
                        conversationId = -1L
                    )
                    getInputPromptUseCase(params = prompt) {
                        it.fold(
                            ::handleCompanyInputResponseFailure,
                            ::handleCompanyInputResponseSuccess
                        )
                    }
                }

                is StructuredConversation -> {
                    val prompt = ConversationPrompt(
                        query = _selectedCompany.value.inputQuery,
                        conversationId = selectedConversationId.value
                    )
                    getInputPromptUseCase(params = prompt) {
                        it.fold(
                            ::handleCompanyInputResponseFailure,
                            ::handleCompanyInputResponseSuccess
                        )
                    }
                }

                else -> {
                }
            }
        }
    }

    fun getSuggestedPromptResponse(query: String) {
        _selectedCompany.update { it.copy(loading = true) }
        val prompt = ConversationPrompt(
            query = query,
            conversationId = selectedConversationId.value
        )
        getInputPromptUseCase(params = prompt) {
            it.fold(
                ::handleCompanyInputResponseFailure,
                ::handleCompanyInputResponseSuccess
            )
        }
    }

    private fun handleCompanyInputResponseFailure(failure: Failure) {
        Logger.e(failure.toString())
        _selectedCompany.update {
            it.copy(
                error = "Something went wrong.",
                loading = false,
                inputQuery = ""
            )
        }
    }

    private fun handleCompanyInputResponseSuccess(conversation: Conversation) {
        val s = conversation as StructuredConversation
        Logger.e(s.toString())
        _selectedCompany.update {
            it.copy(
                conversation = conversation,
                loading = false,
                inputQuery = ""
            )
        }
        _selectedCompany.update { it.copy(genText = s.messageList.last().response.toString()) }
        selectedConversationId.update { s.id }
    }

    fun handleCompanyDetailEvent(event: CompanyDetailEvent) {
        when (event) {
            CompanyDetailEvent.GoBack -> {
                processCompanyDetailAction(OnGoBack)
            }

            is CompanyDetailEvent.QueryInputChanged -> {
                getQuery(event.query)
            }

            CompanyDetailEvent.SendClick -> {
                getInputResponse()
            }

            is CompanyDetailEvent.UpdateTicker -> {
                updateTicker(event.ticker)
            }

            is CompanyDetailEvent.SuggestedPromptClicked -> {
                getSuggestedPromptResponse(event.query)
            }

            is CompanyDetailEvent.CopyToClipboard -> {
                processCompanyDetailAction(OnCopy(event.text))
            }

            is CompanyDetailEvent.ModelChange -> {
                _selectedCompany.update {
                    it.copy(selectedModel = event.model)
                }
            }

            CompanyDetailEvent.JoinWaitList -> {
                upgradeModelId?.let { modelId ->
                    joinModelWaitlist(modelId = modelId)
                }
            }

            is CompanyDetailEvent.SelectWaitlistOption -> {
                selectWaitListOption(option = event.option)
            }

            is CompanyDetailEvent.UpgradeModel -> {
                _selectedCompany.update {
                    it.copy(showWaitListBottomSheet = event.showBottomSheet)
                }
                event.modelId?.let {
                    upgradeModelId = it
                }
            }
        }
    }

    fun processCompanyDetailAction(action: CompanyDetailAction) {
        viewModelScope.launch {
            _companyDetailAction.emit(action)
        }
    }

    private fun getAvailableModels() {
        viewModelScope.launch {
            modelsRepository.getAvailableModels().onSuccess { models ->
                _selectedCompany.update { it.copy(availableModels = models) }
            }
        }
    }

    private fun selectWaitListOption(option: String) {
        if (_selectedCompany.value.selectedWaitlistOptions.contains(option)) {
            _selectedCompany.update {
                it.copy(
                    selectedWaitlistOptions =
                    it.selectedWaitlistOptions - option
                )
            }
        } else {
            _selectedCompany.update {
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
                reasons = _selectedCompany.value.selectedWaitlistOptions
            ).onSuccess {
                getAvailableModels()
            }.onFailure { failure ->
                Logger.e(failure.stackTraceToString())
            }
        }
    }
}

sealed interface CompanyDetailEvent {
    data class UpdateTicker(val ticker: String) : CompanyDetailEvent
    data class QueryInputChanged(val query: String) : CompanyDetailEvent
    data object GoBack : CompanyDetailEvent
    data object SendClick : CompanyDetailEvent
    data class SuggestedPromptClicked(val query: String) : CompanyDetailEvent
    data class CopyToClipboard(val text: String) : CompanyDetailEvent
    data class ModelChange(val model: AvailableModel) : CompanyDetailEvent
    data class SelectWaitlistOption(val option: String) : CompanyDetailEvent
    data object JoinWaitList : CompanyDetailEvent
    data class UpgradeModel(val showBottomSheet: Boolean, val modelId: String? = null) : CompanyDetailEvent
}

sealed interface CompanyDetailAction {
    data object OnGoBack : CompanyDetailAction
    data class OnNavigateToWebView(val url: String) : CompanyDetailAction
    data class OnCopy(val text: String) : CompanyDetailAction
}
