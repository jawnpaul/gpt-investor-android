package com.thejawnpaul.gptinvestor.features.profile.presentation.state

data class ProfileUiState(
    val userName: String = "",
    val queryCount: Int = 0,
    val watchingCount: Int = 0,
    val savedCompaniesCount: Int = 0,
    val savedPicksCount: Int = 0,
    val savedTidbitsCount: Int = 0,
    val showSignOutConfirmation: Boolean = false,
    val isPremiumUser: Boolean = false,
    val isNotificationsEnabled: Boolean = false,
    val currentTheme: String = "",
    val version: String = ""
) {
    val savedPicksCountString: String = savedPicksCount.toString()
    val savedTidbitsCountString: String = savedTidbitsCount.toString()
}
