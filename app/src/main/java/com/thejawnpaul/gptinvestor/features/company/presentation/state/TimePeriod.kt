package com.thejawnpaul.gptinvestor.features.company.presentation.state

sealed interface TimePeriod {
    val title: String

    data class OneWeek(override val title: String = "1 week") : TimePeriod
    data class OneMonth(override val title: String = "1 month") : TimePeriod
    data class ThreeMonths(override val title: String = "3 months") : TimePeriod
    data class OneYear(override val title: String = "1 year") : TimePeriod
}
