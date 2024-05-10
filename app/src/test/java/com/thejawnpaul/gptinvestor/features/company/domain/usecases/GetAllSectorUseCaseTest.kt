package com.thejawnpaul.gptinvestor.features.company.domain.usecases

import com.google.common.truth.Truth
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
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

class GetAllSectorUseCaseTest {

    @MockK
    lateinit var repository: ICompanyRepository

    private lateinit var useCase: GetAllSectorUseCase
    private lateinit var dispatcher: TestDispatcher
    private lateinit var scope: TestScope

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcher = StandardTestDispatcher()
        scope = TestScope()
        useCase = GetAllSectorUseCase(dispatcher, scope, repository)
    }

    @Test
    fun `should call getAllSector`() = runTest {
        coEvery { repository.getAllSector() } returns flow { emit(Either.Right(listOf())) }

        useCase.run(params = GetAllSectorUseCase.None())
        coVerify(exactly = 1) { repository.getAllSector() }
    }

    @Test
    fun `should return a list of sectors`() = runTest {
        coEvery { repository.getAllSector() } returns flow { emit(Either.Right(listOf(SectorInput.CustomSector("", "")))) }

        repository.getAllSector().collect {
            Truth.assertThat(it.isRight).isTrue()
            it as Either.Right
            Truth.assertThat(it.b).isNotEmpty()
        }
    }
}
