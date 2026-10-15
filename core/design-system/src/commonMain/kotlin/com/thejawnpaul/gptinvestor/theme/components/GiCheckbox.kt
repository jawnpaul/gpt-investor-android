package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// The whole row is the tap target. Use GiSwitch for settings that apply at once.
@Composable
fun GiCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val state = when {
        indeterminate -> ToggleableState.Indeterminate
        checked -> ToggleableState.On
        else -> ToggleableState.Off
    }
    GiSelectionRow(
        label = label,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier,
        selectionModifier = Modifier.triStateToggleable(
            state = state,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onClick = {
                haptics.performHapticFeedback(GiHaptics.Selection)
                onCheckedChange(!checked)
            }
        )
    ) {
        val isFilled = state != ToggleableState.Off
        Box(
            modifier = Modifier
                .giFocusRing(interactionSource, GiRadius.xs)
                .size(22.dp)
                .clip(GiRadius.xs)
                .background(if (isFilled) colors.violet else colors.raised)
                .then(
                    if (isFilled) {
                        Modifier
                    } else {
                        Modifier.border(2.dp, if (isError) colors.error else colors.control, GiRadius.xs)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isFilled) {
                Icon(
                    imageVector = if (indeterminate) GiIcons.minusBold else GiIcons.checkBold,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.onViolet
                )
            }
        }
    }
}

// Shared by checkbox and radio: min 44 tall, bleeds 10 past its bounds so the control lines up with the content edge.
@Composable
internal fun GiSelectionRow(
    label: String?,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    selectionModifier: Modifier,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit
) {
    if (label == null) {
        Box(modifier = modifier.alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA).then(selectionModifier)) {
            control()
        }
        return
    }
    Row(
        modifier = modifier
            .bleedHorizontal(ROW_BLEED)
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .heightIn(min = GiSpace.touchTarget)
            .clip(GiRadius.sm)
            .then(selectionModifier)
            .background(GiTheme.colors.ink.copy(alpha = stateLayerAlpha(interactionSource)))
            .padding(horizontal = ROW_BLEED),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        control()
        GiText(text = label, style = GiTextStyle.Label, fontWeight = FontWeight.Medium)
    }
}

private val ROW_BLEED = 10.dp

private fun Modifier.bleedHorizontal(amount: Dp): Modifier = layout { measurable, constraints ->
    val extra = (amount * 2).roundToPx()
    val placeable = measurable.measure(constraints.offset(horizontal = extra))
    layout((placeable.width - extra).coerceAtLeast(0), placeable.height) {
        placeable.place(-extra / 2, 0)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiCheckboxPreview() {
    GiPreviewSurface {
        var alerts by remember { mutableStateOf(true) }
        var brief by remember { mutableStateOf(false) }
        Column {
            GiCheckbox(
                checked = alerts && brief,
                indeterminate = alerts != brief,
                onCheckedChange = {
                    alerts = it
                    brief = it
                },
                label = "All notifications"
            )
            GiCheckbox(checked = alerts, onCheckedChange = { alerts = it }, label = "Price alerts")
            GiCheckbox(checked = brief, onCheckedChange = { brief = it }, label = "Morning brief")
            GiCheckbox(checked = false, onCheckedChange = {}, label = "I agree to the Terms", isError = true)
            GiCheckbox(checked = false, onCheckedChange = {}, label = "Disabled", enabled = false)
        }
    }
}
