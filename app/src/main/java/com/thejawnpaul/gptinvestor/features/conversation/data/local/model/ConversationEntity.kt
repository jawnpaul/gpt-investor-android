package com.thejawnpaul.gptinvestor.features.conversation.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_table")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val conversationId: Long = 0,
    val title: String,
    val createdAt: Long,
    val tokenCount: Int = 0,
    val lastMessageTimestamp: Long? = null
)