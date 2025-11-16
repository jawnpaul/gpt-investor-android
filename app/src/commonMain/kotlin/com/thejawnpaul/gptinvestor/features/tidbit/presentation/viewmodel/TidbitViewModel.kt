package com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction.OnGoBack
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction.OnOpenSource
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitDetailAction.OnShare
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TidbitViewModel(
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

    private val _currentPagingFilter = MutableStateFlow<PagingFilterType>(PagingFilterType.All)
    val currentPagingFilter: StateFlow<PagingFilterType> = _currentPagingFilter

    @OptIn(ExperimentalCoroutinesApi::class)
    val tidbitsPagingData: Flow<PagingData<TidbitPresentation>> =
        _currentPagingFilter.flatMapLatest { filterType ->
            val sourceFlow: Flow<PagingData<Tidbit>> = when (filterType) {
                PagingFilterType.All -> repository.getAllTidbitsPaged()
                PagingFilterType.New -> repository.getNewTidbitsPaged()
                PagingFilterType.Saved -> repository.getBookmarkedTidbitsPaged()
                PagingFilterType.Trending -> repository.getTrendingTidbitsPaged()
            }
            sourceFlow.map { pagingData ->
                pagingData.map { tidbit ->
                    mapTidbitToPresentation(tidbit)
                }
            }.cachedIn(viewModelScope)
        }

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
                handleDetailAction(action = OnGoBack)
            }

            is TidbitDetailEvent.OnClickShare -> {
                viewModelScope.launch {
                    repository.getShareableLink(_tidbitDetailState.value.id).onSuccess {
                        handleDetailAction(action = OnShare(shareText = it))
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

            is TidbitDetailEvent.OnClickBookmark -> {
                viewModelScope.launch {
                    if (event.newValue) {
                        repository.bookmarkTidbit(tidbitId = event.id)
                    } else {
                        repository.removeBookmark(tidbitId = event.id)
                    }
                }
            }

            is TidbitDetailEvent.OnClickLike -> {
                viewModelScope.launch {
                    if (event.newValue) {
                        repository.likeTidbit(tidbitId = event.id)
                    } else {
                        repository.unlikeTidbit(tidbitId = event.id)
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

    private fun mapTidbitToPresentation(tidbit: Tidbit): TidbitPresentation = when (tidbit.type) {
        "text" -> TidbitPresentation.ArticlePresentation(
            id = tidbit.id,
            name = tidbit.title,
            previewUrl = tidbit.previewUrl,
            mediaUrl = tidbit.mediaUrl,
            title = tidbit.title,
            content = tidbit.content,
            originalAuthor = tidbit.originalAuthor,
            category = tidbit.category,
            sourceUrl = tidbit.sourceUrl,
            isBookmarked = tidbit.isBookmarked,
            isLiked = tidbit.isLiked,
            summary = tidbit.summary
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
            sourceUrl = tidbit.sourceUrl,
            isBookmarked = tidbit.isBookmarked,
            isLiked = tidbit.isLiked,
            summary = tidbit.summary
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
            sourceUrl = tidbit.sourceUrl,
            isBookmarked = tidbit.isBookmarked,
            isLiked = tidbit.isLiked,
            summary = tidbit.summary
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
                sourceUrl = tidbit.sourceUrl,
                isBookmarked = tidbit.isBookmarked,
                isLiked = tidbit.isLiked
            )
        }
    }

    fun handleMainScreenEvent(event: TidbitScreenEvent) {
        when (event) {
            TidbitScreenEvent.GetAllTidbits -> {
                if (_tidbitMainScreenState.value.selectedOption == null) {
                    getAllTidbits()
                } else {
                    handleFilter(_tidbitMainScreenState.value.selectedOption!!)
                }
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

            is TidbitScreenEvent.OnTidbitLikeClick -> {
                viewModelScope.launch {
                    if (event.newValue) {
                        repository.likeTidbit(tidbitId = event.tidbitId)
                    } else {
                        repository.unlikeTidbit(tidbitId = event.tidbitId)
                    }
                }
            }

            is TidbitScreenEvent.OnTidbitSaveClick -> {
                viewModelScope.launch {
                    if (event.newValue) {
                        repository.bookmarkTidbit(tidbitId = event.tidbitId)
                    } else {
                        repository.removeBookmark(tidbitId = event.tidbitId)
                    }
                }
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
        /*_tidbitMainScreenState.update { it.copy(isLoading = true) }
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
        }*/
    }

    /*private fun handleFilter(filter: SectorInput) {
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
    }*/

    private fun handleFilter(filter: SectorInput) {
        val newFilterType = when (filter) {
            SectorInput.AllSector -> PagingFilterType.All
            is SectorInput.CustomSector -> {
                when (filter.sectorKey) {
                    "new" -> PagingFilterType.New
                    "trending" -> PagingFilterType.Trending
                    "saved" -> PagingFilterType.Saved
                    else -> PagingFilterType.All
                }
            }
        }
        _currentPagingFilter.value = newFilterType
    }

    fun handleAction(action: TidbitAction) {
        viewModelScope.launch {
            _actions.emit(action)
        }
    }
}

data class TidbitDetailState(val id: String = "", val isLoading: Boolean = false, val presentation: TidbitPresentation? = null)

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

    data class OnTidbitLikeClick(val tidbitId: String, val newValue: Boolean) : TidbitScreenEvent

    data class OnTidbitSaveClick(val tidbitId: String, val newValue: Boolean) : TidbitScreenEvent

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
    data class OnClickLike(val id: String, val newValue: Boolean) : TidbitDetailEvent
    data class OnClickBookmark(val id: String, val newValue: Boolean) : TidbitDetailEvent
    data object OnClickSource : TidbitDetailEvent
    data object OnClickShare : TidbitDetailEvent
}

sealed interface TidbitDetailAction {
    data object OnGoBack : TidbitDetailAction
    data class OnOpenSource(val url: String) : TidbitDetailAction
    data class OnShare(val shareText: String) : TidbitDetailAction
}

sealed interface PagingFilterType {
    data object All : PagingFilterType
    data object Saved : PagingFilterType
    data object Trending : PagingFilterType
    data object New : PagingFilterType
}
