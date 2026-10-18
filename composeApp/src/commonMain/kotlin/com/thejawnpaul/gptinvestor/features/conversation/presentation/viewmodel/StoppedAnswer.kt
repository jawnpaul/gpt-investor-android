package com.thejawnpaul.gptinvestor.features.conversation.presentation.viewmodel

import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage

data class StoppedAnswer(val messages: List<GenAiMessage>, val restoredQuery: String?)

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
