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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.arrow_right_gradient
import com.thejawnpaul.gptinvestor.ask_me_anything_about_markets
import com.thejawnpaul.gptinvestor.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.copy_success
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerAction
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerContent
import com.thejawnpaul.gptinvestor.core.navigation.NavDrawerEvent
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.curated_for_you
import com.thejawnpaul.gptinvestor.daily_learn_tidbit
import com.thejawnpaul.gptinvestor.discover
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.company.presentation.ui.GptInvestorBottomSheet
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeErrorCard
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeGreeting
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeSearchBar
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeSectionHeader
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeTopPicksSection
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeTrendingSection
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.QuestionInput
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.ui.HomeTidbitSection
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.gpt_investor
import com.thejawnpaul.gptinvestor.ic_menu
import com.thejawnpaul.gptinvestor.ic_search_status_two
import com.thejawnpaul.gptinvestor.join_list
import com.thejawnpaul.gptinvestor.join_the_waitlist
import com.thejawnpaul.gptinvestor.movers_right_now
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import com.thejawnpaul.gptinvestor.today_s_lesson_didn_t_load
import com.thejawnpaul.gptinvestor.top_picks_today
import com.thejawnpaul.gptinvestor.trending_today
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
                onCloseDrawer = { scope.launch { drawerState.close() } },
                onEvent = { event ->
                    when (event) {
                        NavDrawerEvent.SignOut -> onEvent(HomeEvent.SignOut)
                        is NavDrawerEvent.ChangeTheme -> onEvent(HomeEvent.ChangeTheme(event.theme))
                    }
                },
                onAction = { action ->
                    when (action) {
                        NavDrawerAction.OnGoToSavedPicks -> onEvent(HomeEvent.GoToSavedPicks)
                        NavDrawerAction.OnGoToSettings -> onEvent(HomeEvent.GoToSettings)
                        NavDrawerAction.OnGoToHistory -> onEvent(HomeEvent.GoToHistory)
                        NavDrawerAction.OnGoToSavedTidbits -> onEvent(HomeEvent.GoToSavedTidbits)
                    }
                },
                state = state.drawerState
            )
        }
    ) {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                QuestionInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(insets = WindowInsets.ime),
                    onSendClick = { onEvent(HomeEvent.SendClick) },
                    hint = stringResource(Res.string.ask_me_anything_about_markets),
                    onTextChange = { onEvent(HomeEvent.ChatInputChanged(it)) },
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
                    },
                    showModelSelector = false
                )
            },
            topBar = {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = vectorResource(Res.drawable.ic_menu),
                                contentDescription = null
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.gpt_investor),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

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
                            IconButton(onClick = { onEvent(HomeEvent.GoToDiscover) }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_search_status_two),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 0.dp, vertical = 8.dp)
                ) {
                    // Guest banner
                    if (state.isGuestSession) {
                        TopGuestLabel(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = { onEvent(HomeEvent.GoToSignUp) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    HomeGreeting(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        timePeriod = state.timePeriod,
                        name = state.currentUser?.name
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    HomeSearchBar(modifier = Modifier.padding(horizontal = 16.dp), onClick = {
                        onEvent(HomeEvent.GoToSearch)
                    })
                    Spacer(modifier = Modifier.height(24.dp))

                    HomeSectionHeader(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        emoji = "🔥",
                        label = stringResource(Res.string.trending_today),
                        title = stringResource(Res.string.movers_right_now),
                        onSeeAll = { onEvent(HomeEvent.GoToAllTrending) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HomeTrendingSection(
                        view = state.trendingCompaniesView,
                        onRetry = { onEvent(HomeEvent.RetryTrendingCompanies) },
                        onClick = { onEvent(HomeEvent.ClickTrendingCompany(it)) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    HomeSectionHeader(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        emoji = "🔖",
                        label = stringResource(Res.string.top_picks_today),
                        title = stringResource(Res.string.curated_for_you),
                        onSeeAll = { onEvent(HomeEvent.GoToAllTopPicks) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HomeTopPicksSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        view = state.topPicksView,
                        onRetry = { onEvent(HomeEvent.RetryTopPicks) },
                        onClickPick = { onEvent(HomeEvent.ClickTopPick(it)) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    when {
                        state.homeTidbitView != null -> {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = stringResource(Res.string.daily_learn_tidbit).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalGPTInvestorColors.current.textColors.secondary50
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HomeTidbitSection(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                tidbit = state.homeTidbitView,
                                onClick = { onEvent(HomeEvent.ClickTidbit(it)) }
                            )
                        }

                        state.tidbitError != null -> {
                            HomeErrorCard(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                message = stringResource(Res.string.today_s_lesson_didn_t_load),
                                onRetry = { onEvent(HomeEvent.RetryTidbit) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (state.showWaitlistBottomSheet) {
                    GptInvestorBottomSheet(modifier = Modifier, onDismiss = {
                        onEvent(HomeEvent.UpgradeModel(showBottomSheet = false))
                    }) {
                        WaitlistBottomSheetContent(
                            modifier = Modifier,
                            options = state.waitlistAvailableOptions,
                            selectedOptions = state.selectedWaitlistOptions,
                            onSelectOption = { onEvent(HomeEvent.SelectWaitListOption(it)) },
                            onJoinWaitList = { onEvent(HomeEvent.JoinWaitlist) },
                            onDismiss = { onEvent(HomeEvent.UpgradeModel(showBottomSheet = false)) }
                        )
                    }
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.join_the_waitlist).uppercase(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(
                        Res.string.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach {
                        SingleWaitlistOption(
                            modifier = Modifier,
                            isSelected = selectedOptions.contains(it),
                            onSelectOption = { onSelectOption(it) },
                            text = it
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFF947F6), Color(0xFF0095FF))
                        )
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    onClick = {
                        onJoinWaitList()
                        content = 1
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(Res.string.join_list).uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF947F6), Color(0xFF0095FF))
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
                Image(
                    painter = painterResource(Res.drawable.copy_success),
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.you_re_on_the_list),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(
                        Res.string.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    onClick = { onDismiss() },
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

@PreviewLightDark
@Composable
private fun HomeScreenPreview() {
    GPTInvestorTheme {
        HomeScreenContent(
            state = HomeUiState(
                isGuestSession = true,
                topPicksView = TopPicksView(
                    topPicks = listOf(
                        TopPickPresentation(
                            id = "1",
                            ticker = "JPM",
                            companyName = "JP Morgan Chase & Co.",
                            rationale = "JPM is leveraging its massive scale to lead the " +
                                "banking sector's digital revolution.",
                            metrics = emptyList(),
                            risks = emptyList(),
                            confidenceScore = 80,
                            isSaved = false,
                            percentageChange = 1.2f,
                            imageUrl = "",
                            currentPrice = 185.0f
                        )
                    )
                ),
                trendingCompaniesView = TrendingCompaniesView(
                    companies = listOf(
                        TrendingStockPresentation("NVIDIA Corporation", "NVDA", "", 2.84f),
                        TrendingStockPresentation("Apple", "AAPL", "", 0.62f)
                    )
                ),
                homeTidbitView = HomeTidbitView(
                    id = "1",
                    previewUrl = "",
                    title = "When good traits lead to bad outcomes",
                    description = "Learn how compounding works."
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
