package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiIconButtonVariant { Default, Ghost }

// contentDescription names the action ("Save Apple"), not the icon.
@Composable
fun GiIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GiIconButtonVariant = GiIconButtonVariant.Default,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    GiIconButtonLayout(
        icon = icon,
        contentColor = if (variant == GiIconButtonVariant.Ghost) GiTheme.colors.muted else GiTheme.colors.ink,
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

// Pass the Fill icon as checkedIcon; checked also turns the icon violet.
@Composable
fun GiIconToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector,
    checkedIcon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    variant: GiIconButtonVariant = GiIconButtonVariant.Default,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val uncheckedColor = if (variant == GiIconButtonVariant.Ghost) GiTheme.colors.muted else GiTheme.colors.ink
    GiIconButtonLayout(
        icon = if (checked) checkedIcon else icon,
        contentColor = if (checked) GiTheme.colors.violet else uncheckedColor,
        variant = variant,
        enabled = enabled,
        loading = false,
        interactionSource = interactionSource,
        modifier = modifier
            .semantics { this.contentDescription = contentDescription }
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange
            )
    )
}

@Composable
private fun GiIconButtonLayout(
    icon: ImageVector,
    contentColor: Color,
    variant: GiIconButtonVariant,
    enabled: Boolean,
    loading: Boolean,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier
) {
    val colors = GiTheme.colors
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) GiMotion.PRESSED_SCALE else 1f,
        animationSpec = GiMotion.snappy()
    )
    val layerAlpha = stateLayerAlpha(interactionSource)
    val isGhost = variant == GiIconButtonVariant.Ghost
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
            .background(if (isGhost) Color.Transparent else colors.raised)
            .then(if (isGhost) Modifier else Modifier.border(GiEdge.width, colors.line, GiRadius.full))
            .background(colors.ink.copy(alpha = layerAlpha)),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            GiSpinner(color = contentColor)
        } else {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = contentColor)
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
        }
    }
}
