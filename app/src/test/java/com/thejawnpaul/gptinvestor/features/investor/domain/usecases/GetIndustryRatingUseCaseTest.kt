package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
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

class GetIndustryRatingUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var ticker: String
    private lateinit var useCase: GetIndustryRatingUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        ticker = "AAPL"
        useCase = GetIndustryRatingUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getIndustryAnalysis`() = runTest {
        coEvery { repository.getIndustryAnalysis(ticker) } returns flow { emit(Either.Right("")) }

        useCase.run(params = ticker)

        coVerify(exactly = 1) { repository.getIndustryAnalysis(ticker) }
    }

    @Test
    fun `should return industry analysis when successful`() = runTest {
        coEvery { repository.getIndustryAnalysis(ticker) } returns flow { emit(Either.Right("")) }

        repository.getIndustryAnalysis(ticker).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(String::class.java)
        }
    }
}
