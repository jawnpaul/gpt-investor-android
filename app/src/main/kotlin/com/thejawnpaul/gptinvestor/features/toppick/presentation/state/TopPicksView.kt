package com.thejawnpaul.gptinvestor.features.toppick.presentation.state

import com.thejawnpaul.gptinvestor.features.toppick.presentation.model.TopPickPresentation

data class TopPicksView(
    val loading: Boolean = false,
    val topPicks: List<TopPickPresentation> = emptyList(),
    val error: String? = null
) {
    val showError = error != null && topPicks.isEmpty()
}
