package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiEasing
import com.thejawnpaul.gptinvestor.theme.GiMotion
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import com.thejawnpaul.gptinvestor.theme.isReducedMotionEnabled
import kotlin.math.abs
import kotlin.math.roundToLong

enum class GiPriceSize { Large, Medium }

@Composable
fun GiPriceDisplay(
    price: Double,
    modifier: Modifier = Modifier,
    size: GiPriceSize = GiPriceSize.Large,
    currencySymbol: String = "$"
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val (integerStyle, decimalStyle) = when (size) {
        GiPriceSize.Large -> type.display to type.statNumber
        GiPriceSize.Medium -> type.screenTitle to type.companyName
    }
    val parts = splitPrice(price)
    val sign = if (parts.negative) "−" else ""
    val integerText = "$sign$currencySymbol${parts.integer}"
    val decimalText = ".${parts.decimals}"
    val text = integerText + decimalText
    val reducedMotion = isReducedMotionEnabled()
    val roll = remember { Animatable(1f) }
    var shown by remember { mutableStateOf(text) }
    var previous by remember { mutableStateOf<String?>(null) }
    var rising by remember { mutableStateOf(true) }
    var shownPrice by remember { mutableDoubleStateOf(price) }
    LaunchedEffect(text) {
        if (text == shown) return@LaunchedEffect
        if (reducedMotion) {
            previous = null
        } else {
            previous = shown
            rising = price >= shownPrice
            roll.snapTo(0f)
        }
        shown = text
        shownPrice = price
        if (!reducedMotion) {
            roll.animateTo(1f, tween(GiMotion.BASE, easing = GiEasing.emphasised))
            previous = null
        }
    }
    val integerSpan = integerStyle.copy(fontFeatureSettings = "tnum", color = colors.ink)
    val decimalSpan = decimalStyle.copy(fontFeatureSettings = "tnum", color = colors.muted)
    val old = previous
    Row(modifier = modifier.clearAndSetSemantics { contentDescription = text }) {
        val width = maxOf(text.length, old?.length ?: 0)
        val newPadded = text.padStart(width)
        val oldPadded = old?.padStart(width)
        val decimalStart = width - decimalText.length
        for (index in 0 until width) {
            val style = if (index >= decimalStart) decimalSpan else integerSpan
            val now = newPadded[index].toString()
            val before = oldPadded?.get(index)?.toString()
            if (before == null || before == now) {
                Text(text = now, modifier = Modifier.alignByBaseline(), style = style, softWrap = false)
            } else {
                PriceDigitRoll(before = before, now = now, rising = rising, progress = { roll.value }, style = style)
            }
        }
    }
}

@Composable
private fun RowScope.PriceDigitRoll(
    before: String,
    now: String,
    rising: Boolean,
    progress: () -> Float,
    style: TextStyle
) {
    val travel = with(LocalDensity.current) { GiSpace.x2.toPx() }
    val direction = if (rising) -1f else 1f
    Box(modifier = Modifier.alignByBaseline().clipToBounds()) {
        Text(
            text = before,
            modifier = Modifier.graphicsLayer {
                val p = progress()
                translationY = direction * travel * p
                alpha = 1f - p
            },
            style = style,
            softWrap = false
        )
        Text(
            text = now,
            modifier = Modifier.graphicsLayer {
                val p = progress()
                translationY = -direction * travel * (1f - p)
                alpha = p
            },
            style = style,
            softWrap = false
        )
    }
}

private data class PriceParts(val negative: Boolean, val integer: String, val decimals: String)

private fun splitPrice(price: Double): PriceParts {
    val cents = (abs(price) * 100).roundToLong()
    val integer = (cents / 100).toString().reversed().chunked(3).joinToString(",").reversed()
    val decimals = (cents % 100).toString().padStart(2, '0')
    return PriceParts(negative = price < 0 && cents > 0, integer = integer, decimals = decimals)
}

@PreviewLightDark
@Preview(name = "200%", fontScale = 2f)
@Composable
private fun GiPriceDisplayPreview() {
    GiPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(GiSpace.x2)) {
            GiPriceDisplay(price = 379.07)
            GiPriceDisplay(price = 1234.5)
            GiPriceDisplay(price = 214.5, size = GiPriceSize.Medium)
        }
    }
}
