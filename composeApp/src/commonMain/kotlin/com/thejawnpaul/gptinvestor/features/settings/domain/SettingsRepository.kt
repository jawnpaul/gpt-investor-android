package com.thejawnpaul.gptinvestor.features.settings.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.UserSettingsResponse
import org.koin.core.annotation.Singleton

interface SettingsRepository {
    suspend fun getUserSettings(): Result<UserSettingsResponse>
}

@Singleton(binds = [SettingsRepository::class])
class SettingsRepositoryImpl(private val apiService: KtorApiService) : SettingsRepository {
    override suspend fun getUserSettings(): Result<UserSettingsResponse> = try {
        val response = apiService.getUserSettings()
        if (response.isSuccessful) {
            response.body?.let { data ->
                Result.success(data)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch user settings"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
