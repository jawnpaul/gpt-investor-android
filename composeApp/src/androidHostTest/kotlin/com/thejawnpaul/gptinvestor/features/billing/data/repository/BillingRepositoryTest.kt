package com.thejawnpaul.gptinvestor.features.billing.data.repository

import android.content.Context
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.api.KtorResponse
import com.thejawnpaul.gptinvestor.features.billing.data.PremiumPurchaseTracker
import com.thejawnpaul.gptinvestor.features.billing.data.remote.VerifyPurchaseResponse
import com.thejawnpaul.gptinvestor.features.billing.domain.model.BillingPurchase
import com.thejawnpaul.gptinvestor.remote.TokenStorage
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

class BillingRepositoryTest {

    private val apiService: KtorApiService = mockk()
    private val analyticsLogger: AnalyticsLogger = mockk(relaxed = true)
    private val tracker = PremiumPurchaseTracker(analyticsLogger)
    private val repository = BillingRepository(
        context = mockk<Context>(relaxed = true),
        apiService = apiService,
        tokenStorage = mockk<TokenStorage>(relaxed = true),
        premiumPurchaseTracker = tracker,
        scope = TestScope()
    )
    private val purchase = BillingPurchase(purchaseToken = "token", productId = "premium_monthly", orderId = "order")

    @Test
    fun `successful verify logs premium_purchased once with the paywall trigger`() = runTest {
        tracker.onPurchaseFlowLaunched("watchlist_full", "premium_monthly", value = 4.99, currency = "USD")
        coEvery { apiService.verifyPlayPurchase(any()) } returns
            KtorResponse(
                isSuccessful = true,
                body = VerifyPurchaseResponse(success = true),
                errorBody = null,
                code = 200
            )

        repository.syncPurchaseToBackend(purchase)
        repository.syncPurchaseToBackend(purchase)

        verify(exactly = 1) {
            analyticsLogger.logEvent(
                "premium_purchased",
                mapOf(
                    "trigger" to "watchlist_full",
                    "subscription_id" to "premium_monthly",
                    "value" to 4.99,
                    "currency" to "USD"
                )
            )
        }
    }

    @Test
    fun `failed verify logs nothing`() = runTest {
        tracker.onPurchaseFlowLaunched("watchlist_full", "premium_monthly", value = 4.99, currency = "USD")
        coEvery { apiService.verifyPlayPurchase(any()) } returns
            KtorResponse(isSuccessful = false, body = null, errorBody = """{"error":"expired"}""", code = 400)

        repository.syncPurchaseToBackend(purchase)

        verify(exactly = 0) { analyticsLogger.logEvent("premium_purchased", any()) }
    }

    @Test
    fun `verify without a paywall purchase logs nothing`() = runTest {
        coEvery { apiService.verifyPlayPurchase(any()) } returns
            KtorResponse(
                isSuccessful = true,
                body = VerifyPurchaseResponse(success = true),
                errorBody = null,
                code = 200
            )

        repository.syncPurchaseToBackend(purchase)

        verify(exactly = 0) { analyticsLogger.logEvent("premium_purchased", any()) }
    }
}
