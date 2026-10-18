package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.LocalGiColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// The three haptics the spec allows; Compose maps them to the native feedback on Android and iOS.
object GiHaptics {
    val Selection = HapticFeedbackType.SegmentTick
    val Confirm = HapticFeedbackType.Confirm
    val Reject = HapticFeedbackType.Reject
}

// Phones have no hover, so pressed is the only state layer.
@Composable
internal fun pressedLayerAlpha(
    interactionSource: InteractionSource,
    pressedAlpha: Float = GiMotion.PRESSED_ALPHA
): Float {
    val isPressed by interactionSource.collectIsPressedAsState()
    return if (isPressed) pressedAlpha else 0f
}

// Touch never focuses a clickable on Android, so this only shows for keyboard and switch access.
internal fun Modifier.giFocusRing(interactionSource: InteractionSource, shape: Shape): Modifier =
    this then GiFocusRingElement(interactionSource, shape)

private data class GiFocusRingElement(val interactionSource: InteractionSource, val shape: Shape) :
    ModifierNodeElement<GiFocusRingNode>() {
    override fun create() = GiFocusRingNode(interactionSource, shape)

    override fun update(node: GiFocusRingNode) {
        node.update(interactionSource, shape)
    }
}

private class GiFocusRingNode(private var interactionSource: InteractionSource, private var shape: Shape) :
    Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {
    private var isFocused = false
    private var collector: Job? = null

    override fun onAttach() {
        collectFocus()
    }

    fun update(interactionSource: InteractionSource, shape: Shape) {
        if (this.shape != shape) {
            this.shape = shape
            invalidateDraw()
        }
        if (this.interactionSource != interactionSource) {
            this.interactionSource = interactionSource
            if (isAttached) {
                collector?.cancel()
                isFocused = false
                collectFocus()
                invalidateDraw()
            }
        }
    }

    private fun collectFocus() {
        val source = interactionSource
        collector = coroutineScope.launch {
            val focuses = mutableListOf<FocusInteraction.Focus>()
            source.interactions.collect { interaction ->
                when (interaction) {
                    is FocusInteraction.Focus -> focuses.add(interaction)
                    is FocusInteraction.Unfocus -> focuses.remove(interaction.focus)
                }
                val focused = focuses.isNotEmpty()
                if (focused != isFocused) {
                    isFocused = focused
                    invalidateDraw()
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (!isFocused) return
        val color = currentValueOf(LocalGiColors).focus
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

private val FOCUS_RING_OFFSET = 2.dp
