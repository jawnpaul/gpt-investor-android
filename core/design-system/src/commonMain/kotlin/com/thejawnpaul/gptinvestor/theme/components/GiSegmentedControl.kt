package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Filters one list 2–5 ways (All / US / NGX, 1D–5Y) with one-word labels. Sections with different
// content use GiTabs.
@Composable
fun GiSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = GiTheme.colors
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .clip(GiRadius.full)
            .background(colors.track)
            .border(GiEdge.width, colors.line, GiRadius.full)
            .padding(3.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        options.forEachIndexed { index, option ->
            GiSegment(
                text = option,
                selected = index == selectedIndex,
                enabled = enabled,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GiSegment(text: String, selected: Boolean, enabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .giFocusRing(interactionSource, GiRadius.full)
            .heightIn(min = 36.dp)
            .clip(GiRadius.full)
            .background(if (selected) colors.raised else Color.Transparent)
            .then(if (selected) Modifier.border(GiEdge.width, colors.line, GiRadius.full) else Modifier)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = {
                    if (!selected) {
                        haptics.performHapticFeedback(GiHaptics.Selection)
                        onClick()
                    }
                }
            )
            .background(colors.ink.copy(alpha = if (selected) 0f else pressedLayerAlpha(interactionSource)))
            .padding(horizontal = GiSpace.x3),
        contentAlignment = Alignment.Center
    ) {
        GiText(
            text = text,
            style = GiTextStyle.BodySmall,
            color = if (selected) colors.ink else colors.muted,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiSegmentedControlPreview() {
    GiPreviewSurface {
        var market by remember { mutableIntStateOf(0) }
        var range by remember { mutableIntStateOf(2) }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiSegmentedControl(
                options = listOf("All", "US", "NGX"),
                selectedIndex = market,
                onSelect = { market = it },
                modifier = Modifier.fillMaxWidth()
            )
            GiSegmentedControl(
                options = listOf("1D", "1W", "1M", "1Y", "5Y"),
                selectedIndex = range,
                onSelect = { range = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
