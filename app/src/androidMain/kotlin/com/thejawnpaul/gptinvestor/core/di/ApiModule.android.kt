package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.features.conversation.data.repository.FirebaseAiApi
import com.thejawnpaul.gptinvestor.features.conversation.domain.repository.IFirebaseAiApi
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformApiModule = module {
    singleOf(::FirebaseAiApi) bind IFirebaseAiApi::class
}