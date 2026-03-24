package com.thejawnpaul.gptinvestor.features.billing.domain.repository

import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import kotlinx.coroutines.flow.Flow

interface IBillingRepository {

    suspend fun connect(): Result<Unit>

    suspend fun launchPurchaseFlow(platformContext: PlatformContext, productId: String): BillingResult

    fun currentPurchases(): Flow<List<BillingPurchase>>

    suspend fun acknowledgePurchase(purchase: BillingPurchase): Result<Unit>

    suspend fun syncPurchaseToBackend(purchase: BillingPurchase): Result<Unit>
}
