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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.SectorChoiceQuestion
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.model.TidbitPresentation
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenEvent
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.viewmodel.TidbitScreenState
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TidbitScreen(modifier: Modifier = Modifier, state: TidbitScreenState, onEvent: (TidbitScreenEvent) -> Unit) {
    LaunchedEffect(Unit) {
        onEvent(TidbitScreenEvent.GetAllTidbits)
    }

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
                        contentDescription = stringResource(id = R.string.back)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = 16.dp
                ),
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

                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    if (state.tidbits.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No tidbits found.",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        items(state.tidbits, key = { it.id }) { tidbit ->
                            SingleTidbitItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                tidbit = tidbit,
                                onItemClick = { onEvent(TidbitScreenEvent.OnTidbitClick(tidbit.id)) },
                                onLikeClick = {
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitLikeClick(
                                            tidbitId = tidbit.id
                                        )
                                    )
                                },
                                onSaveClick = {
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitSaveClick(
                                            tidbitId = tidbit.id
                                        )
                                    )
                                },
                                onShareClick = {
                                    onEvent(
                                        TidbitScreenEvent.OnTidbitShareClick(
                                            tidbitId = tidbit.id
                                        )
                                    )
                                }
                            )
                        }
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
            onEvent = {}
        )
    }
}
