package com.thejawnpaul.gptinvestor.features.tidbit.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import timber.log.Timber

class TidbitPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val preferences: GPTInvestorPreferences,
    private val tidbitType: TidbitType
) : PagingSource<Int, Tidbit>() {
    override fun getRefreshKey(state: PagingState<Int, Tidbit>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tidbit> {
        return try {
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val userId = preferences.userId.first() ?: ""

            val response = when (tidbitType) {
                TidbitType.ALL -> apiService.getAllTidbit(page, pageSize, userId)
                TidbitType.TRENDING -> apiService.getTrendingTidbit(page, pageSize, userId)
                TidbitType.LATEST -> apiService.getLatestTidbits(page, pageSize, userId)
            }

            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val tidbits = data.data.map { remote ->
                        with(remote) {
                            Tidbit(
                                id = id,
                                previewUrl = previewUrl,
                                title = title,
                                content = content,
                                originalAuthor = originalAuthor,
                                category = category,
                                mediaUrl = mediaUrl,
                                sourceUrl = source,
                                type = type,
                                isLiked = isLiked ?: false,
                                isBookmarked = isBookmarked ?: false
                            )
                        }
                    }

                    val nextKey = if (tidbits.isEmpty() || tidbits.size < pageSize) {
                        null
                    } else {
                        page + 1
                    }

                    val prevKey = if (page == 1) null else page - 1

                    LoadResult.Page(
                        data = tidbits,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                } ?: LoadResult.Error(Exception("Empty response body"))
            } else {
                LoadResult.Error(Exception("Failed to fetch tidbits: ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            LoadResult.Error(e)
        }
    }
}
