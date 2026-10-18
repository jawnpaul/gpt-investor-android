package com.thejawnpaul.gptinvestor.features.settings.data.remote.model

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class UpdateUserSettingsRequestTest {

    private val json = Json { encodeDefaults = true }

    @Test
    fun `timezone-only request omits digest_delivery_hour`() {
        val request = UpdateUserSettingsRequest(timezoneIana = "Europe/Berlin", utcOffsetMinutes = 120)

        assertThat(json.encodeToString(request))
            .isEqualTo("""{"timezone_iana":"Europe/Berlin","utc_offset_minutes":120}""")
    }

    @Test
    fun `hour-only request omits timezone fields`() {
        val request = UpdateUserSettingsRequest(digestDeliveryHour = 7)

        assertThat(json.encodeToString(request)).isEqualTo("""{"digest_delivery_hour":7}""")
    }
}
