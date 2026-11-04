package com.thejawnpaul.gptinvestor.features.news.data

import com.google.common.truth.Truth.assertThat
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.news.data.remote.NewsTopicRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.PricePointRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.StoryPriceRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.StoryRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.StoryStockRemote
import com.thejawnpaul.gptinvestor.features.news.data.remote.TopStoryRemote
import com.thejawnpaul.gptinvestor.features.news.domain.NewsFailure
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceBasis
import com.thejawnpaul.gptinvestor.features.news.domain.model.StoryPriceStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Test

class NewsRepositoryImplTest {

    private val apiService: KtorApiService = mockk()
    private val repository = NewsRepositoryImpl(apiService, UnconfinedTestDispatcher())

    private fun <T> ok(body: T) = KtorResponse(isSuccessful = true, body = body, errorBody = null, code = 200)

    private fun <T> error(code: Int) = KtorResponse<T>(isSuccessful = false, body = null, errorBody = "", code = code)

    private val stock = StoryStockRemote(
        ticker = "DAL",
        name = "Delta Air Lines",
        exchange = "NYSE",
        logoUrl = "http://logo/dal.png",
        reason = "Jet fuel is a big cost",
        onWatchlist = true,
        history = listOf(PricePointRemote("2026-10-20", 55.0), PricePointRemote("2026-10-21", 56.63)),
        historyStoryIndex = 1,
        price = StoryPriceRemote(
            basis = "story_time",
            atStory = 56.63,
            atStoryTime = 1_000L,
            now = 58.40,
            nowTime = 2_000L,
            changePercent = 3.13,
            status = "live"
        )
    )

    private val story = StoryRemote(
        id = "s1",
        title = "Oil prices fall after OPEC+ agrees to pump more",
        summary = " ",
        link = "https://news/s1",
        publisher = "Reuters",
        publishedAt = 1_000L,
        imageUrl = "",
        topics = listOf(NewsTopicRemote(id = "energy", label = "Energy")),
        connectedCount = 6,
        movedCount = 3,
        movesBasis = "story_time",
        moved = listOf(stock),
        barelyMoved = listOf(stock.copy(ticker = "KO", name = "Coca-Cola", onWatchlist = false))
    )

    @Test
    fun `should map the backend story without deriving anything`() = runTest {
        coEvery { apiService.getStory("s1") } returns ok(story)

        val mapped = (repository.getStory("s1") as Either.Right).b

        assertThat(mapped.topics.single().label).isEqualTo("Energy")
        assertThat(mapped.summary).isNull()
        assertThat(mapped.imageUrl).isNull()
        assertThat(mapped.movedCount).isEqualTo(3)
        assertThat(mapped.connectedCount).isEqualTo(6)
        assertThat(mapped.movesBasis).isEqualTo(StoryPriceBasis.STORY_TIME)
        assertThat(mapped.moved.single().logoUrl).isEqualTo("https://logo/dal.png")
        assertThat(mapped.moved.single().price.status).isEqualTo(StoryPriceStatus.LIVE)
        assertThat(mapped.barelyMoved.single().ticker).isEqualTo("KO")
        assertThat(mapped.moved.single().history.map { it.close }).containsExactly(55.0, 56.63).inOrder()
        assertThat(mapped.moved.single().historyStoryIndex).isEqualTo(1)
    }

    @Test
    fun `should report a missing story as not found`() = runTest {
        coEvery { apiService.getStory("old") } returns error(404)

        assertThat(repository.getStory("old")).isEqualTo(Either.Left(NewsFailure.NotFound))
    }

    @Test
    fun `should report other server errors as server errors`() = runTest {
        coEvery { apiService.getStory("s1") } returns error(500)

        assertThat(repository.getStory("s1")).isEqualTo(Either.Left(Failure.ServerError))
    }

    @Test
    fun `should report a thrown request as a network failure`() = runTest {
        coEvery { apiService.getStory(any()) } throws RuntimeException("offline")

        assertThat(repository.getStory("s1")).isEqualTo(Either.Left(Failure.NetworkConnection))
    }

    @Test
    fun `should report an unreadable response as a data error`() = runTest {
        coEvery { apiService.getStory(any()) } throws SerializationException("bad json")

        assertThat(repository.getStory("s1")).isEqualTo(Either.Left(Failure.DataError))
    }

    @Test
    fun `should return no top story on a quiet day`() = runTest {
        coEvery { apiService.getTopStory() } returns ok(TopStoryRemote(story = null))

        assertThat(repository.getTopStory()).isEqualTo(Either.Right(null))
    }
}
