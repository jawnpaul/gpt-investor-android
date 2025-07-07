package com.thejawnpaul.gptinvestor.features.conversation.data.firestore

import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import javax.inject.Inject
import timber.log.Timber

class ConversationSyncManager @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val firestoreConversationRepository: FirestoreConversationRepository
) {

    suspend fun syncConversationToCloud(conversation: ConversationEntity) {
        firestoreConversationRepository.saveConversation(conversation)
            .onFailure { error ->
                Timber.e("DataSync Failed to sync conversation: ${error.message}")
            }
    }

    suspend fun syncMessageToCloud(message: MessageEntity) {
        firestoreConversationRepository.saveMessage(message)
            .onFailure { error ->
                Timber.e("DataSync Failed to sync message: ${error.message}")
            }
    }

    suspend fun syncMessageFeedbackToCloud(conversationId: Long, messageId: Long, feedbackStatus: Int) {
        firestoreConversationRepository.updateMessageFeedback(conversationId.toString(), messageId.toString(), feedbackStatus)
            .onFailure { error ->
                Timber.e("DataSync Failed to sync message feedback: ${error.message}")
            }
    }

    suspend fun syncFromCloud(): Result<Unit> = try {
        // Sync conversations
        firestoreConversationRepository.getAllConversations()
            .onSuccess { cloudConversations ->
                cloudConversations.forEach { cloudConv ->
                    val roomConv = cloudConv.toRoomEntity()
                    conversationDao.insertConversation(roomConv)

                    // Sync messages for this conversation
                    firestoreConversationRepository.getMessagesForConversation(cloudConv.conversationId)
                        .onSuccess { cloudMessages ->
                            cloudMessages.forEach { cloudMsg ->
                                messageDao.insertMessage(cloudMsg.toRoomEntity())
                            }
                        }
                }
            }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
