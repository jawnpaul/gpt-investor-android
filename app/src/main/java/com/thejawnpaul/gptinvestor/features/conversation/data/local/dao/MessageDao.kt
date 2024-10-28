package com.thejawnpaul.gptinvestor.features.conversation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM message_table WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    suspend fun getMessagesForConversation(conversationId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Delete
    suspend fun deleteMessage(message: MessageEntity)

    @Query("SELECT * FROM message_table WHERE messageId =:id")
    suspend fun getSingleMessage(id: Long): MessageEntity

    @Update
    suspend fun updateMessage(message: MessageEntity)
}
