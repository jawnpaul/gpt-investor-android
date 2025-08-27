package com.thejawnpaul.gptinvestor.features.tidbit.domain

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.core.remoteconfig.RemoteConfig
import com.thejawnpaul.gptinvestor.core.utility.Constants
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import javax.inject.Inject

interface TidbitRepository {
    suspend fun getTodayTidbit(): Result<Tidbit>
    suspend fun getTidbit(id: String): Result<Tidbit>
    suspend fun getAllTidbits(): Result<List<Tidbit>>

    suspend fun getShareableLink(id: String): Result<String>
}

class TidbitRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val remoteConfig: RemoteConfig,
    private val analyticsLogger: AnalyticsLogger
) :
    TidbitRepository {
    override suspend fun getTodayTidbit(): Result<Tidbit> {
        return try {
            val response = apiService.getTodayTidbit()
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
                            type = type
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
            val response = apiService.getSingleTidbit(id)
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
                            type = type
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
            val response = apiService.getAllTidbit()
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
                                type = type
                            )
                        }
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
}
