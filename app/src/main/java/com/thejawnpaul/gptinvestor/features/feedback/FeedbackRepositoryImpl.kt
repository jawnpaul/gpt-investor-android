package com.thejawnpaul.gptinvestor.features.feedback

import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import timber.log.Timber
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(private val messageDao: MessageDao) : FeedbackRepository {
    override suspend fun giveFeedback(messageId: Long, status: Int, reason: String?) {
        try {
            val message = messageDao.getSingleMessage(messageId).copy(feedbackStatus = status)
            messageDao.updateMessage(message)
            Timber.d("Feedback submitted")
        } catch (e: Exception) {
            Timber.e(e.stackTrace.toString())
        }
    }
}
