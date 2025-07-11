package com.thejawnpaul.gptinvestor.features.conversation.data.firestore

import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyNews
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.HistoricalData
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity

data class FirestoreConversation(
    val conversationId: String = "",
    val title: String = "",
    val createdAt: Long = 0,
    val tokenCount: Int = 0,
    val lastMessageTimestamp: Long? = null
) {
    // Convert from Room entity
    constructor(entity: ConversationEntity) : this(
        conversationId = entity.conversationId.toString(),
        title = entity.title,
        createdAt = entity.createdAt,
        tokenCount = entity.tokenCount,
        lastMessageTimestamp = entity.lastMessageTimestamp
    )

    // Convert to Room entity
    fun toRoomEntity(): ConversationEntity {
        return ConversationEntity(
            conversationId = conversationId.toLongOrNull() ?: 0,
            title = title,
            createdAt = createdAt,
            tokenCount = tokenCount,
            lastMessageTimestamp = lastMessageTimestamp
        )
    }
}

data class FirestoreMessage(
    val messageId: String = "",
    val conversationId: String = "",
    val query: String? = null,
    val response: String? = null,
    val companyDetailRemoteResponse: Map<String, Any>? = null,
    val createdAt: Long = 0,
    val feedbackStatus: Int = 0
) {
    constructor(entity: MessageEntity) : this(
        messageId = entity.messageId.toString(),
        conversationId = entity.conversationId.toString(),
        query = entity.query,
        response = entity.response,
        companyDetailRemoteResponse = entity.companyDetailRemoteResponse?.toMap(),
        createdAt = entity.createdAt,
        feedbackStatus = entity.feedbackStatus
    )

    fun toRoomEntity(): MessageEntity {
        return MessageEntity(
            messageId = messageId.toLongOrNull() ?: 0,
            conversationId = conversationId.toLongOrNull() ?: 0,
            query = query,
            response = response,
            companyDetailRemoteResponse = companyDetailRemoteResponse?.let {
                // Convert Map back to CompanyDetailRemoteResponse
                // convertFromMap(it)
                CompanyDetailRemoteResponse(
                    ticker = it.getOrDefault("ticker", "") as String,
                    about = it.getOrDefault("summary", "") as String,
                    marketCap = it.getOrDefault("market_cap", 0L) as Long,
                    news = it.getOrDefault("news", emptyList<CompanyNews>()) as List<CompanyNews>,
                    peRatio = (it.getOrDefault("pe_ratio", 0f) as Double).toFloat(),
                    change = (it.getOrDefault("percentage_change", 0f) as Double).toFloat(),
                    revenue = it.getOrDefault("revenue", 0L) as Long,
                    price = (it.getOrDefault("current_price", 0f) as Double).toFloat(),
                    historicalData = it.getOrDefault("historical_data", emptyList<HistoricalData>()) as List<HistoricalData>,
                    name = it.getOrDefault("company_name", "") as String,
                    imageUrl = it.getOrDefault("logo_url", "") as String

                )
            },
            createdAt = createdAt,
            feedbackStatus = feedbackStatus
        )
    }
}
