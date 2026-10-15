package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

// Filter: toggles in a scrolling row. Quiet: an AI suggestion under an input; tapping it is an action.
enum class GiChipStyle { Filter, Quiet }

enum class GiChipSize { Medium, Large }

@Composable
fun GiChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    style: GiChipStyle = GiChipStyle.Filter,
    size: GiChipSize = GiChipSize.Medium,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isQuiet = style == GiChipStyle.Quiet
    val chipColors = when {
        isQuiet -> GiChipColors(container = colors.tile, content = colors.body, edge = null)
        selected -> GiChipColors(container = colors.violetSoft, content = colors.violet, edge = colors.violetSoft)
        else -> GiChipColors(container = colors.raised, content = colors.ink, edge = colors.line)
    }
    val content = chipColors.content
    val edge = chipColors.edge
    val isLarge = size == GiChipSize.Large
    val clickModifier = if (isQuiet) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick
        )
    } else {
        Modifier.selectable(
            selected = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onClick = {
                haptics.performHapticFeedback(GiHaptics.Selection)
                onClick()
            }
        )
    }
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .giFocusRing(interactionSource, GiRadius.xs)
            .height(if (isLarge) 44.dp else 32.dp)
            .clip(GiRadius.xs)
            .background(chipColors.container)
            .then(if (edge != null) Modifier.border(GiEdge.width, edge, GiRadius.xs) else Modifier)
            .then(clickModifier)
            .background(content.copy(alpha = stateLayerAlpha(interactionSource)))
            .padding(horizontal = if (isLarge) GiSpace.x4 else GiSpace.x3),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(16.dp), tint = content)
        }
        GiText(
            text = text,
            style = if (isLarge) GiTextStyle.Label else GiTextStyle.BodySmall,
            color = content,
            fontWeight = if (selected && !isQuiet) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

private data class GiChipColors(val container: Color, val content: Color, val edge: Color?)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiChipPreview() {
    GiPreviewSurface {
        var selected by remember { mutableStateOf("Tech") }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                listOf("All", "Tech", "Banking", "Energy").forEach { label ->
                    GiChip(text = label, onClick = { selected = label }, selected = label == selected)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
                GiChip(text = "Is Apple a buy?", onClick = {}, style = GiChipStyle.Quiet)
                GiChip(text = "+5%", onClick = {}, enabled = false)
            }
            GiChip(
                text = "Apple",
                onClick = {},
                selected = true,
                size = GiChipSize.Large,
                leadingIcon = GiIcons.check
            )
        }
    }
}
