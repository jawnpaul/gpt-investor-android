package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.TrendingCompany
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetTrendingCompaniesUseCase(
    dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: ICompanyRepository
) :
    BaseUseCase<GetTrendingCompaniesUseCase.None, List<TrendingCompany>>(
        coroutineScope,
        dispatcher
    ) {
    class None

    override suspend fun run(params: None): Flow<Either<Failure, List<TrendingCompany>>> =
        repository.getTrendingCompanies()
}
