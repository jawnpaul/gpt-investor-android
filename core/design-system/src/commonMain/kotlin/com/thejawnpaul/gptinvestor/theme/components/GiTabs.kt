package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiEdge
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

// Sections of one page (Overview / News / Financials). Labels are 1–2 word nouns; more than four scroll.
// Never more than one tab bar on a screen.
@Composable
fun GiTabs(tabs: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = GiTheme.colors
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    val tabBounds = remember { mutableStateMapOf<Int, TabBounds>() }
    val lineColor = colors.line
    Box(
        modifier = modifier.drawBehind {
            val y = size.height - GiEdge.width.toPx() / 2
            drawLine(lineColor, Offset(0f, y), Offset(size.width, y), GiEdge.width.toPx())
        }
    ) {
        Box(modifier = Modifier.horizontalScroll(scrollState)) {
            Row(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(GiSpace.x1)
            ) {
                tabs.forEachIndexed { index, tab ->
                    GiTab(
                        text = tab,
                        selected = index == selectedIndex,
                        onClick = { onSelect(index) },
                        modifier = Modifier.onPlaced { coordinates ->
                            with(density) {
                                tabBounds[index] = TabBounds(
                                    left = coordinates.positionInParent().x.toDp(),
                                    width = coordinates.size.width.toDp()
                                )
                            }
                        }
                    )
                }
            }
            tabBounds[selectedIndex]?.let { bounds ->
                GiTabIndicator(
                    left = bounds.left + TAB_PADDING,
                    width = bounds.width - TAB_PADDING * 2,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
    LaunchedEffect(selectedIndex, tabBounds[selectedIndex]) {
        val bounds = tabBounds[selectedIndex] ?: return@LaunchedEffect
        val left = with(density) { bounds.left.roundToPx() }
        val right = with(density) { (bounds.left + bounds.width).roundToPx() }
        val viewport = scrollState.viewportSize
        when {
            left < scrollState.value -> scrollState.animateScrollTo(left)
            right > scrollState.value + viewport -> scrollState.animateScrollTo(right - viewport)
        }
    }
}

@Composable
private fun GiTab(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
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
            .background(colors.ink.copy(alpha = stateLayerAlpha(interactionSource)))
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

@Composable
private fun GiTabIndicator(left: Dp, width: Dp, modifier: Modifier = Modifier) {
    val spec: AnimationSpec<Dp> = if (isReducedMotionEnabled()) snap() else GiMotion.smooth()
    val animatedLeft by animateDpAsState(targetValue = left, animationSpec = spec)
    val animatedWidth by animateDpAsState(targetValue = width, animationSpec = spec)
    Box(
        modifier = modifier
            .offset(x = animatedLeft)
            .width(animatedWidth)
            .height(2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(GiTheme.colors.violet)
    )
}

private data class TabBounds(val left: Dp, val width: Dp)

private val TAB_PADDING = 12.dp
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
