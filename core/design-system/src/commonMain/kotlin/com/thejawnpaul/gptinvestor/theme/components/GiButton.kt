package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiButtonSize { Large, Medium, Small }

// Caution is the outlined Retry inside the offline banner, and nowhere else.
enum class GiButtonVariant { Primary, Secondary, Destructive, Caution, Text, TextMuted }

@Composable
fun GiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: GiButtonSize = GiButtonSize.Large,
    variant: GiButtonVariant = GiButtonVariant.Primary,
    leadingIcon: ImageVector? = null,
    leadingPainter: Painter? = null,
    tintLeadingIcon: Boolean = true,
    enabled: Boolean = true,
    loading: Boolean = false,
    proPulse: Boolean = false
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) GiMotion.PRESSED_SCALE else 1f,
        animationSpec = GiMotion.snappy()
    )
    val containerAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else GiMotion.DISABLED_ALPHA,
        animationSpec = tween(GiMotion.FAST, easing = GiEasing.emphasised)
    )
    val labelAlpha by animateFloatAsState(
        targetValue = if (loading) 0f else 1f,
        animationSpec = tween(if (loading) GiMotion.INSTANT else GiMotion.FAST, easing = GiEasing.linear)
    )
    val buttonColors = when (variant) {
        GiButtonVariant.Primary -> GiButtonColors(container = colors.primary, content = colors.onPrimary)
        GiButtonVariant.Secondary -> GiButtonColors(container = Color.Transparent, content = colors.ink)
        GiButtonVariant.Destructive -> GiButtonColors(container = colors.error, content = colors.onError)
        GiButtonVariant.Caution -> GiButtonColors(container = Color.Transparent, content = colors.caution)
        GiButtonVariant.Text -> GiButtonColors(container = Color.Transparent, content = colors.violet)
        GiButtonVariant.TextMuted -> GiButtonColors(container = Color.Transparent, content = colors.muted)
    }
    val metrics = when (size) {
        GiButtonSize.Large -> GiButtonMetrics(
            minHeight = GiControlHeight.buttonL,
            horizontalPadding = 24.dp,
            textStyle = GiTextStyle.Button
        )
        GiButtonSize.Medium -> GiButtonMetrics(
            minHeight = GiControlHeight.target,
            horizontalPadding = 16.dp,
            textStyle = GiTextStyle.Label
        )
        GiButtonSize.Small -> GiButtonMetrics(
            minHeight = GiControlHeight.buttonS,
            horizontalPadding = 14.dp,
            textStyle = GiTextStyle.Link
        )
    }
    val content = buttonColors.content
    val isTextVariant = variant == GiButtonVariant.Text || variant == GiButtonVariant.TextMuted
    val paddingX = if (isTextVariant) 0.dp else metrics.horizontalPadding
    val iconSize = if (size == GiButtonSize.Small) GiIconSize.sm else GiIconSize.md
    val iconTint = if (tintLeadingIcon) content else Color.Unspecified
    val paddingY = if (isLargeText()) GiSpace.x3 else 0.dp
    Box(
        modifier = modifier
            .then(
                if (isTextVariant) {
                    Modifier.heightIn(min = GiSpace.touchTarget)
                } else {
                    Modifier.minimumInteractiveComponentSize()
                }
            )
            .graphicsLayer {
                alpha = containerAlpha
                scaleX = scale
                scaleY = scale
            }
            .heightIn(min = metrics.minHeight)
            .then(
                if (proPulse) {
                    Modifier.giBeam(
                        active = !loading,
                        shape = GiRadius.full,
                        strength = PRO_PULSE_STRENGTH,
                        style = GiBeamStyle.Pulse
                    )
                } else {
                    Modifier
                }
            )
            .clip(GiRadius.full)
            .background(buttonColors.container)
            .then(
                when (variant) {
                    GiButtonVariant.Secondary -> Modifier.border(GiEdge.width, colors.line, GiRadius.full)
                    GiButtonVariant.Caution -> Modifier.border(GiEdge.width, colors.caution, GiRadius.full)
                    else -> Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            )
            .background(if (isPressed) content.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .then(
                if (loading) {
                    Modifier.clearAndSetSemantics {
                        contentDescription = text
                        progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                    }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = paddingX, vertical = paddingY),
        contentAlignment = Alignment.Center
    ) {
        // Stays laid out while loading so the button keeps its width.
        Row(
            modifier = Modifier.graphicsLayer { alpha = labelAlpha },
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = iconTint
                )
            } else if (leadingPainter != null) {
                Icon(
                    painter = leadingPainter,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = iconTint
                )
            }
            GiText(text = text, style = metrics.textStyle, color = content, textAlign = TextAlign.Center)
        }
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(tween(GiMotion.FAST, delayMillis = GiMotion.INSTANT, easing = GiEasing.emphasised)) +
                scaleIn(GiMotion.snappy(), initialScale = SPINNER_INITIAL_SCALE),
            exit = fadeOut(tween(GiMotion.INSTANT, easing = GiEasing.linear))
        ) {
            GiSpinner(color = content)
        }
    }
}

private const val SPINNER_INITIAL_SCALE = 0.8f
private const val PRO_PULSE_STRENGTH = 0.6f

private data class GiButtonColors(val container: Color, val content: Color)

private data class GiButtonMetrics(val minHeight: Dp, val horizontalPadding: Dp, val textStyle: GiTextStyle)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiButtonPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiButton(text = "Add to watchlist", onClick = {})
            GiButton(text = "Subscribe now", onClick = {}, modifier = Modifier.fillMaxWidth(), proPulse = true)
            GiButton(text = "Add", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.Secondary)
            GiButton(text = "Unlock", onClick = {}, size = GiButtonSize.Small)
            GiButton(text = "New chat", onClick = {}, size = GiButtonSize.Medium, leadingIcon = GiIcons.notePencil)
            GiButton(
                text = "Sign up with Google",
                onClick = {},
                variant = GiButtonVariant.Secondary,
                leadingPainter = rememberVectorPainter(GiIcons.mark),
                tintLeadingIcon = false
            )
            GiButton(
                text = "Log in",
                onClick = {},
                variant = GiButtonVariant.Secondary,
                leadingPainter = rememberVectorPainter(GiIcons.mark),
                loading = true
            )
            GiButton(text = "Delete account", onClick = {}, variant = GiButtonVariant.Destructive)
            GiButton(text = "Get brief", onClick = {}, size = GiButtonSize.Medium, loading = true)
            GiButton(text = "Get brief", onClick = {}, size = GiButtonSize.Medium, enabled = false)
            GiButton(text = "Log in", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.Text)
            GiButton(text = "Not now", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.TextMuted)
        }
    }
}
