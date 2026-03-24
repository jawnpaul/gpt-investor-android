package com.thejawnpaul.gptinvestor.features.notification.domain

import org.koin.core.annotation.Singleton

@Singleton(binds = [NotificationRepository::class])
class IosNotificationRepository : NotificationRepository {
    override suspend fun saveToken(token: String) = Unit
    override suspend fun syncTokenIfNeeded() = Unit
}
