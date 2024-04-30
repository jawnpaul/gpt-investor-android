package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SimilarCompanies
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetSimilarCompaniesUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: IInvestorRepository
) : BaseUseCase<SimilarCompanyRequest, SimilarCompanies>(coroutineScope, dispatcher) {

    override suspend fun run(params: SimilarCompanyRequest): Flow<Either<Failure, SimilarCompanies>> {
        return repository.getSimilarCompanies(params)
    }
}
