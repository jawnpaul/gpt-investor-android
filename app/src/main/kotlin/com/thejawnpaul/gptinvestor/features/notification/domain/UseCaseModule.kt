package com.thejawnpaul.gptinvestor.features.notification.domain

import org.koin.dsl.module

val notificationUseCaseModule = module {
    factory<TokenSyncManager> { TokenSyncManager(get()) }
}