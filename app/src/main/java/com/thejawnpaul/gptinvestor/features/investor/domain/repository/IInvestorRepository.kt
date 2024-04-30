package com.thejawnpaul.gptinvestor.features.investor.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.FinalAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.GetPdfRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SimilarCompanies
import kotlinx.coroutines.flow.Flow

interface IInvestorRepository {
    suspend fun getSimilarCompanies(request: SimilarCompanyRequest): Flow<Either<Failure, SimilarCompanies>>

    suspend fun compareCompany(request: CompareCompaniesRequest): Flow<Either<Failure, String>>

    suspend fun getSentimentAnalysis(request: SentimentAnalysisRequest): Flow<Either<Failure, String>>

    suspend fun getAnalystRating(ticker: String): Flow<Either<Failure, String>>

    suspend fun getIndustryAnalysis(ticker: String): Flow<Either<Failure, String>>

    suspend fun getFinalAnalysis(request: FinalAnalysisRequest): Flow<Either<Failure, String>>

    suspend fun downloadAnalysisPdf(request: GetPdfRequest): Flow<Either<Failure, String>>
}
