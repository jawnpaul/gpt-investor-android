package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyBriefRemote
import org.junit.Test

class CompanyBriefTest {

    @Test
    fun `uses the company's currency symbol for the price and market cap`() {
        val brief = CompanyBriefRemote(
            ticker = "ZENITHBANK.NG",
            currentPrice = 126.0,
            currencySymbol = "₦",
            marketCap = 3_950_000_000_000
        ).toBrief()

        assertThat(brief.currencySymbol).isEqualTo("₦")
        assertThat(brief.keyNumbers.single { it.key == KeyNumberType.MarketCap }.value).isEqualTo("₦4.0T")
    }

    @Test
    fun `falls back to dollars when the response has no currency symbol`() {
        val brief = CompanyBriefRemote(ticker = "AAPL", currentPrice = 270.0, marketCap = 4_000_000_000_000).toBrief()

        assertThat(brief.currencySymbol).isEqualTo("$")
        assertThat(brief.keyNumbers.single { it.key == KeyNumberType.MarketCap }.value).isEqualTo("$4.0T")
    }

    @Test
    fun `keeps a missing daily change unknown instead of zero`() {
        val brief = CompanyBriefRemote(ticker = "ZENITHBANK.NG", currentPrice = 126.0).toBrief()

        assertThat(brief.change).isNull()
    }
}
