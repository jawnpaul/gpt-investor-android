package com.thejawnpaul.gptinvestor.features.settings.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserSettingsRequest(
    @SerialName("digest_delivery_hour") val digestDeliveryHour: Int? = null,
    @SerialName("timezone_iana") val timezoneIana: String? = null,
    @SerialName("utc_offset_minutes") val utcOffsetMinutes: Int? = null
)
