package com.thejawnpaul.gptinvestor.features.company.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.utility.toHttpsUrl
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.data.paging.CompanyPagingSource
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyLogosRequest
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyBrief
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.model.TrendingCompany
import com.thejawnpaul.gptinvestor.features.company.domain.model.toBrief
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton(binds = [ICompanyRepository::class])
class CompanyRepository(
    private val apiService: KtorApiService,
    private val companyDao: CompanyDao,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val appPreferences: AppPreferences
) : ICompanyRepository {

    override suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>> = flow {
        val list = listOf(
            SectorInput.CustomSector(
                sectorName = "Top picks",
                sectorKey = "top-picks",
                hasImage = true
            ),
            SectorInput.AllSector
        )
        val companyList = companyDao.getAllCompanies()
        val default = listOf(
            SectorInput.CustomSector("Technology", sectorKey = "technology"),
            SectorInput.CustomSector("Healthcare", sectorKey = "healthcare"),
            SectorInput.CustomSector("Energy", sectorKey = "energy"),
            SectorInput.CustomSector("Financial Services", sectorKey = "financial-services"),
            SectorInput.CustomSector("Real Estate", sectorKey = "real-estate")
        )

        if (companyList.isEmpty()) {
            emit(Either.Right(list + default))
        } else {
            val sectors =
                companyList.map { it.toSector() }.filterNot { it.sectorName.lowercase() == "n/a" }
                    .toSet()

            emit(Either.Right(list + sectors))
        }
    }

    override suspend fun getCompany(ticker: String): Flow<Either<Failure, CompanyDetailRemoteResponse>> = flow {
        try {
            val response =
                apiService.getCompanyInfo(request = CompanyDetailRemoteRequest(ticker = ticker))
            if (response.isSuccessful) {
                response.body?.let {
                    emit(Either.Right(it))
                    analyticsLogger.logEvent(
                        eventName = "company-selected",
                        params = mapOf(
                            "company_ticker" to ticker,
                            "company_name" to it.name.toString()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getCompanyFinancials(ticker: String): Flow<Either<Failure, CompanyFinancials>> = flow {
        try {
            val request = CompanyFinancialsRequest(ticker = ticker, years = 1)
            val response = apiService.getCompanyFinancials(request)
            if (response.isSuccessful) {
                response.body?.let {
                    val domainObject = it.toDomainObject()
                    emit(Either.Right(domainObject))
                }
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getCompaniesInSector(sector: String?): Flow<Either<Failure, List<Company>>> = flow {
        if (sector == null) {
            val companies = companyDao.getAllCompanies().map { it.toDomainObject() }
            emit(Either.Right(companies))
        } else {
            val companies = companyDao.getCompaniesInSector(sector).map { it.toDomainObject() }
            emit(Either.Right(companies))
            analyticsLogger.logEvent(
                eventName = "industry-category-selected",
                params = mapOf("industry_name" to sector)
            )
        }
    }

    override suspend fun getTrendingCompanies(): Flow<Either<Failure, List<TrendingCompany>>> = flow {
        try {
            val response = apiService.getTrendingTickers()
            if (response.isSuccessful) {
                response.body?.let { trendingRemoteList ->
                    val trendingCompanies = trendingRemoteList.map { remote ->
                        with(remote) {
                            TrendingCompany(
                                tickerSymbol = tickerSymbol,
                                companyName = name,
                                percentageChange = percentageChange,
                                imageUrl = logo.toHttpsUrl()
                            )
                        }
                    }.sortedByDescending { it.change }.take(20)

                    emit(Either.Right(trendingCompanies))
                }
            } else {
                emit(Either.Left(Failure.ServerError))
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun searchCompany(query: SearchCompanyQuery): Flow<Either<Failure, List<Company>>> = flow {
        try {
            if (query.sector == null) {
                // search entire table
                val companies =
                    companyDao.searchAllCompanies(query = query.query)
                        .map { it.toDomainObject() }
                emit(Either.Right(companies))
            } else {
                // filter by column name
                val companies =
                    companyDao.searchCompaniesInSector(
                        query = query.query.trim(),
                        sectorKey = query.sector
                    )
                        .map { it.toDomainObject() }
                emit(Either.Right(companies))
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    override suspend fun getCompanyBrief(ticker: String): Flow<Either<Failure, CompanyBrief>> = flow {
        val isGuest = appPreferences.isGuestLoggedIn.first() == true
        val userType = if (isGuest) "guest" else "logged_in"

        analyticsLogger.logEvent(
            eventName = "brief-started",
            params = mapOf("ticker" to ticker, "user_type" to userType)
        )

        val startMs = Clock.System.now().toEpochMilliseconds()
        try {
            val response = apiService.getCompanyBrief(ticker)
            if (response.isSuccessful) {
                response.body?.let {
                    analyticsLogger.logEvent(
                        eventName = "brief-completed",
                        params = mapOf(
                            "ticker" to ticker,
                            "duration_ms" to (Clock.System.now().toEpochMilliseconds() - startMs),
                            "user_type" to userType
                        )
                    )
                    emit(Either.Right(it.toBrief()))
                }
            } else {
                analyticsLogger.logEvent(
                    eventName = "brief-failed",
                    params = mapOf(
                        "ticker" to ticker,
                        "error_type" to response.code,
                        "user_type" to userType
                    )
                )
                emit(Either.Left(Failure.ServerError))
            }
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            analyticsLogger.logEvent(
                eventName = "brief-failed",
                params = mapOf(
                    "ticker" to ticker,
                    "error_type" to e.message.toString(),
                    "user_type" to userType
                )
            )
            emit(Either.Left(Failure.ServerError))
        }
    }

    override fun searchCompaniesPaged(query: SearchCompanyQuery): Flow<PagingData<Company>> = Pager(
        config = PagingConfig(
            pageSize = CompanyPagingSource.PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = CompanyPagingSource.PAGE_SIZE
        ),
        pagingSourceFactory = {
            CompanyPagingSource(
                apiService = apiService,
                query = query.query.ifBlank { null },
                sector = query.sector
            )
        }
    ).flow

    override suspend fun searchCompaniesFromNetwork(query: String): Flow<Either<Failure, List<Company>>> = flow {
        val response = apiService.getPagedCompanies(query = query, page = 1, pageSize = 1)
        if (response.isSuccessful) {
            val companies = response.body?.data?.map { remote ->
                Company(
                    ticker = remote.ticker,
                    summary = remote.summary,
                    name = remote.name,
                    logo = remote.logoUrl.toHttpsUrl(),
                    change = PriceChange(change = 0f, date = 1L)
                )
            } ?: emptyList()
            emit(Either.Right(companies))
        } else {
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getCompanyLogos(tickers: List<String>): Either<Failure, Map<String, String>> {
        val response = apiService.getCompanyLogos(CompanyLogosRequest(tickers))
        return if (response.isSuccessful) {
            Either.Right(response.body?.associate { it.ticker to (it.logoUrl?.toHttpsUrl() ?: "") } ?: emptyMap())
        } else {
            Either.Left(Failure.ServerError)
        }
    }
}
