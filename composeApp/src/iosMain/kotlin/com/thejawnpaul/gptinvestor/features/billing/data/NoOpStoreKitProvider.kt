package com.thejawnpaul.gptinvestor.features.billing.data

import org.koin.core.annotation.Singleton

@Singleton
class NoOpStoreKitProvider : StoreKitProvider {
    override fun purchase(
        productId: String,
        onSuccess: (purchaseToken: String, productId: String, orderId: String) -> Unit,
        onError: (message: String) -> Unit,
        onCancelled: () -> Unit
    ) {
        onError("StoreKitProvider not configured")
    }

    override fun fetchCurrentEntitlements(onResult: (List<String>, List<String>) -> Unit) {
        onResult(emptyList(), emptyList())
    }
}
