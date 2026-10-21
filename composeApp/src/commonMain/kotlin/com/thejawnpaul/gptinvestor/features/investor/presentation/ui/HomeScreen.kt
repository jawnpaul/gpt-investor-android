package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.ask_me_anything_about_markets
import com.thejawnpaul.gptinvestor.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.core.platform.NotificationPermissionController
import com.thejawnpaul.gptinvestor.features.company.presentation.model.TrendingStockPresentation
import com.thejawnpaul.gptinvestor.features.guest.presentation.TopGuestLabel
import com.thejawnpaul.gptinvestor.features.investor.presentation.state.TrendingCompaniesView
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.DailyDigestCard
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.HomeTrendingSection
import com.thejawnpaul.gptinvestor.features.investor.presentation.ui.component.homeGreeting
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeEvent
import com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel.HomeUiState
import com.thejawnpaul.gptinvestor.features.tidbit.presentation.state.HomeTidbitView
import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation
import com.thejawnpaul.gptinvestor.features.toppick.presentation.state.TopPicksView
import com.thejawnpaul.gptinvestor.join_list
import com.thejawnpaul.gptinvestor.join_the_waitlist
import com.thejawnpaul.gptinvestor.movers_right_now
import com.thejawnpaul.gptinvestor.profile
import com.thejawnpaul.gptinvestor.send
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiAskComposer
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiCheckbox
import com.thejawnpaul.gptinvestor.theme.components.GiDivider
import com.thejawnpaul.gptinvestor.theme.components.GiGreetingHeader
import com.thejawnpaul.gptinvestor.theme.components.GiIconTile
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileSize
import com.thejawnpaul.gptinvestor.theme.components.GiIconTileTone
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiListGroup
import com.thejawnpaul.gptinvestor.theme.components.GiSectionHeader
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.components.GiToastHost
import com.thejawnpaul.gptinvestor.theme.components.GiToastState
import com.thejawnpaul.gptinvestor.theme.components.rememberGiToastState
import com.thejawnpaul.gptinvestor.trending_today
import com.thejawnpaul.gptinvestor.you_re_on_the_list
import com.thejawnpaul.gptinvestor.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    toastState: GiToastState,
    modifier: Modifier = Modifier,
    composerModifier: Modifier = Modifier
) {
    val notificationPermissionController: NotificationPermissionController = koinInject()

    HomeScreenContent(
        state = state,
        onEvent = onEvent,
        notificationPermissionController = notificationPermissionController,
        toastState = toastState,
        modifier = modifier,
        composerModifier = composerModifier
    )
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    notificationPermissionController: NotificationPermissionController,
    toastState: GiToastState,
    modifier: Modifier = Modifier,
    composerModifier: Modifier = Modifier
) {
    notificationPermissionController.RequestPermissionIfNeeded(
        shouldRequest = state.requestForNotificationPermission == null && !state.isGuestSession,
        onGrant = { onEvent(HomeEvent.NotificationPermissionGranted) },
        onDeny = { onEvent(HomeEvent.NotificationPermissionDenied) }
    )
    val gutter = Modifier.padding(horizontal = GiSpace.gutter)
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            GiToastHost(state = toastState, modifier = Modifier.padding(horizontal = GiSpace.gutter))
        },
        bottomBar = {
            // Floats over the scrolling content; the fade keeps cards from colliding with the composer.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(0f to Color.Transparent, 0.4f to GiTheme.colors.bg))
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .windowInsetsPadding(WindowInsets.ime)
                    .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x6, bottom = GiSpace.x4)
            ) {
                GiAskComposer(
                    value = state.chatInput ?: "",
                    onValueChange = { onEvent(HomeEvent.ChatInputChanged(it)) },
                    onSend = { onEvent(HomeEvent.SendClick) },
                    placeholder = stringResource(Res.string.ask_me_anything_about_markets),
                    sendContentDescription = stringResource(Res.string.send),
                    modifier = composerModifier
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(top = GiSpace.x3, bottom = innerPadding.calculateBottomPadding() + GiSpace.x6),
                verticalArrangement = Arrangement.spacedBy(GiSpace.sectionGap)
            ) {
                // The greeting is the page title; the app name lives on splash and onboarding, not Home.
                GiGreetingHeader(
                    greeting = homeGreeting(timePeriod = state.timePeriod, name = state.firstName),
                    avatarInitials = state.firstName?.firstOrNull()?.uppercase(),
                    avatarContentDescription = stringResource(Res.string.profile),
                    onAvatarClick = { onEvent(HomeEvent.GoToProfile) },
                    modifier = gutter,
                    overline = state.todayLabel
                )

                if (state.isGuestSession) {
                    TopGuestLabel(modifier = gutter, onClick = { onEvent(HomeEvent.GoToSignUp) })
                }

                DailyDigestCard(
                    modifier = gutter,
                    view = state.dailyDigestView,
                    onAddStock = { onEvent(HomeEvent.AddStockToDigest(it)) },
                    onBrowseAll = { onEvent(HomeEvent.BrowseAllCompanies) },
                    onSeeFullDigest = { onEvent(HomeEvent.SeeFullDigest) },
                    onUnlockPremiumPending = { onEvent(HomeEvent.UnlockPremium("digest_pending_card")) },
                    onUnlockPremiumReady = { onEvent(HomeEvent.UnlockPremium("digest_ready_card")) },
                    onRetry = { onEvent(HomeEvent.RetryDailyDigest) }
                )

                Column(verticalArrangement = Arrangement.spacedBy(GiSpace.titleToContent)) {
                    GiSectionHeader(
                        title = stringResource(Res.string.movers_right_now),
                        modifier = gutter,
                        eyebrow = stringResource(Res.string.trending_today),
                        eyebrowIcon = GiIcons.fire
                    )
                    HomeTrendingSection(
                        view = state.trendingCompaniesView,
                        onRetry = { onEvent(HomeEvent.RetryTrendingCompanies) },
                        onClick = { onEvent(HomeEvent.ClickTrendingCompany(it)) }
                    )
                }
            }

            if (state.showWaitlistBottomSheet) {
                WaitlistBottomSheet(
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

// No board for the waitlist: built on the v4 sheet pattern (StatNumber title, body, options group, CTA L).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitlistBottomSheet(
    options: List<String>,
    selectedOptions: List<String>,
    onJoinWaitList: () -> Unit,
    onDismiss: () -> Unit,
    onSelectOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var joined by rememberSaveable { mutableStateOf(false) }
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = waitlistTitle(joined),
        subtitle = waitlistSubtitle(joined),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        eyebrow = if (joined) {
            { WaitlistJoinedTile() }
        } else {
            null
        }
    ) {
        WaitlistBody(
            joined = joined,
            options = options,
            selectedOptions = selectedOptions,
            onSelectOption = onSelectOption,
            onJoin = {
                onJoinWaitList()
                joined = true
            },
            onDismiss = onDismiss
        )
    }
}

// Body for callers that still host the waitlist inside their own sheet; draws the same header.
@Composable
fun WaitlistBottomSheetContent(
    options: List<String>,
    selectedOptions: List<String>,
    onJoinWaitList: () -> Unit,
    onDismiss: () -> Unit,
    onSelectOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var joined by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, bottom = GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x4)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            if (joined) WaitlistJoinedTile()
            GiText(
                text = waitlistTitle(joined),
                style = GiTextStyle.StatNumber,
                modifier = Modifier.semantics { heading() }
            )
            GiText(text = waitlistSubtitle(joined), style = GiTextStyle.Body, color = GiTheme.colors.body)
        }
        WaitlistBody(
            joined = joined,
            options = options,
            selectedOptions = selectedOptions,
            onSelectOption = onSelectOption,
            onJoin = {
                onJoinWaitList()
                joined = true
            },
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun waitlistTitle(joined: Boolean): String =
    stringResource(if (joined) Res.string.you_re_on_the_list else Res.string.join_the_waitlist)

@Composable
private fun waitlistSubtitle(joined: Boolean): String = stringResource(
    if (joined) {
        Res.string.you_re_one_step_closer_to_unlocking_the_power_of_quantum_edge
    } else {
        Res.string.choose_the_capabilities_you_d_love_in_the_advanced_ai_model
    }
)

@Composable
private fun WaitlistJoinedTile() {
    GiIconTile(icon = GiIcons.check, size = GiIconTileSize.Large, tone = GiIconTileTone.Gain)
}

@Composable
private fun WaitlistBody(
    joined: Boolean,
    options: List<String>,
    selectedOptions: List<String>,
    onSelectOption: (String) -> Unit,
    onJoin: () -> Unit,
    onDismiss: () -> Unit
) {
    if (joined) {
        GiButton(
            text = stringResource(Res.string.continue_),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        )
        return
    }
    if (options.isNotEmpty()) {
        GiListGroup {
            options.forEachIndexed { index, option ->
                if (index > 0) GiDivider()
                SingleWaitlistOption(
                    isSelected = selectedOptions.contains(option),
                    text = option,
                    onSelectOption = { onSelectOption(option) },
                    modifier = Modifier.padding(horizontal = GiSpace.x4, vertical = GiSpace.x1)
                )
            }
        }
    }
    GiButton(
        text = stringResource(Res.string.join_list),
        onClick = onJoin,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SingleWaitlistOption(isSelected: Boolean, text: String, onSelectOption: () -> Unit, modifier: Modifier = Modifier) {
    GiCheckbox(
        checked = isSelected,
        onCheckedChange = { onSelectOption() },
        label = text,
        modifier = modifier.fillMaxWidth()
    )
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
            toastState = rememberGiToastState(),
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
