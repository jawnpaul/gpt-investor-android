package com.thejawnpaul.gptinvestor.core.utility

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSLocale
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.currentLocale

actual class PlatformNumberFormatter {

    private val formatter: NSNumberFormatter by lazy {
        NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            locale = NSLocale.currentLocale()
            usesGroupingSeparator = true
        }
    }
    actual fun format(number: BigDecimal, places: Int): String {
        formatter.maximumFractionDigits = places.toULong()

        // Convert the multiplatform BigDecimal to a string, then to NSDecimalNumber for iOS formatter
        val nsDecimalValue =
            NSDecimalNumber(string = number.toString()) // toString() gives a precise string
        return formatter.stringFromNumber(nsDecimalValue) ?: number.toString()
    }
}