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
            }.onSuccess {
                Timber.e("DataSync Successfully synced conversation: ${conversation.conversationId}")
            }
    }

    suspend fun syncMessageToCloud(message: MessageEntity) {
        firestoreConversationRepository.saveMessage(message)
            .onFailure { error ->
                Timber.e("DataSync Failed to sync message: ${error.message}")
            }.onSuccess {
                Timber.e("DataSync Successfully synced message: ${message.messageId}")
            }
    }

    suspend fun syncMessageFeedbackToCloud(conversationId: Long, messageId: Long, feedbackStatus: Int) {
        firestoreConversationRepository.updateMessageFeedback(conversationId.toString(), messageId.toString(), feedbackStatus)
            .onFailure { error ->
                Timber.e("DataSync Failed to sync message feedback: ${error.message}")
            }.onSuccess {
                Timber.e("DataSync Successfully synced message feedback: $messageId")
            }
    }

    suspend fun syncFromCloud(): Result<Unit> = try {
        // Sync conversations
        firestoreConversationRepository.getAllConversations()
            .onSuccess { cloudConversations ->
                // Iterate over conversations. If any step inside fails and throws,
                // the whole sync operation will be caught by the outer try-catch.
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
                        .onFailure { messagesError ->
                            Timber.e(messagesError, "DataSync: Failed to sync messages for conversation ${cloudConv.conversationId}")
                            throw messagesError // Propagate error to fail the entire sync
                        }
                }
            }
            .onFailure { conversationsError ->
                Timber.e(conversationsError, "DataSync: Failed to sync conversations")
                throw conversationsError // Propagate error to fail the entire sync
            }
        // If all operations were successful and no exceptions were thrown, return success.
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "DataSync: Exception during syncFromCloud")
        Result.failure(e)
    }
}
