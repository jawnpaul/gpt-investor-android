package com.thejawnpaul.gptinvestor.features.tidbit.domain

import com.thejawnpaul.gptinvestor.core.api.ApiService
import com.thejawnpaul.gptinvestor.features.tidbit.domain.model.Tidbit
import javax.inject.Inject

interface TidbitRepository {
    suspend fun getTodayTidbit(): Result<Tidbit>
    suspend fun getTidbit(id: String): Result<Tidbit>
    suspend fun getAllTidbits(): Result<List<Tidbit>>
}

class TidbitRepositoryImpl @Inject constructor(private val apiService: ApiService) :
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
                            content = content
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
                            content = content
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
                                content = content
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
}
