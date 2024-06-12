package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.google.common.truth.Truth
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
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

class GetAllCompaniesUseCaseTest {

    @MockK
    lateinit var repository: ICompanyRepository

    private lateinit var useCase: GetAllCompaniesUseCase
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        useCase = GetAllCompaniesUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getAllCompanies`() = runTest {
        coEvery { repository.getAllCompanies() } returns flow {
            emit(Either.Right(listOf()))
        }

        useCase.run(params = GetAllCompaniesUseCase.None())

        coVerify(exactly = 1) { repository.getAllCompanies() }
    }

    @Test
    fun `should return companyList when successful`() = runTest {
        coEvery { repository.getAllCompanies() } returns flow {
            emit(Either.Right(listOf(Company("", "", "", ""))))
        }

        repository.getAllCompanies().collect {
            Truth.assertThat(it.isRight).isTrue()
            it as Either.Right
            Truth.assertThat(it.b).isNotEmpty()
        }
    }

    @Test
    fun `should return unauthorized when auth fails`() = runTest {
        coEvery { repository.getAllCompanies() } returns flow {
            emit(Either.Left(Failure.UnAuthorizedError))
        }

        repository.getAllCompanies().collect {
            Truth.assertThat(it.isLeft).isTrue()
            it as Either.Left
            Truth.assertThat(it.a).isInstanceOf(Failure.UnAuthorizedError::class.java)
        }
    }

    @Test
    fun `should return server error when server error occurs`() = runTest {
        coEvery { repository.getAllCompanies() } returns flow {
            emit(Either.Left(Failure.ServerError))
        }

        repository.getAllCompanies().collect {
            Truth.assertThat(it.isLeft).isTrue()
            it as Either.Left
            Truth.assertThat(it.a).isInstanceOf(Failure.ServerError::class.java)
        }
    }

    @Test
    fun `should return data error when data error occurs`() = runTest {
        coEvery { repository.getAllCompanies() } returns flow {
            emit(Either.Left(Failure.DataError))
        }

        repository.getAllCompanies().collect {
            Truth.assertThat(it.isLeft).isTrue()
            it as Either.Left
            Truth.assertThat(it.a).isInstanceOf(Failure.DataError::class.java)
        }
    }
}
