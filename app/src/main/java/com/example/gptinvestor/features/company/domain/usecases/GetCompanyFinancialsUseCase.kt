package com.example.gptinvestor.features.company.domain.usecases

import com.example.gptinvestor.core.baseusecase.BaseUseCase
import com.example.gptinvestor.core.di.IoDispatcher
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetCompanyFinancialsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: ICompanyRepository
) : BaseUseCase<String, Unit>(coroutineScope, dispatcher) {

    override suspend fun run(params: String): Flow<Either<Failure, Unit>> {
        return repository.getCompanyFinancials(params)
    }
}