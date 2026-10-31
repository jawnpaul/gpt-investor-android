package com.thejawnpaul.gptinvestor.features.company.domain.model

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyBriefRemote
import kotlinx.serialization.json.Json
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
        assertThat(brief.keyNumbers.single { it.key == KeyNumberType.MarketCap }.value).startsWith("₦")
    }

    @Test
    fun `falls back to dollars when the response has no currency symbol`() {
        val brief = CompanyBriefRemote(ticker = "AAPL", currentPrice = 270.0, marketCap = 4_000_000_000_000).toBrief()

        assertThat(brief.currencySymbol).isEqualTo("$")
        assertThat(brief.keyNumbers.single { it.key == KeyNumberType.MarketCap }.value).startsWith("$")
    }

    @Test
    fun `reads market and currency fields as the API sends them`() {
        val remote = Json { ignoreUnknownKeys = true }.decodeFromString<CompanyBriefRemote>(
            """{"ticker": "ZENITHBANK.NG", "market": "NG", "currency": "NGN", "currency_symbol": "₦",
                "current_price": 119, "percentage_change": -0.83}"""
        )

        assertThat(remote.market).isEqualTo("NG")
        assertThat(remote.currency).isEqualTo("NGN")
        assertThat(remote.toBrief().currencySymbol).isEqualTo("₦")
    }

    @Test
    fun `keeps a missing daily change unknown instead of zero`() {
        val brief = CompanyBriefRemote(ticker = "ZENITHBANK.NG", currentPrice = 126.0).toBrief()

        assertThat(brief.change).isNull()
    }
}
