package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenEvent
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenState
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.tidbit_filter_all
import com.thejawnpaul.gptinvestor.tidbit_filters
import com.thejawnpaul.gptinvestor.tidbits_empty_body
import com.thejawnpaul.gptinvestor.tidbits_empty_title
import com.thejawnpaul.gptinvestor.tidbits_title
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource

@Composable
fun TidbitScreen(
    state: TidbitScreenState,
    tidbitsPagingData: Flow<PagingData<TidbitPresentation>>,
    onEvent: (TidbitScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyPagingItems = tidbitsPagingData.collectAsLazyPagingItems()
    val allLabel = stringResource(Res.string.tidbit_filter_all)
    val filtersLabel = stringResource(Res.string.tidbit_filters)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            GiTopBar(
                title = stringResource(Res.string.tidbits_title),
                onBack = { onEvent(TidbitScreenEvent.OnBackClick) },
                backContentDescription = stringResource(Res.string.back)
            )
        }
    ) { innerPadding ->
        TidbitPagedContent(
            lazyPagingItems = lazyPagingItems,
            onEvent = onEvent,
            emptyIcon = GiIcons.lightbulb,
            emptyTitle = stringResource(Res.string.tidbits_empty_title),
            emptyBody = stringResource(Res.string.tidbits_empty_body),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            header = {
                item(key = "filters") {
                    LazyRow(
                        modifier = Modifier
                            .bleedHorizontally(GiSpace.gutter)
                            .fillMaxWidth()
                            .selectableGroup()
                            .semantics { contentDescription = filtersLabel },
                        contentPadding = PaddingValues(horizontal = GiSpace.gutter),
                        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
                    ) {
                        items(state.options, key = { it.chipKey() }) { option ->
                            GiChip(
                                text = when (option) {
                                    SectorInput.AllSector -> allLabel
                                    is SectorInput.CustomSector -> option.sectorName
                                },
                                selected = option == state.selectedOption,
                                singleChoice = true,
                                onClick = { onEvent(TidbitScreenEvent.OnFilterSelected(filter = option)) }
                            )
                        }
                    }
                }
            }
        )
    }
}

private fun SectorInput.chipKey(): String = when (this) {
    SectorInput.AllSector -> "all"
    is SectorInput.CustomSector -> sectorKey
}

private fun Modifier.bleedHorizontally(amount: androidx.compose.ui.unit.Dp): Modifier = layout {
        measurable,
        constraints
    ->
    val extra = amount.roundToPx() * 2
    val placeable = measurable.measure(
        constraints.copy(maxWidth = constraints.maxWidth + extra, minWidth = constraints.minWidth + extra)
    )
    layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun TidbitScreenPreview() {
    GPTInvestorTheme {
        TidbitScreen(
            state = TidbitScreenState(),
            tidbitsPagingData = flowOf(PagingData.from(TidbitPreviewParameterProvider().values.toList())),
            onEvent = {}
        )
    }
}
