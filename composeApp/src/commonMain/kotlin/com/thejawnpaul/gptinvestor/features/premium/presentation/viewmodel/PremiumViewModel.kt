package com.thejawnpaul.gptinvestor.features.premium.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.PlatformActions
import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingResult
import com.thejawnpaul.gptinvestor.features.billing.domain.repository.IBillingRepository
import com.thejawnpaul.gptinvestor.features.premium.domain.PremiumEventBus
import com.thejawnpaul.gptinvestor.features.premium.presentation.state.PremiumSheetState
import com.thejawnpaul.gptinvestor.shared.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import org.koin.core.annotation.Provided

@KoinViewModel
class PremiumViewModel(
    private val premiumEventBus: PremiumEventBus,
    private val billingRepository: IBillingRepository,
    private val platformContext: PlatformContext,
    private val platformActions: PlatformActions,
    @Provided private val analyticsLogger: AnalyticsLogger
) : ViewModel() {

    private val _state = MutableStateFlow(PremiumSheetState())
    val state: StateFlow<PremiumSheetState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            premiumEventBus.events.collect { source ->
                analyticsLogger.logEvent("premium_sheet_shown", mapOf("source" to source))
                _state.update { it.copy(isVisible = true) }
            }
        }
    }

    fun dismiss() {
        _state.update { it.copy(isVisible = false) }
    }

    fun purchase() {
        viewModelScope.launch {
            _state.update { it.copy(isPurchasing = true) }
            val result = billingRepository.launchPurchaseFlow(
                platformContext = platformContext,
                productId = BuildConfig.BILLING_PRODUCT_ID
            )
            _state.update { it.copy(isPurchasing = false) }
            when (result) {
                BillingResult.Success, BillingResult.UserCancelled -> dismiss()
                BillingResult.NotSupported -> {
                    dismiss()
                    platformActions.showMessage("In-app purchases coming soon on iOS")
                }
                is BillingResult.Error -> {
                    dismiss()
                    platformActions.showMessage(result.message)
                }
            }
        }
    }
}
