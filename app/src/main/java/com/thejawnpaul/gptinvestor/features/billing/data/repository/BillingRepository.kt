package com.thejawnpaul.gptinvestor.features.billing.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.features.billing.data.remote.VerifyPurchaseRequest
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult as DomainBillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: KtorApiService,
    private val scope: CoroutineScope
) : IBillingRepository, PurchasesUpdatedListener {


    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context)
            .setListener(this)
            .enablePendingPurchases(com.android.billingclient.api.PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
    }

    private val _purchases = MutableStateFlow<List<BillingPurchase>>(emptyList())

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    scope.launch(Dispatchers.IO) {
                        val domainPurchase = purchase.toDomainPurchase()
                        acknowledgePurchase(domainPurchase)
                        syncPurchaseToBackend(domainPurchase)
                        refreshPurchases()
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.d("User cancelled purchase")
            }
            else -> {
                Timber.e("Billing error: ${billingResult.debugMessage}")
            }
        }
    }

    override suspend fun connect(): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (billingClient.isReady) {
            Timber.d("Billing client is already ready")
            cont.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }
        Timber.d("Connecting to billing client...")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Timber.d("Billing setup finished: ${billingResult.responseCode}, ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    cont.resume(Result.success(Unit))
                } else {
                    cont.resume(Result.failure(Exception(billingResult.debugMessage)))
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.d("Billing service disconnected")
                if (cont.isActive) {
                    cont.resume(Result.failure(Exception("Billing service disconnected")))
                }
            }
        })
    }

    override suspend fun launchPurchaseFlow(activity: Activity, productId: String): DomainBillingResult {
        Timber.d("Launching purchase flow for: $productId")
        return withContext(Dispatchers.Main) {
            connect().getOrElse {
                Timber.e("Connection failed: ${it.message}")
                return@withContext DomainBillingResult.Error(it.message ?: "Billing unavailable")
            }
            Timber.d("Querying product details for: $productId")
            val productDetails = queryProductDetails(productId)
            if (productDetails == null) {
                Timber.e("Product details null for: $productId")
                return@withContext DomainBillingResult.Error("Product not found: $productId")
            }

            val productDetailsParamsList = productDetails.subscriptionOfferDetails?.let { offers ->
                if (offers.isEmpty()) {
                    Timber.d("No offers found for product, using basic details")
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                } else {
                    Timber.d("Found ${offers.size} offers, using first offerToken")
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .setOfferToken(offers.first().offerToken)
                            .build()
                    )
                }
            } ?: run {
                Timber.d("subscriptionOfferDetails is null, using basic details")
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
            }

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            Timber.d("Calling launchBillingFlow")
            val result = billingClient.launchBillingFlow(activity, billingFlowParams)
            Timber.d("launchBillingFlow result: ${result.responseCode}, ${result.debugMessage}")

            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> DomainBillingResult.Success
                BillingClient.BillingResponseCode.USER_CANCELED -> DomainBillingResult.UserCancelled
                else -> DomainBillingResult.Error(result.debugMessage)
            }
        }
    }

    private suspend fun queryProductDetails(productId: String): ProductDetails? {
        val subscriptionDetails = queryProductDetailsByType(productId, BillingClient.ProductType.SUBS)
        if (subscriptionDetails != null) return subscriptionDetails

        Timber.d("Product not found as SUBS, trying INAPP for: $productId")
        return queryProductDetailsByType(productId, BillingClient.ProductType.INAPP)
    }

    private suspend fun queryProductDetailsByType(productId: String, productType: String): ProductDetails? =
        suspendCancellableCoroutine { cont ->
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(productType)
                            .build()
                    )
                )
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
                Timber.d("Query product details ($productType) result: ${billingResult.responseCode}, ${billingResult.debugMessage}")
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        val list = queryProductDetailsResult.productDetailsList
                        Timber.d("Product details ($productType) list size: ${list?.size ?: 0}")
                        cont.resume(list?.getOrNull(0))
                    }

                    else -> {
                        Timber.e("Query product details ($productType) failed: ${billingResult.debugMessage}")
                        cont.resume(null)
                    }
                }
            }
        }

    override fun currentPurchases(): Flow<List<BillingPurchase>> = _purchases.asStateFlow()

    override suspend fun acknowledgePurchase(purchase: BillingPurchase): Result<Unit> =
        try {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            val result = suspendCancellableCoroutine<com.android.billingclient.api.BillingResult> { cont ->
                billingClient.acknowledgePurchase(params) { cont.resume(it) }
            }
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(result.debugMessage))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to acknowledge purchase")
            Result.failure(e)
        }

    override suspend fun syncPurchaseToBackend(purchase: BillingPurchase): Result<Unit> =
        try {
            val request = VerifyPurchaseRequest(
                purchaseToken = purchase.purchaseToken,
                productId = purchase.productId,
                orderId = purchase.orderId
            )
            val response = apiService.verifyPlayPurchase(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Backend verification failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to sync purchase to backend")
            Result.failure(e)
        }


    private suspend fun refreshPurchases() {
        suspendCancellableCoroutine<Unit> { cont ->
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _purchases.value = purchases?.map { it.toDomainPurchase() } ?: emptyList()
                }
                cont.resume(Unit)
            }
        }
    }

    private fun Purchase.toDomainPurchase() = BillingPurchase(
        purchaseToken = purchaseToken,
        productId = if (products.isNotEmpty()) products[0] else "",
        orderId = orderId
    )
}
