package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiElevation
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiInfoTooltip(text: String, contentDescription: String, modifier: Modifier = Modifier) {
    var isOpen by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(GiRadius.full)
            .clickable(role = Role.Button) { isOpen = !isOpen }
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = GiIcons.info, contentDescription = null, modifier = Modifier.size(16.dp), tint = GiTheme.colors.muted)
        if (isOpen) {
            GiTooltipPopup(text = text, onDismiss = { isOpen = false })
        }
    }
}

@Composable
private fun GiTooltipPopup(text: String, onDismiss: () -> Unit) {
    val density = LocalDensity.current
    var caretX by remember { mutableIntStateOf(0) }
    var isAbove by remember { mutableStateOf(true) }
    val positionProvider = remember(density) {
        GiTooltipPositionProvider(
            gap = with(density) { 4.dp.roundToPx() },
            margin = with(density) { GiSpace.gutter.roundToPx() }
        ) { x, above ->
            caretX = x
            isAbove = above
        }
    }
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        val caretOffset = with(density) { caretX.toDp() } - CARET_WIDTH / 2
        Column {
            if (!isAbove) GiTooltipCaret(pointsUp = true, modifier = Modifier.offset(x = caretOffset))
            GiText(
                text = text,
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(GiRadius.sm)
                    .background(GiTheme.colors.ink)
                    .padding(horizontal = GiSpace.x3, vertical = GiSpace.x2),
                style = GiTextStyle.BodySmall,
                color = GiTheme.colors.bg
            )
            if (isAbove) GiTooltipCaret(pointsUp = false, modifier = Modifier.offset(x = caretOffset))
        }
    }
}

@Composable
private fun GiTooltipCaret(pointsUp: Boolean, modifier: Modifier = Modifier) {
    val color = GiTheme.colors.ink
    Canvas(modifier = modifier.size(width = CARET_WIDTH, height = CARET_HEIGHT)) {
        val path = Path().apply {
            if (pointsUp) {
                moveTo(size.width / 2, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
            } else {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
            }
            close()
        }
        drawPath(path, color)
    }
}

private class GiTooltipPositionProvider(
    private val gap: Int,
    private val margin: Int,
    private val onPlaced: (caretX: Int, above: Boolean) -> Unit
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val maxX = (windowSize.width - margin - popupContentSize.width).coerceAtLeast(margin)
        val x = (anchorBounds.center.x - popupContentSize.width / 2).coerceIn(margin, maxX)
        val aboveY = anchorBounds.top - gap - popupContentSize.height
        val above = aboveY >= 0
        val y = if (above) aboveY else anchorBounds.bottom + gap
        onPlaced(anchorBounds.center.x - x, above)
        return IntOffset(x, y)
    }
}

private val CARET_WIDTH = 16.dp
private val CARET_HEIGHT = 8.dp

// Doesn't position itself; place it next to what it describes.
@Composable
fun GiCoachMark(
    title: String,
    body: String,
    actionText: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    stepCount: Int = 1,
    currentStep: Int = 0
) {
    val colors = GiTheme.colors
    Column(
        modifier = modifier
            .width(280.dp)
            .dropShadow(shape = GiRadius.md, shadow = GiElevation.overlay(colors.isDark))
            .clip(GiRadius.md)
            .background(colors.raised)
            .border(GiEdge.width, colors.line, GiRadius.md)
            .padding(GiSpace.x4),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
    ) {
        GiText(text = title, style = GiTextStyle.Label)
        GiText(text = body, style = GiTextStyle.BodySmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (stepCount > 1) {
                GiStepDots(count = stepCount, current = currentStep)
            }
            Box(modifier = Modifier.weight(1f))
            GiButton(text = actionText, onClick = onDismiss, size = GiButtonSize.Medium, variant = GiButtonVariant.Text)
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTooltipPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GiText(text = "P/E ratio", style = GiTextStyle.Link, color = GiTheme.colors.ink)
                GiInfoTooltip(
                    text = "P/E compares the share price with yearly profit per share.",
                    contentDescription = "What is P/E ratio?"
                )
            }
            GiCoachMark(
                title = "New: price alerts",
                body = "Tap the bell on any stock to get told when it moves.",
                actionText = "Got it",
                onDismiss = {}
            )
        }
    }
}
