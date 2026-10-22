package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

enum class GiIconButtonVariant { Default, Ghost, Tonal }

// contentDescription names the action ("Save Apple"), not the icon.
@Composable
fun GiIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GiIconButtonVariant = GiIconButtonVariant.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    iconColor: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    GiIconButtonLayout(
        icon = icon,
        contentColor = iconColor ?: defaultIconColor(variant),
        variant = variant,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            )
    )
}

@Composable
private fun defaultIconColor(variant: GiIconButtonVariant): Color =
    if (variant == GiIconButtonVariant.Ghost) GiTheme.colors.muted else GiTheme.colors.ink

@Composable
fun GiIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    checkedIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    variant: GiIconButtonVariant = GiIconButtonVariant.Default,
    enabled: Boolean = true,
    loading: Boolean = false,
    checkedHaptic: HapticFeedbackType? = GiHaptics.Confirm,
    iconColor: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptics = LocalHapticFeedback.current
    val reducedMotion = isReducedMotionEnabled()
    val uncheckedColor = iconColor ?: defaultIconColor(variant)
    val checkedColor = GiTheme.colors.violet
    val fill = animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised),
        label = "toggleFill"
    )
    GiIconButtonLayout(
        icon = icon,
        contentColor = if (checked) checkedColor else uncheckedColor,
        variant = variant,
        enabled = enabled,
        loading = loading,
        interactionSource = interactionSource,
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Switch,
                onValueChange = { on ->
                    if (on && checkedHaptic != null) haptics.performHapticFeedback(checkedHaptic)
                    onCheckedChange(on)
                }
            )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(GiIconSize.md)
                    .graphicsLayer { alpha = 1f - fill.value },
                tint = uncheckedColor
            )
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(GiIconSize.md)
                    .graphicsLayer {
                        val progress = fill.value
                        alpha = progress
                        val scale = if (checked &&
                            !reducedMotion
                        ) {
                            SAVE_START_SCALE + (1f - SAVE_START_SCALE) * progress
                        } else {
                            1f
                        }
                        scaleX = scale
                        scaleY = scale
                    },
                tint = checkedColor
            )
        }
    }
}

private const val SAVE_START_SCALE = 0.9f

@Composable
private fun GiIconButtonLayout(
    icon: ImageVector,
    contentColor: Color,
    variant: GiIconButtonVariant,
    enabled: Boolean,
    loading: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    iconContent: (@Composable () -> Unit)? = null
) {
    val colors = GiTheme.colors
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) GiMotion.PRESSED_SCALE else 1f,
        animationSpec = GiMotion.snappy()
    )
    val layerAlpha = pressedLayerAlpha(interactionSource)
    val spinnerAlpha = animateFloatAsState(
        targetValue = if (loading) 1f else 0f,
        animationSpec = if (loading) {
            tween(GiMotion.FAST, easing = EaseOutQuart)
        } else {
            tween(GiMotion.INSTANT, easing = LinearEasing)
        }
    )
    val swapping by remember { derivedStateOf { spinnerAlpha.value > 0f } }
    Box(
        modifier = Modifier
            .alpha(if (enabled) 1f else GiMotion.DISABLED_ALPHA)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .giFocusRing(interactionSource, GiRadius.full)
            .then(modifier)
            .size(44.dp)
            .clip(GiRadius.full)
            .background(
                when (variant) {
                    GiIconButtonVariant.Default -> colors.raised
                    GiIconButtonVariant.Ghost -> Color.Transparent
                    GiIconButtonVariant.Tonal -> colors.tile
                }
            )
            .then(
                if (variant == GiIconButtonVariant.Default) {
                    Modifier.border(GiEdge.width, colors.line, GiRadius.full)
                } else {
                    Modifier
                }
            )
            .background(colors.ink.copy(alpha = layerAlpha)),
        contentAlignment = Alignment.Center
    ) {
        val iconFade = if (swapping) Modifier.graphicsLayer { alpha = 1f - spinnerAlpha.value } else Modifier
        if (iconContent != null) {
            Box(modifier = iconFade, contentAlignment = Alignment.Center) { iconContent() }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(GiIconSize.md)
                    .then(iconFade),
                tint = contentColor
            )
        }
        if (swapping) {
            GiSpinner(modifier = Modifier.graphicsLayer { alpha = spinnerAlpha.value }, color = contentColor)
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiIconButtonPreview() {
    GiPreviewSurface {
        var saved by remember { mutableStateOf(true) }
        Row(horizontalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiIconButton(icon = GiIcons.caretLeft, contentDescription = "Back", onClick = {})
            GiIconButton(
                icon = GiIcons.copy,
                contentDescription = "Copy answer",
                onClick = {},
                variant = GiIconButtonVariant.Ghost
            )
            GiIconToggleButton(
                checked = saved,
                onCheckedChange = { saved = it },
                icon = GiIcons.star,
                checkedIcon = GiIcons.starFill,
                contentDescription = "Save Apple"
            )
            GiIconButton(icon = GiIcons.bell, contentDescription = "Alerts", onClick = {}, enabled = false)
            GiIconButton(icon = GiIcons.bell, contentDescription = "Alerts", onClick = {}, loading = true)
            GiIconToggleButton(
                checked = false,
                onCheckedChange = {},
                icon = GiIcons.star,
                checkedIcon = GiIcons.starFill,
                contentDescription = "Save Apple",
                loading = true
            )
            GiIconButton(icon = GiIcons.x, contentDescription = "Close", onClick = {
            }, variant = GiIconButtonVariant.Tonal)
        }
    }
}
