package com.thejawnpaul.gptinvestor.features.billing.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository

val LocalBillingRepository = staticCompositionLocalOf<IBillingRepository> {
    error("No IBillingRepository provided. Ensure LocalBillingRepository is provided at the root.")
}
