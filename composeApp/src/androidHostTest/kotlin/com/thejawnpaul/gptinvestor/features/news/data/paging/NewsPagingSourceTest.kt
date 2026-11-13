package com.thejawnpaul.gptinvestor.features.news.data.paging

import androidx.paging.PagingSource
import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.features.news.data.remote.NewsFeedRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.NewsTopicRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.StoryRemote
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NewsPagingSourceTest {

    private val apiService: KtorApiService = mockk()
    private val metas = mutableListOf<NewsFeedMeta>()
    private val source = NewsPagingSource(
        apiService = apiService,
        ioDispatcher = UnconfinedTestDispatcher(),
        filter = "energy",
        onFirstPage = { metas += it }
    )

    private fun stories(count: Int, from: Int = 0) = (from until from + count).map {
        StoryRemote(
            id = "s$it",
            title = "Story $it",
            link = "https://news/$it",
            publisher = "Reuters",
            publishedAt = 1L
        )
    }

    private fun page(page: Int, stories: List<StoryRemote>) = KtorResponse(
        isSuccessful = true,
        body = NewsFeedRemote(
            page = page,
            pageSize = NewsPagingSource.PAGE_SIZE,
            topics = listOf(NewsTopicRemote(id = "energy", label = "Energy")),
            latestPublishedAt = 500L,
            personalised = true,
            stories = stories
        ),
        errorBody = null,
        code = 200
    )

    private fun refresh() = PagingSource.LoadParams.Refresh<Int>(key = null, loadSize = 20, placeholdersEnabled = false)

    @Test
    fun `should request the first page for the filter and report its metadata`() = runTest {
        coEvery { apiService.getNewsFeed("energy", 1, NewsPagingSource.PAGE_SIZE, null) } returns page(1, stories(20))

        val result = source.load(refresh()) as PagingSource.LoadResult.Page

        assertThat(result.prevKey).isNull()
        assertThat(result.nextKey).isEqualTo(2)
        assertThat(metas.single().latestPublishedAt).isEqualTo(500L)
        assertThat(metas.single().topics.single().label).isEqualTo("Energy")
        assertThat(result.data).hasSize(20)
    }

    @Test
    fun `should freeze later pages at the first page's time and stop after a short page`() = runTest {
        coEvery { apiService.getNewsFeed("energy", 1, NewsPagingSource.PAGE_SIZE, null) } returns page(1, stories(20))
        coEvery { apiService.getNewsFeed("energy", 2, NewsPagingSource.PAGE_SIZE, 500L) } returns
            page(2, stories(5, 20))
        source.load(refresh())
        metas.clear()

        val result = source.load(
            PagingSource.LoadParams.Append(key = 2, loadSize = 20, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertThat(result.prevKey).isEqualTo(1)
        assertThat(result.nextKey).isNull()
        assertThat(metas).isEmpty()
    }

    @Test
    fun `should return an error when the request fails`() = runTest {
        coEvery { apiService.getNewsFeed(any(), any(), any(), any()) } throws RuntimeException("offline")

        assertThat(source.load(refresh())).isInstanceOf(PagingSource.LoadResult.Error::class.java)
    }
}
