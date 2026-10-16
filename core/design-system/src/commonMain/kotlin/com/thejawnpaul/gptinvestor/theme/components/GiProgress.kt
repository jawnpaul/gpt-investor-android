package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GiStepDots(count: Int, current: Int, modifier: Modifier = Modifier, contentDescription: String? = null) {
    val spec: AnimationSpec<Dp> = if (isReducedMotionEnabled()) snap() else GiMotion.smooth()
    Row(
        modifier = modifier.semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = (current + 1).toFloat(),
                range = 1f..count.toFloat(),
                steps = (count - 2).coerceAtLeast(0)
            )
            if (contentDescription != null) this.contentDescription = contentDescription
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(count) { index ->
            val isCurrent = index == current
            val width = animateDpAsState(targetValue = if (isCurrent) 24.dp else 6.dp, animationSpec = spec)
            Box(
                modifier = Modifier
                    .layout { measurable, _ ->
                        val size = width.value.roundToPx()
                        val height = 6.dp.roundToPx()
                        val placeable = measurable.measure(Constraints.fixed(size, height))
                        layout(size, height) { placeable.place(0, 0) }
                    }
                    .clip(GiRadius.full)
                    .background(if (isCurrent) GiTheme.colors.violet else GiTheme.colors.chip)
            )
        }
    }
}

@Composable
fun GiReadingProgress(progress: Float, modifier: Modifier = Modifier) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = if (isReducedMotionEnabled()) snap() else GiMotion.smooth()
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(GiTheme.colors.line)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(current = clamped, range = 0f..1f) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .fillMaxHeight()
                .background(GiTheme.colors.violet)
        )
    }
}

/**
 * Deferred-read variant of [GiReadingProgress] for scroll-driven progress: [progress] is read in a
 * snapshot flow and the fill is drawn in the draw phase, so scrolling never recomposes the caller.
 */
@Composable
fun GiReadingProgress(progress: () -> Float, modifier: Modifier = Modifier) {
    val latest = rememberUpdatedState(progress)
    val reducedMotion = isReducedMotionEnabled()
    val animated = remember { Animatable(progress().coerceIn(0f, 1f)) }
    LaunchedEffect(reducedMotion) {
        snapshotFlow { latest.value().coerceIn(0f, 1f) }.collectLatest { target ->
            if (reducedMotion) animated.snapTo(target) else animated.animateTo(target, GiMotion.smooth())
        }
    }
    // Semantics only change per whole percent, keeping recomposition bounded while scrolling.
    val percent by remember { derivedStateOf { (latest.value().coerceIn(0f, 1f) * PERCENT).roundToInt() } }
    val fill = GiTheme.colors.violet
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(GiTheme.colors.line)
            .semantics { progressBarRangeInfo = ProgressBarRangeInfo(current = percent / PERCENT, range = 0f..1f) }
            .drawBehind {
                val width = (size.width * animated.value).roundToInt().toFloat()
                val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                drawRect(color = fill, topLeft = Offset(x, 0f), size = Size(width, size.height))
            }
    )
}

private const val PERCENT = 100f

@Composable
fun GiSheetHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 40.dp, height = 4.dp)
            .clip(GiRadius.full)
            .background(GiTheme.colors.chip)
            .clearAndSetSemantics { }
    )
}

@PreviewLightDark
@Composable
private fun GiProgressPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiStepDots(count = 3, current = 0)
            GiReadingProgress(progress = 0.4f)
            GiSheetHandle()
            GiSpinner()
        }
    }
}
