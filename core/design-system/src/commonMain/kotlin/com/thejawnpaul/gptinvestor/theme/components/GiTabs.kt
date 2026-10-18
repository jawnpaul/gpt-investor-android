package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

@Composable
fun GiTabs(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val scrollState = rememberScrollState()
    val tabSlots = remember { GiTabSlots() }
    val indicatorSpec: AnimationSpec<Float> = if (isReducedMotionEnabled()) snap() else GiMotion.smooth()
    val indicatorPosition = animateFloatAsState(targetValue = selectedIndex.toFloat(), animationSpec = indicatorSpec)
    val lineColor = colors.line
    val indicatorColor = colors.violet
    Box(
        modifier = modifier.drawBehind {
            val y = size.height - GiEdge.width.toPx() / 2
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), GiEdge.width.toPx())
        }
    ) {
        Layout(
            content = {
                tabs.forEachIndexed { index, tab ->
                    GiTab(text = tab, selected = index == selectedIndex, onClick = { onSelect(index) })
                }
            },
            modifier = Modifier
                .horizontalScroll(scrollState)
                .selectableGroup()
                .drawWithContent {
                    drawContent()
                    drawTabIndicator(tabSlots, indicatorPosition.value, indicatorColor)
                }
        ) { measurables, constraints ->
            val gap = GiSpace.x1.roundToPx()
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
            val contentWidth = placeables.sumOf { it.width } + gap * (placeables.size - 1).coerceAtLeast(0)
            val width = constraints.constrainWidth(contentWidth)
            val height = constraints.constrainHeight(placeables.maxOfOrNull { it.height } ?: 0)
            tabSlots.update(placeables.map { it.width }, gap, width, layoutDirection)
            layout(width, height) {
                var x = 0
                placeables.forEach {
                    it.placeRelative(x, 0)
                    x += it.width + gap
                }
            }
        }
    }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= tabSlots.count) withFrameNanos { }
        if (selectedIndex >= tabSlots.count) return@LaunchedEffect
        val left = tabSlots.lefts[selectedIndex]
        val right = left + tabSlots.widths[selectedIndex]
        val viewport = scrollState.viewportSize
        when {
            left < scrollState.value -> scrollState.animateScrollTo(left)
            right > scrollState.value + viewport -> scrollState.animateScrollTo(right - viewport)
        }
    }
}

@Composable
private fun GiTab(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .giFocusRing(interactionSource, TAB_SHAPE)
            .heightIn(min = 44.dp)
            .clip(TAB_SHAPE)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = {
                    if (!selected) {
                        haptics.performHapticFeedback(GiHaptics.Selection)
                        onClick()
                    }
                }
            )
            .background(colors.ink.copy(alpha = pressedLayerAlpha(interactionSource)))
            .padding(horizontal = TAB_PADDING),
        contentAlignment = Alignment.Center
    ) {
        GiText(
            text = text,
            style = GiTextStyle.Label,
            color = if (selected) colors.ink else colors.muted,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

private class GiTabSlots {
    var lefts = IntArray(0)
        private set
    var widths = IntArray(0)
        private set
    val count: Int get() = lefts.size

    fun update(tabWidths: List<Int>, gap: Int, rowWidth: Int, layoutDirection: LayoutDirection) {
        val newLefts = IntArray(tabWidths.size)
        var x = 0
        tabWidths.forEachIndexed { index, tabWidth ->
            newLefts[index] = if (layoutDirection == LayoutDirection.Rtl) rowWidth - x - tabWidth else x
            x += tabWidth + gap
        }
        lefts = newLefts
        widths = tabWidths.toIntArray()
    }
}

private fun DrawScope.drawTabIndicator(slots: GiTabSlots, position: Float, color: Color) {
    if (slots.count == 0) return
    val clamped = position.coerceIn(0f, (slots.count - 1).toFloat())
    val from = clamped.toInt()
    val to = (from + 1).coerceAtMost(slots.count - 1)
    val fraction = clamped - from
    val inset = TAB_PADDING.toPx()
    val left = slots.lefts[from] + (slots.lefts[to] - slots.lefts[from]) * fraction + inset
    val width = slots.widths[from] + (slots.widths[to] - slots.widths[from]) * fraction - inset * 2
    val height = INDICATOR_HEIGHT.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(left, size.height - height),
        size = Size(width, height),
        cornerRadius = CornerRadius(height / 2)
    )
}

private val TAB_PADDING = 12.dp
private val INDICATOR_HEIGHT = 2.dp
private val TAB_SHAPE = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiTabsPreview() {
    GiPreviewSurface {
        var section by remember { mutableIntStateOf(0) }
        var more by remember { mutableIntStateOf(3) }
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiTabs(
                tabs = listOf("Overview", "News", "Financials"),
                selectedIndex = section,
                onSelect = { section = it },
                modifier = Modifier.fillMaxWidth()
            )
            GiTabs(
                tabs = listOf("Overview", "News", "Financials", "Peers", "Owners"),
                selectedIndex = more,
                onSelect = { more = it },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
