package com.example.gptinvestor.features.company.domain.repository

import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.domain.model.Company
import com.example.gptinvestor.features.company.domain.model.SectorInput
import kotlinx.coroutines.flow.Flow

interface ICompanyRepository {
    suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>>

    suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>>

    suspend fun getCompany(ticker: String): Flow<Either<Failure, Company>>
}
