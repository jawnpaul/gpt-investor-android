package com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.features.component.GPTInvestorButton
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.InvestingGoal
import com.thejawnpaul.gptinvestor.features.postauthonboarding.domain.model.RiskTolerance
import com.thejawnpaul.gptinvestor.features.postauthonboarding.presentation.state.PostAuthOnboardingUiState
import com.thejawnpaul.gptinvestor.onboarding_skip_for_now
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
import com.thejawnpaul.gptinvestor.post_auth_onboarding_subtitle
import com.thejawnpaul.gptinvestor.post_auth_onboarding_title
import com.thejawnpaul.gptinvestor.something_went_wrong
import com.thejawnpaul.gptinvestor.theme.GPTInvestorTheme
import com.thejawnpaul.gptinvestor.theme.LocalGPTInvestorColors
import org.jetbrains.compose.resources.stringResource

private const val TOTAL_STEPS = 2

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
    AnimatedContent(
        targetState = state.currentStep,
        modifier = modifier,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(tween(280)) { it } + fadeIn(tween(220)) togetherWith
                    slideOutHorizontally(tween(220)) { -it } + fadeOut(tween(150))
            } else {
                slideInHorizontally(tween(280)) { -it } + fadeIn(tween(220)) togetherWith
                    slideOutHorizontally(tween(220)) { it } + fadeOut(tween(150))
            }
        },
        label = "postAuthOnboardingStep"
    ) { step ->
        when (step) {
            0 -> StepOneContent(
                state = state,
                onSelectExperience = onSelectExperience,
                onContinue = onContinue,
                onSkip = onSkip
            )
            else -> StepTwoContent(
                state = state,
                onSelectGoal = onSelectGoal,
                onSelectRiskTolerance = onSelectRiskTolerance,
                onContinue = onContinue,
                onSkip = onSkip,
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepOneContent(
    state: PostAuthOnboardingUiState,
    onSelectExperience: (InvestingExperience) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            StepIndicator(currentStep = 0, totalSteps = TOTAL_STEPS)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.post_auth_onboarding_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.post_auth_onboarding_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OnboardingSection(label = stringResource(Res.string.post_auth_onboarding_experience_label)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InvestingExperience.entries.forEach { option ->
                        FilterChip(
                            selected = state.experience == option,
                            onClick = { onSelectExperience(option) },
                            label = { Text(stringResource(option.labelRes())) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = gptInvestorColors.accentColors.allAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            GPTInvestorButton(
                text = stringResource(Res.string.continue_),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(onClick = onSkip) {
                Text(
                    text = stringResource(Res.string.onboarding_skip_for_now),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepTwoContent(
    state: PostAuthOnboardingUiState,
    onSelectGoal: (InvestingGoal) -> Unit,
    onSelectRiskTolerance: (RiskTolerance) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val gptInvestorColors = LocalGPTInvestorColors.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
                StepIndicator(
                    currentStep = 1,
                    totalSteps = TOTAL_STEPS,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(Res.string.post_auth_onboarding_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OnboardingSection(label = stringResource(Res.string.post_auth_onboarding_goal_label)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InvestingGoal.entries.forEach { option ->
                        FilterChip(
                            selected = state.goal == option,
                            onClick = { onSelectGoal(option) },
                            label = { Text(stringResource(option.labelRes())) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = gptInvestorColors.accentColors.allAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OnboardingSection(label = stringResource(Res.string.post_auth_onboarding_risk_label)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RiskTolerance.entries.forEach { option ->
                        FilterChip(
                            selected = state.riskTolerance == option,
                            onClick = { onSelectRiskTolerance(option) },
                            label = { Text(stringResource(option.labelRes())) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = gptInvestorColors.accentColors.allAccent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            if (state.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.something_went_wrong),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                GPTInvestorButton(
                    text = stringResource(Res.string.continue_),
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(onClick = onSkip, enabled = !state.isLoading) {
                Text(
                    text = stringResource(Res.string.onboarding_skip_for_now),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(totalSteps) { index ->
            val color = if (index == currentStep) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
            Surface(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape),
                color = color,
                content = {}
            )
        }
    }
}

@Composable
private fun OnboardingSection(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = label, style = MaterialTheme.typography.titleSmall)
        content()
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

@PreviewLightDark
@Composable
private fun StepOnePreview() {
    GPTInvestorTheme {
        Surface {
            PostAuthOnboardingScreen(
                state = PostAuthOnboardingUiState(currentStep = 0, experience = InvestingExperience.BEGINNER),
                onSelectExperience = {},
                onSelectGoal = {},
                onSelectRiskTolerance = {},
                onContinue = {},
                onSkip = {},
                onBack = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun StepTwoPreview() {
    GPTInvestorTheme {
        Surface {
            PostAuthOnboardingScreen(
                state = PostAuthOnboardingUiState(
                    currentStep = 1,
                    goal = InvestingGoal.GROW_WEALTH,
                    riskTolerance = RiskTolerance.BALANCED
                ),
                onSelectExperience = {},
                onSelectGoal = {},
                onSelectRiskTolerance = {},
                onContinue = {},
                onSkip = {},
                onBack = {}
            )
        }
    }
}
