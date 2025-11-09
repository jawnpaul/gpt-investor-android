package com.thejawnpaul.gptinvestor.features.history.domain.usecases

import com.thejawnpaul.gptinvestor.core.di.IosDispatcher
import org.koin.dsl.module

val historyUseCaseModule = module {
    factory<GetAllHistoryUseCase> {
        GetAllHistoryUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetSingleHistoryUseCase> {
        GetSingleHistoryUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
}