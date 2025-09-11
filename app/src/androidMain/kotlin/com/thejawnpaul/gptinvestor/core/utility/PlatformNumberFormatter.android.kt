package com.thejawnpaul.gptinvestor.core.utility

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toJavaBigDecimal
import java.text.NumberFormat
import java.util.Locale

actual class PlatformNumberFormatter {
    private val formatter: NumberFormat by lazy {
        val locale = Locale.getDefault()
        NumberFormat.getNumberInstance(locale).apply {
            isGroupingUsed = true
        }
    }
    actual fun format(number: BigDecimal, places: Int): String {
        formatter.maximumFractionDigits = places
        formatter.roundingMode = java.math.RoundingMode.HALF_UP

        // Convert the multiplatform BigDecimal to java.math.BigDecimal for the JVM formatter
        val javaBigDecimal = number.toJavaBigDecimal() // Multiplatform bignum provides this conversion
        return formatter.format(javaBigDecimal)
    }
}
