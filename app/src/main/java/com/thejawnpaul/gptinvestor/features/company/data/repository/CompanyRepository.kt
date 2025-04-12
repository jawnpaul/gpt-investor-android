package com.thejawnpaul.gptinvestor.features.company.data.repository

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyDetailRemoteResponse
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyFinancialsRequest
import com.thejawnpaul.gptinvestor.features.company.data.remote.model.CompanyPriceRequest
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import com.thejawnpaul.gptinvestor.features.company.domain.model.CompanyFinancials
import com.thejawnpaul.gptinvestor.features.company.domain.model.SearchCompanyQuery
import com.thejawnpaul.gptinvestor.features.company.domain.model.SectorInput
import com.thejawnpaul.gptinvestor.features.company.domain.model.TrendingCompany
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class CompanyRepository @Inject constructor(
    private val apiService: ApiService,
    private val companyDao: CompanyDao,
    private val analyticsLogger: AnalyticsLogger
) :
    ICompanyRepository {

    override suspend fun getAllCompanies(): Flow<Either<Failure, List<Company>>> = flow {
        try {
            // emit local
            val local =
                companyDao.getAllCompanies().map { companyEntity -> companyEntity.toDomainObject() }
            if (local.isNotEmpty()) {
                emit(Either.Right(local))
            }

            val response = apiService.getCompanies()
            if (response.isSuccessful) {
                response.body()?.let {
                    val remoteEntities = it.map { companyRemote -> companyRemote.toEntity() }

                    if (local.isEmpty()) {
                        companyDao.insertAll(remoteEntities)
                    } else {
                        // update with new data coming from remote
                        val updatedEntities = remoteEntities.map { entity ->
                            companyDao.getCompany(entity.ticker)
                                .copy(
                                    summary = entity.summary
                                )
                        }
                        companyDao.updateCompanies(updatedEntities)
                    }

                    val updatedList = companyDao.getAllCompanies()
                        .map { companyEntity -> companyEntity.toDomainObject() }
                    emit(Either.Right(updatedList))
                } ?: emit(Either.Left(Failure.DataError))
            }
            updateCompanyList()?.let {
                emit(Either.Right(it))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getAllSector(): Flow<Either<Failure, List<SectorInput>>> = flow {
        val list = listOf(SectorInput.AllSector)
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
                response.body()?.let {
                    emit(Either.Right(it))
                    analyticsLogger.logEvent(
                        eventName = "Company Selected",
                        params = mapOf("company_ticker" to ticker, "company_name" to it.name)
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.ServerError))
        }
    }

    override suspend fun getCompanyFinancials(ticker: String): Flow<Either<Failure, CompanyFinancials>> = flow {
        try {
            val request = CompanyFinancialsRequest(ticker = ticker, years = 1)
            val response = apiService.getCompanyFinancials(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    val domainObject = it.toDomainObject()
                    emit(Either.Right(domainObject))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
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
                eventName = "Industry Category Selected",
                params = mapOf("industry_name" to sector)
            )
        }
    }

    override suspend fun getTrendingCompanies(): Flow<Either<Failure, List<TrendingCompany>>> = flow {
        try {
            val response = apiService.getTrendingTickers()
            if (response.isSuccessful) {
                response.body()?.let { trendingRemoteList ->
                    val trendingCompanies = trendingRemoteList.map { remote ->
                        with(remote) {
                            TrendingCompany(
                                tickerSymbol = tickerSymbol,
                                companyName = name,
                                percentageChange = percentageChange,
                                imageUrl = logo
                            )
                        }
                    }.sortedByDescending { it.change }.take(20)

                    emit(Either.Right(trendingCompanies))
                }
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
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
            Timber.e(e.stackTraceToString())
            emit(Either.Left(Failure.DataError))
        }
    }

    private suspend fun updateCompanyList(): List<Company>? {
        try {
            val tickers = companyDao.getAllCompanies().map { it.ticker }
            val batches = tickers.chunked(100)
            val start = System.currentTimeMillis()
            batches.forEach { batch ->
                val request = CompanyPriceRequest(tickers = batch)
                val response = apiService.getCompanyPrice(request)
                if (response.isSuccessful) {
                    response.body()?.let { responseList ->
                        val entities = responseList.map { priceResponse ->
                            companyDao.getCompany(priceResponse.ticker)
                                .copy(
                                    currentPrice = priceResponse.price,
                                    priceChange = PriceChange(
                                        change = priceResponse.change,
                                        date = System.currentTimeMillis()
                                    )
                                )
                        }
                        companyDao.updateCompanies(entities)
                    }
                }
            }
            val end = System.currentTimeMillis()
            // This takes about 30 seconds to complete
            Timber.e("Elapsed: ${end - start}")
            return companyDao.getAllCompanies().map { it.toDomainObject() }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            return null
        }
    }
}
