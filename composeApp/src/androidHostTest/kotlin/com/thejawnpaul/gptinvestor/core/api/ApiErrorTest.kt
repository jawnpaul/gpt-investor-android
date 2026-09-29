package com.thejawnpaul.gptinvestor.core.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ApiErrorTest {

    @Test
    fun `parses valid error body with code and message`() {
        val body = """{"code":"guest_session_expired","message":"Session has expired"}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("guest_session_expired")
        assertThat(error.message).isEqualTo("Session has expired")
    }

    @Test
    fun `parses guest_limit_reached with limit field`() {
        val body = """{"code":"guest_limit_reached","message":"Limit reached","limit":5}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("guest_limit_reached")
        assertThat(error.limit).isEqualTo(5)
    }

    @Test
    fun `parses rate_limited with retry_after_seconds`() {
        val body = """{"code":"rate_limited","message":"Too many requests","retry_after_seconds":30}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("rate_limited")
        assertThat(error.retryAfterSeconds).isEqualTo(30)
    }

    @Test
    fun `returns empty ApiError for null body`() {
        val error = parseApiError(null)
        assertThat(error.code).isNull()
        assertThat(error.message).isNull()
    }

    @Test
    fun `returns empty ApiError for blank body`() {
        val error = parseApiError("  ")
        assertThat(error.code).isNull()
    }

    @Test
    fun `returns empty ApiError for non-JSON body`() {
        val error = parseApiError("Internal Server Error")
        assertThat(error.code).isNull()
        assertThat(error.message).isNull()
    }

    @Test
    fun `handles body with unknown extra fields gracefully`() {
        val body = """{"code":"token_expired","message":"Token expired","extra_field":"ignored"}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("token_expired")
    }

    @Test
    fun `handles missing code field`() {
        val body = """{"message":"Something went wrong"}"""
        val error = parseApiError(body)
        assertThat(error.code).isNull()
        assertThat(error.message).isEqualTo("Something went wrong")
    }
}
