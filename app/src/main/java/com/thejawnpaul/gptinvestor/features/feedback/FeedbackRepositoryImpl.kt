package com.thejawnpaul.gptinvestor.features.feedback

import com.thejawnpaul.gptinvestor.features.conversation.data.firestore.ConversationSyncManager
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import javax.inject.Inject
import timber.log.Timber

class FeedbackRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val conversationSyncManager: ConversationSyncManager
) :
    FeedbackRepository {
    override suspend fun giveFeedback(messageId: Long, status: Int, reason: String?) {
        try {
            val message = messageDao.getSingleMessage(messageId).copy(feedbackStatus = status)
            messageDao.updateMessage(message)
            // Sync feedback to Firestore
            conversationSyncManager.syncMessageFeedbackToCloud(
                conversationId = message.conversationId,
                messageId = message.messageId,
                feedbackStatus = status
            )
            Timber.d("Feedback submitted and synced")
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }
}
