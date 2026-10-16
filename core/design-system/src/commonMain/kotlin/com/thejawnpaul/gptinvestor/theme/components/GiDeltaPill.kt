package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiControlHeight
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
fun GiDeltaPill(percent: Double, modifier: Modifier = Modifier, suffix: String? = null) {
    val colors = GiTheme.colors
    val isUnchanged = toHundredths(percent) == 0L
    val isLoss = percent < 0 && !isUnchanged
    val content = when {
        isUnchanged -> colors.muted
        isLoss -> colors.loss
        else -> colors.gain
    }
    val container = when {
        isUnchanged -> colors.tile
        isLoss -> colors.lossSoft
        else -> colors.gainSoft
    }
    val sign = when {
        isUnchanged -> ""
        isLoss -> "−"
        else -> "+"
    }
    val description = deltaDescription(percent, suffix)
    Row(
        modifier = modifier
            .heightIn(min = GiControlHeight.pill)
            .clip(GiRadius.xs)
            .background(container)
            .padding(horizontal = GiSpace.x2)
            .clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isUnchanged) GiDeltaTriangle(up = !isLoss, color = content)
        GiText(
            text = "$sign${formatAbsolutePercent(percent)}%" + suffix?.let { " $it" }.orEmpty(),
            style = GiTextStyle.Delta,
            color = content,
            maxLines = 1
        )
    }
}

@Composable
internal fun GiDeltaTriangle(up: Boolean, color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.size(8.dp).drawWithCache {
            val unit = size.width / 8f
            val path = Path().apply {
                if (up) {
                    moveTo(4f * unit, 1f * unit)
                    lineTo(7.5f * unit, 6.5f * unit)
                    lineTo(0.5f * unit, 6.5f * unit)
                } else {
                    moveTo(4f * unit, 7f * unit)
                    lineTo(7.5f * unit, 1.5f * unit)
                    lineTo(0.5f * unit, 1.5f * unit)
                }
                close()
            }
            onDrawBehind { drawPath(path = path, color = color) }
        }
    )
}

internal fun deltaDescription(percent: Double, suffix: String? = null): String {
    if (toHundredths(percent) == 0L) return "unchanged" + suffix?.let { " $it" }.orEmpty()
    val isLoss = percent < 0 && toHundredths(percent) > 0
    return "${if (isLoss) "down" else "up"} ${formatAbsolutePercent(percent)} percent" +
        suffix?.let { " $it" }.orEmpty()
}

private fun toHundredths(percent: Double): Long = (abs(percent) * 100).roundToLong()

private fun formatAbsolutePercent(percent: Double): String {
    val hundredths = toHundredths(percent)
    return "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiDeltaPillPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiDeltaPill(percent = 1.24)
            GiDeltaPill(percent = -0.82)
            GiDeltaPill(percent = -0.95, suffix = "today")
            GiDeltaPill(percent = 0.0)
            GiDeltaPill(percent = -0.004)
        }
    }
}
