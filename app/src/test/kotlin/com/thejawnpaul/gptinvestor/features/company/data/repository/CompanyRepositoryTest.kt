package com.thejawnpaul.gptinvestor.features.company.data.repository

import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import io.mockk.impl.annotations.MockK
import okhttp3.mockwebserver.MockWebServer

class CompanyRepositoryTest {

    @MockK
    lateinit var companyRepository: ICompanyRepository
    private lateinit var mockWebServer: MockWebServer
    private lateinit var gptInvestorDatabase: GPTInvestorDatabase

   /* @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = RequestDispatcher().RequestDispatcher()
        mockWebServer.start()

        gptInvestorDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GPTInvestorDatabase::class.kotlin
        ).allowMainThreadQueries().build()
        companyRepository = CompanyRepository(
            makeTestApiService(mockWebServer),
            gptInvestorDatabase.companyDao()
        )
    }*/
}
