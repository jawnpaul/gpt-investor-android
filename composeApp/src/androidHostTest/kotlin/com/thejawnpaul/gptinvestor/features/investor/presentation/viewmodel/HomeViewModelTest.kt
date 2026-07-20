package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `should return 12 AM for midnight rollover`() {
        assertThat(computeNextHourLabel(23)).isEqualTo("12 AM")
    }

    @Test
    fun `should return 12 PM for noon`() {
        assertThat(computeNextHourLabel(11)).isEqualTo("12 PM")
    }

    @Test
    fun `should return PM for afternoon hours`() {
        assertThat(computeNextHourLabel(14)).isEqualTo("3 PM")
    }

    @Test
    fun `should return AM for morning hours`() {
        assertThat(computeNextHourLabel(7)).isEqualTo("8 AM")
    }

    @Test
    fun `should return 1 AM for hour after midnight`() {
        assertThat(computeNextHourLabel(0)).isEqualTo("1 AM")
    }

    @Test
    fun `should return 1 PM for hour after noon`() {
        assertThat(computeNextHourLabel(12)).isEqualTo("1 PM")
    }

    @Test
    fun `should return first name when full name provided`() {
        assertThat(extractFirstName("John Doe")).isEqualTo("John")
    }

    @Test
    fun `should return first word when name has multiple parts`() {
        assertThat(extractFirstName("John Michael Doe")).isEqualTo("John")
    }

    @Test
    fun `should return single name as-is`() {
        assertThat(extractFirstName("John")).isEqualTo("John")
    }

    @Test
    fun `should return null when userName is null`() {
        assertThat(extractFirstName(null)).isNull()
    }

    @Test
    fun `should return null when userName is blank`() {
        assertThat(extractFirstName("   ")).isNull()
    }

    @Test
    fun `should handle leading and trailing spaces`() {
        assertThat(extractFirstName("  John Doe  ")).isEqualTo("John")
    }
}
