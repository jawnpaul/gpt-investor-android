package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetSectorCompaniesUseCase(
    dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: ICompanyRepository
) :
    BaseUseCase<String?, List<Company>>(coroutineScope, dispatcher) {
    override suspend fun run(params: String?): Flow<Either<Failure, List<Company>>> =
        repository.getCompaniesInSector(params)
}
