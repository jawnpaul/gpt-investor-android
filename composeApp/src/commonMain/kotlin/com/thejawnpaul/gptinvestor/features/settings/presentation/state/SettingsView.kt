package com.thejawnpaul.gptinvestor.features.settings.presentation.state

data class SettingsView(
    val deleteAccount: String? = null,
    val isPremium: Boolean = false,
    val digestDeliveryHour: Int? = null,
    val digestDeliveryHourLabel: String? = null,
    val showDeleteDialog: Boolean = false,
    val showTimePicker: Boolean = false
)
