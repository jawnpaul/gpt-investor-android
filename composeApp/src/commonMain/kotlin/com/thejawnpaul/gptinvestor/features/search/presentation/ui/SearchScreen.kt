package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.clear
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchEvent
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchUiState
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchScreen(state: SearchUiState, onEvent: (SearchEvent) -> Unit, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
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
        ) {
            if (state.isLoading && state.sections.isEmpty()) {
                SearchLoadingContent()
            } else if (state.error != null && state.sections.isEmpty()) {
                SearchErrorContent(
                    message = state.error,
                    onRetry = { onEvent(SearchEvent.OnRetry) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    state.sections.forEach { section ->
                        when (section) {
                            is SearchSection.RecentList -> {
                                item(key = "header_${section.id}") {
                                    SearchSectionHeader(
                                        title = section.title,
                                        actionLabel = if (section.clearable) stringResource(Res.string.clear) else null,
                                        onAction = { onEvent(SearchEvent.OnClearHistory) },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                items(section.items, key = { "recent_${it.ticker}" }) { stock ->
                                    SearchStockRow(
                                        stock = stock,
                                        onClick = { onEvent(SearchEvent.OnStockClick(stock.ticker)) }
                                    )
                                }
                                item(key = "spacer_${section.id}") { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                            is SearchSection.PromptList -> {
                                item(key = "header_${section.id}") {
                                    SearchSectionHeader(
                                        title = section.title,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                items(section.items, key = { "prompt_${it.label}" }) { prompt ->
                                    SearchPromptRow(
                                        prompt = prompt,
                                        onClick = { onEvent(SearchEvent.OnPromptClick(prompt.query)) }
                                    )
                                }
                                item(key = "spacer_${section.id}") { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                            is SearchSection.ChipList -> {
                                item(key = "header_${section.id}") {
                                    SearchSectionHeader(
                                        title = section.title,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                    ChipListSection(
                                        chips = section.items,
                                        onChipClick = { key -> onEvent(SearchEvent.OnSectorClick(key)) },
                                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
                                    )
                                }
                            }
                            is SearchSection.StockList -> {
                                item(key = "header_${section.id}") {
                                    SearchSectionHeader(
                                        title = section.title,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                items(section.items, key = { "stock_${it.ticker}" }) { stock ->
                                    SearchStockRow(
                                        stock = stock,
                                        onClick = { onEvent(SearchEvent.OnStockClick(stock.ticker)) }
                                    )
                                }
                                item(key = "spacer_${section.id}") { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                            is SearchSection.AskGpt -> {
                                item(key = section.id) {
                                    SearchAskGptRow(
                                        title = section.title,
                                        query = section.query,
                                        onClick = { onEvent(SearchEvent.OnAskGptClick(section.query)) },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                            is SearchSection.EmptyState -> {
                                item(key = section.id) {
                                    SearchEmptyStateSection(
                                        message = section.message,
                                        subtitle = section.subtitle,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp, vertical = 24.dp)
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenLoadingPreview() {
    GPTInvestorTheme {
        Surface {
            SearchScreen(state = SearchUiState(isLoading = true), onEvent = {})
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenErrorPreview() {
    GPTInvestorTheme {
        Surface {
            SearchScreen(
                state = SearchUiState(error = "Something went wrong. Please try again."),
                onEvent = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchScreenResultsPreview() {
    GPTInvestorTheme {
        Surface {
            SearchScreen(
                state = SearchUiState(
                    query = "apple",
                    sections = listOf(
                        SearchSection.RecentList(
                            id = "recent",
                            title = "Recent",
                            clearable = true,
                            items = listOf(
                                StockItem("AAPL", "Apple Inc.", ""),
                                StockItem("AMZN", "Amazon.com Inc.", "")
                            )
                        ),
                        SearchSection.StockList(
                            id = "stocks",
                            title = "Stocks",
                            items = listOf(
                                StockItem("AAPL", "Apple Inc.", ""),
                                StockItem("APLE", "Apple Hospitality REIT", "")
                            )
                        ),
                        SearchSection.AskGpt(
                            id = "ask_gpt",
                            title = "Ask GPT Investor",
                            query = "apple"
                        )
                    )
                ),
                onEvent = {}
            )
        }
    }
}
