package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
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

class GetCompanyFinancialsUseCaseTest {

    @MockK
    lateinit var repository: ICompanyRepository

    private lateinit var useCase: GetCompanyFinancialsUseCase
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var ticker: String

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        ticker = "AAPL"
        useCase = GetCompanyFinancialsUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getCompanyFinancials`() = runTest {
        coEvery { repository.getCompanyFinancials(ticker) } returns flow {
            emit(
                Either.Right(
                    CompanyFinancials(1F, 1F, 1F, 1L, 1F, "", 1L, emptyList(), "", "", "")
                )
            )
        }

        useCase.run(params = ticker)

        coVerify(exactly = 1) { repository.getCompanyFinancials(ticker) }
    }

    @Test
    fun `should return CompanyFinancials when successful`() = runTest {
        coEvery { repository.getCompanyFinancials(ticker) } returns flow {
            emit(
                Either.Right(
                    CompanyFinancials(1F, 1F, 1F, 1L, 1F, "", 1L, emptyList(), "", "", "")
                )
            )
        }

        repository.getCompanyFinancials(ticker).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(CompanyFinancials::class.java)
        }
    }

    @Test
    fun `should return unauthorized when auth fails`() = runTest {
        coEvery { repository.getCompanyFinancials(ticker) } returns flow {
            emit(
                Either.Left(
                    Failure.UnAuthorizedError
                )
            )
        }

        repository.getCompanyFinancials(ticker).collect {
            assertThat(it.isLeft).isTrue()
            it as Either.Left
            assertThat(it.a).isInstanceOf(Failure.UnAuthorizedError::class.java)
        }
    }

    @Test
    fun `should return server error when error occurs`() = runTest {
        coEvery { repository.getCompanyFinancials(ticker) } returns flow {
            emit(
                Either.Left(
                    Failure.ServerError
                )
            )
        }

        repository.getCompanyFinancials(ticker).collect {
            assertThat(it.isLeft).isTrue()
            it as Either.Left
            assertThat(it.a).isInstanceOf(Failure.ServerError::class.java)
        }
    }
}
