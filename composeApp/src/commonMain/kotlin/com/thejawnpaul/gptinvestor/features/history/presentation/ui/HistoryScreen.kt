package com.thejawnpaul.gptinvestor.features.history.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.features.history.presentation.state.HistoryScreenView
import com.thejawnpaul.gptinvestor.features.history.presentation.viewmodel.HistoryScreenEvent
import com.thejawnpaul.gptinvestor.your_gpt_investor_history
import org.jetbrains.compose.resources.stringResource

@Composable
fun HistoryScreen(state: HistoryScreenView, onEvent: (HistoryScreenEvent) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier.statusBarsPadding().fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onEvent(HistoryScreenEvent.GoBack)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }

                Text(
                    text = stringResource(Res.string.your_gpt_investor_history),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding), contentAlignment = Alignment.TopStart) {
            Column(modifier = Modifier) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    if (state.loading) {
                        item {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    }

                    state.list.forEach { (title, items) ->
                        item {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        item {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                items.forEachIndexed { index, conversation ->
                                    SingleHistoryItem(
                                        modifier = Modifier,
                                        conversation = conversation,
                                        onClick = { id ->
                                            onEvent(HistoryScreenEvent.HistoryItemClicked(id))
                                        }
                                    )
                                    if (index < items.size - 1) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(
                                                start = 16.dp,
                                                end = 16.dp,
                                                bottom = 8.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
