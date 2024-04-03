package com.example.gptinvestor.features.investor.domain.repository

import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.example.gptinvestor.features.investor.domain.model.SimilarCompanies
import kotlinx.coroutines.flow.Flow

interface IInvestorRepository {
    suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, List<String>>>
    suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, SimilarCompanies>>
}
