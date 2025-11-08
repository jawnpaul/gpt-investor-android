package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CompareCompaniesUseCase @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, coroutineScope: CoroutineScope, private val repository: IInvestorRepository) :
    BaseUseCase<CompareCompaniesRequest, String>(coroutineScope, dispatcher) {

    override suspend fun run(params: CompareCompaniesRequest): Flow<Either<Failure, String>> = repository.compareCompany(params)
}
