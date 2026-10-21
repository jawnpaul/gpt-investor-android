package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.ANSWER_FAILED_FALLBACK
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import io.mockk.mockk
import org.junit.Test

class WithdrawFailedAnswerTest {

    private val firstAnswer =
        GenAiTextMessage(id = 1, query = "Is Apple a good buy?", feedbackStatus = 0, response = "Yes")
    private val firstEntity = GenAiEntityMessage(id = 2, entity = mockk<CompanyDetailRemoteResponse>())
    private val failed =
        GenAiTextMessage(id = 3, query = "And Nvidia?", feedbackStatus = 0, response = ANSWER_FAILED_FALLBACK)

    @Test
    fun `should remove the failed answer and return its id`() {
        val result = withdrawFailedAnswer(listOf(firstAnswer, failed), query = "And Nvidia?")

        assertThat(result?.messageId).isEqualTo(3)
        assertThat(result?.messages).containsExactly(firstAnswer)
    }

    @Test
    fun `should drop entities that arrived for the failed answer but keep earlier ones`() {
        val lateEntity = GenAiEntityMessage(id = 4, entity = mockk<CompanyDetailRemoteResponse>())

        val result = withdrawFailedAnswer(listOf(firstAnswer, firstEntity, failed, lateEntity), query = "And Nvidia?")

        assertThat(result?.messages).containsExactly(firstAnswer, firstEntity).inOrder()
    }

    @Test
    fun `should leave an empty thread when the only question failed`() {
        val result = withdrawFailedAnswer(listOf(failed), query = "And Nvidia?")

        assertThat(result?.messages).isEmpty()
    }

    @Test
    fun `should return null when the latest answer did not fail`() {
        assertThat(withdrawFailedAnswer(listOf(failed, firstAnswer), query = "Is Apple a good buy?")).isNull()
    }

    @Test
    fun `should return null when the query is not the latest question`() {
        assertThat(withdrawFailedAnswer(listOf(firstAnswer, failed), query = "Is Apple a good buy?")).isNull()
    }

    @Test
    fun `should remember an answer that failed offline`() {
        val conversation = StructuredConversation(id = 1, title = "", messageList = mutableListOf(firstAnswer, failed))

        assertThat(setOf(9L).withFailedAnswer(conversation, offline = true)).containsExactly(9L, 3L)
    }

    @Test
    fun `should forget an answer that failed for another reason`() {
        val conversation = StructuredConversation(id = 1, title = "", messageList = mutableListOf(firstAnswer, failed))

        assertThat(setOf(3L).withFailedAnswer(conversation, offline = false)).isEmpty()
    }

    @Test
    fun `should leave the set alone when the latest answer did not fail`() {
        val conversation = StructuredConversation(id = 1, title = "", messageList = mutableListOf(failed, firstAnswer))

        assertThat(setOf(3L).withFailedAnswer(conversation, offline = true)).containsExactly(3L)
    }
}
