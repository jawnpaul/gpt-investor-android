package com.thejawnpaul.gptinvestor.features.investor.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
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
}
