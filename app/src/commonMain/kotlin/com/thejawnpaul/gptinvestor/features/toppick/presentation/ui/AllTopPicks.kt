package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import gptinvestor.app.generated.resources.Res
import gptinvestor.app.generated.resources.all_top_picks
import gptinvestor.app.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
fun AllTopPicksScreen(modifier: Modifier, state: TopPicksView, onGoBack: () -> Unit, onGoToDetail: (String) -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Column(modifier = Modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = stringResource(Res.string.back)
                    )
                }
                Text(
                    text = stringResource(Res.string.all_top_picks),
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                if (state.loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }

                items(
                    items = state.topPicks,
                    key = { topPickPresentation -> topPickPresentation.id }
                ) { pickPresentation ->
                    SingleTopPickItem(
                        modifier = Modifier,
                        pickPresentation = pickPresentation,
                        onClick = {
                            onGoToDetail(pickPresentation.id)
                        }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
