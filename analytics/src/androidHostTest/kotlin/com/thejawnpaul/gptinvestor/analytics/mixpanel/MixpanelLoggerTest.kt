package com.thejawnpaul.gptinvestor.analytics.mixpanel

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class MixpanelLoggerTest {

    private val people: MixpanelAPI.People = mockk(relaxed = true)
    private val mixpanel: MixpanelAPI = mockk(relaxed = true) {
        every { this@mockk.people } returns this@MixpanelLoggerTest.people
    }

    @Before
    fun setUp() {
        mockkStatic(MixpanelAPI::class)
        every { MixpanelAPI.getInstance(any<Context>(), any<String>(), any<Boolean>()) } returns mixpanel
    }

    @After
    fun tearDown() {
        unmockkStatic(MixpanelAPI::class)
    }

    @Test
    fun `setUserProperty registers a super property and a people property`() {
        val logger = MixpanelLogger(mockk(relaxed = true))

        logger.setUserProperty("free_watchlist_limit", "3")

        verify(exactly = 1) { mixpanel.registerSuperPropertiesMap(mapOf("free_watchlist_limit" to "3")) }
        verify(exactly = 1) { people.set("free_watchlist_limit", "3") }
    }
}
