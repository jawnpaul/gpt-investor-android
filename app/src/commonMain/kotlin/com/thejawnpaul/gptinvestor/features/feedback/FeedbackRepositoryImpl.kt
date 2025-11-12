package com.thejawnpaul.gptinvestor.features.feedback

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao

class FeedbackRepositoryImpl(private val messageDao: MessageDao) : FeedbackRepository {
    override suspend fun giveFeedback(messageId: Long, status: Int, reason: String?) {
        try {
            val message = messageDao.getSingleMessage(messageId).copy(feedbackStatus = status)
            messageDao.updateMessage(message)
            Logger.d("Feedback submitted")
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
        }
    }
}
