package com.thejawnpaul.gptinvestor.features.settings.data.remote.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Unset fields are omitted so a partial PUT doesn't null out the stored values.
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class UpdateUserSettingsRequest(
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("digest_delivery_hour")
    val digestDeliveryHour: Int? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("timezone_iana")
    val timezoneIana: String? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("utc_offset_minutes")
    val utcOffsetMinutes: Int? = null
)
