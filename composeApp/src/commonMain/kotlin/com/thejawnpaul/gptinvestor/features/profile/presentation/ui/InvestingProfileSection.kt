package com.thejawnpaul.gptinvestor.features.profile.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui.hintRes
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui.labelRes
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui.rememberHideThen
import com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui.rememberQuestionSheetState
import com.thejawnpaul.gptinvestor.features.profile.presentation.state.ProfileUiState
import com.thejawnpaul.gptinvestor.features.profile.presentation.viewmodel.ProfileEvent
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_label
import com.thejawnpaul.gptinvestor.profile_experience
import com.thejawnpaul.gptinvestor.profile_experience_footnote
import com.thejawnpaul.gptinvestor.profile_investing_profile
import com.thejawnpaul.gptinvestor.profile_not_set
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiOptionCard
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InvestingProfileSection(
    state: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AccountSection(
        title = stringResource(Res.string.profile_investing_profile),
        modifier = modifier,
        footnote = stringResource(Res.string.profile_experience_footnote)
    ) {
        AccountSettingsRow(
            icon = GiIcons.lightbulb,
            label = stringResource(Res.string.profile_experience),
            value = stringResource(state.experience?.labelRes() ?: Res.string.profile_not_set),
            onClick = { onEvent(ProfileEvent.ExperienceClicked) }
        )
    }
    if (state.showExperienceSheet) {
        val sheetState = rememberQuestionSheetState()
        val hideThen = rememberHideThen(sheetState)
        GiBottomSheet(
            onDismissRequest = { onEvent(ProfileEvent.DismissExperienceSheet) },
            title = stringResource(Res.string.post_auth_onboarding_experience_label),
            closeContentDescription = stringResource(Res.string.close),
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                InvestingExperience.entries.forEach { option ->
                    GiOptionCard(
                        title = stringResource(option.labelRes()),
                        selected = option == state.experience,
                        onClick = { hideThen { onEvent(ProfileEvent.ExperienceSelected(option)) } },
                        detail = stringResource(option.hintRes())
                    )
                }
            }
        }
    }
}
