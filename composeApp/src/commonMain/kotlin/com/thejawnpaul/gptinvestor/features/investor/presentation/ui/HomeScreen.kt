package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.arrow_right_gradient
import com.thejawnpaul.gptinvestor.ask_me_a_question
import com.thejawnpaul.gptinvestor.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.copy_success
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerAction
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerContent
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerEvent
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultPrompt
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.HomeDefaultPrompts
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.HomeTidbitItem
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.ic_menu
import com.thejawnpaul.gptinvestor.ic_search_status_two
import com.thejawnpaul.gptinvestor.join_list
import com.thejawnpaul.gptinvestor.join_the_waitlist
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.you_re_on_the_list
import com.thejawnpaul.gptinvestor.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

@Composable
fun HomeScreen(state: HomeUiState, onEvent: (HomeEvent) -> Unit, modifier: Modifier = Modifier) {
    val notificationPermissionController: NotificationPermissionController = koinInject()

    HomeScreenContent(
        state = state,
        onEvent = onEvent,
        notificationPermissionController = notificationPermissionController,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    notificationPermissionController: NotificationPermissionController,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    notificationPermissionController.RequestPermissionIfNeeded(
        shouldRequest = state.requestForNotificationPermission == null,
        onGrant = { onEvent(HomeEvent.NotificationPermissionGranted) },
        onDeny = { onEvent(HomeEvent.NotificationPermissionDenied) }
    )

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
                            onEvent(HomeEvent.SignOut)
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
                            onEvent(HomeEvent.GoToSavedPicks)
                        }

                        NavDrawerAction.OnGoToSettings -> {
                            onEvent(HomeEvent.GoToSettings)
                        }

                        NavDrawerAction.OnGoToHistory -> {
                            onEvent(HomeEvent.GoToHistory)
                        }

                        NavDrawerAction.OnGoToSavedTidbits -> {
                            onEvent(HomeEvent.GoToSavedTidbits)
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
                        onSendClick = {
                            onEvent(HomeEvent.SendClick)
                        },
                        hint = stringResource(Res.string.ask_me_a_question),
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
                Column {
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
                                imageVector = vectorResource(Res.drawable.ic_menu),
                                contentDescription = null
                            )
                        }

                        Text(
                            text = stringResource(Res.string.gpt_investor),
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null,
                                onClick = { onEvent(HomeEvent.GoToDiscover) }
                            ),
                            horizontalArrangement = Arrangement.spacedBy(0.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.discover),
                                style = MaterialTheme.typography.titleMedium
                            )

                            IconButton(modifier = Modifier, onClick = {
                                onEvent(HomeEvent.GoToDiscover)
                            }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_search_status_two),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            modifier = Modifier,
                            onClick = {
                            }
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
                            onSelectOption = {
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

                state.homeTidbitView?.let { item ->
                    HomeTidbitItem(
                        modifier = Modifier.align(Alignment.Center),
                        imageUrl = item.previewUrl,
                        title = item.title,
                        description = item.description,
                        tidbitId = item.id,
                        onTidbitClick = {
                            onEvent(HomeEvent.ClickTidbit(it))
                        },
                        onClickSeeAll = {
                            onEvent(HomeEvent.GoToAllTidbits)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WaitlistBottomSheetContent(
    options: List<String>,
    selectedOptions: List<String>,
    onJoinWaitList: () -> Unit,
    onDismiss: () -> Unit,
    onSelectOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var content by remember { mutableIntStateOf(0) }

        when (content) {
            0 -> {
                // Text
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.join_the_waitlist).uppercase(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                // Text
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(
                        Res.string.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
                    ),
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
                            onSelectOption = {
                                onSelectOption(it)
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
                                text = stringResource(Res.string.join_list).uppercase(),
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
                                painter = painterResource(Res.drawable.arrow_right_gradient),
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            1 -> {
                Spacer(modifier = Modifier.height(16.dp))
                // Image
                Image(
                    painter = painterResource(Res.drawable.copy_success),
                    contentDescription = null
                )

                Text(
                    text = stringResource(Res.string.you_re_on_the_list),
                    style = MaterialTheme.typography.titleLarge
                )

                // Text
                Text(
                    text = stringResource(
                        Res.string.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge
                    ),
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
                    Text(text = stringResource(Res.string.continue_).uppercase())
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SingleWaitlistOption(isSelected: Boolean, text: String, onSelectOption: () -> Unit, modifier: Modifier = Modifier) {
    val gptInvestorColors = LocalGPTInvestorColors.current
    if (isSelected) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(corner = CornerSize(20.dp)),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface),
            onClick = onSelectOption
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
            onClick = onSelectOption
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

@Preview
@Composable
private fun HomeScreenPreview() {
    GPTInvestorTheme {
        HomeScreenContent(
            state = HomeUiState(
                isGuestSession = true,
                defaultPrompts = listOf(
                    DefaultPrompt(
                        title = "What is the best way to invest in stocks?",
                        query = "What is the best way to invest in stocks?"
                    ),
                    DefaultPrompt(
                        title = "How to save for retirement?",
                        query = "How to save for retirement?"
                    )
                ),
                availableModels = listOf(DefaultModel()),
                selectedModel = DefaultModel(),
                homeTidbitView = HomeTidbitView(
                    id = "1",
                    previewUrl = "",
                    title = "The Power of Compounding",
                    description = "Learn how compounding works and why it is the key to wealth creation."
                )
            ),
            onEvent = {},
            notificationPermissionController = object : NotificationPermissionController {
                @Composable
                override fun RequestPermissionIfNeeded(
                    shouldRequest: Boolean,
                    onGrant: () -> Unit,
                    onDeny: () -> Unit
                ) {
                }
            }
        )
    }
}
