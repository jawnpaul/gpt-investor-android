package com.thejawnpaul.gptinvestor.features.tidbit.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfigClient
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.tidbit.data.paging.TidbitPagingSource
import com.thejawnpaul.gptinvestor.features.tidbit.data.paging.TidbitType
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeRequest
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

interface TidbitRepository {
    suspend fun getTodayTidbit(): Result<Tidbit>
    suspend fun getTidbit(id: String): Result<Tidbit>

    suspend fun getShareableLink(id: String): Result<String>

    suspend fun likeTidbit(tidbitId: String): Result<Unit>
    suspend fun unlikeTidbit(tidbitId: String): Result<Unit>
    suspend fun bookmarkTidbit(tidbitId: String): Result<Unit>
    suspend fun removeBookmark(tidbitId: String): Result<Unit>

    fun getAllTidbitsPaged(): Flow<PagingData<Tidbit>>

    fun getTrendingTidbitsPaged(): Flow<PagingData<Tidbit>>

    fun getNewTidbitsPaged(): Flow<PagingData<Tidbit>>

    fun getBookmarkedTidbitsPaged(): Flow<PagingData<Tidbit>>
}

@Singleton(binds = [TidbitRepository::class])
class TidbitRepositoryImpl(
    private val apiService: KtorApiService,
    private val remoteConfig: RemoteConfigClient,
    @Provided private val analyticsLogger: AnalyticsLogger,
    private val preferences: AppPreferences
) : TidbitRepository {
    override suspend fun getTodayTidbit(): Result<Tidbit> = try {
        val response = apiService.getTodayTidbit()
        if (response.isSuccessful) {
            response.body?.let { data ->
                val res = with(data) {
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
                Result.success(res)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch today's tidbit"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTidbit(id: String): Result<Tidbit> = try {
        val response = apiService.getSingleTidbit(id = id)
        if (response.isSuccessful) {
            response.body?.let { data ->
                val res = with(data) {
                    Tidbit(
                        id = id,
                        previewUrl = previewUrl,
                        title = title,
                        mediaUrl = mediaUrl,
                        content = content,
                        originalAuthor = originalAuthor,
                        category = category,
                        sourceUrl = source,
                        type = type,
                        isLiked = isLiked ?: false,
                        isBookmarked = isBookmarked ?: false,
                        summary = summary
                    )
                }
                Result.success(res)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch today's tidbit"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getShareableLink(id: String): Result<String> = try {
        val domain = remoteConfig.fetchAndActivateStringValue(Constants.WEBSITE_DOMAIN_KEY)

        val urlToShare = "${domain}tidbit/$id"

        val data = "Check out this tidbit from GPT Investor: \n${urlToShare}\n"
        analyticsLogger.logEvent(
            eventName = "share",
            params = mapOf(
                "tidbit_id" to id,
                "content_type" to "tidbit"
            )
        )

        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun likeTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.likeTidbit(
            request = TidbitLikeRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to like tidbit"))
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun unlikeTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.unlikeTidbit(
            request = TidbitLikeRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to like tidbit"))
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun bookmarkTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.bookmarkTidbit(
            request = TidbitBookmarkRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to like tidbit"))
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun removeBookmark(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.removeBookmark(
            request = TidbitBookmarkRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to like tidbit"))
        }
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override fun getAllTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, TidbitType.ALL)
        }
    ).flow

    override fun getTrendingTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, TidbitType.TRENDING)
        }
    ).flow

    override fun getNewTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, TidbitType.LATEST)
        }
    ).flow

    override fun getBookmarkedTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, TidbitType.SAVED)
        }
    ).flow
}
