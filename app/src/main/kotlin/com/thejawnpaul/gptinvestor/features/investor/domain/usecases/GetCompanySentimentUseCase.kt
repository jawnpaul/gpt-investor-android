package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompanySentimentUseCase @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, coroutineScope: CoroutineScope, private val repository: IInvestorRepository) :
    BaseUseCase<SentimentAnalysisRequest, String>(coroutineScope, dispatcher) {

    override suspend fun run(params: SentimentAnalysisRequest): Flow<Either<Failure, String>> = repository.getSentimentAnalysis(params)
}
