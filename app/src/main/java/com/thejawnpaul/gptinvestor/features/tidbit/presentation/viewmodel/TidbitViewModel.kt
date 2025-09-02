package com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TidbitViewModel @Inject constructor(
    private val repository: TidbitRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tidbitId: String?
        get() = savedStateHandle.get<String>("tidbitId")

    private val _tidbitDetailState = MutableStateFlow(TidbitDetailState())
    val tidbitDetailState get() = _tidbitDetailState

    private val _tidbitMainScreenState = MutableStateFlow(TidbitScreenState())
    val tidbitMainScreenState get() = _tidbitMainScreenState

    private val _tidbitDetailActions = MutableSharedFlow<TidbitDetailAction>()
    val tidbitDetailActions get() = _tidbitDetailActions

    private val _actions = MutableSharedFlow<TidbitAction>()
    val actions get() = _actions

    private fun getTidbit() {
        _tidbitDetailState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            tidbitId?.let { id ->
                repository.getTidbit(id).onSuccess { tidbit ->
                    _tidbitDetailState.update {
                        it.copy(
                            id = tidbit.id,
                            isLoading = false,
                            presentation = mapTidbitToPresentation(tidbit)
                        )
                    }
                }.onFailure {
                    _tidbitDetailState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun handleDetailEvent(event: TidbitDetailEvent) {
        when (event) {
            is TidbitDetailEvent.GetTidbit -> {
                updateTidbitId(tidbitId = event.id)
            }

            TidbitDetailEvent.GoBack -> {
                handleDetailAction(action = TidbitDetailAction.OnGoBack)
            }

            is TidbitDetailEvent.OnClickLike -> {
            }

            is TidbitDetailEvent.OnClickShare -> {
                viewModelScope.launch {
                    repository.getShareableLink(_tidbitDetailState.value.id).onSuccess {
                        handleDetailAction(action = TidbitDetailAction.OnShare(shareText = it))
                    }
                }
            }

            is TidbitDetailEvent.OnClickSource -> {
                _tidbitDetailState.value.presentation?.let { presentation ->
                    when (presentation) {
                        is TidbitPresentation.ArticlePresentation -> {
                            handleDetailAction(action = OnOpenSource(url = presentation.sourceUrl))
                        }

                        is TidbitPresentation.VideoPresentation -> {
                            handleDetailAction(action = OnOpenSource(url = presentation.sourceUrl))
                        }

                        is TidbitPresentation.AudioPresentation -> {
                            handleDetailAction(action = OnOpenSource(url = presentation.sourceUrl))
                        }
                    }
                }
            }
        }
    }

    private fun updateTidbitId(tidbitId: String) {
        savedStateHandle["tidbitId"] = tidbitId
        getTidbit()
    }

    fun handleDetailAction(action: TidbitDetailAction) {
        viewModelScope.launch {
            _tidbitDetailActions.emit(action)
        }
    }

    private fun mapTidbitToPresentation(tidbit: Tidbit): TidbitPresentation {
        return when (tidbit.type) {
            "text" -> TidbitPresentation.ArticlePresentation(
                id = tidbit.id,
                name = tidbit.title,
                previewUrl = tidbit.previewUrl,
                mediaUrl = tidbit.mediaUrl,
                title = tidbit.title,
                content = tidbit.content,
                originalAuthor = tidbit.originalAuthor,
                category = tidbit.category,
                sourceUrl = tidbit.sourceUrl
            )

            "video" -> TidbitPresentation.VideoPresentation(
                id = tidbit.id,
                name = tidbit.title,
                previewUrl = tidbit.previewUrl,
                mediaUrl = tidbit.mediaUrl,
                title = tidbit.title,
                content = tidbit.content,
                originalAuthor = tidbit.originalAuthor,
                category = tidbit.category,
                sourceUrl = tidbit.sourceUrl

            )

            "audio" -> TidbitPresentation.AudioPresentation(
                id = tidbit.id,
                name = tidbit.title,
                previewUrl = tidbit.previewUrl,
                mediaUrl = tidbit.mediaUrl,
                title = tidbit.title,
                content = tidbit.content,
                originalAuthor = tidbit.originalAuthor,
                category = tidbit.category,
                sourceUrl = tidbit.sourceUrl

            )

            else -> {
                TidbitPresentation.ArticlePresentation(
                    id = tidbit.id,
                    name = tidbit.title,
                    previewUrl = tidbit.previewUrl,
                    mediaUrl = tidbit.mediaUrl,
                    title = tidbit.title,
                    content = tidbit.content,
                    originalAuthor = tidbit.originalAuthor,
                    category = tidbit.category,
                    sourceUrl = tidbit.sourceUrl
                )
            }
        }
    }

    fun handleMainScreenEvent(event: TidbitScreenEvent) {
        when (event) {
            TidbitScreenEvent.GetAllTidbits -> {
                getAllTidbits()
            }

            TidbitScreenEvent.OnBackClick -> {
                handleAction(action = TidbitAction.OnGoBack)
            }
            is TidbitScreenEvent.OnFilterSelected -> {
                _tidbitMainScreenState.update {
                    it.copy(selectedOption = event.filter)
                }
                handleFilter(event.filter)
            }

            TidbitScreenEvent.OnSearchClick -> {}
            is TidbitScreenEvent.OnTidbitClick -> {
                handleAction(action = TidbitAction.OnGoToTidbitDetail(tidbitId = event.tidbitId))
            }
            is TidbitScreenEvent.OnTidbitLikeClick -> {}
            is TidbitScreenEvent.OnTidbitSaveClick -> {
            }

            is TidbitScreenEvent.OnTidbitShareClick -> {
                viewModelScope.launch {
                    repository.getShareableLink(event.tidbitId).onSuccess {
                        handleAction(action = TidbitAction.OnShare(shareText = it))
                    }
                }
            }
        }
    }

    private fun getAllTidbits() {
        _tidbitMainScreenState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getAllTidbits().onSuccess { tidbits ->
                _tidbitMainScreenState.update {
                    it.copy(
                        isLoading = false,
                        tidbits = tidbits.map { tidbit ->
                            mapTidbitToPresentation(tidbit)
                        }
                    )
                }
            }.onFailure {
            }
        }
    }

    private fun handleFilter(filter: SectorInput) {
        when (filter) {
            SectorInput.AllSector -> {
                getAllTidbits()
            }

            is SectorInput.CustomSector -> {
                when (filter.sectorKey) {
                    "new" -> {
                        getNewTidbits()
                    }

                    "trending" -> {
                        getTrendingTidbits()
                    }

                    "saved" -> {
                        getSavedTidbits()
                    }

                    else -> {
                        getAllTidbits()
                    }
                }
            }
        }
    }

    private fun getTrendingTidbits() {
        _tidbitMainScreenState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getTrendingTidbits().onSuccess { tidbits ->
                _tidbitMainScreenState.update {
                    it.copy(
                        isLoading = false,
                        tidbits = tidbits.map { tidbit ->
                            mapTidbitToPresentation(tidbit)
                        }
                    )
                }
            }.onFailure {
            }
        }
    }

    private fun getSavedTidbits() {
        _tidbitMainScreenState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getSavedTidbits().onSuccess { tidbits ->
                _tidbitMainScreenState.update {
                    it.copy(
                        isLoading = false,
                        tidbits = tidbits.map { tidbit ->
                            mapTidbitToPresentation(tidbit)
                        }
                    )
                }
            }
        }
    }

    private fun getNewTidbits() {
        _tidbitMainScreenState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            repository.getNewTidbits().onSuccess { tidbits ->
                _tidbitMainScreenState.update {
                    it.copy(
                        isLoading = false,
                        tidbits = tidbits.map { tidbit ->
                            mapTidbitToPresentation(tidbit)
                        }
                    )
                }
            }
        }
    }

    fun handleAction(action: TidbitAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

data class TidbitDetailState(
    val id: String = "",
    val isLoading: Boolean = false,
    val presentation: TidbitPresentation? = null
)

data class TidbitScreenState(
    val options: List<SectorInput> = listOf(
        SectorInput.AllSector,
        SectorInput.CustomSector(
            sectorName = "New",
            sectorKey = "new"
        ),
        SectorInput.CustomSector(
            sectorName = "Saved",
            sectorKey = "saved"
        ),
        SectorInput.CustomSector(
            sectorName = "Trending",
            sectorKey = "trending"
        )
    ),
    val selectedOption: SectorInput? = SectorInput.AllSector,
    val isLoading: Boolean = false,
    val tidbits: List<TidbitPresentation> = emptyList()
)

sealed interface TidbitScreenEvent {

    data object GetAllTidbits : TidbitScreenEvent
    data object OnBackClick : TidbitScreenEvent

    data object OnSearchClick : TidbitScreenEvent

    data class OnFilterSelected(val filter: SectorInput) : TidbitScreenEvent

    data class OnTidbitClick(val tidbitId: String) : TidbitScreenEvent

    data class OnTidbitLikeClick(val tidbitId: String) : TidbitScreenEvent

    data class OnTidbitSaveClick(val tidbitId: String) : TidbitScreenEvent

    data class OnTidbitShareClick(val tidbitId: String) : TidbitScreenEvent
}

sealed interface TidbitAction {
    data object OnGoBack : TidbitAction
    data class OnShare(val shareText: String) : TidbitAction
    data class OnGoToTidbitDetail(val tidbitId: String) : TidbitAction
}

sealed interface TidbitDetailEvent {
    data class GetTidbit(val id: String) : TidbitDetailEvent
    data object GoBack : TidbitDetailEvent
    data class OnClickLike(val id: String) : TidbitDetailEvent
    data object OnClickSource : TidbitDetailEvent
    data object OnClickShare : TidbitDetailEvent
}

sealed interface TidbitDetailAction {
    data object OnGoBack : TidbitDetailAction
    data class OnOpenSource(val url: String) : TidbitDetailAction
    data class OnShare(val shareText: String) : TidbitDetailAction
}
