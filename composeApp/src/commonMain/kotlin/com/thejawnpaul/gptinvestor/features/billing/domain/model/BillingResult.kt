package com.thejawnpaul.gptinvestor.features.billing.domain.model

sealed class BillingResult {
    data object Success : BillingResult()
    data object UserCancelled : BillingResult()
    data object NotSupported : BillingResult()
    data class Error(val message: String) : BillingResult()
}
