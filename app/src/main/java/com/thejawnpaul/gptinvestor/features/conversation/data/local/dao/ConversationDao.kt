package com.thejawnpaul.gptinvestor.features.conversation.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity

@Dao
interface ConversationDao {

    @Query("SELECT * FROM conversation_table ORDER BY lastMessageTimestamp DESC")
    suspend fun getAllConversations(): List<ConversationEntity>

    @Query("SELECT * FROM conversation_table WHERE conversationId =:id")
    suspend fun getSingleConversation(id: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Delete
    suspend fun deleteConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)
}