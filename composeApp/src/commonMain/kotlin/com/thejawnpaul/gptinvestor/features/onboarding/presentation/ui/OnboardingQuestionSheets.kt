package com.thejawnpaul.gptinvestor.features.onboarding.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.alerts_ask_accept
import com.thejawnpaul.gptinvestor.alerts_ask_body
import com.thejawnpaul.gptinvestor.alerts_ask_title
import com.thejawnpaul.gptinvestor.close
import com.thejawnpaul.gptinvestor.continue_
import com.thejawnpaul.gptinvestor.experience_hint_beginner
import com.thejawnpaul.gptinvestor.experience_hint_experienced
import com.thejawnpaul.gptinvestor.experience_hint_intermediate
import com.thejawnpaul.gptinvestor.features.onboarding.domain.model.InvestingExperience
import com.thejawnpaul.gptinvestor.not_now
import com.thejawnpaul.gptinvestor.onboarding_experience_sheet_body
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_beginner
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_experienced
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_intermediate
import com.thejawnpaul.gptinvestor.post_auth_onboarding_experience_label
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.components.GiBottomSheet
import com.thejawnpaul.gptinvestor.theme.components.GiButton
import com.thejawnpaul.gptinvestor.theme.components.GiButtonVariant
import com.thejawnpaul.gptinvestor.theme.components.GiChip
import com.thejawnpaul.gptinvestor.theme.components.GiChipSize
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiSheetSubtitle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun InvestingExperience.labelRes(): StringResource = when (this) {
    InvestingExperience.BEGINNER -> Res.string.post_auth_onboarding_experience_beginner
    InvestingExperience.INTERMEDIATE -> Res.string.post_auth_onboarding_experience_intermediate
    InvestingExperience.EXPERIENCED -> Res.string.post_auth_onboarding_experience_experienced
}

internal fun InvestingExperience.hintRes(): StringResource = when (this) {
    InvestingExperience.BEGINNER -> Res.string.experience_hint_beginner
    InvestingExperience.INTERMEDIATE -> Res.string.experience_hint_intermediate
    InvestingExperience.EXPERIENCED -> Res.string.experience_hint_experienced
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperienceQuestionSheet(
    selected: InvestingExperience?,
    canContinue: Boolean,
    onSelect: (InvestingExperience) -> Unit,
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val sheetState = rememberQuestionSheetState()
    val hideThen = rememberHideThen(sheetState)
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.post_auth_onboarding_experience_label),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        subtitle = stringResource(Res.string.onboarding_experience_sheet_body),
        subtitleStyle = GiSheetSubtitle.Below,
        sheetState = sheetState
    ) {
        FlowRow(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            InvestingExperience.entries.forEach { option ->
                GiChip(
                    text = stringResource(option.labelRes()),
                    onClick = { onSelect(option) },
                    selected = option == selected,
                    size = GiChipSize.Large,
                    singleChoice = true
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = GiSpace.x4),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiButton(
                text = stringResource(Res.string.continue_),
                onClick = {
                    haptics.performHapticFeedback(GiHaptics.Confirm)
                    hideThen(onContinue)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canContinue
            )
            GiButton(
                text = stringResource(Res.string.not_now),
                onClick = { hideThen(onDismiss) },
                modifier = Modifier.fillMaxWidth(),
                variant = GiButtonVariant.TextMuted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsQuestionSheet(ticker: String, onAccept: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val sheetState = rememberQuestionSheetState()
    val hideThen = rememberHideThen(sheetState)
    GiBottomSheet(
        onDismissRequest = onDismiss,
        title = stringResource(Res.string.alerts_ask_title, ticker),
        closeContentDescription = stringResource(Res.string.close),
        modifier = modifier,
        subtitle = stringResource(Res.string.alerts_ask_body),
        subtitleStyle = GiSheetSubtitle.Below,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = GiSpace.x2),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiButton(
                text = stringResource(Res.string.alerts_ask_accept),
                onClick = { hideThen(onAccept) },
                modifier = Modifier.fillMaxWidth()
            )
            GiButton(
                text = stringResource(Res.string.not_now),
                onClick = { hideThen(onDismiss) },
                modifier = Modifier.fillMaxWidth(),
                variant = GiButtonVariant.TextMuted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberQuestionSheetState(): SheetState = rememberBottomSheetState(
    initialValue = SheetValue.Hidden,
    enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun rememberHideThen(sheetState: SheetState): (() -> Unit) -> Unit {
    val scope = rememberCoroutineScope()
    var leaving by remember { mutableStateOf(false) }
    return { action ->
        if (!leaving) {
            leaving = true
            scope.launch { sheetState.hide() }.invokeOnCompletion { action() }
        }
    }
}
