package com.thejawnpaul.gptinvestor.features.conversation.data.local.model

import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "conversation_table")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true)
    val conversationId: Long = 0,
    val title: String,
    val createdAt: Long,
    val tokenCount: Int = 0,
    val lastMessageTimestamp: Long? = null
)

data class ConversationWithMessages(
    @Embedded val conversation: ConversationWithLastMessage,

    @Relation(
        parentColumn = "conversationId",
        entityColumn = "conversationId"
    )
    val messages: List<MessageEntity>
)

@DatabaseView(
    """
    SELECT c.conversationId, MAX(m.createdAt) as timestamp, c.title
    FROM conversation_table as c INNER JOIN message_table as m on c.conversationId = m.conversationId 
    GROUP BY c.conversationId
"""
)
data class ConversationWithLastMessage(
    val conversationId: Long,
    val title: String = "",
    val timestamp: Long = -1
)
