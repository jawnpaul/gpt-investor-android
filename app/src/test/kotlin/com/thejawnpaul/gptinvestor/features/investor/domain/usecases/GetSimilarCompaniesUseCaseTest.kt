package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.features.investor.data.remote.SimilarCompanyRequest
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SimilarCompanies
import com.thejawnpaul.gptinvestor.features.investor.domain.repository.IInvestorRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSimilarCompaniesUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var useCase: GetSimilarCompaniesUseCase
    private lateinit var request: SimilarCompanyRequest

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        request = SimilarCompanyRequest("", "", "", "", emptyList())
        useCase = GetSimilarCompaniesUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getSimilarCompanies`() = runTest {
        coEvery { repository.getSimilarCompanies(request) } returns flow {
            emit(
                Either.Right(
                    SimilarCompanies("", emptyList())
                )
            )
        }

        useCase.run(params = request)

        coVerify(exactly = 1) { repository.getSimilarCompanies(request) }
    }

    @Test
    fun `should return similar companies when successful`() = runTest {
        coEvery { repository.getSimilarCompanies(request) } returns flow {
            emit(
                Either.Right(
                    SimilarCompanies("", emptyList())
                )
            )
        }

        repository.getSimilarCompanies(request).collect {
            Truth.assertThat(it.isRight).isTrue()
            it as Either.Right
            Truth.assertThat(it.b).isInstanceOf(SimilarCompanies::class.java)
        }
    }
}
