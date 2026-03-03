package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.features.investor.domain.model.SentimentAnalysisRequest
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

class GetCompanySentimentUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var useCase: GetCompanySentimentUseCase
    private lateinit var request: SentimentAnalysisRequest

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        request = SentimentAnalysisRequest(ticker = "", news = emptyList())
        useCase = GetCompanySentimentUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getSentimentAnalysis`() = runTest {
        coEvery { repository.getSentimentAnalysis(request) } returns flow { emit(Either.Right("")) }

        useCase.run(params = request)

        coVerify(exactly = 1) { repository.getSentimentAnalysis(request) }
    }

    @Test
    fun `should return sentiment when successful`() = runTest {
        coEvery { repository.getSentimentAnalysis(request) } returns flow { emit(Either.Right("")) }

        repository.getSentimentAnalysis(request).collect {
            assertThat(it.isRight).isTrue()
            it as Either.Right
            assertThat(it.b).isInstanceOf(String::class.java)
        }
    }
}
