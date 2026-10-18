package com.thejawnpaul.gptinvestor.features.toppick.presentation.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MetricDisplayTest {

    private fun display(raw: String) = displayMetric(parseMetric(raw))

    @Test
    fun `large money values are abbreviated with a dollar sign`() {
        assertThat(display("Revenue: 775680032768")).isEqualTo("Revenue" to "$775.68B")
        assertThat(display("Revenue: 445,865,984,000")).isEqualTo("Revenue" to "$445.87B")
        assertThat(display("Market Cap: 3200000000000")).isEqualTo("Market cap" to "$3.20T")
    }

    @Test
    fun `growth is shortened and signed`() {
        assertThat(display("Revenue Growth: 19.6%")).isEqualTo("Growth" to "+19.6%")
        assertThat(display("Revenue Growth: -4.1%")).isEqualTo("Growth" to "-4.1%")
    }

    @Test
    fun `labels are sentence case and keep ratios and acronyms`() {
        assertThat(display("Gross Margins: 50.8%")).isEqualTo("Gross margin" to "50.8%")
        assertThat(display("P/E Ratio: 29.11")).isEqualTo("P/E ratio" to "29.11")
        assertThat(display("EPS: 6.42")).isEqualTo("EPS" to "6.42")
    }
}
