package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.presentation.model.CompanyFinancialsPresentation
import com.thejawnpaul.gptinvestor.features.investor.domain.model.CompareCompaniesRequest
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

class CompareCompaniesUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository

    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var useCase: CompareCompaniesUseCase
    private lateinit var request: CompareCompaniesRequest

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        request = CompareCompaniesRequest(
            CompanyFinancialsPresentation(
                "",
                "",
                "",
                "",
                "",
                "",
                emptyList(),
                "",
                "",
                ""
            ),
            "",
            ""
        )
        useCase = CompareCompaniesUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call compareCompany`() = runTest {
        coEvery { repository.compareCompany(request) } returns flow { emit(Either.Right("")) }

        useCase.run(params = request)

        coVerify(exactly = 1) { repository.compareCompany(request) }
    }

    @Test
    fun `should return unavailable error when getCompanyFinancials fails`() = runTest {
        coEvery { repository.compareCompany(request) } returns flow { emit(Either.Left(Failure.UnAvailableError)) }

        repository.compareCompany(request).collect {
            assertThat(it.isLeft).isTrue()
            it as Either.Left
            assertThat(it.a).isInstanceOf(Failure.UnAvailableError::class.java)
        }
    }

    @Test
    fun `should return company comparison when successful`() = runTest {
        coEvery { repository.compareCompany(request) } returns flow { emit(Either.Right("")) }

        repository.compareCompany(request).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(String::class.java)
        }
    }
}
