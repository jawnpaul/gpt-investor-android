package com.thejawnpaul.gptinvestor.features.watchlist.domain.model

data class WatchlistItem(
    val ticker: String,
    val companyName: String,
    val dateAdded: String,
    val locked: Boolean,
    val logoUrl: String,
    val currentPrice: Double? = null,
    val priceChange: Double? = null,
    val briefStatus: WatchlistBriefStatus? = null
)

/** Where a stock stands in the Morning Brief, from the API's `digest_status`. */
enum class WatchlistBriefStatus { IN_BRIEF, LOCKED, PENDING_NEXT_BRIEF }

// Older backends only send `locked` (over the plan cap); treat that as locked and show no status otherwise.
internal fun watchlistBriefStatusOf(digestStatus: String?, locked: Boolean): WatchlistBriefStatus? =
    when (digestStatus?.lowercase()) {
        "in_brief" -> WatchlistBriefStatus.IN_BRIEF
        "locked" -> WatchlistBriefStatus.LOCKED
        "pending_next_brief" -> WatchlistBriefStatus.PENDING_NEXT_BRIEF
        else -> if (locked) WatchlistBriefStatus.LOCKED else null
    }
