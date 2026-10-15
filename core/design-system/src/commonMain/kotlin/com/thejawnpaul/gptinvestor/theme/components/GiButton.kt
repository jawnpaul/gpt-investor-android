package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

enum class GiButtonSize { Large, Medium, Small }

enum class GiButtonVariant { Primary, Secondary, Text, TextMuted }

@Composable
fun GiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: GiButtonSize = GiButtonSize.Large,
    variant: GiButtonVariant = GiButtonVariant.Primary
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) GiMotion.PRESSED_SCALE else 1f,
        animationSpec = GiMotion.snappy()
    )
    val (container, content) = when (variant) {
        GiButtonVariant.Primary -> colors.primary to colors.onPrimary
        GiButtonVariant.Secondary -> Color.Transparent to colors.ink
        GiButtonVariant.Text -> Color.Transparent to colors.violet
        GiButtonVariant.TextMuted -> Color.Transparent to colors.muted
    }
    val (minHeight, paddingX, style) = when (size) {
        GiButtonSize.Large -> Triple(56.dp, 24.dp, GiTheme.type.button)
        GiButtonSize.Medium -> Triple(44.dp, 16.dp, GiTheme.type.label)
        GiButtonSize.Small -> Triple(36.dp, 14.dp, GiTheme.type.link)
    }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .heightIn(min = minHeight)
            .clip(GiRadius.full)
            .background(container)
            .then(
                if (variant == GiButtonVariant.Secondary) {
                    Modifier.border(GiEdge.width, colors.line, GiRadius.full)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .background(if (isPressed) content.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
            .padding(horizontal = paddingX),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = style, color = content, textAlign = TextAlign.Center)
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiButtonPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiButton(text = "Add to watchlist", onClick = {})
            GiButton(text = "Add", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.Secondary)
            GiButton(text = "Unlock", onClick = {}, size = GiButtonSize.Small)
            GiButton(text = "Log in", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.Text)
            GiButton(text = "Not now", onClick = {}, size = GiButtonSize.Medium, variant = GiButtonVariant.TextMuted)
        }
    }
}
