package com.thejawnpaul.gptinvestor.features.settings.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsResponse(
    @SerialName("digest_delivery_hour") val digestDeliveryHour: Int? = null,
    @SerialName("digest_utc_hour") val digestUtcHour: Int? = null,
    @SerialName("notif_digest_enabled") val notifDigestEnabled: Boolean? = null,
    @SerialName("timezone_iana") val timezoneIana: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("utc_offset_minutes") val utcOffsetMinutes: Int? = null
)
