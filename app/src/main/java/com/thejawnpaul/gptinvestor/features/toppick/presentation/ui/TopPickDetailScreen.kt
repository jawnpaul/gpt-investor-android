package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPickDetailScreen(modifier: Modifier = Modifier, navController: NavController, topPickId: String, viewModel: TopPickViewModel) {
    LaunchedEffect(topPickId) {
        viewModel.updateTopPickId(topPickId)
    }

    val state = viewModel.topPickView.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.top_pick_details)) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Company Name, Ticker, and Confidence Score (always visible)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(
                        text = state.value.topPick?.companyName ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                    Text(
                        text = state.value.topPick?.ticker ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${state.value.topPick?.confidenceScore}/10",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Protected content
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Content (blurred when not signed in)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (!state.value.isLoggedIn) {
                                Modifier.blur(radius = 8.dp)
                            } else {
                                Modifier
                            }
                        )
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Rationale
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Investment Rationale",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = state.value.topPick?.rationale ?: ""
                        )
                    }

                    // Metrics
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.List,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.key_metrics),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        state.value.topPick?.let { topPick ->
                            topPick.metrics.forEach { metric ->
                                Text(
                                    text = metric
                                )
                            }
                        }
                    }

                    // Risks
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(R.string.key_risks),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        state.value.topPick?.risks.let { risks ->
                            risks?.forEach { risk ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = "•"
                                    )
                                    Text(
                                        text = risk
                                    )
                                }
                            }
                        }
                    }
                }

                // Login overlay (only shown when not signed in)
                if (!state.value.isLoggedIn) {
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.sign_in_to_view_full_details),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(
                                onClick = { viewModel.loginUser() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.sign_in))
                            }
                        }
                    }
                }
            }
        }
    }
}
