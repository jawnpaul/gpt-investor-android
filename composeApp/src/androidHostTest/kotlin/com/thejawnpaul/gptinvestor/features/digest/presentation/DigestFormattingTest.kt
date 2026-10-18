package com.thejawnpaul.gptinvestor.features.digest.presentation

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.TimeZone
import org.junit.Test

class DigestFormattingTest {

    @Test
    fun `maps API sentiment and ignores unknown values`() {
        assertThat(digestSentimentOf("positive")).isEqualTo(DigestSentiment.POSITIVE)
        assertThat(digestSentimentOf("Neutral")).isEqualTo(DigestSentiment.NEUTRAL)
        assertThat(digestSentimentOf("negative")).isEqualTo(DigestSentiment.NEGATIVE)
        assertThat(digestSentimentOf(null)).isNull()
        assertThat(digestSentimentOf("unchanged")).isNull()
    }

    @Test
    fun `formats the trading day a brief covers`() {
        assertThat(formatMarketDate("2026-10-09")).isEqualTo("Fri 9 Oct")
        assertThat(formatMarketDate("2026-01-12")).isEqualTo("Mon 12 Jan")
        assertThat(formatMarketDate(null)).isNull()
        assertThat(formatMarketDate("Fri")).isNull()
    }

    @Test
    fun `formats generated_at in the user's time zone`() {
        val berlin = TimeZone.of("Europe/Berlin")

        assertThat(formatUpdatedAt("2026-10-10T17:01:11Z", berlin)).isEqualTo("Sat 19:01")
        assertThat(formatUpdatedAt("2026-10-10T22:05:00Z", berlin)).isEqualTo("Sun 00:05")
        assertThat(formatUpdatedAt("2026-10-10T10:00:00Z", TimeZone.UTC)).isEqualTo("Sat 10:00")
    }

    @Test
    fun `old RFC 1123 or missing generated_at gives no label`() {
        assertThat(formatUpdatedAt("Sat, 10 Oct 2026 23:01:11 GMT", TimeZone.UTC)).isNull()
        assertThat(formatUpdatedAt(null, TimeZone.UTC)).isNull()
    }

    @Test
    fun `formats price change with sign and one decimal`() {
        assertThat(formatPriceChange(1.5)).isEqualTo("+1.5%")
        assertThat(formatPriceChange(-1.11)).isEqualTo("-1.1%")
        assertThat(formatPriceChange(15.64)).isEqualTo("+15.6%")
        assertThat(formatPriceChange(0.0)).isEqualTo("0.0%")
        assertThat(formatPriceChange(-0.04)).isEqualTo("0.0%")
    }

    @Test
    fun `joins tickers the way the locked card and tracking row read them`() {
        assertThat(formatTickerList(emptyList())).isEmpty()
        assertThat(formatTickerList(listOf("TSLA"))).isEqualTo("TSLA")
        assertThat(formatTickerList(listOf("TSLA", "MOH"))).isEqualTo("TSLA & MOH")
        assertThat(formatTickerList(listOf("AAPL", "TSLA", "MOH"))).isEqualTo("AAPL, TSLA & MOH")
    }
}
