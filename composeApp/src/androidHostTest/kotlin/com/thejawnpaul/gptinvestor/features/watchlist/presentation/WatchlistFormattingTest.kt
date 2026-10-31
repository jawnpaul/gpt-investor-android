package com.thejawnpaul.gptinvestor.features.watchlist.presentation

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.WatchlistBriefStatus
import com.thejawnpaul.gptinvestor.features.watchlist.domain.model.watchlistBriefStatusOf
import org.junit.Test

class WatchlistFormattingTest {

    @Test
    fun `formats prices with two decimals and thousands separators`() {
        assertThat(formatPrice(382.7, "$")).isEqualTo("$382.70")
        assertThat(formatPrice(1234.5, "$")).isEqualTo("$1,234.50")
        assertThat(formatPrice(1_000_000.0, "$")).isEqualTo("$1,000,000.00")
        assertThat(formatPrice(0.456, "$")).isEqualTo("$0.46")
    }

    @Test
    fun `formats prices with the company's currency symbol`() {
        assertThat(formatPrice(126.0, "₦")).isEqualTo("₦126.00")
        assertThat(formatPrice(1234.5, "₦")).isEqualTo("₦1,234.50")
    }

    @Test
    fun `maps digest_status and falls back to locked for older backends`() {
        assertThat(watchlistBriefStatusOf("in_brief", locked = false)).isEqualTo(WatchlistBriefStatus.IN_BRIEF)
        assertThat(watchlistBriefStatusOf("locked", locked = false)).isEqualTo(WatchlistBriefStatus.LOCKED)
        assertThat(watchlistBriefStatusOf("pending_next_brief", locked = false))
            .isEqualTo(WatchlistBriefStatus.PENDING_NEXT_BRIEF)
        assertThat(watchlistBriefStatusOf(null, locked = true)).isEqualTo(WatchlistBriefStatus.LOCKED)
        assertThat(watchlistBriefStatusOf(null, locked = false)).isNull()
    }
}
