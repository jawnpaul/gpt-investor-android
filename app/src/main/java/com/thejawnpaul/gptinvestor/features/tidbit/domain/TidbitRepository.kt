package com.thejawnpaul.gptinvestor.features.tidbit.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

interface TidbitRepository {
    suspend fun getTodayTidbit(): Result<Tidbit>
    suspend fun getTidbit(id: String): Result<Tidbit>
    suspend fun getAllTidbits(): Result<List<Tidbit>>

    suspend fun getShareableLink(id: String): Result<String>

    suspend fun getTrendingTidbits(): Result<List<Tidbit>>
    suspend fun getSavedTidbits(): Result<List<Tidbit>>
    suspend fun getNewTidbits(): Result<List<Tidbit>>

    suspend fun likeTidbit(tidbitId: String): Result<Unit>
    suspend fun unlikeTidbit(tidbitId: String): Result<Unit>
    suspend fun bookmarkTidbit(tidbitId: String): Result<Unit>
    suspend fun removeBookmark(tidbitId: String): Result<Unit>

    fun getAllTidbitsPaged(): Flow<PagingData<Tidbit>>
}

class TidbitRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val remoteConfig: RemoteConfig,
    private val analyticsLogger: AnalyticsLogger,
    private val preferences: GPTInvestorPreferences
) :
    TidbitRepository {
    override suspend fun getTodayTidbit(): Result<Tidbit> {
        return try {
            val userId = preferences.userId.first() ?: ""
            val response = apiService.getTodayTidbit(userId = userId)
            if (response.isSuccessful) {
                response.body()?.let { data ->
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
                            isBookmarked = isBookmarked ?: false
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
    }

    override suspend fun getTidbit(id: String): Result<Tidbit> {
        return try {
            val userId = preferences.userId.first() ?: ""
            val response = apiService.getSingleTidbit(id = id, userId = userId)
            if (response.isSuccessful) {
                response.body()?.let { data ->
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
                            isBookmarked = isBookmarked ?: false
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
    }

    override suspend fun getAllTidbits(): Result<List<Tidbit>> {
        return try {
            val userId = preferences.userId.first() ?: ""
            val response = apiService.getAllTidbit(userId = userId)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val res = data.data.map {
                        with(it) {
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
                    Result.success(res)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch today's tidbit"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getShareableLink(id: String): Result<String> {
        return try {
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
    }

    override suspend fun getTrendingTidbits(): Result<List<Tidbit>> {
        return try {
            val userId = preferences.userId.first() ?: ""
            val response = apiService.getTrendingTidbit(userId = userId)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val res = data.data.map {
                        with(it) {
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
                    Result.success(res)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch today's tidbit"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getSavedTidbits(): Result<List<Tidbit>> {
        return try {
            val userId = preferences.userId.first()
            val response = apiService.getSavedTidbits(userId = userId ?: "")
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val res = data.map {
                        with(it) {
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
                    Result.success(res)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch today's tidbit"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getNewTidbits(): Result<List<Tidbit>> {
        return try {
            val userId = preferences.userId.first() ?: ""
            val response = apiService.getLatestTidbits(userId = userId)
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    val res = data.data.map {
                        with(it) {
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
                    Result.success(res)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Failed to fetch today's tidbit"))
            }
        } catch (e: Exception) {
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun likeTidbit(tidbitId: String): Result<Unit> {
        return try {
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
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun unlikeTidbit(tidbitId: String): Result<Unit> {
        return try {
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
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun bookmarkTidbit(tidbitId: String): Result<Unit> {
        return try {
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
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun removeBookmark(tidbitId: String): Result<Unit> {
        return try {
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
            Timber.e(e.stackTraceToString())
            Result.failure(e)
        }
    }

    override fun getAllTidbitsPaged(): Flow<PagingData<Tidbit>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                TidbitPagingSource(apiService, preferences, TidbitType.ALL)
            }
        ).flow
    }
}
