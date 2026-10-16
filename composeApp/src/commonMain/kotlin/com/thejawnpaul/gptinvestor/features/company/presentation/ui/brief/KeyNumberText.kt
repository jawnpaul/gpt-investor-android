package com.thejawnpaul.gptinvestor.features.company.presentation.ui.brief

import com.thejawnpaul.gptinvestor.Res
import com.thejawnpaul.gptinvestor.features.company.domain.model.KeyNumberType
import com.thejawnpaul.gptinvestor.key_definition_dividend_yield
import com.thejawnpaul.gptinvestor.key_definition_market_cap
import com.thejawnpaul.gptinvestor.key_definition_pe_ratio
import com.thejawnpaul.gptinvestor.key_definition_revenue_growth
import com.thejawnpaul.gptinvestor.key_dividend_yield
import com.thejawnpaul.gptinvestor.key_pe_ratio
import com.thejawnpaul.gptinvestor.key_period_last_12_months
import com.thejawnpaul.gptinvestor.key_period_today
import com.thejawnpaul.gptinvestor.key_question_dividend_yield
import com.thejawnpaul.gptinvestor.key_question_market_cap
import com.thejawnpaul.gptinvestor.key_question_pe_ratio
import com.thejawnpaul.gptinvestor.key_question_revenue_growth
import com.thejawnpaul.gptinvestor.key_revenue_growth
import com.thejawnpaul.gptinvestor.market_cap
import org.jetbrains.compose.resources.StringResource

internal fun KeyNumberType.labelRes(): StringResource = when (this) {
    KeyNumberType.MarketCap -> Res.string.market_cap
    KeyNumberType.PeRatio -> Res.string.key_pe_ratio
    KeyNumberType.RevenueGrowth -> Res.string.key_revenue_growth
    KeyNumberType.DividendYield -> Res.string.key_dividend_yield
}

internal fun KeyNumberType.periodRes(): StringResource = when (this) {
    KeyNumberType.MarketCap -> Res.string.key_period_today
    KeyNumberType.PeRatio,
    KeyNumberType.RevenueGrowth,
    KeyNumberType.DividendYield -> Res.string.key_period_last_12_months
}

internal fun KeyNumberType.definitionRes(): StringResource = when (this) {
    KeyNumberType.MarketCap -> Res.string.key_definition_market_cap
    KeyNumberType.PeRatio -> Res.string.key_definition_pe_ratio
    KeyNumberType.RevenueGrowth -> Res.string.key_definition_revenue_growth
    KeyNumberType.DividendYield -> Res.string.key_definition_dividend_yield
}

internal fun KeyNumberType.questionRes(): StringResource = when (this) {
    KeyNumberType.MarketCap -> Res.string.key_question_market_cap
    KeyNumberType.PeRatio -> Res.string.key_question_pe_ratio
    KeyNumberType.RevenueGrowth -> Res.string.key_question_revenue_growth
    KeyNumberType.DividendYield -> Res.string.key_question_dividend_yield
}
