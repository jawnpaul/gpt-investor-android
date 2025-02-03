package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.thejawnpaul.gptinvestor.features.toppick.presentation.TopPickViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopPickDetailScreen(modifier: Modifier = Modifier, navController: NavController, topPickId: String, viewModel: TopPickViewModel) {
    LaunchedEffect(topPickId) {
        viewModel.updateTopPickId(topPickId)
    }

    val state = viewModel.topPickView.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Top Pick Details") }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }, actions = {
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
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.value.isLoggedIn) {
                // show confidence
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.TopStart),
                        text = state.value.topPick?.ticker ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        modifier = Modifier.align(Alignment.BottomStart),
                        text = state.value.topPick?.companyName ?: "",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Surface(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "${state.value.topPick?.confidenceScore}/10",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                // blur other parts

                // Show login button
            } else {
                // show all
            }
        }
    }
}
