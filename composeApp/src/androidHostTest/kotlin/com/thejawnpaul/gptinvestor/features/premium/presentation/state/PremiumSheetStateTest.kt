package com.thejawnpaul.gptinvestor.features.premium.presentation.state

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PremiumSheetStateTest {

    @Test
    fun `should show the used count the server sends`() {
        assertThat(PremiumSheetState(watchlistCap = 10, watchlistUsed = 8).slotsUsed).isEqualTo(8)
    }

    @Test
    fun `should assume a full watchlist when the server sends no used count`() {
        assertThat(PremiumSheetState(watchlistCap = 10).slotsUsed).isEqualTo(10)
    }

    @Test
    fun `should keep the used count within the cap`() {
        assertThat(PremiumSheetState(watchlistCap = 10, watchlistUsed = 12).slotsUsed).isEqualTo(10)
    }

    @Test
    fun `should have no used count without a cap`() {
        assertThat(PremiumSheetState(watchlistUsed = 3).slotsUsed).isNull()
    }
}
