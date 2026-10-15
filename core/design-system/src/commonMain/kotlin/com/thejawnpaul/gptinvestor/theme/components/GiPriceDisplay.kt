package com.thejawnpaul.gptinvestor.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.thejawnpaul.gptinvestor.theme.GiSpace
import com.thejawnpaul.gptinvestor.theme.GiTheme
import kotlin.math.abs
import kotlin.math.roundToLong

enum class GiPriceSize { Medium }

@Composable
fun GiPriceDisplay(
    price: Double,
    modifier: Modifier = Modifier,
    size: GiPriceSize = GiPriceSize.Medium,
    currencySymbol: String = "$"
) {
    val colors = GiTheme.colors
    val type = GiTheme.type
    val (integerStyle, decimalStyle) = when (size) {
        GiPriceSize.Medium -> type.screenTitle to type.companyName
    }
    val parts = splitPrice(price)
    val sign = if (parts.negative) "−" else ""
    val integerText = "$sign$currencySymbol${parts.integer}"
    val decimalText = ".${parts.decimals}"
    Text(
        text = buildAnnotatedString {
            append(integerText)
            withStyle(decimalStyle.toSpanStyle().copy(color = colors.muted)) { append(decimalText) }
        },
        modifier = modifier.clearAndSetSemantics { contentDescription = integerText + decimalText },
        color = colors.ink,
        softWrap = false,
        style = integerStyle.copy(fontFeatureSettings = "tnum")
    )
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
        }
    }
}
