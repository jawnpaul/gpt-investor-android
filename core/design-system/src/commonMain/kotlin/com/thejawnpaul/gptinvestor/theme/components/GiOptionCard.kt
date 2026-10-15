package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// A radio choice with more detail (plans, experience level). Put the cards of one group inside
// Modifier.selectableGroup().
@Composable
fun GiOptionCard(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    detail: String? = null,
    enabled: Boolean = true
) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .giFocusRing(interactionSource, GiRadius.md)
            .clip(GiRadius.md)
            .background(colors.raised)
            .border(
                width = if (selected) SELECTED_EDGE_WIDTH else GiEdge.width,
                color = if (selected) colors.violet else colors.line,
                shape = GiRadius.md
            )
            .selectable(
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
            .background(colors.ink.copy(alpha = pressedLayerAlpha(interactionSource)))
            .padding(GiSpace.cardPad),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            GiText(text = title, style = GiTextStyle.Label)
            if (detail != null) {
                GiText(text = detail, style = GiTextStyle.BodySmall, color = colors.muted)
            }
        }
        GiRadioCircle(selected = selected)
    }
}

private val SELECTED_EDGE_WIDTH = 1.5.dp

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiOptionCardPreview() {
    GiPreviewSurface {
        var plan by remember { mutableStateOf("Yearly") }
        Column(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiOptionCard(
                title = "Yearly",
                detail = "$59.99 · save 40%",
                selected = plan == "Yearly",
                onClick = { plan = "Yearly" }
            )
            GiOptionCard(
                title = "Monthly",
                detail = "$7.99",
                selected = plan == "Monthly",
                onClick = { plan = "Monthly" }
            )
        }
    }
}
