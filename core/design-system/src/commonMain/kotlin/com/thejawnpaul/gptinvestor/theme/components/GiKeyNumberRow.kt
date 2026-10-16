package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.thejawnpaul.gptinvestor.theme.GiColors
import com.thejawnpaul.gptinvestor.theme.GiIconSize
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiRadius
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme

private val LabelColumnWidth = 104.dp

@Composable
fun GiKeyNumberRow(
    label: String,
    value: String,
    insight: String?,
    tone: GiSentiment,
    modifier: Modifier = Modifier,
    valueTone: GiSentiment? = null,
    onClick: (() -> Unit)? = null
) {
    val colors = GiTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = if (onClick != null) interactionSource.collectIsPressedAsState().value else false
    val rowModifier = modifier
        .fillMaxWidth()
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = Role.Button,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
        .background(if (isPressed) colors.ink.copy(alpha = GiMotion.PRESSED_ALPHA) else Color.Transparent)
        .padding(vertical = GiSpace.x4)
    val valueColor = when (valueTone) {
        GiSentiment.Positive -> colors.gain
        GiSentiment.Negative -> colors.loss
        GiSentiment.Neutral, null -> colors.ink
    }

    if (isLargeText()) {
        Column(modifier = rowModifier, verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiKeyNumberLabel(label = label, value = value, valueColor = valueColor)
            if (insight != null) {
                GiKeyNumberInsight(
                    insight = insight,
                    tone = tone,
                    showCaret = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x4),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GiKeyNumberLabel(
                label = label,
                value = value,
                valueColor = valueColor,
                modifier = Modifier.width(LabelColumnWidth)
            )
            if (insight != null) {
                GiKeyNumberInsight(
                    insight = insight,
                    tone = tone,
                    showCaret = onClick != null,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun GiKeyNumberHero(
    caption: String,
    value: String,
    modifier: Modifier = Modifier,
    tone: GiSentiment = GiSentiment.Neutral,
    showTrend: Boolean = false
) {
    val colors = GiTheme.colors
    val toneColors = colors.keyNumberColors(tone)
    val valueColor = when (tone) {
        GiSentiment.Positive -> colors.gain
        GiSentiment.Negative -> colors.loss
        GiSentiment.Neutral -> colors.ink
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(GiRadius.sm)
            .background(toneColors.container)
            .padding(GiSpace.x4),
        verticalArrangement = Arrangement.spacedBy(GiSpace.x1)
    ) {
        GiText(
            text = caption,
            style = GiTextStyle.Eyebrow,
            color = if (tone == GiSentiment.Neutral) colors.muted else toneColors.content
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showTrend && tone != GiSentiment.Neutral) {
                GiDeltaTriangle(up = tone == GiSentiment.Positive, color = valueColor)
            }
            Text(
                text = value,
                color = valueColor,
                style = GiTheme.type.display.copy(fontFeatureSettings = "tnum")
            )
        }
    }
}

@Composable
private fun GiKeyNumberLabel(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        GiText(text = label, style = GiTextStyle.Meta)
        Text(
            text = value,
            color = valueColor,
            style = GiTheme.type.statNumber.copy(fontFeatureSettings = "tnum")
        )
    }
}

@Composable
private fun GiKeyNumberInsight(
    insight: String,
    tone: GiSentiment,
    showCaret: Boolean,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    val toneColors = GiTheme.colors.keyNumberColors(tone)
    Row(
        modifier = modifier
            .clip(GiRadius.sm)
            .background(toneColors.container)
            .padding(start = GiSpace.x3, end = GiSpace.x2, top = GiSpace.x2, bottom = GiSpace.x2),
        horizontalArrangement = Arrangement.spacedBy(GiSpace.x2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GiText(
            text = insight,
            modifier = Modifier.weight(1f),
            style = GiTextStyle.BodySmall,
            color = toneColors.content,
            fontWeight = FontWeight.Medium,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        if (showCaret) {
            Icon(
                imageVector = GiIcons.caretRight,
                contentDescription = null,
                modifier = Modifier.size(GiIconSize.sm),
                tint = toneColors.content
            )
        }
    }
}

private data class GiKeyNumberColors(val container: Color, val content: Color)

private fun GiColors.keyNumberColors(tone: GiSentiment): GiKeyNumberColors = when (tone) {
    GiSentiment.Positive -> GiKeyNumberColors(container = gainSoft, content = gain)
    GiSentiment.Negative -> GiKeyNumberColors(container = lossSoft, content = loss)
    GiSentiment.Neutral -> GiKeyNumberColors(container = tile, content = body)
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiKeyNumberRowPreview() {
    GiPreviewSurface {
        Column {
            GiKeyNumberRow(
                label = "Market cap",
                value = "$605.8B",
                insight = "The company's large size shows it is stable",
                tone = GiSentiment.Positive,
                onClick = {}
            )
            GiDivider()
            GiKeyNumberRow(
                label = "P/E ratio",
                value = "29.1",
                insight = "Investors pay a bit more than average for each dollar of profit",
                tone = GiSentiment.Neutral,
                onClick = {}
            )
            GiDivider()
            GiKeyNumberRow(
                label = "Revenue growth",
                value = "−2.9%",
                insight = "Sales fell compared with last year",
                tone = GiSentiment.Negative,
                valueTone = GiSentiment.Negative,
                onClick = {}
            )
            GiDivider()
            GiKeyNumberRow(label = "Dividend yield", value = "3.1%", insight = null, tone = GiSentiment.Neutral)
        }
    }
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiKeyNumberHeroPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x3)) {
            GiKeyNumberHero(caption = "Last 12 months", value = "+6.0%", tone = GiSentiment.Positive, showTrend = true)
            GiKeyNumberHero(caption = "Today", value = "$605.8B")
        }
    }
}
