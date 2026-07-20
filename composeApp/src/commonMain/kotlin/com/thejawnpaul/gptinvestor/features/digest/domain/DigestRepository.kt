package com.thejawnpaul.gptinvestor.features.digest.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.DigestResponse
import org.koin.core.annotation.Singleton

interface DigestRepository {
    suspend fun getDailyDigest(): Result<DigestResponse>
}

@Singleton(binds = [DigestRepository::class])
class DigestRepositoryImpl(private val apiService: KtorApiService) : DigestRepository {
    override suspend fun getDailyDigest(): Result<DigestResponse> = try {
        val response = apiService.getDailyDigest()
        if (response.isSuccessful) {
            response.body?.let { data ->
                Result.success(data)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch daily digest"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
