package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.di.IosDispatcher
import org.koin.dsl.module

val companyUseCaseModule = module {
    factory<GetAllCompaniesUseCase> {
        GetAllCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetAllSectorUseCase> {
        GetAllSectorUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetCompanyDetailInputResponseUseCase> {
        GetCompanyDetailInputResponseUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetCompanyFinancialsUseCase> {
        GetCompanyFinancialsUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetCompanyUseCase> {
        GetCompanyUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetSectorCompaniesUseCase> {
        GetSectorCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetTrendingCompaniesUseCase> {
        GetTrendingCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<SearchCompaniesUseCase> {
        SearchCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
}