package com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.features.tidbit.domain.TidbitRepository
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
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

    private val _uiState = MutableStateFlow(TidbitDetailState())
    val uiState get() = _uiState

    private val _actions = MutableSharedFlow<TidbitAction>()
    val actions get() = _actions

    private fun getTidbit() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            tidbitId?.let { id ->
                repository.getTidbit(id).onSuccess { tidbit ->
                    _uiState.update {
                        it.copy(
                            id = tidbit.id,
                            isLoading = false,
                            presentation = mapTidbitToPresentation(tidbit)
                        )
                    }
                }.onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun handleEvent(event: TidbitEvent) {
        when (event) {
            is TidbitEvent.GetTidbit -> {
                updateTidbitId(tidbitId = event.id)
            }

            TidbitEvent.GoBack -> {
                handleAction(action = TidbitAction.OnGoBack)
            }

            is TidbitEvent.OnClickLike -> {
            }

            is TidbitEvent.OnClickShare -> {
                viewModelScope.launch {
                    repository.getShareableLink(_uiState.value.id).onSuccess {
                        handleAction(action = TidbitAction.OnShare(shareText = it))
                    }
                }
            }

            is TidbitEvent.OnClickSource -> {
                // handleAction(action = TidbitAction.OnOpenSource(url = _uiState.value.sourceUrl))
            }
        }
    }

    private fun updateTidbitId(tidbitId: String) {
        savedStateHandle["tidbitId"] = tidbitId
        getTidbit()
    }

    fun handleAction(action: TidbitAction) {
        viewModelScope.launch {
            _actions.emit(action)
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
}

data class TidbitDetailState(
    val id: String = "",
    val isLoading: Boolean = false,
    val presentation: TidbitPresentation? = null
)

sealed interface TidbitEvent {
    data class GetTidbit(val id: String) : TidbitEvent
    data object GoBack : TidbitEvent
    data class OnClickLike(val id: String) : TidbitEvent
    data object OnClickSource : TidbitEvent
    data object OnClickShare : TidbitEvent
}

sealed interface TidbitAction {
    data object OnGoBack : TidbitAction
    data class OnOpenSource(val url: String) : TidbitAction
    data class OnShare(val shareText: String) : TidbitAction
}
