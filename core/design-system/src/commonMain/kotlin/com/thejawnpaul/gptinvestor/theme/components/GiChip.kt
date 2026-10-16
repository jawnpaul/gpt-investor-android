package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
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
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

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
        selected -> GiChipColors(container = colors.violetSoft, content = colors.violet, edge = Color.Transparent)
        else -> GiChipColors(container = colors.raised, content = colors.ink, edge = colors.line)
    }
    val reducedMotion = isReducedMotionEnabled()
    val colorSpec: AnimationSpec<Color> = if (reducedMotion) snap() else GiMotion.snappy()
    val container by animateColorAsState(chipColors.container, colorSpec)
    val content by animateColorAsState(chipColors.content, colorSpec)
    val edge by animateColorAsState(chipColors.edge ?: Color.Transparent, colorSpec)
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
            .heightIn(min = if (isLarge) 44.dp else 32.dp)
            .clip(GiRadius.xs)
            .background(container)
            .then(if (isQuiet) Modifier else Modifier.border(GiEdge.width, edge, GiRadius.xs))
            .then(clickModifier)
            .background(content.copy(alpha = pressedLayerAlpha(interactionSource)))
            .padding(horizontal = if (isLarge) GiSpace.x4 else GiSpace.x3, vertical = GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            GiChipIcon(icon = leadingIcon, tint = content)
        } else if (!isQuiet) {
            GiChipCheck(visible = selected, tint = content, reducedMotion = reducedMotion)
        }
        GiText(
            text = text,
            modifier = Modifier.weight(1f, fill = false),
            style = if (isLarge) GiTextStyle.Label else GiTextStyle.BodySmall,
            color = content,
            fontWeight = if (selected && !isQuiet) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@Composable
private fun RowScope.GiChipCheck(visible: Boolean, tint: Color, reducedMotion: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = if (reducedMotion) {
            EnterTransition.None
        } else {
            expandHorizontally(GiMotion.snappy(), expandFrom = Alignment.Start) +
                scaleIn(GiMotion.snappy(), initialScale = 0.6f) +
                fadeIn(tween(GiMotion.FAST, easing = EaseOutQuart))
        },
        exit = if (reducedMotion) {
            ExitTransition.None
        } else {
            shrinkHorizontally(GiMotion.snappy(), shrinkTowards = Alignment.Start) +
                scaleOut(GiMotion.snappy(), targetScale = 0.6f) +
                fadeOut(tween(GiMotion.INSTANT, easing = LinearEasing))
        }
    ) {
        GiChipIcon(icon = GiIcons.check, tint = tint)
    }
}

@Composable
private fun GiChipIcon(icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier
            .padding(end = 6.dp)
            .size(16.dp),
        tint = tint
    )
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
            var experience by remember { mutableStateOf("Some experience") }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
            ) {
                listOf("I'm new to investing", "Some experience", "Comfortable with volatility").forEach { label ->
                    GiChip(
                        text = label,
                        onClick = { experience = label },
                        selected = label == experience,
                        size = GiChipSize.Large
                    )
                }
            }
        }
    }
}
