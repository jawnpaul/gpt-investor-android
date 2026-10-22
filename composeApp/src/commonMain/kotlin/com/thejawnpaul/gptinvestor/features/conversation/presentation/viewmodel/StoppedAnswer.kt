package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.Conversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.StructuredConversation
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.answerFailed

data class StoppedAnswer(val messages: List<GenAiMessage>, val restoredQuery: String?)

data class FailedAnswer(val messageId: Long, val messages: List<GenAiMessage>)

fun stopAnswer(messages: List<GenAiMessage>): StoppedAnswer? {
    val index = messages.indexOfLast { it is GenAiTextMessage }
    val last = messages.getOrNull(index) as? GenAiTextMessage
    if (last == null || !last.loading) return null
    return if (!last.response.isNullOrBlank()) {
        StoppedAnswer(
            messages = messages.toMutableList().also { it[index] = last.copy(loading = false) },
            restoredQuery = null
        )
    } else {
        StoppedAnswer(
            messages = messages.filterIndexed { i, message ->
                i != index && !(message is GenAiEntityMessage && message.id > last.id)
            },
            restoredQuery = last.query
        )
    }
}

fun withdrawFailedAnswer(messages: List<GenAiMessage>, query: String): FailedAnswer? {
    val index = messages.indexOfLast { it is GenAiTextMessage }
    val last = messages.getOrNull(index) as? GenAiTextMessage
    if (last == null || !last.answerFailed || last.query != query) return null
    return FailedAnswer(
        messageId = last.id,
        messages = messages.filterIndexed { i, message ->
            i != index && !(message is GenAiEntityMessage && message.id > last.id)
        }
    )
}

fun Set<Long>.withFailedAnswer(conversation: Conversation?, offline: Boolean): Set<Long> {
    val last = (conversation as? StructuredConversation)?.messageList?.lastOrNull { it is GenAiTextMessage }
    val id = (last as? GenAiTextMessage)?.takeIf { it.answerFailed }?.id ?: return this
    return if (offline) this + id else this - id
}
