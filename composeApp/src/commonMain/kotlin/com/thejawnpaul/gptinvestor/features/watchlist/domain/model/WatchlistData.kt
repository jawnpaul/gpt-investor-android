package com.thejawnpaul.gptinvestor.features.watchlist.domain.model

import com.thejawnpaul.gptinvestor.features.digest.data.remote.model.RecommendedDigestCompany

data class WatchlistData(val items: List<WatchlistItem>, val recommendations: List<RecommendedDigestCompany>)
