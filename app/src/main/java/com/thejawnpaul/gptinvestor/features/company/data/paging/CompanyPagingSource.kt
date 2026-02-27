package com.thejawnpaul.gptinvestor.features.company.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.company.data.local.model.PriceChange
import com.thejawnpaul.gptinvestor.features.company.domain.model.Company
import timber.log.Timber

class CompanyPagingSource(
    private val apiService: KtorApiService,
    private val query: String? = null,
    private val sector: String? = null
) : PagingSource<Int, Company>() {

    override fun getRefreshKey(state: PagingState<Int, Company>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Company> {
        return try {
            val page = params.key ?: 1
            val pageSize = PAGE_SIZE

            val response = apiService.getPagedCompanies(
                query = query,
                page = page,
                sector = sector,
                pageSize = pageSize
            )

            if (response.isSuccessful) {
                response.body?.let { data ->
                    val companies = data.data.map { remote ->
                        with(remote) {
                            Company(
                                ticker = ticker,
                                summary = summary,
                                name = name,
                                logo = logoUrl,
                                change =  PriceChange(change = 0f, date = 1L),
                            )
                        }
                    }

                    val nextKey = if (companies.isEmpty() || companies.size < pageSize) {
                        null
                    } else {
                        page + 1
                    }

                    val prevKey = if (page == 1) null else page - 1

                    LoadResult.Page(
                        data = companies,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                } ?: LoadResult.Error(Exception("Empty response body"))
            } else {
                LoadResult.Error(Exception("Failed to fetch companies: ${response.code}"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
