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

    @Test
    fun `reads a market cap and revenue sent with decimals`() {
        val remote = Json { ignoreUnknownKeys = true }.decodeFromString<CompanyBriefRemote>(
            """{"ticker": "INTENEGINS.NG", "market_cap": 2311200000.75, "revenue": 15000000.4}"""
        )

        assertThat(remote.marketCap).isEqualTo(2_311_200_001L)
        assertThat(remote.revenue).isEqualTo(15_000_000L)
    }

    @Test
    fun `reads a whole-number market cap and a missing one`() {
        val json = Json { ignoreUnknownKeys = true }

        assertThat(json.decodeFromString<CompanyBriefRemote>("""{"ticker": "AAPL", "market_cap": 4000}""").marketCap)
            .isEqualTo(4000L)
        assertThat(json.decodeFromString<CompanyBriefRemote>("""{"ticker": "AAPL", "market_cap": null}""").marketCap)
            .isNull()
    }
}
