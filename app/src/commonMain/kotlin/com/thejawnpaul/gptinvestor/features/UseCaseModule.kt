package com.thejawnpaul.gptinvestor.features

import com.thejawnpaul.gptinvestor.features.company.domain.usecases.companyUseCaseModule
import com.thejawnpaul.gptinvestor.features.conversation.domain.usecases.conversationUseCaseModule
import com.thejawnpaul.gptinvestor.features.history.domain.usecases.historyUseCaseModule
import com.thejawnpaul.gptinvestor.features.investor.domain.usecases.investorUseCaseModule
import com.thejawnpaul.gptinvestor.features.notification.domain.notificationUseCaseModule
import com.thejawnpaul.gptinvestor.features.toppick.domain.usecases.toppickUseCaseModule

val useCaseModules = listOf(
    companyUseCaseModule,
    conversationUseCaseModule,
    historyUseCaseModule,
    investorUseCaseModule,
    notificationUseCaseModule,
    toppickUseCaseModule
)
