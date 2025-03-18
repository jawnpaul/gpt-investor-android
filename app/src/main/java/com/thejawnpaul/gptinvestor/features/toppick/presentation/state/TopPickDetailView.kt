package com.thejawnpaul.gptinvestor.features.toppick.presentation.state

import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation

data class TopPickDetailView(
    val isLoggedIn: Boolean = false,
    val topPick: TopPickPresentation? = null,
    val loading: Boolean = false,
    val error: String? = null
)
