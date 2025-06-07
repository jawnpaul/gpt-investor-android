package com.thejawnpaul.gptinvestor.features.conversation.domain.repository

import com.thejawnpaul.gptinvestor.core.preferences.GPTInvestorPreferences
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AnotherModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.AvailableModel
import com.thejawnpaul.gptinvestor.features.conversation.domain.model.DefaultModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import timber.log.Timber

interface ModelsRepository {
    suspend fun getAvailableModels(): Result<List<AvailableModel>>
    suspend fun putUserOnModelWaitlist(modelId: String, reasons: List<String> = emptyList()): Result<Unit>
}

class ModelsRepositoryImpl @Inject constructor(
    private val gptInvestorPreferences: GPTInvestorPreferences
) : ModelsRepository {
    override suspend fun getAvailableModels(): Result<List<AvailableModel>> {
        return try {
            val isUserOnWaitlist = gptInvestorPreferences.isUserOnModelWaitlist.first()
            val models = buildList {
                add(DefaultModel())
                add(
                    AnotherModel(
                        isDefault = false,
                        modelTitle = "Intermediate",
                        modelSubtitle = "For in-depth advance use",
                        canUpgrade = isUserOnWaitlist != true,
                        isUserOnWaitlist = isUserOnWaitlist,
                        modelId = "gemini-2.5-flash"
                    )
                )
            }
            Result.success(models)
        } catch (e: Exception) {
            Timber.e(e.stackTrace.toString())
            Result.failure(e)
        }
    }

    override suspend fun putUserOnModelWaitlist(modelId: String, reasons: List<String>): Result<Unit> {
        return try {
            // Make api call to waitlist end point
            gptInvestorPreferences.setIsUserOnModelWaitlist(isOnWaitlist = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e.stackTrace.toString())
            Result.failure(e)
        }
    }
}
