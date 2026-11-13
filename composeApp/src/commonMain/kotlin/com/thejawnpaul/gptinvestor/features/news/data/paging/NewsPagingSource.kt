package com.thejawnpaul.gptinvestor.features.news.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.news.data.remote.toDomain
import com.thejawnpaul.gptinvestor.features.news.data.remote.toMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NewsPagingSource(
    private val apiService: KtorApiService,
    private val ioDispatcher: CoroutineDispatcher,
    private val filter: String,
    private val onFirstPage: (NewsFeedMeta) -> Unit
) : PagingSource<Int, Story>() {

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }

    private var asOf: Long? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> = withContext(ioDispatcher) {
        try {
            val page = params.key ?: 1
            val response = apiService.getNewsFeed(filter = filter, page = page, pageSize = PAGE_SIZE, asOf = asOf)
            val body = response.body
            if (response.isSuccessful && body != null) {
                if (page == 1) {
                    asOf = body.latestPublishedAt.takeIf { it > 0L }
                    onFirstPage(body.toMeta())
                }
                val stories = body.stories.map { it.toDomain() }
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (stories.size < PAGE_SIZE) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to fetch news: ${response.code}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            LoadResult.Error(e)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
