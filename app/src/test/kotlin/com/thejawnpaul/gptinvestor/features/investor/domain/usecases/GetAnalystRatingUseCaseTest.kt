package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
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

class GetAnalystRatingUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var useCase: GetAnalystRatingUseCase
    private lateinit var ticker: String

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        ticker = "BAX"
        useCase = GetAnalystRatingUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getAnalystRating`() = runTest {
        coEvery { repository.getAnalystRating(ticker) } returns flow { emit(Either.Right("")) }

        useCase.run(params = ticker)

        coVerify(exactly = 1) { repository.getAnalystRating(ticker) }
    }

    @Test
    fun `should return analyst rating when successful`() = runTest {
        coEvery { repository.getAnalystRating(ticker) } returns flow { emit(Either.Right("")) }

        repository.getAnalystRating(ticker).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(String::class.java)
        }
    }

    @Test
    fun `should return server error when exception occurs`() = runTest {
        coEvery { repository.getAnalystRating(ticker) } returns flow { emit(Either.Left(Failure.ServerError)) }

        repository.getAnalystRating(ticker).collect {
            assertThat(it.isLeft).isTrue()
            it as Either.Left
            assertThat(it.a).isInstanceOf(Failure.ServerError::class.java)
        }
    }

    @Test
    fun `should return data error when response body isn't parsed`() = runTest {
        coEvery { repository.getAnalystRating(ticker) } returns flow { emit(Either.Left(Failure.DataError)) }

        repository.getAnalystRating(ticker).collect {
            assertThat(it.isLeft).isTrue()
            it as Either.Left
            assertThat(it.a).isInstanceOf(Failure.DataError::class.java)
        }
    }
}
