package com.thejawnpaul.gptinvestor.core.session

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JwtExpiryCheckerTest {

    private fun buildToken(payloadJson: String): String {
        val encoder = java.util.Base64.getUrlEncoder().withoutPadding()
        val header = encoder.encodeToString("""{"alg":"HS256"}""".toByteArray())
        val payload = encoder.encodeToString(payloadJson.toByteArray())
        return "$header.$payload.fakesignature"
    }

    @Test
    fun `should return true when token exp is in the past`() {
        val pastExp = 1_000_000L
        val token = buildToken("""{"sub":"guest123","exp":$pastExp}""")
        assertThat(JwtExpiryChecker.isExpired(token, nowEpochSeconds = pastExp + 1)).isTrue()
    }

    @Test
    fun `should return false when token exp is in the future`() {
        val futureExp = Long.MAX_VALUE / 2
        val token = buildToken("""{"sub":"guest123","exp":$futureExp}""")
        assertThat(JwtExpiryChecker.isExpired(token, nowEpochSeconds = 1_000_000L)).isFalse()
    }

    @Test
    fun `should return false for malformed token with no dots`() {
        assertThat(JwtExpiryChecker.isExpired("notavalidtoken", nowEpochSeconds = 9_999_999L)).isFalse()
    }

    @Test
    fun `should return false when payload is not valid JSON`() {
        val token = "header.!!!notbase64!!!.sig"
        assertThat(JwtExpiryChecker.isExpired(token, nowEpochSeconds = 9_999_999L)).isFalse()
    }

    @Test
    fun `should return false when exp claim is missing from payload`() {
        val token = buildToken("""{"sub":"guest123","iat":1000000}""")
        assertThat(JwtExpiryChecker.isExpired(token, nowEpochSeconds = 9_999_999L)).isFalse()
    }

    @Test
    fun `should return true exactly at exp boundary`() {
        val exp = 5_000_000L
        val token = buildToken("""{"exp":$exp}""")
        assertThat(JwtExpiryChecker.isExpired(token, nowEpochSeconds = exp)).isTrue()
    }
}
