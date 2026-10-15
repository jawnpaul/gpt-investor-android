package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiTheme

// The three haptics the spec allows; Compose maps them to the native feedback on Android and iOS.
object GiHaptics {
    val Selection = HapticFeedbackType.SegmentTick
    val Confirm = HapticFeedbackType.Confirm
    val Reject = HapticFeedbackType.Reject
}

@Composable
internal fun stateLayerAlpha(
    interactionSource: InteractionSource,
    hoverAlpha: Float = GiMotion.HOVER_ALPHA,
    pressedAlpha: Float = GiMotion.PRESSED_ALPHA
): Float {
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    return when {
        isPressed -> pressedAlpha
        isHovered -> hoverAlpha
        else -> 0f
    }
}

// Touch never focuses a clickable on Android, so this only shows for keyboard and switch access.
@Composable
internal fun Modifier.giFocusRing(interactionSource: InteractionSource, shape: Shape): Modifier {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val color = GiTheme.colors.focus
    return drawWithContent {
        drawContent()
        if (isFocused) {
            val strokeWidth = GiEdge.focusWidth.toPx()
            val outset = FOCUS_RING_OFFSET.toPx() + strokeWidth / 2
            val outline = shape.createOutline(
                Size(size.width + outset * 2, size.height + outset * 2),
                layoutDirection,
                this
            )
            translate(-outset, -outset) {
                drawOutline(outline = outline, color = color, style = Stroke(strokeWidth))
            }
        }
    }
}

private val FOCUS_RING_OFFSET = 2.dp
