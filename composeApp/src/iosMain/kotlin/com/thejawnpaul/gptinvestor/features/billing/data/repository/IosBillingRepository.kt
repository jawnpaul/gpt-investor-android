package com.thejawnpaul.gptinvestor.features.billing.data.repository

import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.annotation.Singleton

@Singleton(binds = [IBillingRepository::class])
class IosBillingRepository : IBillingRepository {
    override suspend fun connect(): Result<Unit> = Result.success(Unit)

    override suspend fun launchPurchaseFlow(platformContext: PlatformContext, productId: String): BillingResult =
        BillingResult.Error("Billing not supported on iOS")

    override fun currentPurchases(): Flow<List<BillingPurchase>> = flowOf(emptyList())

    override suspend fun acknowledgePurchase(purchase: BillingPurchase): Result<Unit> =
        Result.failure(IllegalStateException("Billing not supported on iOS"))

    override suspend fun syncPurchaseToBackend(purchase: BillingPurchase): Result<Unit> =
        Result.failure(IllegalStateException("Billing not supported on iOS"))
}
