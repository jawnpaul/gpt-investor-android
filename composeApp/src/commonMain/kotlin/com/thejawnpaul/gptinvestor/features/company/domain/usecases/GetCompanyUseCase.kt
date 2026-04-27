package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.thejawnpaul.gptinvestor.core.baseusecase.BaseUseCase
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Provided

@Factory
class GetCompanyUseCase(
    @Provided @param:IoDispatcher private val dispatcher: CoroutineDispatcher,
    @Provided coroutineScope: CoroutineScope,
    private val repository: ICompanyRepository
) : BaseUseCase<String, CompanyDetailRemoteResponse>(coroutineScope, dispatcher) {

    override suspend fun run(params: String): Flow<Either<Failure, CompanyDetailRemoteResponse>> =
        repository.getCompany(params)
}
