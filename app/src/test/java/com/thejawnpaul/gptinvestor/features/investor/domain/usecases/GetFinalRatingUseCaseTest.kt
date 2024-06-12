package com.thejawnpaul.gptinvestor.features.investor.domain.usecases

import com.google.common.truth.Truth
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.features.investor.domain.model.FinalAnalysisRequest
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

class GetFinalRatingUseCaseTest {

    @MockK
    lateinit var repository: IInvestorRepository
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope
    private lateinit var request: FinalAnalysisRequest
    private lateinit var useCase: GetFinalRatingUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        request = FinalAnalysisRequest("", "", "", "", "")
        useCase = GetFinalRatingUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getFinalAnalysis`() = runTest {
        coEvery { repository.getFinalAnalysis(request) } returns flow { emit(Either.Right("")) }

        useCase.run(params = request)

        coVerify(exactly = 1) { repository.getFinalAnalysis(request) }
    }

    @Test
    fun `should return final rating when successful`() = runTest {
        coEvery { repository.getFinalAnalysis(request) } returns flow { emit(Either.Right("")) }

        repository.getFinalAnalysis(request).collect {
            Truth.assertThat(it.isRight).isTrue()
            it as Either.Right
            Truth.assertThat(it.b).isInstanceOf(String::class.java)
        }
    }
}
