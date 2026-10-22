package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.clear
import com.thejawnpaul.gptinvestor.discover_no_matches_body
import com.thejawnpaul.gptinvestor.discover_no_matches_title
import com.thejawnpaul.gptinvestor.features.search.domain.model.ChipItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.PromptItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchEvent
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchUiState
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchScreen(state: SearchUiState, onEvent: (SearchEvent) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        topBar = {
            SearchTopBar(
                query = state.query,
                focusRequester = focusRequester,
                onQueryChange = { onEvent(SearchEvent.OnQueryChange(it)) },
                onSearch = { onEvent(SearchEvent.OnQuerySubmit) },
                onBack = { onEvent(SearchEvent.OnBack) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
        ) {
            when {
                state.isLoading && state.sections.isEmpty() -> SearchLoadingContent()
                state.error != null && state.sections.isEmpty() -> SearchErrorContent(
                    query = state.query,
                    onRetry = { onEvent(SearchEvent.OnRetry) }
                )
                else -> SearchSections(state = state, onEvent = onEvent)
            }
        }
    }
}

@Composable
private fun SearchSections(state: SearchUiState, onEvent: (SearchEvent) -> Unit) {
    val isNoResults = state.sections.any { it is SearchSection.EmptyState }
    val haptics = LocalHapticFeedback.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = GiSpace.gutter,
            end = GiSpace.gutter,
            top = GiSpace.sectionGap,
            bottom = GiSpace.x10
        ),
        verticalArrangement = Arrangement.spacedBy(if (isNoResults) GiSpace.x8 else GiSpace.sectionGap)
    ) {
        state.sections.forEach { section ->
            when (section) {
                is SearchSection.RecentList -> if (section.items.isNotEmpty()) {
                    item(key = section.id, contentType = "recent") {
                        Column(
                            verticalArrangement = if (section.clearable) {
                                Arrangement.spacedBy(GiSpace.x2)
                            } else {
                                Arrangement.spacedBy(GiSpace.titleToContent)
                            }
                        ) {
                            SearchSectionHeader(
                                title = section.title,
                                actionLabel = if (section.clearable) stringResource(Res.string.clear) else null,
                                onAction = {
                                    haptics.performHapticFeedback(GiHaptics.Reject)
                                    onEvent(SearchEvent.OnClearHistory)
                                }
                            )
                            SearchStockList(
                                stocks = section.items,
                                onClick = { onEvent(SearchEvent.OnStockClick(it.ticker)) }
                            )
                        }
                    }
                }

                is SearchSection.PromptList -> if (section.items.isNotEmpty()) {
                    item(key = section.id, contentType = "prompts") {
                        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                            SearchSectionHeader(title = section.title)
                            Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                                section.items.forEach { prompt ->
                                    SearchPromptRow(
                                        prompt = prompt,
                                        onClick = { onEvent(SearchEvent.OnPromptClick(prompt.query)) }
                                    )
                                }
                            }
                        }
                    }
                }

                is SearchSection.ChipList -> if (section.items.isNotEmpty()) {
                    item(key = section.id, contentType = "chips") {
                        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                            SearchSectionHeader(title = section.title)
                            ChipListSection(
                                chips = section.items,
                                onChipClick = { key -> onEvent(SearchEvent.OnSectorClick(key)) }
                            )
                        }
                    }
                }

                is SearchSection.StockList -> if (section.items.isNotEmpty()) {
                    item(key = section.id, contentType = "stocks") {
                        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                            SearchSectionHeader(title = section.title)
                            SearchStockList(
                                stocks = section.items,
                                onClick = { onEvent(SearchEvent.OnStockClick(it.ticker)) }
                            )
                        }
                    }
                }

                is SearchSection.AskGpt -> item(key = section.id, contentType = "askGpt") {
                    SearchAskGptRow(
                        query = section.query,
                        onClick = { onEvent(SearchEvent.OnAskGptClick(section.query)) }
                    )
                }

                is SearchSection.EmptyState -> item(key = section.id, contentType = "empty") {
                    val query = state.query.trim()
                    SearchEmptyStateSection(
                        message = if (query.isNotEmpty()) {
                            stringResource(Res.string.discover_no_matches_title, query)
                        } else {
                            section.message
                        },
                        subtitle = if (query.isNotEmpty()) {
                            stringResource(Res.string.discover_no_matches_body)
                        } else {
                            section.subtitle
                        }
                    )
                }
            }
        }
    }
}

private val previewRecentsState = SearchUiState(
    sections = listOf(
        SearchSection.RecentList(
            id = "recent",
            title = "Recent",
            clearable = true,
            items = listOf(
                StockItem("JNJ", "Johnson & Johnson", ""),
                StockItem("TSLA", "Tesla, Inc.", ""),
                StockItem("AMZN", "Amazon.com, Inc.", "")
            )
        ),
        SearchSection.PromptList(
            id = "prompts",
            title = "Try asking",
            items = listOf(
                PromptItem("What makes a stock go up?", "What makes a stock go up?"),
                PromptItem("Making sense of ESG investing", "Making sense of ESG investing")
            )
        ),
        SearchSection.ChipList(
            id = "sectors",
            title = "Browse by sector",
            items = listOf(ChipItem("healthcare", "Healthcare"), ChipItem("energy", "Energy"))
        )
    )
)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun SearchScreenRecentsPreview() {
    GPTInvestorTheme {
        SearchScreen(state = previewRecentsState, onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenResultsPreview() {
    GPTInvestorTheme {
        SearchScreen(
            state = SearchUiState(
                query = "nvi",
                sections = listOf(
                    SearchSection.StockList(
                        id = "stocks",
                        title = "Stocks",
                        items = listOf(
                            StockItem("NVDA", "NVIDIA Corporation", ""),
                            StockItem("INVH", "Invitation Homes Inc.", "")
                        )
                    ),
                    SearchSection.ChipList(
                        id = "themes",
                        title = "Sectors and themes",
                        items = listOf(ChipItem("technology", "Technology"), ChipItem("real-estate", "Real estate"))
                    ),
                    SearchSection.AskGpt(id = "ask_gpt", title = "Ask GPT Investor", query = "nvi")
                )
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenNoResultsPreview() {
    GPTInvestorTheme {
        SearchScreen(
            state = SearchUiState(
                query = "zzzzqq",
                sections = listOf(
                    SearchSection.EmptyState(id = "empty", message = "No results", subtitle = ""),
                    SearchSection.AskGpt(id = "ask_gpt", title = "Ask GPT Investor", query = "zzzzqq")
                )
            ),
            onEvent = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenLoadingPreview() {
    GPTInvestorTheme {
        SearchScreen(state = SearchUiState(isLoading = true), onEvent = {})
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenErrorPreview() {
    GPTInvestorTheme {
        SearchScreen(state = SearchUiState(query = "msft", error = "Something went wrong"), onEvent = {})
    }
}
