package com.thejawnpaul.gptinvestor.features.premium.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Singleton

const val PREMIUM_SOURCE_WATCHLIST_FULL = "watchlist_full"

data class PremiumRequest(val source: String, val watchlistCap: Int? = null)

@Singleton
class PremiumEventBus {
    private val _events = MutableSharedFlow<PremiumRequest>(extraBufferCapacity = 1)
    val events: SharedFlow<PremiumRequest> = _events.asSharedFlow()

    fun requestPremium(source: String, watchlistCap: Int? = null) {
        _events.tryEmit(PremiumRequest(source = source, watchlistCap = watchlistCap))
    }
}
