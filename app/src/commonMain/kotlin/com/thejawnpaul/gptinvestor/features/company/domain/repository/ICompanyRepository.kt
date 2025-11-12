package com.thejawnpaul.gptinvestor.features.company.domain.repository

import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.model.TrendingCompany
import kotlinx.coroutines.flow.Flow

interface ICompanyRepository {
    suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>>

    suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>>

    suspend fun getCompany(ticker: String): Flow<Either<Failure, CompanyDetailRemoteResponse>>

    suspend fun getCompanyFinancials(ticker: String): Flow<Either<Failure, CompanyFinancials>>

    suspend fun getCompaniesInSector(sector: String?): Flow<Either<Failure, List<Company>>>

    suspend fun getTrendingCompanies(): Flow<Either<Failure, List<TrendingCompany>>>

    suspend fun searchCompany(query: SearchCompanyQuery): Flow<Either<Failure, List<Company>>>
}
