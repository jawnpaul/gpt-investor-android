package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
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

class GetCompanyUseCaseTest {

    @MockK
    lateinit var repository: ICompanyRepository

    private lateinit var ticker: String
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var useCase: GetCompanyUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        ticker = "BAX"
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        useCase = GetCompanyUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getCompany`() = runTest {
        coEvery { repository.getCompany(ticker) } returns flow {
            emit(
                Either.Right(
                    Company(
                        "",
                        "",
                        "",
                        ""
                    )
                )
            )
        }

        useCase.run(params = ticker)

        coVerify(exactly = 1) { repository.getCompany(ticker) }
    }

    @Test
    fun `should return Company`() = runTest {
        coEvery { repository.getCompany(ticker) } returns flow {
            emit(
                Either.Right(
                    Company(
                        "",
                        "",
                        "",
                        ""
                    )
                )
            )
        }

        repository.getCompany(ticker).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(Company::class.java)
        }
    }
}
