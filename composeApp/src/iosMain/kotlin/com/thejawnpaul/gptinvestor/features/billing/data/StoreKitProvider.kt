package com.thejawnpaul.gptinvestor.features.billing.data

/**
 * ObjC-visible protocol bridging StoreKit purchases to Swift.
 *
 * StoreKit 2 is a Swift-only async API that cannot be called directly from
 * Kotlin/Native. This interface is exposed as an ObjC protocol in the ComposeApp
 * framework, allowing Swift to implement it using the StoreKit framework.
 *
 * The default binding is NoOpStoreKitProvider. The iosApp overrides it at startup
 * via [storeKitProviderModule], mirroring the MixpanelProvider bridge pattern.
 *
 * Acknowledgement note: StoreKit 2 requires calling `transaction.finish()` to remove
 * a purchase from the queue. The Swift implementation must call `finish()` before
 * invoking [onSuccess], so [IosBillingRepository.acknowledgePurchase] is a no-op.
 */
interface StoreKitProvider {

    /**
     * Fetches the product and launches the StoreKit purchase sheet.
     *
     * @param productId The App Store Connect product identifier.
     * @param onSuccess Called with (jwsRepresentation, productId, transactionId) on success.
     * @param onError Called with a human-readable message on failure.
     * @param onCancelled Called when the user dismisses the purchase sheet.
     */
    fun purchase(
        productId: String,
        onSuccess: (purchaseToken: String, productId: String, orderId: String) -> Unit,
        onError: (message: String) -> Unit,
        onCancelled: () -> Unit
    )

    /**
     * Fetches all active entitlements from Transaction.currentEntitlements.
     *
     * @param onResult Called with parallel lists of (purchaseTokens, productIds).
     * Both lists have the same length; index N in each list describes the same entitlement.
     */
    fun fetchCurrentEntitlements(onResult: (purchaseTokens: List<String>, productIds: List<String>) -> Unit)
}
