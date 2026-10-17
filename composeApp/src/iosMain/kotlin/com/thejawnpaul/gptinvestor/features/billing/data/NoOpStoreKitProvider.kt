package com.thejawnpaul.gptinvestor.features.billing.data

/**
 * Fallback no-op StoreKitProvider. Not registered via @Singleton — the real
 * SwiftStoreKitProvider is always injected by mainViewController before Koin starts,
 * so @ComponentScan must not discover this class, or it will override the real provider.
 */
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
