package com.thejawnpaul.gptinvestor.theme.components

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled

// Onboarding steps: the current step is a 24-wide violet bar, the rest are 6 dots.
@Composable
fun GiStepDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    val spec: AnimationSpec<Dp> = if (isReducedMotionEnabled()) snap() else GiMotion.smooth()
    Row(
        modifier = modifier.semantics {
            progressBarRangeInfo = ProgressBarRangeInfo(
                current = (current + 1).toFloat(),
                range = 1f..count.toFloat(),
                steps = (count - 2).coerceAtLeast(0)
            )
        },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(count) { index ->
            val isCurrent = index == current
            val width by animateDpAsState(targetValue = if (isCurrent) 24.dp else 6.dp, animationSpec = spec)
            Box(
                modifier = Modifier
                    .size(width = width, height = 6.dp)
                    .clip(GiRadius.full)
                    .background(if (isCurrent) GiTheme.colors.violet else GiTheme.colors.chip)
            )
        }
    }
}

// How far through an article the reader is; progress is 0..1.
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

// Drag affordance at the top of bottom sheets; the sheet also has a close button.
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
