package com.thejawnpaul.gptinvestor.features.news.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.di.IoDispatcher
import com.thejawnpaul.gptinvestor.core.functional.Either
import com.thejawnpaul.gptinvestor.core.functional.Failure
import com.thejawnpaul.gptinvestor.features.news.data.paging.NewsPagingSource
import com.thejawnpaul.gptinvestor.features.news.data.remote.toDomain
import com.thejawnpaul.gptinvestor.features.news.domain.NewsFailure
import com.thejawnpaul.gptinvestor.features.news.domain.NewsRepository
import com.thejawnpaul.gptinvestor.features.news.domain.model.Explainer
import com.thejawnpaul.gptinvestor.features.news.domain.model.NewsFeedMeta
import com.thejawnpaul.gptinvestor.features.news.domain.model.Story
import io.ktor.http.HttpStatusCode
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton(binds = [NewsRepository::class])
class NewsRepositoryImpl(
    private val apiService: KtorApiService,
    @Provided @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NewsRepository {

    override fun getFeedPaged(filter: String, onFirstPage: (NewsFeedMeta) -> Unit): Flow<PagingData<Story>> = Pager(
        config = PagingConfig(
            pageSize = NewsPagingSource.PAGE_SIZE,
            enablePlaceholders = false,
            initialLoadSize = NewsPagingSource.PAGE_SIZE
        ),
        pagingSourceFactory = {
            NewsPagingSource(
                apiService = apiService,
                ioDispatcher = ioDispatcher,
                filter = filter,
                onFirstPage = onFirstPage
            )
        }
    ).flow

    override suspend fun getStory(id: String): Either<Failure, Story> = withContext(ioDispatcher) {
        try {
            val response = apiService.getStory(id)
            val body = response.body
            when {
                response.isSuccessful && body != null -> Either.Right(body.toDomain())
                response.code == HttpStatusCode.NotFound.value -> Either.Left(NewsFailure.NotFound)
                else -> Either.Left(Failure.ServerError)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            Either.Left(e.toNewsFailure())
        }
    }

    override suspend fun getTopStory(): Either<Failure, Story?> = withContext(ioDispatcher) {
        try {
            val response = apiService.getTopStory()
            val body = response.body
            if (response.isSuccessful && body != null) {
                Either.Right(body.story?.toDomain())
            } else {
                Either.Left(Failure.ServerError)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            Either.Left(e.toNewsFailure())
        }
    }

    override suspend fun getExplainer(storyId: String): Either<Failure, Explainer> = withContext(ioDispatcher) {
        try {
            val response = apiService.getExplainer(storyId)
            val body = response.body
            when {
                response.isSuccessful && body != null -> Either.Right(body.toDomain())
                response.code == HttpStatusCode.NotFound.value -> Either.Left(NewsFailure.NotFound)
                else -> Either.Left(Failure.ServerError)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            Either.Left(e.toNewsFailure())
        }
    }

    override suspend fun getShareImage(url: String): Either<Failure, ByteArray> = withContext(ioDispatcher) {
        try {
            val response = apiService.getShareImage(url)
            val body = response.body
            if (response.isSuccessful && body != null && body.isNotEmpty()) {
                Either.Right(body)
            } else {
                Either.Left(Failure.ServerError)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e(e.stackTraceToString())
            Either.Left(e.toNewsFailure())
        }
    }
}
