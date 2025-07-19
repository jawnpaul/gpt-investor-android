package com.thejawnpaul.gptinvestor.features.conversation.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiEntityMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiMessage
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.GenAiTextMessage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Entity(
    tableName = "message_table",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["conversationId"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
@OptIn(ExperimentalTime::class)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val messageId: Long = 0,
    val conversationId: Long,
    val query: String? = null,
    val response: String? = null,
    val companyDetailRemoteResponse: CompanyDetailRemoteResponse? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @ColumnInfo(defaultValue = "0")
    val feedbackStatus: Int = 0
) {
    fun toGenAiMessage(): GenAiMessage {
        return if (companyDetailRemoteResponse != null) {
            GenAiEntityMessage(id = messageId, entity = companyDetailRemoteResponse)
        } else {
            GenAiTextMessage(
                id = messageId,
                query = query.toString(),
                response = response.toString(),
                feedbackStatus = feedbackStatus
            )
        }
    }
}
