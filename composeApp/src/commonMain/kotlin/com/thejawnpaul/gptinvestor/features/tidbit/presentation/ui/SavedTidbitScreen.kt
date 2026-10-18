package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.check_connection_try_again
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenEvent
import com.thejawnpaul.gptinvestor.saved_count
import com.thejawnpaul.gptinvestor.saved_tidbits
import com.thejawnpaul.gptinvestor.saved_tidbits_empty_body
import com.thejawnpaul.gptinvestor.saved_tidbits_empty_title
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiCard
import com.thejawnpaul.gptinvestor.theme.components.GiErrorState
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiSkeleton
import com.thejawnpaul.gptinvestor.theme.components.GiSpinner
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.tidbits_error_title
import com.thejawnpaul.gptinvestor.tidbits_loading
import com.thejawnpaul.gptinvestor.try_again
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun SavedTidbitScreen(
    tidbitsPagingData: Flow<PagingData<TidbitPresentation>>,
    onEvent: (TidbitScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyPagingItems = tidbitsPagingData.collectAsLazyPagingItems()
    val count = lazyPagingItems.itemCount

    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = stringResource(Res.string.saved_tidbits),
                subtitle = if (count > 0) stringResource(Res.string.saved_count, count) else null,
                onBack = { onEvent(TidbitScreenEvent.OnBackClick) },
                backContentDescription = stringResource(Res.string.back)
            )
        }
    ) { innerPadding ->
        TidbitPagedContent(
            lazyPagingItems = lazyPagingItems,
            onEvent = onEvent,
            emptyIcon = GiIcons.bookmarkSimple,
            emptyTitle = stringResource(Res.string.saved_tidbits_empty_title),
            emptyBody = stringResource(Res.string.saved_tidbits_empty_body),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
internal fun TidbitPagedContent(
    lazyPagingItems: LazyPagingItems<TidbitPresentation>,
    onEvent: (TidbitScreenEvent) -> Unit,
    emptyIcon: ImageVector,
    emptyTitle: String,
    emptyBody: String,
    modifier: Modifier = Modifier,
    showPersonalActions: Boolean = true,
    header: (LazyListScope.() -> Unit)? = null
) {
    val refresh = lazyPagingItems.loadState.refresh
    val isEmpty = lazyPagingItems.itemCount == 0
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    when {
        refresh is LoadState.Error && isEmpty -> Column(modifier = modifier) {
            if (header != null) LazyColumn(modifier = Modifier.padding(horizontal = GiSpace.gutter), content = header)
            GiErrorState(
                title = stringResource(Res.string.tidbits_error_title),
                body = stringResource(Res.string.check_connection_try_again),
                actionText = stringResource(Res.string.try_again),
                onAction = { lazyPagingItems.retry() },
                modifier = Modifier.weight(1f)
            )
        }

        else -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                start = GiSpace.gutter,
                end = GiSpace.gutter,
                top = GiSpace.x2,
                bottom = GiSpace.x10 + navBottom
            ),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
        ) {
            header?.invoke(this)

            if (refresh is LoadState.Loading && isEmpty) {
                item(key = "loading") {
                    val loading = stringResource(Res.string.tidbits_loading)
                    Column(
                        modifier = Modifier.semantics {
                            contentDescription = loading
                            liveRegion = LiveRegionMode.Polite
                        },
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
                    ) {
                        repeat(2) { TidbitCardSkeleton() }
                    }
                }
            } else if (refresh is LoadState.NotLoading && isEmpty) {
                item(key = "empty") {
                    ContentEmptyState(
                        icon = emptyIcon,
                        title = emptyTitle,
                        body = emptyBody
                    )
                }
            }

            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id },
                contentType = lazyPagingItems.itemContentType { "tidbit" }
            ) { index ->
                lazyPagingItems[index]?.let { tidbit ->
                    SingleTidbitItem(
                        tidbit = tidbit,
                        modifier = contentItemMotion(),
                        showPersonalActions = showPersonalActions,
                        onItemClick = { onEvent(TidbitScreenEvent.OnTidbitClick(tidbitId = it)) },
                        onLikeClick = { id, newValue ->
                            onEvent(TidbitScreenEvent.OnTidbitLikeClick(tidbitId = id, newValue = newValue))
                        },
                        onSaveClick = { id, newValue ->
                            onEvent(TidbitScreenEvent.OnTidbitSaveClick(tidbitId = id, newValue = newValue))
                        },
                        onShareClick = { onEvent(TidbitScreenEvent.OnTidbitShareClick(tidbitId = it)) }
                    )
                }
            }

            when (lazyPagingItems.loadState.append) {
                is LoadState.Loading -> item(key = "append") {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        GiSpinner(color = GiTheme.colors.muted)
                    }
                }

                is LoadState.Error -> item(key = "append-error") {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        GiButton(
                            text = stringResource(Res.string.try_again),
                            onClick = { lazyPagingItems.retry() },
                            size = GiButtonSize.Medium,
                            variant = GiButtonVariant.Secondary
                        )
                    }
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun TidbitCardSkeleton(modifier: Modifier = Modifier) {
    GiCard(modifier = modifier.fillMaxWidth(), spacing = GiSpace.x2) {
        GiSkeleton(modifier = Modifier.fillMaxWidth().height(GiSpace.x4))
        GiSkeleton(modifier = Modifier.fillMaxWidth(SKELETON_LINE_FRACTION).height(GiSpace.x3))
        GiSkeleton(
            modifier = Modifier
                .padding(top = GiSpace.x1)
                .fillMaxWidth()
                .height(SKELETON_COVER_HEIGHT),
            shape = GiRadius.sm
        )
    }
}

private const val SKELETON_LINE_FRACTION = 0.7f
private val SKELETON_COVER_HEIGHT = 168.dp

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SavedTidbitScreenPreview() {
    GPTInvestorTheme {
        SavedTidbitScreen(
            tidbitsPagingData = flowOf(PagingData.from(TidbitPreviewParameterProvider().values.toList())),
            onEvent = {}
        )
    }
}
