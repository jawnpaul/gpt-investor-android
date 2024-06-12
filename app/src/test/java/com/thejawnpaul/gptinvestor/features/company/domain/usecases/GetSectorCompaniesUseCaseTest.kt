package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
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

class GetSectorCompaniesUseCaseTest {

    @MockK
    lateinit var repository: ICompanyRepository

    private lateinit var useCase: GetSectorCompaniesUseCase
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var sector: String

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        sector = "Technology"
        useCase = GetSectorCompaniesUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getCompaniesInSector`() = runTest {
        coEvery { repository.getCompaniesInSector(sector = sector) } returns flow {
            emit(Either.Right(listOf()))
        }

        useCase.run(params = sector)

        coVerify(exactly = 1) { repository.getCompaniesInSector(sector = sector) }
    }

    @Test
    fun `should return companyList when successful`() = runTest {
        coEvery { repository.getCompaniesInSector(sector = sector) } returns flow {
            emit(
                Either.Right(
                    listOf()
                )
            )
        }

        repository.getCompaniesInSector(sector).collect {
            assertThat(it.isRight).isTrue()
        }
    }
}
