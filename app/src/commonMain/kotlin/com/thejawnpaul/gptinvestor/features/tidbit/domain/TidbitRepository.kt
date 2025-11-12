package com.thejawnpaul.gptinvestor.features.tidbit.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.tidbit.data.paging.TidbitPagingSource
import com.thejawnpaul.gptinvestor.features.tidbit.data.paging.TidbitType
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitBookmarkRequest
import com.thejawnpaul.gptinvestor.features.tidbit.data.remote.TidbitLikeRequest
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

class TidbitRepositoryImpl(
    private val apiService: ApiService,
    private val remoteConfig: RemoteConfig,
    private val analyticsLogger: AnalyticsLogger,
    private val preferences: GPTInvestorPreferences
) : TidbitRepository {
    override suspend fun getTodayTidbit(): Result<Tidbit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.getTodayTidbit(userId = userId)
        val res = with(response) {
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
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getTidbit(id: String): Result<Tidbit> = try {
        val userId = preferences.userId.first() ?: ""
        val response = apiService.getSingleTidbit(id = id, userId = userId)
        val res = with(response) {
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
                "content_type" to "tidbit"
            )
        )

        Result.success(data)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun likeTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        apiService.likeTidbit(
            request = TidbitLikeRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun unlikeTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        apiService.unlikeTidbit(
            request = TidbitLikeRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun bookmarkTidbit(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        apiService.bookmarkTidbit(
            request = TidbitBookmarkRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )

        Result.success(Unit)
    } catch (e: Exception) {
        Logger.e(e.stackTraceToString())
        Result.failure(e)
    }

    override suspend fun removeBookmark(tidbitId: String): Result<Unit> = try {
        val userId = preferences.userId.first() ?: ""
        apiService.removeBookmark(
            request = TidbitBookmarkRequest(
                userId = userId,
                tidbitId = tidbitId
            )
        )
        Result.success(Unit)
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
            TidbitPagingSource(apiService, preferences, TidbitType.ALL)
        }
    ).flow

    override fun getTrendingTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, preferences, TidbitType.TRENDING)
        }
    ).flow

    override fun getNewTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, preferences, TidbitType.LATEST)
        }
    ).flow

    override fun getBookmarkedTidbitsPaged(): Flow<PagingData<Tidbit>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = {
            TidbitPagingSource(apiService, preferences, TidbitType.SAVED)
        }
    ).flow
}
