package com.thejawnpaul.gptinvestor.features.investor.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.appearance
import com.thejawnpaul.gptinvestor.dark
import com.thejawnpaul.gptinvestor.light
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.components.GiHaptics
import com.thejawnpaul.gptinvestor.theme.components.GiIcons
import com.thejawnpaul.gptinvestor.theme.components.GiText
import com.thejawnpaul.gptinvestor.theme.components.GiTextStyle
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val MENU_WIDTH = 200.dp

private object BelowAnchorEnd : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            anchorBounds.right - popupContentSize.width
        } else {
            anchorBounds.left
        }
        return IntOffset(x, anchorBounds.bottom)
    }
}
private val ITEM_MIN_HEIGHT = 48.dp

@Composable
fun AppearanceMenu(
    expanded: Boolean,
    selectedOption: StringResource,
    options: List<StringResource>,
    onSelect: (StringResource) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = expanded
    if (visibleState.isIdle && !visibleState.currentState) return
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val menuLabel = stringResource(Res.string.appearance)
    Popup(
        popupPositionProvider = BelowAnchorEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        val enter = if (isReducedMotionEnabled()) {
            fadeIn(tween(GiMotion.FAST, easing = GiEasing.emphasised))
        } else {
            fadeIn(tween(GiMotion.FAST, easing = GiEasing.emphasised)) +
                scaleIn(
                    tween(GiMotion.FAST, easing = GiEasing.emphasised),
                    initialScale = GiMotion.ENTER_SCALE,
                    transformOrigin = TransformOrigin(1f, 0f)
                )
        }
        AnimatedVisibility(
            visibleState = visibleState,
            enter = enter,
            exit = fadeOut(tween(GiMotion.INSTANT, easing = GiEasing.emphasised))
        ) {
            Column(
                modifier = modifier
                    .width(MENU_WIDTH)
                    .dropShadow(shape = GiRadius.md, shadow = GiElevation.overlay(colors.isDark))
                    .clip(GiRadius.md)
                    .background(colors.raised)
                    .border(GiEdge.width, colors.line, GiRadius.md)
                    .padding(GiSpace.x1_5)
                    .semantics { contentDescription = menuLabel }
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(GiSpace.x0_5)
            ) {
                options.forEach { option ->
                    AppearanceMenuItem(
                        label = stringResource(option),
                        icon = appearanceIcon(option),
                        selected = option == selectedOption,
                        onClick = {
                            if (option != selectedOption) haptics.performHapticFeedback(GiHaptics.Selection)
                            onDismiss()
                            onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppearanceMenuItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val content = if (selected) colors.violet else colors.ink
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(GiRadius.sm)
            .background(if (selected) colors.violetSoft else Color.Transparent)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.RadioButton,
                onClick = onClick
            )
            .background(if (isPressed) content.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .heightIn(min = ITEM_MIN_HEIGHT)
            .padding(horizontal = GiSpace.x3, vertical = GiSpace.x2),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(GiIconSize.md), tint = content)
        GiText(
            text = label,
            modifier = Modifier.weight(1f),
            style = if (selected) GiTextStyle.Label else GiTextStyle.LabelRegular,
            color = content
        )
        if (selected) {
            Icon(
                imageVector = GiIcons.check,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = content
            )
        }
    }
}

private fun appearanceIcon(option: StringResource): ImageVector = when (option) {
    Res.string.light -> Icons.Outlined.LightMode
    Res.string.dark -> Icons.Outlined.DarkMode
    else -> Icons.Outlined.SettingsBrightness
}
