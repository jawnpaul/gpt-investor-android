package com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.back
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.state.PostAuthOnboardingUiState
import com.thejawnpaul.gptinvestor.onboarding_step_of
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_beginner
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_experienced
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_intermediate
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_label
import com.thejawnpaul.gptinvestor.post_auth_onboarding_goal_generate_income
import com.thejawnpaul.gptinvestor.post_auth_onboarding_goal_grow_wealth
import com.thejawnpaul.gptinvestor.post_auth_onboarding_goal_just_curious
import com.thejawnpaul.gptinvestor.post_auth_onboarding_goal_label
import com.thejawnpaul.gptinvestor.post_auth_onboarding_goal_learn_basics
import com.thejawnpaul.gptinvestor.post_auth_onboarding_risk_aggressive
import com.thejawnpaul.gptinvestor.post_auth_onboarding_risk_balanced
import com.thejawnpaul.gptinvestor.post_auth_onboarding_risk_conservative
import com.thejawnpaul.gptinvestor.post_auth_onboarding_risk_label
import com.thejawnpaul.gptinvestor.post_auth_onboarding_save_error
import com.thejawnpaul.gptinvestor.post_auth_onboarding_subtitle
import com.thejawnpaul.gptinvestor.post_auth_onboarding_title
import com.thejawnpaul.gptinvestor.skip
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiBanner
import com.thejawnpaul.gptinvestor.theme.components.GiBannerTone
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonSize
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiChipSize
import com.thejawnpaul.gptinvestor.theme.components.GiEyebrow
import com.thejawnpaul.gptinvestor.theme.components.GiFillScrollColumn
import com.thejawnpaul.gptinvestor.theme.components.GiIconButton
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiStepBar
import com.thejawnpaul.gptinvestor.theme.components.GiTopBar
import com.thejawnpaul.gptinvestor.theme.rememberGiSharedAxisTransition
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private const val STEP_COUNT = 2

@Composable
fun PostAuthOnboardingScreen(
    state: PostAuthOnboardingUiState,
    onSelectExperience: (InvestingExperience) -> Unit,
    onSelectGoal: (InvestingGoal) -> Unit,
    onSelectRiskTolerance: (RiskTolerance) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberGiSharedAxisTransition()
    val experienceScrollState = rememberScrollState()
    val goalsScrollState = rememberScrollState()
    val scrollStateFor: (Int) -> ScrollState = { step -> if (step == 0) experienceScrollState else goalsScrollState }
    NavigationBackHandler(
        state = rememberNavigationEventState(currentInfo = NavigationEventInfo.None),
        isBackEnabled = state.currentStep > 0,
        onBackCompleted = { if (!state.isLoading) onBack() }
    )
    Scaffold(
        modifier = modifier,
        containerColor = GiTheme.colors.bg,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            PersonaliseTopBar(
                state = state,
                scrolled = scrollStateFor(state.currentStep).canScrollBackward,
                onSkip = onSkip,
                onBack = onBack
            )
        },
        bottomBar = { PersonaliseFooter(state = state, onContinue = onContinue) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.currentStep,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize(),
            transitionSpec = { transition(targetState > initialState) },
            label = "postAuthOnboardingStep"
        ) { step ->
            GiFillScrollColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = GiSpace.gutter),
                scrollState = scrollStateFor(step)
            ) {
                PersonaliseHeading(step = step)
                if (step == 0) {
                    PersonaliseQuestion(
                        label = stringResource(Res.string.post_auth_onboarding_experience_label),
                        options = InvestingExperience.entries,
                        selected = state.experience,
                        optionLabel = { it.labelRes() },
                        onSelect = onSelectExperience,
                        modifier = Modifier.padding(top = GiSpace.x8)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(top = GiSpace.x8),
                        verticalArrangement = Arrangement.spacedBy(GiSpace.x6)
                    ) {
                        PersonaliseQuestion(
                            label = stringResource(Res.string.post_auth_onboarding_goal_label),
                            options = InvestingGoal.entries,
                            selected = state.goal,
                            optionLabel = { it.labelRes() },
                            onSelect = onSelectGoal
                        )
                        PersonaliseQuestion(
                            label = stringResource(Res.string.post_auth_onboarding_risk_label),
                            options = RiskTolerance.entries,
                            selected = state.riskTolerance,
                            optionLabel = { it.labelRes() },
                            onSelect = onSelectRiskTolerance
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonaliseTopBar(
    state: PostAuthOnboardingUiState,
    scrolled: Boolean,
    onSkip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val back = stringResource(Res.string.back)
    GiTopBar(
        title = null,
        onBack = null,
        backContentDescription = back,
        modifier = modifier,
        scrolled = scrolled,
        center = {
            AnimatedVisibility(
                visible = state.currentStep > 0,
                enter = expandHorizontally(GiMotion.smooth(), expandFrom = Alignment.Start) +
                    fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)),
                exit = shrinkHorizontally(GiMotion.smooth(), shrinkTowards = Alignment.Start) +
                    fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
            ) {
                GiIconButton(
                    icon = GiIcons.caretLeft,
                    contentDescription = back,
                    onClick = onBack,
                    modifier = Modifier.padding(end = GiSpace.x4),
                    enabled = !state.isLoading
                )
            }
            GiStepBar(
                count = STEP_COUNT,
                current = state.currentStep,
                contentDescription = stringResource(Res.string.onboarding_step_of, state.currentStep + 1, STEP_COUNT),
                modifier = Modifier.weight(1f)
            )
        }
    ) {
        GiButton(
            text = stringResource(Res.string.skip),
            onClick = onSkip,
            size = GiButtonSize.Medium,
            variant = GiButtonVariant.TextMuted,
            enabled = !state.isLoading
        )
    }
}

@Composable
private fun PersonaliseHeading(step: Int, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    Column(
        modifier = modifier.padding(top = GiSpace.x6),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        GiEyebrow(text = stringResource(Res.string.onboarding_step_of, step + 1, STEP_COUNT))
        Text(
            text = stringResource(Res.string.post_auth_onboarding_title),
            modifier = Modifier.semantics { heading() },
            color = colors.ink,
            style = type.display
        )
        Text(
            text = stringResource(Res.string.post_auth_onboarding_subtitle),
            color = colors.muted,
            style = type.input
        )
    }
}

@Composable
private fun <T> PersonaliseQuestion(
    label: String,
    options: List<T>,
    selected: T?,
    optionLabel: (T) -> StringResource,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.semantics { isTraversalGroup = true },
        verticalArrangement = Arrangement.spacedBy(GiSpace.x3)
    ) {
        Text(text = label, color = GiTheme.colors.ink, style = GiTheme.type.label)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            options.forEach { option ->
                GiChip(
                    text = stringResource(optionLabel(option)),
                    onClick = { onSelect(option) },
                    selected = option == selected,
                    size = GiChipSize.Large
                )
            }
        }
    }
}

@Composable
private fun PersonaliseFooter(state: PostAuthOnboardingUiState, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = GiSpace.gutter, end = GiSpace.gutter, top = GiSpace.x4, bottom = GiSpace.x4)
    ) {
        AnimatedVisibility(
            visible = state.error != null,
            enter = expandVertically(GiMotion.smooth()) +
                fadeIn(tween(GiMotion.BASE, delayMillis = GiMotion.INSTANT, easing = EaseOutQuart)),
            exit = shrinkVertically(GiMotion.smooth()) + fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
        ) {
            GiBanner(
                title = stringResource(Res.string.post_auth_onboarding_save_error),
                modifier = Modifier
                    .padding(bottom = GiSpace.x4)
                    .semantics { liveRegion = LiveRegionMode.Assertive },
                icon = GiIcons.warningCircle,
                tone = GiBannerTone.Error
            )
        }
        GiButton(
            text = stringResource(Res.string.continue_),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            loading = state.isLoading
        )
    }
}

private fun InvestingExperience.labelRes() = when (this) {
    InvestingExperience.BEGINNER -> Res.string.post_auth_onboarding_experience_beginner
    InvestingExperience.INTERMEDIATE -> Res.string.post_auth_onboarding_experience_intermediate
    InvestingExperience.EXPERIENCED -> Res.string.post_auth_onboarding_experience_experienced
}

private fun InvestingGoal.labelRes() = when (this) {
    InvestingGoal.GROW_WEALTH -> Res.string.post_auth_onboarding_goal_grow_wealth
    InvestingGoal.GENERATE_INCOME -> Res.string.post_auth_onboarding_goal_generate_income
    InvestingGoal.LEARN_BASICS -> Res.string.post_auth_onboarding_goal_learn_basics
    InvestingGoal.JUST_CURIOUS -> Res.string.post_auth_onboarding_goal_just_curious
}

private fun RiskTolerance.labelRes() = when (this) {
    RiskTolerance.CONSERVATIVE -> Res.string.post_auth_onboarding_risk_conservative
    RiskTolerance.BALANCED -> Res.string.post_auth_onboarding_risk_balanced
    RiskTolerance.AGGRESSIVE -> Res.string.post_auth_onboarding_risk_aggressive
}

@Composable
private fun PostAuthOnboardingScreenPreview(state: PostAuthOnboardingUiState) {
    GPTInvestorTheme {
        PostAuthOnboardingScreen(
            state = state,
            onSelectExperience = {},
            onSelectGoal = {},
            onSelectRiskTolerance = {},
            onContinue = {},
            onSkip = {},
            onBack = {}
        )
    }
}

private val goalsPreviewState = PostAuthOnboardingUiState(
    currentStep = 1,
    goal = InvestingGoal.GROW_WEALTH,
    riskTolerance = RiskTolerance.BALANCED
)

@PreviewLightDark
@Composable
private fun ExperienceStepPreview() {
    PostAuthOnboardingScreenPreview(
        state = PostAuthOnboardingUiState(currentStep = 0, experience = InvestingExperience.INTERMEDIATE)
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GoalsStepPreview() {
    PostAuthOnboardingScreenPreview(state = goalsPreviewState)
}

@PreviewLightDark
@Composable
private fun SavingPreview() {
    PostAuthOnboardingScreenPreview(state = goalsPreviewState.copy(isLoading = true))
}

@PreviewLightDark
@Composable
private fun SaveErrorPreview() {
    PostAuthOnboardingScreenPreview(state = goalsPreviewState.copy(error = "Something went wrong."))
}
