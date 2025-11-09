package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.thejawnpaul.gptinvestor.core.di.IosDispatcher
import org.koin.dsl.module

val investorUseCaseModule = module {
    factory<CompareCompaniesUseCase> {
        CompareCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<DownloadPdfUseCase> {
        DownloadPdfUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetAnalystRatingUseCase> {
        GetAnalystRatingUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetCompanySentimentUseCase> {
        GetCompanySentimentUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetFinalRatingUseCase> {
        GetFinalRatingUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetIndustryRatingUseCase> {
        GetIndustryRatingUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }

    factory<GetSimilarCompaniesUseCase> {
        GetSimilarCompaniesUseCase(
            get(IosDispatcher),
            get(),
            get()
        )
    }
}