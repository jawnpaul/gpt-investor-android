package com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.SectorChoiceQuestion
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenEvent
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenState
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.back
import gptinvestor.app.generated.resources.error_loading_tidbits
import gptinvestor.app.generated.resources.load_more
import gptinvestor.app.generated.resources.no_tidbits_found
import gptinvestor.app.generated.resources.retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TidbitScreen(modifier: Modifier = Modifier, state: TidbitScreenState, tidbitsPagingData: Flow<PagingData<TidbitPresentation>>, onEvent: (TidbitScreenEvent) -> Unit) {
    LaunchedEffect(Unit) {
        onEvent(TidbitScreenEvent.GetAllTidbits)
    }

    val lazyPagingItems = tidbitsPagingData.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onEvent(TidbitScreenEvent.OnBackClick)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main content (LazyColumn with tidbits)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // row of sectors
                        SectorChoiceQuestion(
                            possibleAnswers = state.options,
                            selectedAnswer = state.selectedOption,
                            onOptionSelected = {
                                onEvent(TidbitScreenEvent.OnFilterSelected(filter = it))
                            }
                        )
                    }

                    // Paged items
                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.id }
                    ) { index ->
                        val tidbit = lazyPagingItems[index]
                        tidbit?.let {
                            SingleTidbitItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                tidbit = it,
                                onItemClick = { tidbitId ->
                                    onEvent(TidbitScreenEvent.OnTidbitClick(tidbitId = tidbitId))
                                },
                                onLikeClick = { _, newValue ->
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitLikeClick(
                                            tidbitId = it.id,
                                            newValue = newValue
                                        )
                                    )
                                },
                                onSaveClick = { _, newValue ->
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitSaveClick(
                                            tidbitId = it.id,
                                            newValue = newValue
                                        )
                                    )
                                },
                                onShareClick = { selectedTidbitId ->
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitShareClick(
                                            tidbitId = selectedTidbitId
                                        )
                                    )
                                }
                            )
                        }
                    }

                    // Handle pagination load more
                    if (lazyPagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    // Handle pagination error
                    if (lazyPagingItems.loadState.append is LoadState.Error) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = { lazyPagingItems.retry() }) {
                                    Text(text = stringResource(Res.string.load_more))
                                }
                            }
                        }
                    }
                }

                if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (lazyPagingItems.loadState.refresh is LoadState.Error && lazyPagingItems.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.error_loading_tidbits),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(onClick = { lazyPagingItems.retry() }) {
                                Text(text = stringResource(Res.string.retry))
                            }
                        }
                    }
                }

                if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.no_tidbits_found),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A2E)
@Composable
fun TidbitScreenPreview() {
    GPTInvestorTheme(darkTheme = true) {
        val sampleTidbits = listOf(
            TidbitPresentation.AudioPresentation(
                id = "1",
                name = "Audio Tidbit Weekend",
                previewUrl = "https://example.com/placeholder_image.jpg", // Use a real or placeholder URL
                mediaUrl = "https://example.com/audio.mp3",
                title = "Learn Everything on Tidbit",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt...",
                originalAuthor = "Morgan Housel",
                category = "Investing 101",
                sourceUrl = "https://example.com"
            ),
            TidbitPresentation.VideoPresentation(
                id = "2",
                name = "Video Tidbit Weekend",
                previewUrl = "https://example.com/placeholder_image.jpg",
                mediaUrl = "https://example.com/video.mp4",
                title = "Learn Everything on Tidbit",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt...",
                originalAuthor = "Morgan Housel",
                category = "Investing 101",
                sourceUrl = "https://example.com"
            ),
            TidbitPresentation.ArticlePresentation(
                id = "3",
                name = "Article Tidbit Weekend",
                previewUrl = "https://example.com/placeholder_image.jpg",
                mediaUrl = "https://example.com/placeholder_image.jpg",
                title = "Learn Everything on Tidbit",
                content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt...",
                originalAuthor = "Morgan Housel",
                category = "Investing 101",
                sourceUrl = "https://example.com"
            )
        )
        TidbitScreen(
            state = TidbitScreenState(
                isLoading = false,
                tidbits = sampleTidbits,
                selectedOption = null,
                options = listOf(
                    SectorInput.AllSector,
                    SectorInput.CustomSector(sectorName = "New", sectorKey = "new")
                )
            ),
            tidbitsPagingData = flowOf(PagingData.from(sampleTidbits)),
            onEvent = {}
        )
    }
}
