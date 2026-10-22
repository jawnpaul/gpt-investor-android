package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
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
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSize
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
        Icon(
            imageVector = GiIcons.info,
            contentDescription = null,
            modifier = Modifier.size(GiIconSize.sm),
            tint = GiTheme.colors.muted
        )
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
        val caretColor = GiTheme.colors.ink
        Box(
            modifier = Modifier
                .drawWithCache {
                    val caretWidth = CARET_WIDTH.toPx()
                    val caretHeight = CARET_HEIGHT.toPx()
                    val up = caretPath(caretWidth, caretHeight, pointsUp = true)
                    val down = caretPath(caretWidth, caretHeight, pointsUp = false)
                    onDrawBehind {
                        val left = caretX - caretWidth / 2
                        if (isAbove) {
                            translate(left, size.height - caretHeight) { drawPath(down, caretColor) }
                        } else {
                            translate(left, 0f) { drawPath(up, caretColor) }
                        }
                    }
                }
                .padding(bottom = CARET_HEIGHT)
        ) {
            GiText(
                text = text,
                modifier = Modifier
                    .graphicsLayer { translationY = if (isAbove) 0f else CARET_HEIGHT.toPx() }
                    .widthIn(max = GiSize.tooltipMax)
                    .clip(GiRadius.sm)
                    .background(GiTheme.colors.ink)
                    .padding(horizontal = GiSpace.x3, vertical = GiSpace.x2),
                style = GiTextStyle.BodySmall,
                color = GiTheme.colors.bg
            )
        }
    }
}

private fun caretPath(width: Float, height: Float, pointsUp: Boolean): Path = Path().apply {
    if (pointsUp) {
        moveTo(width / 2, 0f)
        lineTo(width, height)
        lineTo(0f, height)
    } else {
        moveTo(0f, 0f)
        lineTo(width, 0f)
        lineTo(width / 2, height)
    }
    close()
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
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    body: String? = null,
    stepCount: Int = 1,
    currentStep: Int = 0,
    secondaryActionText: String? = null,
    onSecondaryAction: () -> Unit = {},
    caretEndOffset: Dp? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    val colors = GiTheme.colors
    Box(modifier = modifier.widthIn(max = MAX_COACH_MARK_WIDTH).semantics { paneTitle = title }) {
        Column(
            modifier = Modifier
                .dropShadow(shape = GiRadius.md, shadow = GiElevation.overlay(colors.isDark))
                .clip(GiRadius.md)
                .background(colors.raised)
                .border(GiEdge.width, colors.line, GiRadius.md)
                .padding(GiSpace.x4),
            verticalArrangement = Arrangement.spacedBy(GiSpace.x2)
        ) {
            GiText(text = title, style = GiTextStyle.Label)
            if (content != null) {
                content()
            } else if (body != null) {
                GiText(text = body, style = GiTextStyle.BodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (stepCount > 1) {
                    GiStepDots(count = stepCount, current = currentStep)
                }
                Box(modifier = Modifier.weight(1f))
                if (secondaryActionText != null) {
                    GiButton(
                        text = secondaryActionText,
                        onClick = onSecondaryAction,
                        size = GiButtonSize.Medium,
                        variant = GiButtonVariant.TextMuted
                    )
                    Spacer(modifier = Modifier.width(GiSpace.x5))
                }
                GiButton(
                    text = actionText,
                    onClick = onAction,
                    size = GiButtonSize.Medium,
                    variant = GiButtonVariant.Text
                )
            }
        }
        if (caretEndOffset != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = caretEndOffset)
                    .offset(y = -CARET_SIZE / 2)
                    .size(CARET_SIZE)
                    .rotate(CARET_ROTATION)
                    .background(colors.raised, CARET_SHAPE)
            )
        }
    }
}

@Composable
fun GiCoachMarkRow(icon: ImageVector, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x3),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiIconTile(icon = icon, size = GiIconTileSize.Small, tone = GiIconTileTone.Violet)
        GiText(text = text, modifier = Modifier.weight(1f), style = GiTextStyle.BodySmall)
    }
}

private val MAX_COACH_MARK_WIDTH = 280.dp
private val CARET_SIZE = 12.dp
private val CARET_SHAPE = RoundedCornerShape(2.dp)
private const val CARET_ROTATION = 45f

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
                onAction = {}
            )
            GiCoachMark(
                title = "New in Brief",
                actionText = "Next",
                onAction = {},
                modifier = Modifier.padding(top = GiSpace.x2),
                stepCount = 2,
                secondaryActionText = "Skip",
                caretEndOffset = 68.dp
            ) {
                GiCoachMarkRow(icon = GiIcons.shareNetwork, text = "Share this brief with anyone")
            }
        }
    }
}
