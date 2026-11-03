package com.thejawnpaul.gptinvestor.features.onboarding.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SuggestedStockTest {

    @Test
    fun `should show the ticker before the market suffix`() {
        assertThat(SuggestedStock("ZENITHBANK.NG", "Zenith Bank").displayTicker).isEqualTo("ZENITHBANK")
    }

    @Test
    fun `should show a ticker without a suffix as is`() {
        assertThat(SuggestedStock("AAPL", "Apple").displayTicker).isEqualTo("AAPL")
    }
}
