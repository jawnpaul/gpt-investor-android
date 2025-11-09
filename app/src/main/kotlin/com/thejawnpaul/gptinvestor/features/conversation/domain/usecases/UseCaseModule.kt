package com.thejawnpaul.gptinvestor.features.conversation.domain.usecases

import com.thejawnpaul.gptinvestor.core.di.IosDispatcher
import org.koin.dsl.module

val conversationUseCaseModule = module {
    factory<GetDefaultPromptResponseUseCase> {
        GetDefaultPromptResponseUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
    factory<GetDefaultPromptsUseCase> {
        GetDefaultPromptsUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetInputPromptUseCase> {
        GetInputPromptUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
}