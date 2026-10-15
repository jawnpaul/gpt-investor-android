package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
fun GiDeltaPill(percent: Double, modifier: Modifier = Modifier, suffix: String? = null) {
    val colors = GiTheme.colors
    val isLoss = percent < 0 && toHundredths(percent) > 0
    val content = if (isLoss) colors.loss else colors.gain
    val description = "${if (isLoss) "down" else "up"} ${formatAbsolutePercent(percent)} percent" +
        suffix?.let { " $it" }.orEmpty()
    Row(
        modifier = modifier
            .heightIn(min = 24.dp)
            .clip(GiRadius.xs)
            .background(if (isLoss) colors.lossSoft else colors.gainSoft)
            .padding(horizontal = GiSpace.x2)
            .clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x1),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiDeltaTriangle(up = !isLoss, color = content)
        GiText(
            text = "${if (isLoss) "−" else "+"}${formatAbsolutePercent(percent)}%" + suffix?.let { " $it" }.orEmpty(),
            style = GiTextStyle.Delta,
            color = content,
            maxLines = 1
        )
    }
}

@Composable
internal fun GiDeltaTriangle(up: Boolean, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(8.dp)) {
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
        drawPath(path = path, color = color)
    }
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
        }
    }
}
