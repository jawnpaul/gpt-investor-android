package com.thejawnpaul.gptinvestor.features.tidbit.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit

class TidbitPagingSource(private val apiService: KtorApiService, private val tidbitType: TidbitType) :
    PagingSource<Int, Tidbit>() {
    override fun getRefreshKey(state: PagingState<Int, Tidbit>): Int? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tidbit> = try {
        val page = params.key ?: 1
        val pageSize = PAGE_SIZE

        val response = when (tidbitType) {
            TidbitType.ALL -> apiService.getAllTidbit(page, pageSize)
            TidbitType.TRENDING -> apiService.getTrendingTidbit(page, pageSize)
            TidbitType.LATEST -> apiService.getLatestTidbits(page, pageSize)
            TidbitType.SAVED -> apiService.getSavedTidbits(page, pageSize)
        }

        if (response.isSuccessful) {
            response.body?.let { data ->
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
                            isBookmarked = isBookmarked ?: false,
                            summary = summary
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
            LoadResult.Error(Exception("Failed to fetch tidbits: ${response.code}"))
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        LoadResult.Error(e)
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
