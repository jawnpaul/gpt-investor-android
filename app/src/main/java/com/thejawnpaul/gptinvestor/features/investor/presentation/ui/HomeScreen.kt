package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, state: HomeUiState, onAction: (HomeAction) -> Unit, onEvent: (HomeEvent) -> Unit) {
    val context = LocalContext.current

    // Permission launcher for notifications
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEvent(HomeEvent.NotificationPermissionGranted)
        } else {
            onEvent(HomeEvent.NotificationPermissionDenied)
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                    onEvent(HomeEvent.NotificationPermissionGranted)
                }

                else -> {
                    // Request permission
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For Android 12 and below, notifications are enabled by default
            onEvent(HomeEvent.NotificationPermissionGranted)
        }
    }

    LaunchedEffect(Unit) {
        if (state.requestForNotificationPermission == null) {
            requestNotificationPermission()
        }
    }

    // Home Screen
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            // App bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onAction(HomeAction.OnMenuClick) }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                        contentDescription = null
                    )
                }

                Text(
                    text = stringResource(R.string.gpt_investor),
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                /*ThemeDropdown(
                    modifier = Modifier,
                    onClick = {
                        onEvent(HomeEvent.ChangeTheme(it))
                    },
                    options = listOf("Light", "Dark", "System"),
                    selectedOption = state.theme ?: "Dark"
                )*/

                IconButton(modifier = Modifier, onClick = {
                    onAction(HomeAction.OnGoToDiscover)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_status_two),
                        contentDescription = null
                    )
                }
            }

            /*// Top Picks
            if (state.topPicksView.topPicks.isNotEmpty()) {
                TopPicks(modifier = Modifier, state = state.topPicksView, onClick = {
                    onAction(HomeAction.OnGoToTopPickDetail(it))
                }, onClickRetry = {
                    onEvent(HomeEvent.RetryTopPicks)
                }, onClickSeeAll = {
                    onAction(HomeAction.OnGoToAllTopPicks)
                })
            }*/
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)

        ) {
            QuestionInput(
                modifier = Modifier,
                onSendClicked = {
                    onEvent(HomeEvent.SendClick)
                },
                hint = stringResource(R.string.ask_me_a_question),
                onTextChange = {
                    onEvent(HomeEvent.ChatInputChanged(it))
                },
                text = state.chatInput ?: "",
                availableModels = state.availableModels,
                selectedModel = state.selectedModel,
                onModelChange = {
                    onEvent(HomeEvent.ModelChanged(it))
                }
            )
        }
    }
}
