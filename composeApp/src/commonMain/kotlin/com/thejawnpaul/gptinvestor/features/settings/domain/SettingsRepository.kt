package com.thejawnpaul.gptinvestor.features.settings.domain

import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.TIER_PREMIUM
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.UpdateUserSettingsRequest
import com.thejawnpaul.gptinvestor.features.settings.data.remote.model.UserSettingsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Singleton

interface SettingsRepository {
    val isPremium: StateFlow<Boolean>
    suspend fun getUserSettings(): Result<UserSettingsResponse>
    suspend fun updateUserSettings(request: UpdateUserSettingsRequest): Result<UserSettingsResponse>
}

@Singleton(binds = [SettingsRepository::class])
class SettingsRepositoryImpl(private val apiService: KtorApiService) : SettingsRepository {
    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun getUserSettings(): Result<UserSettingsResponse> = try {
        val response = apiService.getUserSettings()
        if (response.isSuccessful) {
            response.body?.let { data ->
                _isPremium.value = data.tier == TIER_PREMIUM
                Result.success(data)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to fetch user settings"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateUserSettings(request: UpdateUserSettingsRequest): Result<UserSettingsResponse> = try {
        val response = apiService.updateUserSettings(request)
        if (response.isSuccessful) {
            response.body?.let { data ->
                Result.success(data)
            } ?: Result.failure(Exception("Empty response body"))
        } else {
            Result.failure(Exception("Failed to update user settings"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
