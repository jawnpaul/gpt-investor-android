package com.thejawnpaul.gptinvestor.features.watchlist.domain.model

data class WatchlistItem(
    val ticker: String,
    val companyName: String,
    val dateAdded: String,
    val locked: Boolean,
    val logoUrl: String
)
