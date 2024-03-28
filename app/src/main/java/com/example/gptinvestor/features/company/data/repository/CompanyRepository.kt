package com.example.gptinvestor.features.company.data.repository

import com.example.gptinvestor.core.api.ApiService
import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.company.domain.model.Company
import com.example.gptinvestor.features.company.domain.model.SectorInput
import com.example.gptinvestor.features.company.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CompanyRepository @Inject constructor(private val apiService: ApiService) :
    ICompanyRepository {

    override suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>> = flow {
        try {
            // emit local
            val response = apiService.getCompanies()
            if (response.isSuccessful) {
                response.body()?.let {
                    val companies = it.map { companyRemote -> companyRemote.toDomainObject() }
                    emit(Either.Right(companies))
                } ?: emit(Either.Left(Failure.DataError))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
        }
    }

    override suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>> = flow {
        val list = listOf(SectorInput.AllSector)
        val others = listOf(
            SectorInput.CustomSector("Technology"),
            SectorInput.CustomSector("Manufacturing"),
            SectorInput.CustomSector("Sports"),
            SectorInput.CustomSector("Security"),
            SectorInput.CustomSector("Fashion")
        )

        emit(Either.Right(list + others))
    }
}
