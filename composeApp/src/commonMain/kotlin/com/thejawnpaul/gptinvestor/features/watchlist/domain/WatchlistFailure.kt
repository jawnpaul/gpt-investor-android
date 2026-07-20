package com.thejawnpaul.gptinvestor.features.watchlist.domain

import com.thejawnpaul.gptinvestor.core.functional.Failure

sealed class WatchlistFailure : Failure.FeatureFailure() {
    data object SignUpRequired : WatchlistFailure()
    data class WatchlistFull(val cap: Int) : WatchlistFailure()
    data object TickerNotFound : WatchlistFailure()
    data class GeneralError(val message: String) : WatchlistFailure()
}
