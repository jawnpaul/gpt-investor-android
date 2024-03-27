package com.example.gptinvestor.features.investor.data.repository

import com.example.gptinvestor.core.functional.Either
import com.example.gptinvestor.core.functional.Failure
import com.example.gptinvestor.features.investor.domain.model.SectorInput
import com.example.gptinvestor.features.investor.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CompanyRepository @Inject constructor() : ICompanyRepository {
    override suspend fun getAllCompanies(): Flow<Either<Failure, List<String>>> = flow {
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
