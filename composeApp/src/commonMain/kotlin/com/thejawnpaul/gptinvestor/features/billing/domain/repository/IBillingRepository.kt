package com.thejawnpaul.gptinvestor.features.billing.domain.repository

import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IBillingRepository {

    val purchaseVerified: SharedFlow<Unit>

    suspend fun connect(): Result<Unit>

    suspend fun launchPurchaseFlow(platformContext: PlatformContext, productId: String, trigger: String): BillingResult

    fun currentPurchases(): Flow<List<BillingPurchase>>

    suspend fun acknowledgePurchase(purchase: BillingPurchase): Result<Unit>

    suspend fun syncPurchaseToBackend(purchase: BillingPurchase): Result<Unit>
}
