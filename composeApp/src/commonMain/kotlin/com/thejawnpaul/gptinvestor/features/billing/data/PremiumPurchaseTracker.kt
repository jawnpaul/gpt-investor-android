package com.thejawnpaul.gptinvestor.features.billing.data

import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton
class PremiumPurchaseTracker(@Provided private val analyticsLogger: AnalyticsLogger) {

    private data class PaywallPurchase(
        val trigger: String,
        val productId: String,
        val value: Double?,
        val currency: String?
    )

    private val pending = MutableStateFlow<PaywallPurchase?>(null)

    fun onPurchaseFlowLaunched(trigger: String, productId: String, value: Double?, currency: String?) {
        pending.value = PaywallPurchase(trigger, productId, value, currency)
    }

    // Consumed atomically so verify retries or repeated purchase callbacks can't log the same conversion twice.
    fun onPurchaseVerified(productId: String) {
        val purchase = pending.getAndUpdate { if (it?.productId == productId) null else it }
            ?.takeIf { it.productId == productId }
            ?: return
        val params = buildMap<String, Any> {
            put("trigger", purchase.trigger)
            put("subscription_id", purchase.productId)
            if (purchase.value != null && purchase.currency != null) {
                put("value", purchase.value)
                put("currency", purchase.currency)
            }
        }
        analyticsLogger.logEvent("premium_purchased", params)
    }
}
