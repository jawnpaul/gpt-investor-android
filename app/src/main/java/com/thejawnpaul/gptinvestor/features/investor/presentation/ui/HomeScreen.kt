package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerAction
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerContent
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerEvent
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.HomeDefaultPrompts
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeAction
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, state: HomeUiState, onAction: (HomeAction) -> Unit, onEvent: (HomeEvent) -> Unit) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavDrawerContent(
                onCloseDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                },
                onEvent = { event ->
                    when (event) {
                        NavDrawerEvent.SignOut -> {
                            onEvent(HomeEvent.SignOut(context))
                        }

                        is NavDrawerEvent.ChangeTheme -> {
                            // Change theme
                            onEvent(HomeEvent.ChangeTheme(event.theme))
                        }
                    }
                },
                onAction = { action ->
                    when (action) {
                        NavDrawerAction.OnGoToSavedPicks -> {
                            // navController.navigate(Screen.SavedTopPicksScreen.route)
                            onAction(HomeAction.OnGoToSavedPicks)
                        }

                        NavDrawerAction.OnGoToSettings -> {
                            // navController.navigate(Screen.SettingsScreen.route)
                            onAction(HomeAction.OnGoToSettings)
                        }

                        NavDrawerAction.OnGoToHistory -> {
                            // navController.navigate(Screen.HistoryTabScreen.route)
                            onAction(HomeAction.OnGoToHistory)
                        }
                    }
                },
                state = state.drawerState
            )
        }
    ) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                Column(
                    modifier = Modifier

                ) {
                    if (state.defaultPrompts.isNotEmpty()) {
                        HomeDefaultPrompts(
                            modifier = Modifier,
                            prompts = state.defaultPrompts,
                            onClick = {
                                onEvent(HomeEvent.DefaultPromptClicked(it))
                            }
                        )
                    }

                    QuestionInput(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(
                                insets = WindowInsets.ime
                            ),
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
                            if (it.canUpgrade) {
                                onEvent(
                                    HomeEvent.UpgradeModel(
                                        showBottomSheet = true,
                                        modelId = it.modelId
                                    )
                                )
                                return@QuestionInput
                            }
                            onEvent(HomeEvent.ModelChanged(it))
                        }
                    )
                }
            },
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
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

                    IconButton(modifier = Modifier, onClick = {
                        onAction(HomeAction.OnGoToDiscover)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search_status_two),
                            contentDescription = null
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Home Screen
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (state.showWaitlistBottomSheet) {
                    GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                        onEvent(HomeEvent.UpgradeModel(showBottomSheet = false))
                    }) {
                        WaitlistBottomSheetContent(
                            modifier = Modifier,
                            options = state.waitlistAvailableOptions,
                            selectedOptions = state.selectedWaitlistOptions,
                            onOptionSelected = {
                                onEvent(HomeEvent.SelectWaitListOption(it))
                            },
                            onJoinWaitList = {
                                onEvent(HomeEvent.JoinWaitlist)
                            },
                            onDismiss = {
                                onEvent(HomeEvent.UpgradeModel(showBottomSheet = false))
                            }
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                ) {
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WaitlistBottomSheetContent(
    modifier: Modifier,
    options: List<String>,
    selectedOptions: List<String>,
    onOptionSelected: (String) -> Unit,
    onJoinWaitList: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var content by remember { mutableStateOf(0) }

        when (content) {
            0 -> {
                // Text
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.join_the_waitlist).uppercase(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                // Text
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.choose_the_capabilities_you_d_love_in_the_advanced_ai_model),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Flow row
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach {
                        SingleWaitlistOption(
                            modifier = Modifier,
                            isSelected = selectedOptions.contains(it),
                            onOptionSelected = {
                                onOptionSelected(it)
                            },
                            text = it
                        )
                    }
                }

                // Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFF947F6),
                                Color(0xFF0095FF)
                            )
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onJoinWaitList()
                        content = 1
                    }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.join_list).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFF947F6),
                                            Color(0xFF0095FF)
                                        )
                                    )
                                )
                            )
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.arrow_right_gradient),
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            1 -> {
                Spacer(modifier = Modifier.height(16.dp))
                // Image
                Image(painter = painterResource(R.drawable.copy_success), contentDescription = null)

                Text(
                    text = stringResource(R.string.you_re_on_the_list),
                    style = MaterialTheme.typography.titleLarge
                )

                // Text
                Text(
                    text = stringResource(R.string.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = {
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text(text = stringResource(R.string.continue_).uppercase())
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SingleWaitlistOption(modifier: Modifier, isSelected: Boolean, onOptionSelected: () -> Unit, text: String) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    if (isSelected) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            onClick = onOptionSelected
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
            onClick = onOptionSelected
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = gptInvestorColors.textColors.secondary50
            )
        }
    }
}
