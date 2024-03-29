package com.example.gptinvestor.features.company.data.repository

import com.example.gptinvestor.core.api.ApiService
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.data.local.dao.CompanyDao
import com.example.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.example.gptinvestor.features.company.domain.model.Company
import com.example.gptinvestor.features.company.domain.model.CompanyFinancials
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CompanyRepository @Inject constructor(
    private val apiService: ApiService,
    private val companyDao: CompanyDao
) :
    ICompanyRepository {

    override suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>> = flow {
        try {
            // emit local
            val local =
                companyDao.getAllCompanies().map { companyEntity -> companyEntity.toDomainObject() }
            if (local.isNotEmpty()) {
                emit(Either.Right(local))
            }

            val response = apiService.getCompanies()
            if (response.isSuccessful) {
                response.body()?.let {
                    val entities = it.map { companyRemote -> companyRemote.toEntity() }
                    companyDao.insertAll(entities)

                    val updatedList = companyDao.getAllCompanies()
                        .map { companyEntity -> companyEntity.toDomainObject() }
                    emit(Either.Right(updatedList))
                } ?: emit(Either.Left(Failure.DataError))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>> = flow {
        val list = listOf(SectorInput.AllSector)
        // TODO: Show a predefined list
        val others = listOf(
            SectorInput.CustomSector("Technology"),
            SectorInput.CustomSector("Manufacturing"),
            SectorInput.CustomSector("Sports"),
            SectorInput.CustomSector("Security"),
            SectorInput.CustomSector("Fashion")
        )

        emit(Either.Right(list + others))
    }

    override suspend fun getCompany(ticker: String): Flow<Either<Failure, Company>> = flow {
        val company = companyDao.getCompany(ticker)
        emit(Either.Right(company.toDomainObject()))
    }

    override suspend fun getCompanyFinancials(ticker: String): Flow<Either<Failure, CompanyFinancials>> = flow {
        try {
            val request = CompanyFinancialsRequest(ticker = ticker, years = 1)
            val response = apiService.getCompanyFinancials(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    val domainObject = it.toDomainObject()
                    emit(Either.Right(domainObject))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }
}
