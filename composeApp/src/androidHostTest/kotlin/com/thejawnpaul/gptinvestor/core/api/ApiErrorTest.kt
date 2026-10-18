package com.thejawnpaul.gptinvestor.core.api

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Failure
import org.junit.Test

class ApiErrorTest {

    @Test
    fun `parses valid error body with error and message`() {
        val body = """{"error":"guest_session_expired","message":"Session has expired"}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("guest_session_expired")
        assertThat(error.message).isEqualTo("Session has expired")
    }

    @Test
    fun `parses guest_limit_reached with numeric limit and upgrade`() {
        val body = """{"error":"guest_limit_reached","limit":3,"upgrade":true}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("guest_limit_reached")
        assertThat(error.upgrade).isTrue()
    }

    @Test
    fun `parses rate_limited with text limit and retry_after`() {
        val body = """{"error":"rate_limited","message":"Request limit reached","tier":"normal",""" +
            """"limit":"2 per 1 day","retry_after":43210,"upgrade":true}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("rate_limited")
        assertThat(error.retryAfterSeconds).isEqualTo(43210)
        assertThat(error.upgrade).isTrue()
    }

    @Test
    fun `parses rate_limited with null retry_after`() {
        val body = """{"error":"rate_limited","retry_after":null,"upgrade":false}"""
        val error = parseApiError(body)
        assertThat(error.retryAfterSeconds).isNull()
        assertThat(error.upgrade).isFalse()
    }

    @Test
    fun `parses an SSE error frame`() {
        val body = "event: error\ndata: {\"error\":\"guest_limit_reached\",\"limit\":3,\"upgrade\":true}\n\n"
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("guest_limit_reached")
    }

    @Test
    fun `parses JWT error with legacy msg field`() {
        val body = """{"error":"token_expired","msg":"Token has expired"}"""
        val error = parseApiError(body)
        assertThat(error.code).isEqualTo("token_expired")
    }

    @Test
    fun `maps premium rate limit to RateLimitExceeded without upgrade`() {
        val failure = ApiError(code = "rate_limited", upgrade = false, retryAfterSeconds = 30).toRateLimitExceeded()
        assertThat(failure).isEqualTo(Failure.RateLimitExceeded(upgrade = false, retryAfterSeconds = 30))
    }

    @Test
    fun `defaults to upgrade when the body has no upgrade field`() {
        assertThat(ApiError().toRateLimitExceeded()).isEqualTo(Failure.RateLimitExceeded(upgrade = true))
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
        val error = parseApiError("<html><body>429 Too Many Requests: 2 per 1 day</body></html>")
        assertThat(error.code).isNull()
        assertThat(error.message).isNull()
    }

    @Test
    fun `handles missing error field`() {
        val body = """{"message":"Something went wrong"}"""
        val error = parseApiError(body)
        assertThat(error.code).isNull()
        assertThat(error.message).isEqualTo("Something went wrong")
    }
}
