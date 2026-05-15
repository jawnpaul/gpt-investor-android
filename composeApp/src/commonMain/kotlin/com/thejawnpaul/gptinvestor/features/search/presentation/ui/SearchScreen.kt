package com.thejawnpaul.gptinvestor.features.search.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.arrow_right
import com.thejawnpaul.gptinvestor.ask_ai_icon
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.clear
import com.thejawnpaul.gptinvestor.empty
import com.thejawnpaul.gptinvestor.features.component.ShimmerBox
import com.thejawnpaul.gptinvestor.features.search.domain.model.ChipItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.PromptItem
import com.thejawnpaul.gptinvestor.features.search.domain.model.SearchSection
import com.thejawnpaul.gptinvestor.features.search.domain.model.StockItem
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchEvent
import com.thejawnpaul.gptinvestor.features.search.presentation.state.SearchUiState
import com.thejawnpaul.gptinvestor.ic_search
import com.thejawnpaul.gptinvestor.retry
import com.thejawnpaul.gptinvestor.search_a_stock_to_analyse
import com.thejawnpaul.gptinvestor.server_down
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.painterResource
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
                                    StockRow(
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
                                    PromptRow(
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
                                    StockRow(
                                        stock = stock,
                                        onClick = { onEvent(SearchEvent.OnStockClick(stock.ticker)) }
                                    )
                                }
                                item(key = "spacer_${section.id}") { Spacer(modifier = Modifier.height(8.dp)) }
                            }
                            is SearchSection.AskGpt -> {
                                item(key = section.id) {
                                    AskGptRow(
                                        title = section.title,
                                        query = section.query,
                                        onClick = { onEvent(SearchEvent.OnAskGptClick(section.query)) },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                            is SearchSection.EmptyState -> {
                                item(key = section.id) {
                                    EmptyStateSection(
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

@Composable
private fun SearchTopBar(
    query: String,
    focusRequester: FocusRequester,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.back)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = null
                    )
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        value = query,
                        onValueChange = onQueryChange,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (query.isBlank()) {
                                    Text(
                                        text = stringResource(Res.string.search_a_stock_to_analyse),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = gptInvestorColors.textColors.secondary50
                                    )
                                }
                                innerTextField()
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = LocalGPTInvestorColors.current.textColors.secondary50
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
private fun StockRow(stock: StockItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape) {
            AsyncImage(
                model = stock.logoUrl,
                contentDescription = stock.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.ticker,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                color = LocalGPTInvestorColors.current.textColors.secondary50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painter = painterResource(Res.drawable.arrow_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = LocalGPTInvestorColors.current.textColors.secondary50
        )
    }
}

@Composable
private fun PromptRow(prompt: PromptItem, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ask_ai_icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Text(
            text = prompt.label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Icon(
            painter = painterResource(Res.drawable.arrow_right),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = LocalGPTInvestorColors.current.textColors.secondary50
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipListSection(chips: List<ChipItem>, onChipClick: (String) -> Unit, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            SuggestionChip(
                onClick = { onChipClick(chip.key) },
                label = {
                    Text(
                        text = chip.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}

@Composable
private fun AskGptRow(title: String, query: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ask_ai_icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "\"$query\"",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(Res.drawable.arrow_right),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun EmptyStateSection(message: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(Res.drawable.empty),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LocalGPTInvestorColors.current.textColors.secondary50
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = LocalGPTInvestorColors.current.textColors.secondary50,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchErrorContent(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.server_down),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LocalGPTInvestorColors.current.textColors.secondary50
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalGPTInvestorColors.current.textColors.secondary50,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            onClick = onRetry,
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = stringResource(Res.string.retry),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun SearchLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) {
            ShimmerBox(width = 80.dp, height = 12.dp)
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerBox(size = 36.dp, shape = CircleShape)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ShimmerBox(width = 60.dp, height = 14.dp)
                        ShimmerBox(width = 120.dp, height = 12.dp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
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

@PreviewLightDark
@Composable
private fun StockRowPreview() {
    GPTInvestorTheme {
        Surface {
            StockRow(
                stock = StockItem("AAPL", "Apple Inc.", ""),
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PromptRowPreview() {
    GPTInvestorTheme {
        Surface {
            PromptRow(
                prompt = PromptItem("Is Apple a good buy right now?", "Is Apple a good buy right now?"),
                onClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AskGptRowPreview() {
    GPTInvestorTheme {
        Surface {
            AskGptRow(
                title = "Ask GPT Investor",
                query = "Is Tesla overvalued?",
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyStateSectionPreview() {
    GPTInvestorTheme {
        Surface {
            EmptyStateSection(
                message = "No results found",
                subtitle = "Try searching for a stock ticker or company name",
                modifier = Modifier.padding(32.dp)
            )
        }
    }
}
