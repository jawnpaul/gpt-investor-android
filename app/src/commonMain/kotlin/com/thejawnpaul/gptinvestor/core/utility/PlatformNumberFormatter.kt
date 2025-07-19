package com.thejawnpaul.gptinvestor.core.utility

import com.ionspin.kotlin.bignum.decimal.BigDecimal

expect class PlatformNumberFormatter() {
    fun format(number: BigDecimal, places: Int): String
}