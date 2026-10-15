package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Put the radios of one group inside Modifier.selectableGroup().
@Composable
fun GiRadio(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    GiSelectionRow(
        label = label,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier,
        selectionModifier = Modifier.selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = {
                if (!selected) haptics.performHapticFeedback(GiHaptics.Selection)
                onClick()
            }
        )
    ) {
        GiRadioCircle(selected = selected, modifier = Modifier.giFocusRing(interactionSource, GiRadius.full))
    }
}

@Composable
internal fun GiRadioCircle(selected: Boolean, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val ringWidth by animateDpAsState(
        targetValue = if (selected) 7.dp else 2.dp,
        animationSpec = GiMotion.snappy()
    )
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(GiRadius.full)
            .background(colors.raised)
            .border(ringWidth, if (selected) colors.violet else colors.control, GiRadius.full)
    )
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiRadioPreview() {
    GiPreviewSurface {
        var plan by remember { mutableStateOf("Yearly") }
        Column(modifier = Modifier.selectableGroup()) {
            listOf("Monthly", "Yearly").forEach { option ->
                GiRadio(selected = plan == option, onClick = { plan = option }, label = option)
            }
            GiRadio(selected = false, onClick = {}, label = "Lifetime", enabled = false)
            GiRadio(selected = true, onClick = {})
        }
    }
}
