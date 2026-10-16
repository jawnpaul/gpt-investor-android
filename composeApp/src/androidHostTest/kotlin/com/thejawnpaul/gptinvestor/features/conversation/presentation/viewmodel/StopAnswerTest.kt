package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import io.mockk.mockk
import org.junit.Test

class StopAnswerTest {

    private val firstAnswer =
        GenAiTextMessage(id = 1, query = "Is Apple a good buy?", feedbackStatus = 0, response = "Yes")
    private val firstEntity = GenAiEntityMessage(id = 2, entity = mockk<CompanyDetailRemoteResponse>())

    @Test
    fun `should return null when no answer is in flight`() {
        assertThat(stopAnswer(listOf(firstAnswer))).isNull()
    }

    @Test
    fun `should keep partial text and stop loading when text has streamed`() {
        val streaming =
            GenAiTextMessage(id = 3, query = "And Nvidia?", feedbackStatus = 0, response = "NVDA", loading = true)

        val result = stopAnswer(listOf(firstAnswer, streaming))

        assertThat(result?.messages).containsExactly(firstAnswer, streaming.copy(loading = false)).inOrder()
        assertThat(result?.restoredQuery).isNull()
    }

    @Test
    fun `should remove blank answer and restore its query`() {
        val thinking = GenAiTextMessage(id = 3, query = "And Nvidia?", feedbackStatus = 0, loading = true)

        val result = stopAnswer(listOf(firstAnswer, thinking))

        assertThat(result?.messages).containsExactly(firstAnswer)
        assertThat(result?.restoredQuery).isEqualTo("And Nvidia?")
    }

    @Test
    fun `should drop entities that arrived for a blank answer but keep earlier ones`() {
        val thinking = GenAiTextMessage(id = 3, query = "And Tesla?", feedbackStatus = 0, loading = true)
        val lateEntity = GenAiEntityMessage(id = 4, entity = mockk<CompanyDetailRemoteResponse>())

        val result = stopAnswer(listOf(firstAnswer, firstEntity, lateEntity, thinking))

        assertThat(result?.messages).containsExactly(firstAnswer, firstEntity).inOrder()
        assertThat(result?.restoredQuery).isEqualTo("And Tesla?")
    }

    @Test
    fun `should leave an empty thread when the only question is stopped before text`() {
        val thinking = GenAiTextMessage(id = 1, query = "Tell me about Tesla", feedbackStatus = 0, loading = true)
        val lateEntity = GenAiEntityMessage(id = 2, entity = mockk<CompanyDetailRemoteResponse>())

        val result = stopAnswer(listOf(lateEntity, thinking))

        assertThat(result?.messages).isEmpty()
        assertThat(result?.restoredQuery).isEqualTo("Tell me about Tesla")
    }
}
