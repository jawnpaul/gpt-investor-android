package com.thejawnpaul.gptinvestor.features.digest.data.remote.model

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.json.Json
import org.junit.Test

class DigestResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `decodes next_digest_at when the backend sends it`() {
        val body = """{"state":"pending","digest_date":null,"items":[],"next_digest_at":"2026-10-11T01:00:00+00:00"}"""

        assertThat(json.decodeFromString<DigestResponse>(body).nextDigestAt).isEqualTo("2026-10-11T01:00:00+00:00")
    }

    @Test
    fun `next_digest_at is null when the backend doesn't send it yet`() {
        val body = """{"state":"pending","digest_date":null,"items":[]}"""

        assertThat(json.decodeFromString<DigestResponse>(body).nextDigestAt).isNull()
    }
}
