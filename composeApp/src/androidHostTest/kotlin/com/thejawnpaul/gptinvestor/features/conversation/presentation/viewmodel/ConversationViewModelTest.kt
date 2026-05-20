package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ConversationViewModelTest {

    @Test
    fun `should return null when chatInput is null`() {
        assertThat(resolveConversationStart(title = null, chatInput = null)).isNull()
    }

    @Test
    fun `should return null when chatInput is null even if title is present`() {
        assertThat(resolveConversationStart(title = "Some Title", chatInput = null)).isNull()
    }

    @Test
    fun `should return chatInput unchanged when it contains a percent sign`() {
        val query = "If I invest \$10k at 6.29% interest, is this wise?"
        val result = resolveConversationStart(title = null, chatInput = query)
        assertThat(result).isNotNull()
        assertThat(result!!.second).isEqualTo(query)
    }

    @Test
    fun `should return null title when no title provided`() {
        val result = resolveConversationStart(title = null, chatInput = "some query")
        assertThat(result).isNotNull()
        assertThat(result!!.first).isNull()
    }

    @Test
    fun `should return title and chatInput when both are provided`() {
        val title = "Leveraged ETF strategy"
        val query = "Is DHHF a good choice for a 0.29% loan?"
        val result = resolveConversationStart(title = title, chatInput = query)
        assertThat(result).isNotNull()
        assertThat(result!!.first).isEqualTo(title)
        assertThat(result.second).isEqualTo(query)
    }

    @Test
    fun `should return chatInput with multiple percent signs unchanged`() {
        val query = "50% stocks and 50% bonds — good split?"
        val result = resolveConversationStart(title = null, chatInput = query)
        assertThat(result).isNotNull()
        assertThat(result!!.second).isEqualTo(query)
    }

    @Test
    fun `should return chatInput with percent followed by letters unchanged`() {
        val query = "I want 30% allocated to Mutual fund and 70% to cash"
        val result = resolveConversationStart(title = null, chatInput = query)
        assertThat(result).isNotNull()
        assertThat(result!!.second).isEqualTo(query)
    }
}
