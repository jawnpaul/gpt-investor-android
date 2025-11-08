package com.thejawnpaul.gptinvestor.features.feedback

interface FeedbackRepository {

    suspend fun giveFeedback(messageId: Long, status: Int, reason: String?)
}
