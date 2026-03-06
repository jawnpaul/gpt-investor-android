package com.thejawnpaul.gptinvestor.features.billing.domain.model

data class BillingPurchase(
    val purchaseToken: String,
    val productId: String,
    val orderId: String?
)
