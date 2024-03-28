package com.example.gptinvestor.features.company.domain.usecases

import com.example.gptinvestor.core.baseusecase.BaseUseCase
import com.example.gptinvestor.core.di.IoDispatcher
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class GetAllSectorUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: ICompanyRepository
) : BaseUseCase<GetAllSectorUseCase.None, List<SectorInput>>(coroutineScope, dispatcher) {
    class None

    override suspend fun run(params: None): Flow<Either<Failure, List<SectorInput>>> {
        return repository.getAllSector()
    }
}
