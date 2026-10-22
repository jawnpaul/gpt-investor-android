package com.thejawnpaul.gptinvestor.features.history.data.repository

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationWithLastMessage
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationWithMessages
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.presentation.ui.conversationTitle
import io.mockk.mockk
import org.junit.Test

class HistoryItemTest {

    private fun message(id: Long, query: String, entity: CompanyDetailRemoteResponse? = null) = MessageEntity(
        messageId = id,
        conversationId = 1,
        query = query,
        companyDetailRemoteResponse = entity,
        createdAt = id
    )

    @Test
    fun `should carry the first question of the conversation`() {
        val item = ConversationWithMessages(
            conversation = ConversationWithLastMessage(conversationId = 1, title = "Index funds"),
            messages = listOf(message(3, "And ETFs?"), message(2, "What is an index fund?"))
        ).toHistoryItem()

        assertThat((item.messageList.single() as GenAiTextMessage).query).isEqualTo("What is an index fund?")
    }

    @Test
    fun `should skip company cards when picking the first question`() {
        val item = ConversationWithMessages(
            conversation = ConversationWithLastMessage(conversationId = 1),
            messages = listOf(message(1, "", entity = mockk()), message(2, "Tell me about Apple"))
        ).toHistoryItem()

        assertThat((item.messageList.single() as GenAiTextMessage).query).isEqualTo("Tell me about Apple")
    }

    @Test
    fun `an untitled conversation is titled by its first question`() {
        val item = ConversationWithMessages(
            conversation = ConversationWithLastMessage(conversationId = 1, title = ""),
            messages = listOf(message(1, "What is a mutual fund?"))
        ).toHistoryItem()

        assertThat(conversationTitle(item, pendingQuery = null)).isEqualTo("What is a mutual fund?")
    }
}
