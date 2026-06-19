package com.thejawnpaul.gptinvestor.features.profile.presentation.state

data class ProfileUiState(
    val userName: String = "",
    val queryCount: Int = 0,
    val showSignOutConfirmation: Boolean = false
)
