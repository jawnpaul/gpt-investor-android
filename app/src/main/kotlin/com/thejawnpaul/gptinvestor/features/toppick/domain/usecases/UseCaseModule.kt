package com.thejawnpaul.gptinvestor.features.toppick.domain.usecases

import com.thejawnpaul.gptinvestor.core.di.IosDispatcher
import org.koin.dsl.module

val toppickUseCaseModule = module {
    factory<GetTopPicksUseCase> {
        GetTopPicksUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<SaveTopPickUseCase> {
        SaveTopPickUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<ShareTopPickUseCase> {
        ShareTopPickUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetLocalTopPicksUseCase> {
        GetLocalTopPicksUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetSavedTopPicksUseCase> {
        GetSavedTopPicksUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetSingleTopPickUseCase> {
        GetSingleTopPickUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<RemoveTopPickFromSavedUseCase> {
        RemoveTopPickFromSavedUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
}