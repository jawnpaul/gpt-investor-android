package com.thejawnpaul.gptinvestor.features.billing.data.repository

import co.touchlab.kermit.Logger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.data.StoreKitProvider
import com.thejawnpaul.gptinvestor.features.billing.data.remote.VerifyPurchaseRequest
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton
import kotlin.coroutines.resume

@Singleton(binds = [IBillingRepository::class])
class IosBillingRepository(
    private val storeKitProvider: StoreKitProvider,
    private val apiService: KtorApiService,
    private val tokenStorage: TokenStorage,
    @Provided private val scope: CoroutineScope
) : IBillingRepository {

    private val _purchaseVerified = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val purchaseVerified: SharedFlow<Unit> = _purchaseVerified.asSharedFlow()

    private val purchasesFlow = MutableStateFlow<List<BillingPurchase>>(emptyList())

    override suspend fun connect(): Result<Unit> = Result.success(Unit)

    override suspend fun launchPurchaseFlow(
        platformContext: PlatformContext,
        productId: String,
        trigger: String,
    ): BillingResult =
        suspendCancellableCoroutine { cont ->
            storeKitProvider.purchase(
                productId = productId,
                onSuccess = { purchaseToken, pid, orderId ->
                    val purchase = BillingPurchase(
                        purchaseToken = purchaseToken,
                        productId = pid,
                        orderId = orderId
                    )
                    if (cont.isActive) cont.resume(BillingResult.Success)
                    scope.launch(Dispatchers.Default) {
                        val syncResult = syncPurchaseToBackend(purchase)
                        if (syncResult.isSuccess) {
                            _purchaseVerified.tryEmit(Unit)
                            refreshAccessTokenAfterPurchaseSync()
                        }
                        refreshPurchases()
                    }
                },
                onError = { message ->
                    Logger.d { "StoreKit purchase error: $message" }
                    if (cont.isActive) cont.resume(BillingResult.Error(message))
                },
                onCancelled = {
                    Logger.d { "User cancelled StoreKit purchase" }
                    if (cont.isActive) cont.resume(BillingResult.UserCancelled)
                }
            )
        }

    override fun currentPurchases(): Flow<List<BillingPurchase>> = purchasesFlow.asStateFlow()

    // No-op: StoreKit 2 transaction.finish() is called by Swift before onSuccess fires.
    override suspend fun acknowledgePurchase(purchase: BillingPurchase): Result<Unit> = Result.success(Unit)
    override suspend fun syncPurchaseToBackend(purchase: BillingPurchase): Result<Unit> = try {
        val request = VerifyPurchaseRequest(
            purchaseToken = purchase.purchaseToken,
            subscriptionId = purchase.productId
        )
        val response = apiService.verifyAppStorePurchase(request)
        val isVerificationSuccessful = response.isSuccessful && response.body?.success == true
        if (isVerificationSuccessful) {
            Logger.d { "Purchase sync succeeded for productId=${purchase.productId}" }
            Result.success(Unit)
        } else {
            Logger.e {
                "Purchase sync failed for productId=${purchase.productId}: code=${response.code}, " +
                    "successFlag=${response.body?.success}, error=${response.errorBody}"
            }
            Result.failure(Exception("Backend verification failed: ${response.code}"))
        }
    } catch (e: Exception) {
        Logger.e(e) { "Error acknowledging purchase: ${e.message}" }
        Result.failure(e)
    }

    private suspend fun refreshAccessTokenAfterPurchaseSync(): Result<Unit> = try {
        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Logger.e { "Skipping token refresh after purchase sync: refresh token missing" }
            return Result.failure(Exception("Refresh token missing"))
        }
        val refreshResponse = apiService.refreshAccessToken(refreshToken)
        val newAccessToken = refreshResponse.body?.accessToken
        if (refreshResponse.isSuccessful && !newAccessToken.isNullOrBlank()) {
            tokenStorage.saveAccessToken(newAccessToken)
            Logger.d { "Access token refreshed after purchase sync" }
            Result.success(Unit)
        } else {
            Logger.e {
                "Access token refresh failed after purchase sync: code=${refreshResponse.code}, " +
                    "error=${refreshResponse.errorBody}"
            }
            Result.failure(Exception("Refresh failed: ${refreshResponse.code}"))
        }
    } catch (e: Exception) {
        Logger.e(e) { "Error refreshing access token after purchase sync: ${e.message}" }
        Result.failure(e)
    }

    private suspend fun refreshPurchases() {
        suspendCancellableCoroutine<Unit> { cont ->
            storeKitProvider.fetchCurrentEntitlements { purchaseTokens, productIds ->
                purchasesFlow.value = purchaseTokens.zip(productIds).map { (token, productId) ->
                    BillingPurchase(purchaseToken = token, productId = productId, orderId = null)
                }
                cont.resume(Unit)
            }
        }
    }
}
