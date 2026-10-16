package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

@Composable
fun GiStepBar(count: Int, current: Int, contentDescription: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clearAndSetSemantics {
                this.contentDescription = contentDescription
                progressBarRangeInfo = ProgressBarRangeInfo((current + 1).toFloat(), 1f..count.toFloat())
            },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(count) { index ->
            val color = animateColorAsState(
                targetValue = if (index <= current) GiTheme.colors.violet else GiTheme.colors.chip,
                animationSpec = tween(GiMotion.FAST)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(GiRadius.full)
                    .drawBehind { drawRect(color.value) }
            )
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiStepBarPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x4)) {
            GiStepBar(count = 2, current = 0, contentDescription = "Step 1 of 2")
            GiStepBar(count = 2, current = 1, contentDescription = "Step 2 of 2")
            GiStepBar(count = 4, current = 1, contentDescription = "Step 2 of 4")
        }
    }
}
