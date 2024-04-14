package com.thejawnpaul.gptinvestor.features.company.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import kotlinx.coroutines.flow.Flow

interface ICompanyRepository {
    suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>>

    suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>>

    suspend fun getCompany(ticker: String): Flow<Either<Failure, Company>>

    suspend fun getCompanyFinancials(ticker: String): Flow<Either<Failure, CompanyFinancials>>

    suspend fun getCompaniesInSector(sector: String?): Flow<Either<Failure, List<Company>>>
}
