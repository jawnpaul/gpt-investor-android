package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Takes effect at once, no Save button. Pass onCheckedChange = null when the whole settings row is
// toggleable, so the label is part of the tap target.
@Composable
fun GiSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null
) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()
    val knobWidth by animateDpAsState(
        targetValue = if (isPressed) PRESSED_KNOB_WIDTH else KNOB_SIZE,
        animationSpec = GiMotion.snappy()
    )
    val knobOffset by animateDpAsState(
        targetValue = if (checked) TRACK_WIDTH - KNOB_INSET - knobWidth else KNOB_INSET,
        animationSpec = GiMotion.snappy()
    )
    val layerAlpha = stateLayerAlpha(source, hoverAlpha = TRACK_HOVER_ALPHA, pressedAlpha = TRACK_PRESSED_ALPHA)
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .then(
                if (onCheckedChange != null) {
                    Modifier.toggleable(
                        value = checked,
                        interactionSource = source,
                        indication = null,
                        enabled = enabled,
                        role = Role.Switch,
                        onValueChange = {
                            haptics.performHapticFeedback(GiHaptics.Selection)
                            onCheckedChange(it)
                        }
                    )
                } else {
                    Modifier
                }
            )
            .giFocusRing(source, GiRadius.full)
            .size(width = TRACK_WIDTH, height = TRACK_HEIGHT)
            .clip(GiRadius.full)
            .background(if (checked) colors.violet else colors.control)
            .background(colors.ink.copy(alpha = layerAlpha)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset)
                .size(width = knobWidth, height = KNOB_SIZE)
                .clip(GiRadius.full)
                .background(colors.onViolet)
        )
    }
}

private val TRACK_WIDTH = 48.dp
private val TRACK_HEIGHT = 28.dp
private val KNOB_SIZE = 22.dp
private val PRESSED_KNOB_WIDTH = 26.dp
private val KNOB_INSET = 3.dp
private const val TRACK_HOVER_ALPHA = 0.08f
private const val TRACK_PRESSED_ALPHA = 0.16f

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiSwitchPreview() {
    GiPreviewSurface {
        var alerts by remember { mutableStateOf(true) }
        val rowSource = remember { MutableInteractionSource() }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = GiSpace.settingsRow)
                    .toggleable(
                        value = alerts,
                        interactionSource = rowSource,
                        indication = null,
                        role = Role.Switch,
                        onValueChange = { alerts = it }
                    ),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GiText(text = "Price alerts", modifier = Modifier.weight(1f), style = GiTextStyle.Label)
                GiSwitch(checked = alerts, onCheckedChange = null, interactionSource = rowSource)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
                GiSwitch(checked = false, onCheckedChange = {})
                GiSwitch(checked = true, onCheckedChange = {}, enabled = false)
            }
        }
    }
}
