package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.investor.domain.model.GetPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class DownloadPdfUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    coroutineScope: CoroutineScope,
    private val repository: IInvestorRepository
) : BaseUseCase<GetPdfRequest, String>(coroutineScope, dispatcher) {

    override suspend fun run(params: GetPdfRequest): Flow<Either<Failure, String>> {
        return repository.downloadAnalysisPdf(params)
    }
}
