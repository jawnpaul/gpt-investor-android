package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompanyFinancialsUseCase @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, coroutineScope: CoroutineScope, private val repository: ICompanyRepository) :
    BaseUseCase<String, CompanyFinancials>(coroutineScope, dispatcher) {

    override suspend fun run(params: String): Flow<Either<Failure, CompanyFinancials>> = repository.getCompanyFinancials(params)
}
