package com.example.gptinvestor.features.investor.domain.repository

import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.investor.domain.model.SectorInput
import kotlinx.coroutines.flow.Flow

interface ICompanyRepository {
    suspend fun getAllCompanies(): Flow<Either<Failure, List<String>>>

    suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>>
}
