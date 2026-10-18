package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.junit.Test

class HomeViewModelTest {

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

    @Test
    fun `formats next digest time in the user's time zone`() {
        val berlin = TimeZone.of("Europe/Berlin")

        assertThat(formatNextDigestTime("2026-10-11T00:00:00+00:00", berlin)).isEqualTo("02:00")
        assertThat(formatNextDigestTime("2026-10-11T06:00:00+00:00", berlin)).isEqualTo("08:00")
        assertThat(formatNextDigestTime("2026-10-11T12:00:00+00:00", berlin)).isEqualTo("14:00")
    }

    @Test
    fun `formats midnight, noon and non-zero minutes`() {
        assertThat(formatNextDigestTime("2026-10-11T00:00:00+00:00", TimeZone.UTC)).isEqualTo("00:00")
        assertThat(formatNextDigestTime("2026-10-11T12:00:00+00:00", TimeZone.UTC)).isEqualTo("12:00")
        assertThat(formatNextDigestTime("2026-10-11T08:30:00+00:00", TimeZone.of("Asia/Kolkata"))).isEqualTo("14:00")
        assertThat(formatNextDigestTime("2026-10-11T08:00:00+00:00", TimeZone.of("Asia/Kolkata"))).isEqualTo("13:30")
    }

    @Test
    fun `missing or malformed next digest time falls back to null`() {
        assertThat(formatNextDigestTime(null, TimeZone.UTC)).isNull()
        assertThat(formatNextDigestTime("not a date", TimeZone.UTC)).isNull()
    }

    @Test
    fun `formats today as full weekday, day and month`() {
        assertThat(formatTodayLabel(LocalDate(2026, 10, 14))).isEqualTo("Wednesday, 14 October")
        assertThat(formatTodayLabel(LocalDate(2026, 1, 4))).isEqualTo("Sunday, 4 January")
    }
}
