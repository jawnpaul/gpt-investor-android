package com.thejawnpaul.gptinvestor.features.notification.domain

interface NotificationRepository {
    suspend fun saveToken(token: String)
    suspend fun syncTokenIfNeeded()
}
